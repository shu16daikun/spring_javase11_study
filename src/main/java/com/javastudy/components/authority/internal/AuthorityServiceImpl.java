package com.javastudy.components.authority.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.javastudy.components.authority.api.service.AuthorityService;
import com.login.components.authority.api.domain.MyAuthorityEnum;
import com.login.components.authority.api.dto.MyAuthorityInputDto;
import com.login.components.authority.api.dto.MyAuthorityViewDto;
import com.login.components.authority.api.service.MyAuthorityService;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.login.components.user.api.service.MyUsersRefLookUp;
import com.my.util.type.MyType;

import lombok.AllArgsConstructor;

/**
 * 【統合ファサード実装】main 側は本クラス“だけ”を使う。
 * 中で login の {@link MyAuthorityService} を委譲し、入出力を UI 向けに正規化。
 */
@Service
@AllArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

	/* ===== [private] START ===== */
	private final MyAuthorityService myService;
	private final ToADMIN_AuthorityViewDtoMapper toAdminViewDtoMapper;
	// ★ 追加：Users 側の参照ルックアップ（権限の使用中判定を一括で）
	private final MyUsersRefLookUp usersRefLookup;

	private ADMIN_AuthorityViewDto toAdmin(final MyAuthorityViewDto v) {
		if (MyType.isNull(v))
			return null;
		final String viewId = v.systemId();
		final String entityId = this.myService.getEntityId(viewId);
		final boolean isUse = this.myService.isUse(entityId);
		return this.toAdminViewDtoMapper.fromMyViewDto(v, entityId, isUse);
	}
	/* ===== [private] END ===== */

	/* ===== Admin向け View 取得 ===== */

	@Override
	@Transactional(readOnly = true)
	public ADMIN_AuthorityViewDto getAdminViewDtoById(final String id) {
		return this.toAdmin(this.myService.getViewDtoById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public ADMIN_AuthorityViewDto getAdminViewDtoByViewId(final String viewId) {
		return this.toAdmin(this.myService.getViewDtoByViewId(viewId));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ADMIN_AuthorityViewDto> getAdminViewDtoList() {
		return this.myService.findAll().stream()
			.map(this::toAdmin)
			.toList();
	}

	/* ===== パススルー面（login機能の集約提供） ===== */

	@Override
	@Transactional(readOnly = true)
	public String getEntityId(final String viewId) {
		return this.myService.getEntityId(viewId);
	}

	@Override
	@Transactional(readOnly = true)
	public String getNameById(final String id) {
		return this.myService.getNameById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean hasRole(final MyAuthorityEnum role, final MyUsersViewDto userDto) {
		return this.myService.hasRole(role, userDto);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isUse(final String authorityId) {
		if (MyType.isBlank(authorityId))
			return false;
		return this.myService.isUse(authorityId);
	}

	/* === 追加：ID集合→ADMIN_AuthorityViewDto の一括取得（IN 最適化用） === */
	@Override
	@Transactional(readOnly = true)
	public Map<String, ADMIN_AuthorityViewDto> getAdminViewDtoMapByIds(final Set<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return Map.of();
		}
		// 1) my 側で一括取得（key = authority の entityId）
		final Map<String, MyAuthorityViewDto> myMap = this.myService.getViewDtoMapByIds(ids);

		// 2) 使用中の権限IDを一括で拾う（Users を横断。N+1回避）
		final Set<String> used = this.usersRefLookup.findUsedAuthorityIds(myMap.keySet());

		// 3) 値を ADMIN に変換（キーは entityId をそのまま維持）
		return myMap.entrySet().stream().collect(Collectors.toMap(
			Map.Entry::getKey,
			e -> this.toAdminViewDtoMapper.fromMyViewDto(
				e.getValue(),
				e.getKey(), // entityId
				used.contains(e.getKey()) // isUse
			)));
	}
	/* ===== 検索 ===== */

	@Override
	@Transactional(readOnly = true)
	public Page<ADMIN_AuthorityViewDto>
		searchByName(final String likeName, final Pageable pageable) {
		return this.myService.searchByName(likeName, pageable)
			.map(this::toAdmin);
	}

	/* ===== 変更系（UI向けに ADMIN_* へ正規化して返却） ===== */

	@Override
	@Transactional
	public ADMIN_AuthorityViewDto create(final MyAuthorityInputDto input) {
		final MyAuthorityViewDto created = this.myService.create(input);
		return this.toAdmin(created);
	}

	@Override
	@Transactional
	public ADMIN_AuthorityViewDto update(final MyAuthorityInputDto input) {
		final MyAuthorityViewDto updated = this.myService.update(input);
		return this.toAdmin(updated);
	}

	@Override
	@Transactional
	public void updateName(final String viewId, final String newName) {
		this.myService.updateName(viewId, newName);
	}

	@Override
	@Transactional
	public void deleteByViewId(final String viewId) {
		// IN_USE 等の判定・例外は login 側で一元化（重複させない）
		this.myService.delete(viewId);
	}

	@Override
	@Transactional(readOnly = true)
	public ADMIN_AuthorityViewDto toAdminViewDto(final MyAuthorityViewDto myViewDto) {
		return this.toAdmin(myViewDto);
	}
}
