// com/javastudy/components/chapter/internal/ChapterServiceImpl.java
package com.javastudy.components.chapter.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.answer_records.api.service.AnswerRecordsRefLookUp;
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterInputDto;
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.chapter.api.exception.ChapterException;
import com.javastudy.components.chapter.api.service.ChapterService;
import com.javastudy.components.chapter.internal.ChapterDB.ChapterIdParam;
import com.javastudy.components.chapter.internal.ChapterErrorCode.ChapterDbgMsg;
import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsRefLookUp;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.components.weakness.api.service.WeaknessRefLookUp;
import com.my.util.security.id.DbIdSequence;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class ChapterServiceImpl implements ChapterService {

	/* ===== [private] START ===== */
	private final ChapterRepository repository;
	private final ToADMIN_ChapterViewDtoMapper toAdminViewDtoMapper;
	private final ToUSER_ChapterViewDtoMapper toUserViewDtoMapper;
	private final SankouBooksService booksService;

	// ★ RefLookup（Service 直接依存を避ける）
	private final KurohonQuestionsRefLookUp questionsRefLookup;
	private final AnswerRecordsRefLookUp answerRecordsRefLookup;
	private final WeaknessRefLookUp weaknessRefLookup;

	private final DbIdSequence idSeq;

	@PersistenceContext
	private final EntityManager em;

	private final String LOG_KEY_NO = "no=";
	private final String REG_2DIGITS = "^[0-9]{2}$";

	private String trim(final String s) {
		return s == null ? null : s.trim();
	}

	private ChapterEntity getEntityById(final String id) {
		return this.repository
			.findById(id)
			.orElseThrow(() -> new ChapterException(
				ChapterErrorCode.NOT_ENTITY,
				ChapterDbgMsg.notEntity(id)));
	}

	private void assertNoFormat(final String no) {
		if (MyType.isBlank(no) || !no.matches(this.REG_2DIGITS)) {
			throw new ChapterException(
				ChapterErrorCode.BLANK_ID,
				ChapterDbgMsg.blankId("no format invalid: no=" + MyType.orEmpty(no)));
		}
	}
	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */

	/* --- admin: query --- */

	@Override
	@Transactional
	public ADMIN_ChapterViewDto getAdminViewDtoByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("viewId"));
		}
		final String id = ChapterIdBridge.toEntityId(viewId);
		final ChapterEntity e = this.getEntityById(id);
		return this.toAdminViewDtoMapper.fromEntity(
			e,
			this.booksService.getAdminViewDtoById(e.getSankouBookId()),
			this.isUse(e.getId()));
	}

	@Override
	@Transactional
	public ADMIN_ChapterViewDto getAdminViewDtoById(final String id) {
		if (MyType.isBlank(id)) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("id"));
		}
		final ChapterEntity e = this.getEntityById(id);
		return this.toAdminViewDtoMapper.fromEntity(
			e,
			this.booksService.getAdminViewDtoById(e.getSankouBookId()),
			this.isUse(e.getId()));
	}

	@Transactional(readOnly = true)
	@Override
	public List<ADMIN_ChapterViewDto> getAdminViewDtoList() {
		// ① 本体を安定ソートで一括取得
		final List<ChapterEntity> rows = this.repository.findAllByOrderBySankouBookIdAscNoAsc();
		if (rows.isEmpty()) {
			return List.of();
		}

		// ② 参照ID（Books）を一括抽出
		final Set<String> bookIds = rows.stream()
			.map(ChapterEntity::getSankouBookId)
			.collect(Collectors.toSet());

		// ③ Books を一括取得（IN最適化）
		final Map<String, ADMIN_SankouBooksViewDto> booksMap = this.booksService
			.getAdminViewDtoMapByIds(bookIds);

		// ④ DTO 化（isUse は現行APIで逐次判定）
		return rows.stream()
			.map(e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				booksMap.get(e.getSankouBookId()),
				this.isUse(e.getId())))
			.toList();
	}

	@Override
	@Transactional
	public String getEntityId(final String viewId) {
		return ChapterIdBridge.toEntityId(viewId);
	}

	/* ★ 追加：章ID集合 → ADMIN_ViewDto を一括取得（IN最適化） */
	@Override
	@Transactional(readOnly = true)
	public Map<String, ADMIN_ChapterViewDto> getAdminViewDtoMapByIds(final Set<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return Map.of();
		}
		// 1) 本体を IN 句で一括取得
		final List<ChapterEntity> entities = this.repository.findAllByIdIn(ids);
		if (entities.isEmpty()) {
			return Map.of();
		}

		// 2) 参照（Books）を一括解決
		final Set<String> bookIds = entities.stream()
			.map(ChapterEntity::getSankouBookId)
			.collect(Collectors.toSet());
		final Map<String, ADMIN_SankouBooksViewDto> booksMap = this.booksService
			.getAdminViewDtoMapByIds(bookIds);

		// 3) Map 化（キー＝chapter entityId）。isUse は現状逐次判定（必要なら後日バルク化）
		return entities.stream().collect(Collectors.toMap(
			ChapterEntity::getId,
			e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				booksMap.get(e.getSankouBookId()),
				this.isUse(e.getId()))));
	}

	/* --- admin: command --- */

	@Override
	@Transactional
	public void create(final ADMIN_ChapterInputDto input) {
		if (MyType.isNull(input)) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("input"));
		}
		this.assertNoFormat(input.no());
		if (MyType.isBlank(input.name())) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("name"));
		}
		if (MyType.isBlank(input.bookViewId())) {
			throw new ChapterException(
				ChapterErrorCode.BLANK_ID,
				ChapterDbgMsg.blankId("bookViewId"));
		}

		final String sankouBookId = this.booksService.getEntityId(input.bookViewId());
		if (this.repository.existsBySankouBookIdAndNo(sankouBookId, input.no())) {
			throw new ChapterException(
				ChapterErrorCode.DUPLICATE_NO,
				ChapterDbgMsg.duplicateNo(sankouBookId, input.no()));
		}

		this.repository.saveAndReload(
			this.toEntityNew(input, sankouBookId), this.em);
	}

	@Override
	@Transactional
	public void update(final ADMIN_ChapterInputDto input) {
		if (MyType.isNull(input) || MyType.isBlank(input.viewId())) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("viewId"));
		}
		this.assertNoFormat(input.no());
		if (MyType.isBlank(input.name())) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("name"));
		}
		if (MyType.isBlank(input.bookViewId())) {
			throw new ChapterException(
				ChapterErrorCode.BLANK_ID,
				ChapterDbgMsg.blankId("bookViewId"));
		}

		final String id = ChapterIdBridge.toEntityId(input.viewId());
		final String sankouBookId = this.booksService.getEntityId(input.bookViewId());

		if (this.repository.existsBySankouBookIdAndNoAndIdNot(sankouBookId, input.no(), id)) {
			throw new ChapterException(
				ChapterErrorCode.DUPLICATE_NO,
				ChapterDbgMsg.duplicateNo(sankouBookId, input.no()));
		}

		this.repository.save(this.toEntityUpdate(input));
	}

	@Override
	@Transactional
	public void deleteByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("viewId"));
		}
		final String id = ChapterIdBridge.toEntityId(viewId);
		if (!this.repository.existsById(id)) {
			throw new ChapterException(
				ChapterErrorCode.NOT_ENTITY,
				ChapterDbgMsg.notEntity(id));
		}
		// 使用中なら 409: IN_USE
		if (this.isUse(id)) {
			throw new ChapterException(
				ChapterErrorCode.IN_USE,
				ChapterDbgMsg.inUse(id));
		}
		this.repository.deleteById(id);
	}

	/** true=使用中（削除不可）。引数は entityId 固定。 */
	@Override
	@Transactional(readOnly = true)
	public boolean isUse(final String chapterId) {
		if (MyType.isBlank(chapterId))
			return false;
		return this.questionsRefLookup.existsByChapterId(chapterId)
			|| this.answerRecordsRefLookup.existsByChapterId(chapterId)
			|| this.weaknessRefLookup.existsByChapterId(chapterId);
	}

	/* --- user: query --- */

	@Override
	@Transactional
	public List<USER_ChapterViewDto>
		getUserViewDtoListFilterSankouBooks(final String bookViewId) {
		if (MyType.isBlank(bookViewId)) {
			throw new ChapterException(
				ChapterErrorCode.BLANK_ID,
				ChapterDbgMsg.blankId("bookViewId"));
		}
		final String bookId = this.booksService.getEntityId(bookViewId);
		return this.repository.findAllBySankouBookIdOrderByNoAsc(bookId).stream()
			.map(e -> this.toUserViewDtoMapper.fromEntity(
				e,
				this.booksService.getUserViewDtoById(e.getSankouBookId())))
			.toList();
	}

	@Override
	@Transactional
	public USER_ChapterViewDto getUserViewDtoByNo(final String bookViewId, final String no) {
		if (MyType.isBlank(bookViewId)) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("bookId"));
		}
		this.assertNoFormat(no);
		final String bookId = this.booksService.getEntityId(bookViewId);
		final ChapterEntity e = this.repository
			.findBySankouBookIdAndNo(bookId, no)
			.orElseThrow(() -> new ChapterException(
				ChapterErrorCode.NOT_ENTITY,
				ChapterDbgMsg.notEntity("sankouBookId", bookId + ", " + this.LOG_KEY_NO + no)));
		return this.toUserViewDtoMapper.fromEntity(
			e,
			this.booksService.getUserViewDtoById(e.getSankouBookId()));
	}

	@Override
	@Transactional
	public USER_ChapterViewDto getUserViewDtoByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("viewId"));
		}
		final String id = ChapterIdBridge.toEntityId(viewId);
		final ChapterEntity e = this.getEntityById(id);
		return this.toUserViewDtoMapper.fromEntity(
			e,
			this.booksService.getUserViewDtoById(e.getSankouBookId()));
	}

	@Override
	@Transactional
	public USER_ChapterViewDto getUserViewDtoById(final String id) {
		if (MyType.isBlank(id)) {
			throw new ChapterException(ChapterErrorCode.BLANK_ID, ChapterDbgMsg.blankId("id"));
		}
		final ChapterEntity e = this.getEntityById(id);
		return this.toUserViewDtoMapper.fromEntity(
			e,
			this.booksService.getUserViewDtoById(e.getSankouBookId()));
	}

	@Override
	@Transactional
	public boolean existsByNo(final String bookViewId, final String no) {
		if (MyType.isBlank(bookViewId)) {
			throw new ChapterException(
				ChapterErrorCode.BLANK_ID,
				ChapterDbgMsg.blankId("bookViewId"));
		}
		this.assertNoFormat(no);
		final String sankouBookId = this.booksService.getEntityId(bookViewId);
		return this.repository.existsBySankouBookIdAndNo(sankouBookId, no);
	}
	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	private ChapterEntity
		toEntityNew(final ADMIN_ChapterInputDto input, final String sankouBookId) {

		// ★ 衝突回避つき採番（seq → prefix + left-pad、既存と衝突したら再試行）
		final String newId = this.allocateNewId();

		if (this.isExist(newId)) {
			throw new ChapterException(
				ChapterErrorCode.DUPLICATE_ID,
				ChapterDbgMsg.duplicateId(newId));
		}
		return ChapterEntity.builder()
			.id(newId)
			.no(input.no()) // "01".."99"
			.name(this.trim(input.name()))
			.sankouBookId(sankouBookId)
			.build();
	}

	private ChapterEntity
		toEntityUpdate(final ADMIN_ChapterInputDto input) {
		final String id = this.getEntityId(input.viewId());
		final String bookId = this.booksService.getEntityId(input.bookViewId());
		return ChapterEntity.builder()
			.id(id)
			.no(input.no()) // "01".."99"
			.name(this.trim(input.name()))
			.sankouBookId(bookId)
			.build();
	}

	/**
	 * allocateNewId
	 * <p>
	 * 目的：PostgreSQLの <code>ch_id_seq</code> を用い、<code>PREFIX + 左ゼロ埋め(8桁)</code>
	 * の
	 * 新規IDを採番する。既存レコードと衝突した場合は次値で再試行し、上限回数超過時は
	 * <code>MySecurityException(DB_ACCESS_FAILURE)</code> を送出する。
	 * <p>
	 * 実装：{@link DbIdSequence#nextIdAvoidCollision(String, String, int, String, String)}
	 * を使用。テーブルは <code>public.chapter</code>、列は <code>id</code>。
	 *
	 * @return 採番済みの一意ID（例：CH00000001）
	 */
	private String allocateNewId() {
		return this.idSeq.nextIdAvoidCollision(
			ChapterIdParam.SEQUENCE,
			ChapterIdParam.PREFIX,
			ChapterIdParam.PAD,
			"public." + ChapterDB.TABLE,
			ChapterDB.ChapterColumn.ID);
	}

	/**
	 * isExist
	 * <p>
	 * 目的：指定した <strong>entityId</strong>（例：CH00000001）が既に存在するかを主キーで判定する。
	 * <ul>
	 * <li>null/空白は常に false を返す。</li>
	 * <li>判定コストは主キーインデックス1回分。</li>
	 * <li>引数は <em>viewId ではなく entityId</em> 固定（呼び出し元で変換済みであること）。</li>
	 * </ul>
	 *
	 * @param id
	 *            entityId（CH + 8桁）
	 * @return true: 既存 / false: 非存在
	 */
	@Transactional(readOnly = true)
	private boolean isExist(final String id) {
		return this.repository.existsById(id);
	}
	/* ===== [private] END ===== */
}
