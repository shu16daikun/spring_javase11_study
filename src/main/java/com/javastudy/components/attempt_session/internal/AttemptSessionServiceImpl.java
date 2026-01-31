package com.javastudy.components.attempt_session.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.attempt_session.api.dto.ADMIN_AttemptSessionViewDto;
import com.javastudy.components.attempt_session.api.dto.USER_AttemptSessionViewDto;
import com.javastudy.components.attempt_session.api.exception.AttemptSessionException;
import com.javastudy.components.attempt_session.api.service.AttemptSessionService;
import com.javastudy.components.attempt_session.internal.AttemptSessionErrorCode.AttemptSessionDbgMsg;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.javastudy.components.users.api.service.UsersService;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.login.components.user.api.service.MyUsersService;
import com.util.security.id.DbIdSequence;
import com.util.type.MyType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;

/**
 * 解答セッションのユースケース（作成／取得／終了）。
 *
 * - IDは命名規則どおりアプリ採番（DbIdSequence）
 * - started_at は DB DEFAULT（save→refreshで即時反映）
 * - 終了は JPQL 一発更新（二重終了は 409）
 */
@Service
@AllArgsConstructor
public class AttemptSessionServiceImpl implements AttemptSessionService {

	/* 依存（DI） */
	private final AttemptSessionRepository repository;
	private final SankouBooksService sankouBooksService;
	private final UsersService usersService; // ADMIN 向け Users
	private final MyUsersService myUsersService; // USER 向け Users（MyLogin）
	private final ToUSER_AttemptSessionViewDtoMapper toUserViewDtoMapper;
	private final ToADMIN_AttemptSessionViewDtoMapper toAdminViewDtoMapper;
	@PersistenceContext
	private final EntityManager em;
	private final DbIdSequence idSeq;

	/** ViewId → USER_ViewDto */
	@Transactional
	@Override
	public USER_AttemptSessionViewDto getUserViewDtoByViewId(final String viewId) {
		final String id = AttemptSessionIdBridge.toEntityId(viewId);
		return this.getUserViewDtoById(id);
	}

	/** 物理ID → USER_ViewDto */
	@Transactional
	@Override
	public USER_AttemptSessionViewDto getUserViewDtoById(final String id) {
		final AttemptSessionEntity e = this.getEntity(id);
		final MyUsersViewDto u = this.myUsersService.getViewDtoById(e.getUserId());
		final USER_SankouBooksViewDto b = this.sankouBooksService
			.getUserViewDtoById(e.getSankouBookId());
		return this.toUserViewDtoMapper.fromEntity(e, u, b);
	}

	/** ViewId → ADMIN_ViewDto */
	@Transactional
	@Override
	public ADMIN_AttemptSessionViewDto getAdminViewDtoByViewId(final String viewId) {
		final String id = AttemptSessionIdBridge.toEntityId(viewId);
		return this.getAdminViewDtoById(id);
	}

