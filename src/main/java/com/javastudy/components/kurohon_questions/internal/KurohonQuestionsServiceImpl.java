// com/javastudy/components/kurohon_questions/internal/KurohonQuestionsServiceImpl.java
package com.javastudy.components.kurohon_questions.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.answer_records.api.service.AnswerRecordsRefLookUp;
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.chapter.api.service.ChapterService;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsInputDto;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;
import com.javastudy.components.kurohon_questions.api.exception.KurohonQuestionsException;
import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsService;
import com.javastudy.components.kurohon_questions.internal.KurohonQuestionsDB.KurohonQuestionsIdParam;
import com.javastudy.components.kurohon_questions.internal.KurohonQuestionsErrorCode.KurohonQuestionsDbgMsg;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.components.weakness.api.service.WeaknessRefLookUp;
import com.util.security.id.DbIdSequence;
import com.util.security.role.RoleUtil;
import com.util.type.MyType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class KurohonQuestionsServiceImpl implements KurohonQuestionsService {

	private static final String SORT_QUESTION_NO = "questionNo";

	/* 依存 */
	private final KurohonQuestionsRepository repository;
	private final ToUSER_KurohonQuestionsViewDtoMapper toUserViewDtoMapper;
	private final ToADMIN_KurohonQuestionsViewDtoMapper toAdminViewDtoMapper;

	// ★ RefLookup に差し替え（Service を直接持たない）
	private final AnswerRecordsRefLookUp answerRecordsRefLookup;
	private final WeaknessRefLookUp weaknessRefLookup;

	// DTO 構築は従来どおり Service を使用（循環しない経路）
	private final SankouBooksService booksService;
	private final ChapterService chapterService;

	private final DbIdSequence idSeq; // ★ アプリ採番

	@PersistenceContext
	private final EntityManager em;

	/* ===== 参照（既存） ===== */

	/** 参考書の viewId × 章番号(no) → 問題一覧（昇順） */
	@Transactional
	@Override
	public List<USER_KurohonQuestionsViewDto>
		getUserViewDtoListFilter(final String bookViewId, final String no) {
		if (MyType.isBlank(bookViewId) || MyType.isBlank(no)) {
			return List.of();
		}
		final String bookId = this.booksService.getEntityId(bookViewId);

		// 章番号 → 章View（User）→ 章EntityId
		final USER_ChapterViewDto chapView = this.chapterService.getUserViewDtoByNo(bookViewId, no);
		final String chapterId = this.chapterService.getEntityId(chapView.viewId());

		// 呼び出し回数を減らすため、共通の本・章DTOを一度だけ解決
		final USER_SankouBooksViewDto bookView = this.booksService.getUserViewDtoById(bookId);
		final USER_ChapterViewDto chapterView = chapView; // 既に取得済み

		return this.repository
			.findAllBySankouBookIdAndChapterId(
				bookId, chapterId, Sort.by(SORT_QUESTION_NO).ascending())
			.stream()
			.map(e -> this.toUserViewDtoMapper.fromEntity(e, bookView, chapterView))
			.toList();
	}

	/** ID検索（外部キー挿入用) */
	@Transactional
	@Override
	public String getEntityId(final String viewId) {
		return KurohonQuestionsIdBridge.toEntityId(viewId);
	}

	/** 物理IDで1件取得（ユーザー向け） */
	@Transactional
	@Override
	public USER_KurohonQuestionsViewDto getUserViewDtoById(final String id) {
		if (MyType.isBlank(id)) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.BLANK_ID,
				KurohonQuestionsDbgMsg.blankId("id"));
		}
		final KurohonQuestionsEntity e = this.getEntity(id);
		return this.toUserViewDtoMapper.fromEntity(
			e,
			this.booksService.getUserViewDtoById(e.getSankouBookId()),
			this.chapterService.getUserViewDtoById(e.getChapterId()));
	}

	/** 画面用IDで1件取得（ユーザー向け） */
	@Transactional
	@Override
	public USER_KurohonQuestionsViewDto getUserViewDtoByViewId(final String viewId) {
		final String id = KurohonQuestionsIdBridge.toEntityId(viewId);
		final KurohonQuestionsEntity e = this.getEntity(id);
		return this.toUserViewDtoMapper.fromEntity(
			e,
			this.booksService.getUserViewDtoById(e.getSankouBookId()),
			this.chapterService.getUserViewDtoById(e.getChapterId()));
	}

	/* ★ 追加：問題ID集合 → ADMIN_ViewDto を一括取得（IN最適化） */
	@Override
	@Transactional(readOnly = true)
	public Map<String, ADMIN_KurohonQuestionsViewDto>
		getAdminViewDtoMapByIds(final Set<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return Map.of();
		}
		// 1) 本体を IN 句で一括取得
		final List<KurohonQuestionsEntity> entities = this.repository.findAllByIdIn(ids);
		if (entities.isEmpty()) {
			return Map.of();
		}

		// 2) 参照（Books / Chapters）を一括解決
		final Set<String> bookIds = entities.stream()
			.map(KurohonQuestionsEntity::getSankouBookId)
			.collect(Collectors.toSet());
		final Set<String> chapterIds = entities.stream()
			.map(KurohonQuestionsEntity::getChapterId)
			.collect(Collectors.toSet());

		final Map<String, ADMIN_SankouBooksViewDto> booksMap = this.booksService
			.getAdminViewDtoMapByIds(bookIds);
		final Map<String, ADMIN_ChapterViewDto> chaptersMap = this.chapterService
			.getAdminViewDtoMapByIds(chapterIds);

		// 3) isUse をバルク判定（AnswerRecords / Weakness の合算）
		final Set<String> usedAr = this.answerRecordsRefLookup.findUsedKurohonQuestionIds(ids);
		final Set<String> usedWk = this.weaknessRefLookup.findUsedKurohonQuestionIds(ids);
		final Set<String> used = new HashSet<>(usedAr);
		used.addAll(usedWk);

		// 4) Map 化（キー＝question entityId）
		return entities.stream().collect(Collectors.toMap(
			KurohonQuestionsEntity::getId,
			e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				booksMap.get(e.getSankouBookId()),
				chaptersMap.get(e.getChapterId()),
				used.contains(e.getId()))));
	}

	/* ===== 参照（管理向け：追加） ===== */

	/** 管理画面：全件取得（book→chapter→questionNo） */
	@Transactional(readOnly = true)
	@Override
	public List<ADMIN_KurohonQuestionsViewDto> getAdminViewDtoList() {
		// ① 本体を安定ソートで一括取得
		final List<KurohonQuestionsEntity> rows = this.repository
			.findAllByOrderBySankouBookIdAscChapterIdAscQuestionNoAsc();
		if (rows.isEmpty()) {
			return List.of();
		}

		// ② 参照IDを一括抽出（重複排除）
		final Set<String> bookIds = rows.stream()
			.map(KurohonQuestionsEntity::getSankouBookId)
			.collect(Collectors.toSet());
		final Set<String> chapterIds = rows.stream()
			.map(KurohonQuestionsEntity::getChapterId)
			.collect(Collectors.toSet());
		final Set<String> questionIds = rows.stream()
			.map(KurohonQuestionsEntity::getId)
			.collect(Collectors.toSet());

		// ③ Books/Chapters を一括取得（IN最適化）
		final Map<String, ADMIN_SankouBooksViewDto> booksMap = this.booksService
			.getAdminViewDtoMapByIds(bookIds);
		final Map<String, ADMIN_ChapterViewDto> chaptersMap = this.chapterService
			.getAdminViewDtoMapByIds(chapterIds);

		// ④ 使用中判定をバルクで取得（AnswerRecords / Weakness 合算）
		final Set<String> used = new HashSet<>();
		used.addAll(this.answerRecordsRefLookup.findUsedKurohonQuestionIds(questionIds));
		used.addAll(this.weaknessRefLookup.findUsedKurohonQuestionIds(questionIds));

		// ⑤ DTO 変換（順序は rows に従う）
		return rows.stream()
			.map(e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				booksMap.get(e.getSankouBookId()),
				chaptersMap.get(e.getChapterId()),
				used.contains(e.getId())))
			.toList();
	}

	@Override
	@Transactional
	public ADMIN_KurohonQuestionsViewDto getAdminViewDtoById(final String id) {
		if (MyType.isBlank(id)) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.BLANK_ID,
				KurohonQuestionsDbgMsg.blankId("id"));
		}
		final KurohonQuestionsEntity e = this.getEntity(id);
		return this.toAdminViewDtoMapper.fromEntity(
			e,
			this.booksService.getAdminViewDtoById(e.getSankouBookId()),
			this.chapterService.getAdminViewDtoById(e.getChapterId()),
			this.isUse(e.getId()));
	}

	@Override
	@Transactional
	public ADMIN_KurohonQuestionsViewDto getAdminViewDtoByViewId(final String viewId) {
		final String id = KurohonQuestionsIdBridge.toEntityId(viewId);
		final KurohonQuestionsEntity e = this.getEntity(id);
		return this.toAdminViewDtoMapper.fromEntity(
			e,
			this.booksService.getAdminViewDtoById(e.getSankouBookId()),
			this.chapterService.getAdminViewDtoById(e.getChapterId()),
			this.isUse(e.getId()));
	}

	/** true=使用中（削除不可）。引数は entityId 固定。 */
	@Override
	@Transactional(readOnly = true)
	public boolean isUse(final String kurohonQuestionId) {
		if (MyType.isBlank(kurohonQuestionId))
			return false;
		return this.answerRecordsRefLookup.existsByKurohonQuestionId(kurohonQuestionId)
			|| this.weaknessRefLookup.existsByKurohonQuestionId(kurohonQuestionId);
	}

	/* ===== 変更（新增） ===== */

	/** 作成（同一章内 questionNo ユニーク） */
	@Override
	@Transactional
	public void create(final ADMIN_KurohonQuestionsInputDto input) {
		this.validateInputForCreateOrUpdate(input, true);
		final String chapterId = this.chapterService.getEntityId(input.chapterViewId());

		// 重複チェック：同一章内の questionNo ユニーク
		if (this.repository.existsByChapterIdAndQuestionNo(chapterId, input.questionNo())) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.DUPLICATE_QUESTION_NO,
				KurohonQuestionsDbgMsg.duplicateQuestionNo(chapterId, input.questionNo()));
		}

		this.repository.saveAndReload(this.toEntityCreate(input), this.em);
	}

	/** 更新（同一章内 questionNo ユニーク／自分以外不可） */
	@Override
	@Transactional
	public void update(final ADMIN_KurohonQuestionsInputDto input) {
		this.validateInputForCreateOrUpdate(input, false);
		if (MyType.isBlank(input.viewId())) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.BLANK_ID,
				KurohonQuestionsDbgMsg.blankId("viewId"));
		}

		final String id = KurohonQuestionsIdBridge.toEntityId(input.viewId());
		final String chapterId = this.chapterService.getEntityId(input.chapterViewId());

		if (this.repository.existsByChapterIdAndQuestionNoAndIdNot(chapterId, input.questionNo(),
			id)) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.DUPLICATE_QUESTION_NO,
				KurohonQuestionsDbgMsg.duplicateQuestionNo(chapterId, input.questionNo()));
		}

		this.repository.save(this.toEntityUpdate(input));
	}

	/** 削除（使用中なら 409: IN_USE を返す） */
	@Transactional
	@Override
	public void deleteByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.BLANK_ID,
				KurohonQuestionsDbgMsg.blankId("viewId"));
		}
		final String id = KurohonQuestionsIdBridge.toEntityId(viewId);
		if (!this.repository.existsById(id)) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.NOT_ENTITY,
				KurohonQuestionsDbgMsg.notEntity(id));
		}
		// ★ 事前参照チェックに統一（他Repoへ直アクセスしない）
		if (this.isUse(id)) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.IN_USE,
				KurohonQuestionsDbgMsg.inUse(id));
		}
		this.repository.deleteById(id);
	}

	/* ===== private ===== */

	private KurohonQuestionsEntity getEntity(final String id) {
		return this.repository
			.findById(id)
			.orElseThrow(() -> new KurohonQuestionsException(
				KurohonQuestionsErrorCode.NOT_ENTITY,
				KurohonQuestionsDbgMsg.notEntity(id)));
	}

	private KurohonQuestionsEntity toEntityCreate(
		final ADMIN_KurohonQuestionsInputDto in) {

		// ★ 衝突回避つき採番（seq → prefix + left-pad、DB既存と衝突したら再試行）
		final String newId = this.allocateNewId();

		final String bookId = this.booksService.getEntityId(in.sankouBookViewId());
		final String chapterId = this.chapterService.getEntityId(in.chapterViewId());

		// ★ 念のため二重ガード（検証・将来の変更への保険）
		if (this.isExist(newId)) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.DUPLICATE_ID,
				KurohonQuestionsDbgMsg.duplicateId(newId));
		}

		return KurohonQuestionsEntity.builder()
			.id(newId)
			.sankouBookId(bookId)
			.chapterId(chapterId)
			.questionNo(in.questionNo()) // "001".."999"
			.questionHtml(this.trim(in.questionHtml())) // null → DB DEFAULT
			.correctOption(this.trim(in.correctOption()))
			.explanationHtml(this.trim(in.explanationHtml())) // null → DB DEFAULT
			.answerCountMax(this.nullToOne(in.answerCountMax()))
			.optionCount(this.nullToZero(in.optionCount()))
			.build();
	}

	private KurohonQuestionsEntity toEntityUpdate(
		final ADMIN_KurohonQuestionsInputDto in) {
		final String id = this.getEntityId(in.viewId());
		final String bookId = this.booksService.getEntityId(in.sankouBookViewId());
		final String chapterId = this.chapterService.getEntityId(in.chapterViewId());
		return KurohonQuestionsEntity.builder()
			.id(id)
			.sankouBookId(bookId)
			.chapterId(chapterId)
			.questionNo(in.questionNo()) // "001".."999"
			.questionHtml(this.trim(in.questionHtml())) // null → DB DEFAULT
			.correctOption(this.trim(in.correctOption()))
			.explanationHtml(this.trim(in.explanationHtml())) // null → DB DEFAULT
			.answerCountMax(this.nullToOne(in.answerCountMax()))
			.optionCount(this.nullToZero(in.optionCount()))
			.build();
	}

	/**
	 * allocateNewId
	 * <p>
	 * 目的：PostgreSQLの <code>kq_id_seq</code> を用い、<code>PREFIX + 左ゼロ埋め(8桁)</code>
	 * の
	 * 新規IDを採番する。既存レコードと衝突した場合は次値で再試行し、上限回数超過時は
	 * <code>MySecurityException(DB_ACCESS_FAILURE)</code> を送出する。
	 * <p>
	 * 実装：{@link DbIdSequence#nextIdAvoidCollision(String, String, int, String, String)}
	 * を使用。テーブルは <code>public.kurohon_questions</code>、列は <code>id</code>。
	 *
	 * @return 採番済みの一意ID（例：KQ00000001）
	 */
	private String allocateNewId() {
		return this.idSeq.nextIdAvoidCollision(
			KurohonQuestionsIdParam.SEQUENCE,
			KurohonQuestionsIdParam.PREFIX,
			KurohonQuestionsIdParam.PAD,
			"public." + KurohonQuestionsDB.TABLE,
			KurohonQuestionsDB.KurohonQuestionsColumn.ID);
	}

	/**
	 * isExist
	 * <p>
	 * 目的：指定した <strong>entityId</strong>（例：KQ00000001）が既に存在するかを主キーで判定する。
	 * <ul>
	 * <li>null/空白は常に false を返す。</li>
	 * <li>判定コストは主キーインデックス1回分。</li>
	 * <li>引数は <em>viewId ではなく entityId</em> 固定（呼び出し元で変換済みであること）。</li>
	 * </ul>
	 *
	 * @param id
	 *            entityId（KQ + 8桁）
	 * @return true: 既存 / false: 非存在
	 */
	@Transactional(readOnly = true)
	private boolean isExist(final String id) {
		return this.repository.existsById(id);
	}

	private void validateInputForCreateOrUpdate(
		final ADMIN_KurohonQuestionsInputDto in,
		final boolean create) {
		if (MyType.isNull(in)) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.BLANK_ID,
				KurohonQuestionsDbgMsg.blankId("input"));
		}
		// questionNo: "001".."999"
		if (MyType.isBlank(in.questionNo()) || !in.questionNo().matches("^[0-9]{3}$")) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.BLANK_ID,
				KurohonQuestionsDbgMsg
					.blankId("questionNo invalid: questionNo=" + MyType.orEmpty(in.questionNo())));
		}
		if (MyType.isBlank(in.correctOption())) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.BLANK_ID,
				KurohonQuestionsDbgMsg.blankId("correctOption"));
		}
		if (MyType.isBlank(in.sankouBookViewId()) || MyType.isBlank(in.chapterViewId())) {
			throw new KurohonQuestionsException(
				KurohonQuestionsErrorCode.BLANK_ID,
				KurohonQuestionsDbgMsg.blankId("bookViewId/chapterViewId"));
		}
	}

	private String trim(final String s) {
		return s == null ? null : s.trim();
	}

	private int nullToOne(final Integer i) {
		return (i == null || i < 1) ? 1 : i;
	}

	private int nullToZero(final Integer i) {
		return (i == null || i < 0) ? 0 : i;
	}
}
