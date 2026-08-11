// com/javastudy/components/users/internal/UsersServiceImpl.java
package com.javastudy.components.users.internal;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.answer_records.api.service.AnswerRecordsRefLookUp;
import com.javastudy.components.attempt_session.api.service.AttemptSessionRefLookUp;
import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.javastudy.components.authority.api.service.AuthorityService;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.javastudy.components.users.api.dto.USER_UsersInputDto;
import com.javastudy.components.users.api.exception.UsersException;
import com.javastudy.components.users.api.service.UsersService;
import com.javastudy.components.users.internal.UsersErrorCode.UsersDbgMsg;
import com.javastudy.components.weakness.api.service.WeaknessRefLookUp;
import com.login.components.user.api.dto.MyUsersInputDto;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.login.components.user.api.service.MyUsersService;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;
/* ===== [import] END ===== */

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class UsersServiceImpl implements UsersService {

	/* ===== [private] START ===== */
	private final MyUsersService myUsersService; // loginモジュール公開API
	private final AuthorityService authorityService; // 権限Viewの正規化
	private final ToADMIN_UsersViewDtoMapper toViewDtoMapper; // main用 View 変換

	// ★ RefLookup 群：使用中判定のみを依存
	private final AnswerRecordsRefLookUp answerRecordsRefLookup;
	private final AttemptSessionRefLookUp attemptSessionRefLookup;
	private final WeaknessRefLookUp weaknessRefLookup;

	private String getLoginUserViewId() {
		return this.myUsersService.getLoginUser().systemId();
	}

	private String trim(final String s) {
		return s == null ? null : s.trim();
	}

	/** MyUsersViewDto → ADMIN_UsersViewDto 正規化（entityId/権限/isUse を解決） */
	private ADMIN_UsersViewDto toAdmin(final MyUsersViewDto v) {
		if (MyType.isNull(v))
			return null;
		final String entityId = this.myUsersService.getEntityId(v.systemId());
		final ADMIN_AuthorityViewDto auth = this.authorityService
			.toAdminViewDto(v.authorityViewDto());
		final boolean inUse = this.isUse(entityId);
		return this.toViewDtoMapper.fromMyViewDto(v, entityId, auth, inUse);
	}
	/* ===== [private] END ===== */

	/* ===== User向け操作 ===== */

	@Override
	@Transactional
	public void updateUsername(final USER_UsersInputDto dto) {
		final String viewId = this.getLoginUserViewId();
		final String username = this.trim(dto.username());
		this.myUsersService.updateUsername(viewId, username);
	}

	@Override
	@Transactional
	public void resetPassword() {
		final String viewId = this.getLoginUserViewId();
		this.myUsersService.resetPassword(viewId);
	}

	@Override
	@Transactional
	public void resetPassword(final String viewId) {
		this.myUsersService.resetPassword(viewId);
	}

	/* ===== Admin向け View ===== */

	@Override
	@Transactional(readOnly = true)
	public ADMIN_UsersViewDto getAdminViewDtoById(final String id) {
		final MyUsersViewDto myViewDto = this.myUsersService.getViewDtoById(id);
		return this.toAdmin(myViewDto);
	}

	@Override
	@Transactional(readOnly = true)
	public ADMIN_UsersViewDto getAdminViewDtoByViewId(final String viewId) {
		final MyUsersViewDto myViewDto = this.myUsersService.getViewDtoByViewId(viewId);
		return this.toAdmin(myViewDto);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ADMIN_UsersViewDto> getAdminViewDtoList() {
		// ① 全件取得 → ユーザー名昇順で安定ソート
		final List<MyUsersViewDto> rows = this.myUsersService.findAllNoPaging();
		if (rows.isEmpty()) {
			return List.of();
		}
		final Comparator<MyUsersViewDto> byUsernameAsc = Comparator
			.comparing(v -> v.username() == null ? "" : v.username());
		final List<MyUsersViewDto> sorted = rows.stream().sorted(byUsernameAsc).toList();

		// ② user の entityId を一括解決（viewId→entityId）
		final Map<String, String> userEntityIdByViewId = sorted.stream()
			.collect(Collectors.toMap(
				MyUsersViewDto::systemId,
				v -> this.myUsersService.getEntityId(v.systemId()),
				(a, b) -> a,
				java.util.LinkedHashMap::new // 順序維持
			));
		final Set<String> userEntityIds = new java.util.HashSet<>(userEntityIdByViewId.values());

		// ③ 権限：重複排除して entityId 化 → ADMIN 権限を一括取得
		final Set<String> authViewIds = sorted.stream()
			.map(v -> v.authorityViewDto().systemId())
			.collect(Collectors.toSet());
		final Map<String, String> authEntityIdByViewId = authViewIds.stream()
			.collect(Collectors.toMap(vid -> vid, vid -> this.authorityService.getEntityId(vid)));
		final Set<String> authEntityIds = new java.util.HashSet<>(authEntityIdByViewId.values());
		final Map<String, ADMIN_AuthorityViewDto> adminAuthMap = this.authorityService
			.getAdminViewDtoMapByIds(authEntityIds);

		// ④ 使用中ユーザーをバルク判定（AnswerRecords / AttemptSession / Weakness）
		final Set<String> used = new java.util.HashSet<>();
		used.addAll(this.answerRecordsRefLookup.findUsedUserIds(userEntityIds));
		used.addAll(this.attemptSessionRefLookup.findUsedUserIds(userEntityIds));
		used.addAll(this.weaknessRefLookup.findUsedUserIds(userEntityIds));

		// ⑤ DTO 変換（権限は adminAuthMap、使用中は used を参照）
		return sorted.stream()
			.map(v -> {
				final String userId = userEntityIdByViewId.get(v.systemId());
				final String authEntityId = authEntityIdByViewId
					.get(v.authorityViewDto().systemId());
				final ADMIN_AuthorityViewDto adminAuth = adminAuthMap.get(authEntityId);
				final boolean inUse = used.contains(userId);
				return this.toViewDtoMapper.fromMyViewDto(v, userId, adminAuth, inUse);
			})
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ADMIN_UsersViewDto> findAll(final Pageable pageable) {
		return this.myUsersService.findAll(pageable).map(this::toAdmin);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ADMIN_UsersViewDto> searchByUsername(
		final String likeUsername,
		final Pageable pageable) {
		final String kw = MyType.isBlank(likeUsername) ? "" : likeUsername.trim();
		return this.myUsersService.searchByUsername(kw, pageable).map(this::toAdmin);
	}

	/* ===== パススルー ===== */

	@Override
	@Transactional(readOnly = true)
	public String getEntityId(final String viewId) {
		return this.myUsersService.getEntityId(viewId);
	}

	@Override
	@Transactional(readOnly = true)
	public MyUsersViewDto getLoginUser() {
		return this.myUsersService.getLoginUser();
	}

	/* ===== 変更系（戻りは Admin用View に正規化） ===== */

	@Override
	@Transactional
	public void create(final MyUsersInputDto input) {
		this.myUsersService.create(input);
	}

	@Override
	@Transactional
	public void update(final String currentViewId, final MyUsersInputDto input) {
		this.myUsersService.update(currentViewId, input);

	}

	@Override
	@Transactional
	public void update(final MyUsersInputDto input) {
		this.myUsersService.update(input);

	}

	@Override
	@Transactional
	public void updateAuthority(final String userViewId, final String authorityViewId) {
		this.myUsersService.updateAuthority(userViewId, authorityViewId);
	}

	@Override
	@Transactional
	public void changePassword(final String userViewId, final String rawPassword) {
		this.myUsersService.changePassword(userViewId, rawPassword);
	}

	@Override
	@Transactional
	public void deleteByViewId(final String viewId) {
		if (this.isUse(this.myUsersService.getEntityId(viewId))) {
			throw new UsersException(
				UsersErrorCode.IN_USE,
				UsersDbgMsg.inUse(this.myUsersService.getEntityId(viewId)));
		}
		this.myUsersService.delete(viewId);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, ADMIN_UsersViewDto> getViewDtoMapByIds(final Set<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return Map.of();
		}
		// ① Users を IN 句で一括取得（key = user の entityId）
		final Map<String, MyUsersViewDto> usersMap = this.myUsersService.getViewDtoMapByIds(ids);

		// ② 権限IDを一括抽出（My→entityId 化）→ ADMIN 権限を一括取得
		final Set<String> authIds = usersMap.values().stream()
			.map(v -> this.authorityService.getEntityId(v.authorityViewDto().systemId()))
			.collect(Collectors.toSet());
		final Map<String, ADMIN_AuthorityViewDto> adminAuthMap = this.authorityService
			.getAdminViewDtoMapByIds(authIds);

		// ③ 使用中ユーザーを一括判定（RefLookUp のバルクAPIを利用）
		final Set<String> userIds = usersMap.keySet();
		final Set<String> used1 = this.answerRecordsRefLookup.findUsedUserIds(userIds);
		final Set<String> used2 = this.attemptSessionRefLookup.findUsedUserIds(userIds);
		final Set<String> used3 = this.weaknessRefLookup.findUsedUserIds(userIds);
		final Set<String> used = new HashSet<>(used1);
		used.addAll(used2);
		used.addAll(used3);

		// ④ 4引数で変換（キーは user の entityId を維持）
		return usersMap.entrySet().stream().collect(Collectors.toMap(
			Map.Entry::getKey,
			e -> {
				final MyUsersViewDto v = e.getValue();
				final String userEntityId = e.getKey();
				final String authEntityId = this.authorityService
					.getEntityId(v.authorityViewDto().systemId());
				final ADMIN_AuthorityViewDto adminAuth = adminAuthMap.get(authEntityId);
				final boolean inUse = used.contains(userEntityId);
				return this.toViewDtoMapper.fromMyViewDto(v, userEntityId, adminAuth, inUse);
			}));
	}

	/* ===== 参照整合チェック ===== */

	/** Users の削除可否（true = 使用中 = 削除不可） */
	@Override
	@Transactional(readOnly = true)
	public boolean isUse(final String userId) {
		if (MyType.isBlank(userId))
			return false;
		return this.answerRecordsRefLookup.existsByUserId(userId)
			|| this.attemptSessionRefLookup.existsByUserId(userId)
			|| this.weaknessRefLookup.existsByUserId(userId);
	}

	/* ===== 補助 ===== */

	@Override
	@Transactional(readOnly = true)
	public ADMIN_UsersViewDto toAdminViewDto(final MyUsersViewDto myViewDto) {
		return this.toAdmin(myViewDto);
	}
}
