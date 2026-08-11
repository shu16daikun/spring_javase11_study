package com.javastudy.components.answer_records.internal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.answer_records.api.dto.ADMIN_AnswerRecordsViewDto;
import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsInputDto;
import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsStatsViewDto;
import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsViewDto;
import com.javastudy.components.answer_records.api.exception.AnswerRecordsException;
import com.javastudy.components.answer_records.api.service.AnswerRecordsService;
import com.javastudy.components.answer_records.internal.AnswerRecordsDB.AnswerRecordsIdParam;
import com.javastudy.components.answer_records.internal.AnswerRecordsErrorCode.AnswerRecordsDbgMsg;
import com.javastudy.components.attempt_session.api.dto.ADMIN_AttemptSessionViewDto;
import com.javastudy.components.attempt_session.api.service.AttemptSessionService;
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.chapter.api.service.ChapterService;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsService;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.javastudy.components.users.api.service.UsersService;
import com.login.components.user.api.service.MyUsersService;
import com.my.util.security.id.DbIdSequence;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class AnswerRecordsServiceImpl implements AnswerRecordsService {

	private static final String UNKNOWN = "UNKNOWN";

	private final AnswerRecordsRepository repository;
	private final MyUsersService myService;
	private final UsersService usersService;
	private final KurohonQuestionsService questionsService;
	private final SankouBooksService booksService;
	private final ChapterService chapterService;
	private final AttemptSessionService sessionService;
	private final ToUSER_AnswerRecordsViewDtoMapper toUserViewDtoMapper;
	private final ToADMIN_AnswerRecordsViewDtoMapper toAdminViewDtoMapper;
	@PersistenceContext
	private final EntityManager em;
	private final DbIdSequence idSeq;

	/* ユースケース：Formの束をEntityにして一括保存（attempt_no はDB側） */
	@Transactional
	@Override
	public List<AnswerRecordsEntity> saveAllWithAttempt(
		final String sessionViewId,
		final List<USER_AnswerRecordsInputDto> dtoList) {

		if (MyType.isNull(dtoList) || dtoList.isEmpty()) {
			return List.of();
		}

		final List<AnswerRecordsEntity> entities = dtoList.stream()
			.map(dto -> this.toEntity(sessionViewId, dto)).toList();

		return this.repository.saveAllAndReload(entities, this.em);
	}

	// 置き換え
	@Transactional
	@Override
	public List<ADMIN_AnswerRecordsViewDto> getAdminViewDtoList() {
		// ① 本体を安定ソートで一括取得（既存クエリのまま）
		final List<AnswerRecordsEntity> rows = this.repository
			.findAllOrderBySessionAscChapterNoAscQuestionNoAsc();
		if (rows.isEmpty()) {
			return List.of();
		}

		// ② 参照IDを一括抽出（Setで重複排除）
		final Set<String> userIds = rows.stream().map(AnswerRecordsEntity::getUserId)
			.collect(Collectors.toSet());
		final Set<String> bookIds = rows.stream().map(AnswerRecordsEntity::getSankouBookId)
			.collect(Collectors.toSet());
		final Set<String> chapterIds = rows.stream().map(AnswerRecordsEntity::getChapterId)
			.collect(Collectors.toSet());
		final Set<String> questionIds = rows.stream().map(AnswerRecordsEntity::getKurohonQuestionId)
			.collect(Collectors.toSet());
		final Set<String> sessionIds = rows.stream().map(AnswerRecordsEntity::getAttemptSessionId)
			.collect(Collectors.toSet());

		// ③ 各サービスの「ID集合→ADMIN_ViewDto Map」で一括取得（IN最適化）
		final Map<String, ADMIN_UsersViewDto> usersMap = this.usersService
			.getViewDtoMapByIds(userIds);
		final Map<String, ADMIN_SankouBooksViewDto> booksMap = this.booksService
			.getAdminViewDtoMapByIds(bookIds);
		final Map<String, ADMIN_ChapterViewDto> chaptersMap = this.chapterService
			.getAdminViewDtoMapByIds(chapterIds);
		final Map<String, ADMIN_KurohonQuestionsViewDto> questionsMap = this.questionsService
			.getAdminViewDtoMapByIds(questionIds);
		final Map<String, ADMIN_AttemptSessionViewDto> sessionsMap = this.sessionService
			.getAdminViewDtoMapByIds(sessionIds);

		// ④ Map を参照してDTO化
		return rows.stream()
			.map(e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				usersMap.get(e.getUserId()),
				booksMap.get(e.getSankouBookId()),
				chaptersMap.get(e.getChapterId()),
				questionsMap.get(e.getKurohonQuestionId()),
				sessionsMap.get(e.getAttemptSessionId())))
			.toList();
	}

	@Transactional
	@Override
	public String getEntityId(final String viewId) {
		return AnswerRecordsIdBridge.toEntityId(viewId);
	}

	@Transactional
	@Override
	public List<USER_AnswerRecordsViewDto> getUserViewDtoListByAttemptSession(
		final String attemptSessionViewId) {
		final String attemptSessionId = this.sessionService.getEntityId(attemptSessionViewId);
		final List<AnswerRecordsEntity> list = this.repository
			.findAllByAttemptSessionIdOrderByChapterNoAscQuestionNoAsc(attemptSessionId);

		return list.stream()
			.map(e -> this.toUserViewDtoMapper.fromEntity(
				e,
				this.myService.getViewDtoById(e.getUserId()),
				this.questionsService.getUserViewDtoById(e.getKurohonQuestionId()),
				this.chapterService.getUserViewDtoById(e.getChapterId())))
			.toList();
	}

	@Override
	public ADMIN_AnswerRecordsViewDto getAdminViewDtoById(final String id) {
		final AnswerRecordsEntity e = this.getEntity(id);
		return this.toAdminViewDtoMapper.fromEntity(
			e,
			this.usersService.getAdminViewDtoById(e.getUserId()),
			this.booksService.getAdminViewDtoById(e.getSankouBookId()),
			this.chapterService.getAdminViewDtoById(e.getChapterId()),
			this.questionsService.getAdminViewDtoById(e.getKurohonQuestionId()),
			this.sessionService.getAdminViewDtoById(e.getAttemptSessionId()));
	}

	@Override
	public ADMIN_AnswerRecordsViewDto getAdminViewDtoByViewId(final String viewId) {
		final String id = AnswerRecordsIdBridge.toEntityId(viewId);
		final AnswerRecordsEntity e = this.getEntity(id);
		return this.toAdminViewDtoMapper.fromEntity(
			e,
			this.usersService.getAdminViewDtoById(e.getUserId()),
			this.booksService.getAdminViewDtoById(e.getSankouBookId()),
			this.chapterService.getAdminViewDtoById(e.getChapterId()),
			this.questionsService.getAdminViewDtoById(e.getKurohonQuestionId()),
			this.sessionService.getAdminViewDtoById(e.getAttemptSessionId()));
	}

	@Transactional
	@Override
	public USER_AnswerRecordsStatsViewDto
		getStatsByAttemptSession(final String attemptSessionViewId) {
		final String attemptSessionId = this.sessionService.getEntityId(attemptSessionViewId);
		final List<AnswerRecordsEntity> list = this.repository
			.findAllByAttemptSessionIdOrderByChapterNoAscQuestionNoAsc(attemptSessionId);

		final int total = list.size();
		final int correct = (int) list.stream().filter(AnswerRecordsEntity::isCorrect).count();
		final BigDecimal accuracy = (total == 0)
			? BigDecimal.ZERO
			: BigDecimal.valueOf(correct)
				.multiply(BigDecimal.valueOf(100))
				.divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
		return new USER_AnswerRecordsStatsViewDto(total, correct, accuracy);
	}

	/* private：InputDto→Entity（認可/関連解決/正誤判定/ID変換/事前採番ID適用） */
	private AnswerRecordsEntity
		toEntity(final String sessionViewId, final USER_AnswerRecordsInputDto dto) {

		// ★ 衝突回避つき採番に切り替え
		final String newId = this.allocateNewId();

		// 念のための二重防御（理論上不要だがテスト容易性のため残す）
		if (this.isExist(newId)) {
			throw new AnswerRecordsException(
				AnswerRecordsErrorCode.DUPLICATE_ID,
				AnswerRecordsDbgMsg.duplicateId(newId));
		}

		final String loginUserId = this.usersService
			.getEntityId(this.usersService.getLoginUser().systemId());
		final String kqId = this.questionsService.getEntityId(MyType.orEmpty(dto.questionViewId()));
		final String chId = this.chapterService.getEntityId(MyType.orEmpty(dto.chapterViewId()));
		final String sbId = this.booksService.getEntityId(MyType.orEmpty(dto.sankouBookViewId()));
		final String sessionId = this.sessionService.getEntityId(sessionViewId);

		final String selected = dto.selectedOptionOrEmpty();
		final boolean isCorrect = this.isCorrect(selected, dto.correctOption());

		return AnswerRecordsEntity.builder()
			.id(newId) // アプリ採番：AR + 8桁
			.userId(loginUserId)
			.kurohonQuestionId(kqId)
			.chapterId(chId)
			.sankouBookId(sbId)
			.attemptSessionId(sessionId)
			.selectedOption(selected)
			.isCorrect(isCorrect)
			.answeredAt(null) // DB DEFAULT
			.attemptNo(null) // DB DEFAULT
			.build();
	}

	private boolean isCorrect(final String selected, final String correctOption) {
		if (this.isUnknown(selected))
			return false;
		return MyType.isEqual(selected, MyType.orEmpty(correctOption));
	}

	private boolean isUnknown(final String v) {
		return MyType.isEqual(v, UNKNOWN);
	}

	/**
	 * allocateNewId
	 * <p>
	 * 目的：PostgreSQL の <code>ar_id_seq</code>
	 * を用い、<code>PREFIX + 左ゼロ埋め(8桁)</code> の
	 * 一意な新規IDを採番する。既存レコードと衝突した場合は次値で再試行し、上限回数超過時は
	 * <code>MySecurityException(DB_ACCESS_FAILURE)</code> を送出する。
	 * <p>
	 * 実装：{@link DbIdSequence#nextIdAvoidCollision(String, String, int, String, String)}
	 * を使用。
	 * テーブルは <code>public.answer_records</code>、列は <code>id</code>。
	 *
	 * @return 採番済みの一意ID（例：AR00000001）
	 */
	private String allocateNewId() {
		return this.idSeq.nextIdAvoidCollision(
			AnswerRecordsIdParam.SEQUENCE,
			AnswerRecordsIdParam.PREFIX,
			AnswerRecordsIdParam.PAD,
			"public." + AnswerRecordsDB.TABLE,
			AnswerRecordsDB.AnswerRecordsColumn.ID);
	}

	/**
	 * isExist
	 * <p>
	 * 目的：指定した <strong>entityId</strong>（例：AR00000001）が既に存在するかを主キーで判定する。
	 * <ul>
	 * <li>null/空白は常に false を返す。</li>
	 * <li>判定コストは主キーインデックス1回分。</li>
	 * <li>引数は <em>viewId ではなく entityId</em> 固定（呼び出し元で変換済みであること）。</li>
	 * </ul>
	 *
	 * @param id
	 *            entityId（AR + 8桁）
	 * @return true: 既存 / false: 非存在
	 */
	@Transactional(readOnly = true)
	private boolean isExist(final String id) {
		return this.repository.existsById(id);
	}

	@Transactional(readOnly = true)
	private AnswerRecordsEntity getEntity(final String id) {
		return this.repository
			.findById(id)
			.orElseThrow(
				() -> new AnswerRecordsException(
					AnswerRecordsErrorCode.NOT_ENTITY,
					AnswerRecordsDbgMsg.notEntity(id)));
	}
}
