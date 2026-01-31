// com/javastudy/components/sankou_books/internal/SankouBooksServiceImpl.java
package com.javastudy.components.sankou_books.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.answer_records.api.service.AnswerRecordsRefLookUp;
import com.javastudy.components.attempt_session.api.service.AttemptSessionRefLookUp;
import com.javastudy.components.chapter.api.service.ChapterRefLookUp;
import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsRefLookUp;
import com.javastudy.components.sankou_book_color.api.domain.SankouBookColorEnum;
import com.javastudy.components.sankou_book_color.api.service.SankouBookColorService;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksInputDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.exception.SankouBooksException;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.components.sankou_books.internal.SankouBooksDB.SankouBooksIdParam;
import com.javastudy.components.sankou_books.internal.SankouBooksErrorCode.SankouBooksDbgMsg;
import com.util.security.id.DbIdSequence;
import com.util.security.role.RoleUtil;
import com.util.type.MyType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;

/* 機能：参考書サービス（ユーザー向け＋管理系） */
@Service
@AllArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class SankouBooksServiceImpl implements SankouBooksService {

	/* ===== [dep] START ===== */
	private final SankouBooksRepository repository;
	private final ToUSER_SankouBooksViewDtoMapper toUserViewDtoMapper;
	private final ToADMIN_SankouBooksViewDtoMapper toAdminViewDtoMapper;
	private final SankouBookColorService colorService; // 色の解決（ViewId/EntityId/Enum）

	// ★ RefLookup 群：使用中判定のみ依存
	private final ChapterRefLookUp chapterRefLookup;
	private final KurohonQuestionsRefLookUp questionsRefLookup;
	private final AttemptSessionRefLookUp attemptSessionRefLookup;
	private final AnswerRecordsRefLookUp answerRecordsRefLookup;

	private final DbIdSequence idSeq; // 採番器（衝突回避つき）
	@PersistenceContext
	private final EntityManager em;
	/* ===== [dep] END ===== */

	/* ===== [id util] START ===== */

	/** 新規 SankouBooks 用IDを採番（シーケンスと既存IDの衝突を自動回避） */
	String allocateNewId() {
		return this.idSeq.nextIdAvoidCollision(
			SankouBooksIdParam.SEQUENCE,
			SankouBooksIdParam.PREFIX,
			SankouBooksIdParam.PAD,
			SankouBooksDB.TABLE_FQN,
			SankouBooksDB.SankouBooksColumn.ID);
	}

	/* ===== [query] START ===== */

	@Transactional(readOnly = true)
	@Override
	public List<USER_SankouBooksViewDto> getUserViewDtoList() {
		return this.repository.findAll().stream()
			.map(e -> this.toUserViewDtoMapper.fromEntity(
				e,
				this.colorService.getEnumById(e.getColorId())))
			.toList();
	}

	@Transactional(readOnly = true)
	@Override
	public USER_SankouBooksViewDto getUserViewDtoByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_ID,
				SankouBooksDbgMsg.blankId("viewId"));
		}
		final String entityId = SankouBooksIdBridge.toEntityId(viewId);
		final SankouBooksEntity entity = this.getEntity(entityId);
		return this.toUserViewDtoMapper.fromEntity(
			entity,
			this.colorService.getEnumById(entity.getColorId()));
	}

	@Transactional(readOnly = true)
	@Override
	public USER_SankouBooksViewDto getUserViewDtoById(final String id) {
		if (MyType.isBlank(id)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_ID,
				SankouBooksDbgMsg.blankId("id"));
		}
		final SankouBooksEntity entity = this.getEntity(id);
		return this.toUserViewDtoMapper.fromEntity(
			entity,
			this.colorService.getEnumById(entity.getColorId()));
	}

	@Transactional(readOnly = true)
	@Override
	public String getEntityId(final String viewId) {
		return SankouBooksIdBridge.toEntityId(viewId);
	}

	@Transactional(readOnly = true)
	@Override
	public ADMIN_SankouBooksViewDto getAdminViewDtoByViewId(final String viewId) {
		final String id = SankouBooksIdBridge.toEntityId(viewId);
		final SankouBooksEntity entity = this.getEntity(id);
		return this.toAdminViewDtoMapper.fromEntity(
			entity,
			this.colorService.getEnumById(entity.getColorId()),
			this.colorService.getAdminViewDtoById(entity.getColorId()),
			this.isUse(entity.getId()));
	}

	@Transactional(readOnly = true)
	@Override
	public ADMIN_SankouBooksViewDto getAdminViewDtoById(final String id) {
		final SankouBooksEntity entity = this.getEntity(id);
		return this.toAdminViewDtoMapper.fromEntity(
			entity,
			this.colorService.getEnumById(entity.getColorId()),
			this.colorService.getAdminViewDtoById(entity.getColorId()),
			this.isUse(entity.getId()));
	}

	@Transactional(readOnly = true)
	@Override
	public List<ADMIN_SankouBooksViewDto> getAdminViewDtoList() {
		final List<SankouBooksEntity> rows = this.repository.findAllByOrderByNameAsc();
		if (rows.isEmpty()) {
			return List.of();
		}

		final Set<String> bookIds = rows.stream().map(SankouBooksEntity::getId)
			.collect(Collectors.toSet());
		final Set<String> colorIds = rows.stream().map(SankouBooksEntity::getColorId)
			.collect(Collectors.toSet());

		final Set<String> used = new HashSet<>();
		used.addAll(this.chapterRefLookup.findUsedSankouBookIds(bookIds));
		used.addAll(this.questionsRefLookup.findUsedSankouBookIds(bookIds));
		used.addAll(this.attemptSessionRefLookup.findUsedSankouBookIds(bookIds));
		used.addAll(this.answerRecordsRefLookup.findUsedSankouBookIds(bookIds));

		final Map<String, SankouBookColorEnum> colorEnumMap = colorIds.stream()
			.collect(Collectors.toMap(id -> id, id -> this.colorService.getEnumById(id)));

		return rows.stream()
			.map(e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				colorEnumMap.get(e.getColorId()),
				this.colorService.getAdminViewDtoById(e.getColorId()),
				used.contains(e.getId())))
			.toList();
	}

	/** true=使用中（削除不可）。 */
	@Transactional(readOnly = true)
	@Override
	public boolean isUse(final String sankouBookId) {
		if (MyType.isBlank(sankouBookId))
			return false;
		return this.chapterRefLookup.existsBySankouBookId(sankouBookId)
			|| this.questionsRefLookup.existsBySankouBookId(sankouBookId)
			|| this.attemptSessionRefLookup.existsBySankouBookId(sankouBookId)
			|| this.answerRecordsRefLookup.existsBySankouBookId(sankouBookId);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, ADMIN_SankouBooksViewDto> getAdminViewDtoMapByIds(final Set<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return Map.of();
		}
		final List<SankouBooksEntity> entities = this.repository.findAllByIdIn(ids);

		final Set<String> bookIds = entities.stream().map(SankouBooksEntity::getId)
			.collect(Collectors.toSet());
		final Set<String> used = new HashSet<>();
		used.addAll(this.chapterRefLookup.findUsedSankouBookIds(bookIds));
		used.addAll(this.questionsRefLookup.findUsedSankouBookIds(bookIds));
		used.addAll(this.attemptSessionRefLookup.findUsedSankouBookIds(bookIds));
		used.addAll(this.answerRecordsRefLookup.findUsedSankouBookIds(bookIds));

		return entities.stream().collect(Collectors.toMap(
			SankouBooksEntity::getId,
			e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				this.colorService.getEnumById(e.getColorId()),
				this.colorService.getAdminViewDtoById(e.getColorId()),
				used.contains(e.getId()))));
	}

	/* ===== [command] START ===== */

	@Override
	@Transactional
	@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
	public void create(final ADMIN_SankouBooksInputDto input) {
		if (MyType.isNull(input)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_ID,
				SankouBooksDbgMsg.blankId("input"));
		}
		if (MyType.isBlank(input.name())) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_NAME,
				SankouBooksDbgMsg.blankName());
		}
		if (MyType.isBlank(input.colorViewId())) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_COLOR,
				SankouBooksDbgMsg.blankColor());
		}
		if (this.repository.existsByName(input.name())) {
			throw new SankouBooksException(
				SankouBooksErrorCode.DUPLICATE_NAME,
				SankouBooksDbgMsg.duplicateName(input.name()));
		}
		this.repository.saveAndReload(this.toEntityNew(input), this.em);
	}

	@Override
	@Transactional
	@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
	public void update(final ADMIN_SankouBooksInputDto input) {
		if (MyType.isNull(input) || MyType.isBlank(input.viewId())) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_ID,
				SankouBooksDbgMsg.blankId("viewId"));
		}
		final String name = this.trim(input.name());
		if (MyType.isBlank(name)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_NAME,
				SankouBooksDbgMsg.blankName());
		}
		if (MyType.isBlank(input.colorViewId())) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_COLOR,
				SankouBooksDbgMsg.blankColor());
		}

		final String id = SankouBooksIdBridge.toEntityId(input.viewId());
		if (this.repository.existsByNameAndIdNot(name, id)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.DUPLICATE_NAME,
				SankouBooksDbgMsg.duplicateName(name));
		}

		this.repository.save(this.toEntityUpdate(input));
	}

	@Override
	@Transactional
	@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
	public void deleteByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.BLANK_ID,
				SankouBooksDbgMsg.blankId("viewId"));
		}
		final String id = SankouBooksIdBridge.toEntityId(viewId);

		if (!this.repository.existsById(id)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.NOT_ENTITY,
				SankouBooksDbgMsg.notEntity(id));
		}
		if (this.isUse(id)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.IN_USE,
				SankouBooksDbgMsg.inUse(id));
		}
		this.repository.deleteById(id);
	}

	/* ===== [private] START ===== */

	private SankouBooksEntity toEntityNew(final ADMIN_SankouBooksInputDto input) {
		final String newId = this.allocateNewId(); // ★ 衝突回避つき
		if (this.isExist(newId)) {
			throw new SankouBooksException(
				SankouBooksErrorCode.DUPLICATE_ID,
				SankouBooksDbgMsg.duplicateId(newId));
		}
		final String colorId = this.colorService.getEntityId(input.colorViewId()); // ★ 修正：colorViewId を解決
		return SankouBooksEntity.builder()
			.id(newId) // SA + 5桁
			.name(input.name().trim())
			.colorId(colorId)
			.build();
	}

	private SankouBooksEntity toEntityUpdate(final ADMIN_SankouBooksInputDto input) {
		final String id = this.getEntityId(input.viewId());
		final String colorId = this.colorService.getEntityId(input.colorViewId()); // ★ 修正
		return SankouBooksEntity.builder()
			.id(id) // SA + 5桁
			.name(this.trim(input.name()))
			.colorId(colorId)
			.build();
	}

	@Transactional(readOnly = true)
	private SankouBooksEntity getEntity(final String id) {
		return this.repository
			.findById(id)
			.orElseThrow(() -> new SankouBooksException(
				SankouBooksErrorCode.NOT_ENTITY,
				SankouBooksDbgMsg.notEntity(id)));
	}

	private String trim(final String s) {
		return s == null ? null : s.trim();
	}

	@Transactional(readOnly = true)
	private boolean isExist(final String id) {
		return this.repository.existsById(id);
	}
}
