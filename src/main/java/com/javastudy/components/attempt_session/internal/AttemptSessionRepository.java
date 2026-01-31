package com.javastudy.components.attempt_session.internal;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * 解答セッションの永続化（画面非公開）。
 *
 * <ul>
 * <li>実装クラスは作らない（規約：インターフェース一本化 / defaultメソッドOK）
 * <li>SQLはJPQLのみ（native禁止）
 * <li>DB DEFAULT/トリガ値の取り込みは save→flush→refresh を default に集約
 * </ul>
 */
interface AttemptSessionRepository
	extends
	JpaRepository<AttemptSessionEntity, String>,
	JpaSpecificationExecutor<AttemptSessionEntity> {

	/* ▼ 追加：外部キー存在判定（参照“先”の削除可否で使用） */
	boolean existsByUserId(String userId);

	boolean existsBySankouBookId(String sankouBookId);

	@Override
	boolean existsById(String id);

	/* ▼ 追加：IN一括で“使用されているIDだけ”拾う（distinct） */
	@Query("SELECT DISTINCT a.userId FROM AttemptSessionEntity a WHERE a.userId IN :ids")
	Set<String> pickUsedUserIds(@Param("ids") Collection<String> ids);

	@Query("SELECT DISTINCT a.sankouBookId FROM AttemptSessionEntity a WHERE a.sankouBookId IN :ids")
	Set<String> pickUsedSankouBookIds(@Param("ids") Collection<String> ids);

	/* ▼ 追加：ID集合で本体を一括取得（安定ソート不要） */
	@Query("SELECT a FROM AttemptSessionEntity a WHERE a.id IN :ids")
	List<AttemptSessionEntity> findAllByIdIn(@Param("ids") Collection<String> ids);

	/**
	 * 進行中のセッションを「終了」に更新（二重終了は0件にする）。
	 *
	 * <p>
	 * Service 層で 0 件時は 404/409 に振り分ける。
	 *
	 * @param id
	 *            セッションID（物理主キー）
	 * @return 更新件数（1 を期待、0 は未該当 or 既終了）
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("""
			UPDATE AttemptSessionEntity a
			   SET a.finishedAt = CURRENT_TIMESTAMP
			 WHERE a.id = :id
			   AND a.finishedAt IS NULL
		""")
	int finishNowIfOngoing(@Param("id") String id);

	/**
	 * DB DEFAULT/トリガの即時反映ヘルパ。
	 *
	 * <p>
	 * INSERT直後に flush→refresh して started_at を読み戻す。
	 *
	 * @param e
	 *            新規エンティティ
	 * @param em
	 *            EntityManager
	 * @return DB反映済みエンティティ
	 */
	default AttemptSessionEntity saveAndReload(
		final AttemptSessionEntity e,
		final EntityManager em) {
		final AttemptSessionEntity saved = this.save(e);
		em.flush();
		em.refresh(saved);
		return saved;
	}

	/** 管理向け：全件取得（開始日時降順）。 */
	List<AttemptSessionEntity> findAllByOrderByStartedAtDesc();
}