	/** 管理向け：全件取得（開始日時降順）。 */
	@Transactional(readOnly = true)
	@Override
	public List<ADMIN_AttemptSessionViewDto> getAdminViewDtoList() {
		// ① 本体を安定ソートで一括取得
		final List<AttemptSessionEntity> rows = this.repository.findAllByOrderByStartedAtDesc();
		if (rows.isEmpty()) {
			return List.of();
		}

		// ② 参照IDを一括抽出（重複排除）
		final Set<String> userIds = rows.stream()
			.map(AttemptSessionEntity::getUserId)
			.collect(Collectors.toSet());
		final Set<String> bookIds = rows.stream()
			.map(AttemptSessionEntity::getSankouBookId)
			.collect(Collectors.toSet());

		// ③ 参照Viewを一括取得（IN最適化）
		final Map<String, ADMIN_UsersViewDto> usersMap = this.usersService
			.getViewDtoMapByIds(userIds);
		final Map<String, ADMIN_SankouBooksViewDto> booksMap = this.sankouBooksService
			.getAdminViewDtoMapByIds(bookIds);

		// ④ Map を使って DTO へ組み立て（N+1回避）
		return rows.stream()
			.map(e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				usersMap.get(e.getUserId()),
				booksMap.get(e.getSankouBookId())))
			.toList();
	}

	/** 物理ID → ADMIN_ViewDto */
	@Transactional
	@Override
	public ADMIN_AttemptSessionViewDto getAdminViewDtoById(final String id) {
		final AttemptSessionEntity e = this.getEntity(id);
		final ADMIN_UsersViewDto u = this.usersService.getAdminViewDtoById(e.getUserId());
		return this.toAdminViewDtoMapper.fromEntity(
			e,
			u,
			this.sankouBooksService.getAdminViewDtoById(e.getSankouBookId()));
	}

	/** 画面用ID → 物理ID 変換（外部キー挿入用途） */
	@Transactional
	@Override
	public String getEntityId(final String viewId) {
		return AttemptSessionIdBridge.toEntityId(viewId);
	}

	/**
	 * 新規作成：started_at は DB DEFAULT。save→flush→refresh を Repo 側で集約。
	 *
	 * @param users
	 *            ログインユーザーの ViewDto（MyLogin）
	 * @param books
	 *            参考書の ViewDto
	 * @return 作成後の ViewDto
	 */
	@Transactional
	@Override
	public USER_AttemptSessionViewDto createNew(
		final MyUsersViewDto users,
		final USER_SankouBooksViewDto books) {
		if (MyType.isNull(users) || MyType.isNull(books)) {
			final String reason = String.format("users=%s, books=%s",
				(users == null ? "<null>" : "ok"), (books == null ? "<null>" : "ok"));
			throw new AttemptSessionException(
				AttemptSessionErrorCode.BLANK_ID,
				AttemptSessionDbgMsg.blankId(reason));
		}
		final AttemptSessionEntity entity = this.toEntity(users, books);
		final AttemptSessionEntity saved = this.repository.saveAndReload(entity, this.em);
		return this.toUserViewDtoMapper.fromEntity(saved, users, books);
	}

	/**
	 * 終了マーク：進行中のみ終了。0件時は 404/409 を振り分け。
	 *
	 * @param viewId
	 *            画面用ID（AttemptSession の ViewId）
	 * @throws AttemptSessionException
	 *             BLANK_ID: viewId が空／null
	 *             NOT_ENTITY: 対象セッションが存在しない
	 *             ALREADY_FINISHED: 既に終了済み
	 */
	@Transactional
	@Override
	public void atFinished(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new AttemptSessionException(
				AttemptSessionErrorCode.BLANK_ID,
				AttemptSessionDbgMsg.blankId());
		}
		final String id = AttemptSessionIdBridge.toEntityId(viewId);
		final int updated = this.repository.finishNowIfOngoing(id);
		if (updated == 0) {
			if (!this.repository.existsById(id)) {
				throw new AttemptSessionException(
					AttemptSessionErrorCode.NOT_ENTITY,
					AttemptSessionDbgMsg.notEntity(id));
			}
			throw new AttemptSessionException(
				AttemptSessionErrorCode.ALREADY_FINISHED,
				AttemptSessionDbgMsg.alreadyFinished(id));
		}
	}

	/* ===== 追加：ID集合→ADMIN_ViewDto の一括取得 ===== */
	@Override
	@Transactional(readOnly = true)
	public Map<String, ADMIN_AttemptSessionViewDto> getAdminViewDtoMapByIds(final Set<String> ids) {
		if (ids == null || ids.isEmpty()) {
			return Map.of();
		}

		// 1) セッション本体を IN 句で一括取得
		final List<AttemptSessionEntity> entities = this.repository.findAllByIdIn(ids);
		if (entities.isEmpty()) {
			return Map.of();
		}

		// 2) 参照IDを抽出（重複排除）
		final Set<String> userIds = entities.stream()
			.map(AttemptSessionEntity::getUserId)
			.collect(Collectors.toSet());
		final Set<String> bookIds = entities.stream()
			.map(AttemptSessionEntity::getSankouBookId)
			.collect(Collectors.toSet());

		// 3) 参照Viewを各Serviceの一括APIで取得
		final Map<String, ADMIN_UsersViewDto> usersMap = this.usersService
			.getViewDtoMapByIds(userIds);
		final Map<String, ADMIN_SankouBooksViewDto> booksMap = this.sankouBooksService
			.getAdminViewDtoMapByIds(bookIds);

		// 4) 変換して Map<sessionId, ADMIN_AttemptSessionViewDto>
		return entities.stream().collect(Collectors.toMap(
			AttemptSessionEntity::getId,
			e -> this.toAdminViewDtoMapper.fromEntity(
				e,
				usersMap.get(e.getUserId()),
				booksMap.get(e.getSankouBookId()))));
	}

	/* ===== [private] START ===== */

	/** 入力 → Entity（ID事前採番／View→EntityId解決／DB DEFAULTは任せる） */
	private AttemptSessionEntity toEntity(
		final MyUsersViewDto users,
		final USER_SankouBooksViewDto books) {

		// ★ 衝突回避つき採番を使用（seq→prefix+left-pad、既存と衝突したら再試行）
		final String newId = this.allocateNewId();

		// 念のための二重防御（理論上不要だがテスト容易性のため残す）
		if (this.isExist(newId)) {
			throw new AttemptSessionException(
				AttemptSessionErrorCode.DUPLICATE_ID,
				AttemptSessionDbgMsg.duplicateId(newId));
		}

		final String userId = this.usersService.getEntityId(users.viewId());
		final String bookId = this.sankouBooksService.getEntityId(books.viewId());

		return AttemptSessionEntity.builder()
			.id(newId) // アプリ採番：AS + 10桁
			.userId(userId)
			.sankouBookId(bookId)
			// started_at は DB DEFAULT / finished_at は null で開始
			.build();
	}

	/** Repoヘルパ：Id → Entity 取得（404に変換） */
	@Transactional
	private AttemptSessionEntity getEntity(final String id) {
		return this.repository
			.findById(id)
			.orElseThrow(
				() -> new AttemptSessionException(
					AttemptSessionErrorCode.NOT_ENTITY,
					AttemptSessionDbgMsg.notEntity(id)));
	}

	/**
	 * allocateNewId
	 * <p>
	 * 目的：PostgreSQL の <code>as_id_seq</code>
	 * を用い、<code>PREFIX + 左ゼロ埋め(10桁)</code> の
	 * 一意な新規IDを採番する。既存レコードと衝突した場合は次値で再試行し、上限回数超過時は
	 * <code>MySecurityException(DB_ACCESS_FAILURE)</code> を送出する。
	 * <p>
	 * 実装：{@link DbIdSequence#nextIdAvoidCollision(String, String, int, String, String)}
	 * を使用。
	 * テーブルは <code>public.attempt_session</code>、列は <code>id</code>。
	 *
	 * @return 採番済みの一意ID（例：AS0000000001）
	 */
	private String allocateNewId() {
		return this.idSeq.nextIdAvoidCollision(
			AttemptSessionDB.AttemptSessionIdParam.SEQUENCE,
			AttemptSessionDB.AttemptSessionIdParam.PREFIX,
			AttemptSessionDB.AttemptSessionIdParam.PAD,
			"public." + AttemptSessionDB.TABLE,
			AttemptSessionDB.AttemptSessionColumn.ID);
	}

	/**
	 * isExist
	 * <p>
	 * 目的：指定した <strong>entityId</strong>（例：AS0000000001）が既に存在するかを主キーで判定する。
	 * <ul>
	 * <li>null/空白は常に false を返す。</li>
	 * <li>判定コストは主キーインデックス1回分。</li>
	 * <li>引数は <em>viewId ではなく entityId</em> 固定（呼び出し元で変換済みであること）。</li>
	 * </ul>
	 *
	 * @param id
	 *            entityId（AS + 10桁）
	 * @return true: 既存 / false: 非存在
	 */
	@Transactional(readOnly = true)
	private boolean isExist(final String id) {
		return this.repository.existsById(id);
	}

	/* ===== [private] END ===== */
}
