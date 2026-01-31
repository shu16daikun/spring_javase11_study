package com.javastudy.components.answer_records.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.EntityManager;

/* 機能：解答履歴リポジトリ（インターフェースのみ／JPQLのみ） */
interface AnswerRecordsRepository
	extends
	JpaRepository<AnswerRecordsEntity, String>,
	JpaSpecificationExecutor<AnswerRecordsEntity> {

	/* ▼ 追加：外部キー存在判定（参照先が削除可能かの判断材料） */
	boolean existsByUserId(String userId);

	boolean existsBySankouBookId(String sankouBookId);

	boolean existsByChapterId(String chapterId);

	boolean existsByKurohonQuestionId(String kurohonQuestionId);

	boolean existsByAttemptSessionId(String attemptSessionId);

	@Override
	boolean existsById(String id);

	/* ▼ 追加：IN一括で“使用されているIDだけ”拾う（distinct） */
	@Query("SELECT DISTINCT ar.userId FROM AnswerRecordsEntity ar WHERE ar.userId IN :ids")
	Set<String> pickUsedUserIds(@Param("ids") Collection<String> ids);

	@Query("SELECT DISTINCT ar.sankouBookId FROM AnswerRecordsEntity ar WHERE ar.sankouBookId IN :ids")
	Set<String> pickUsedSankouBookIds(@Param("ids") Collection<String> ids);

	@Query("SELECT DISTINCT ar.chapterId FROM AnswerRecordsEntity ar WHERE ar.chapterId IN :ids")
	Set<String> pickUsedChapterIds(@Param("ids") Collection<String> ids);

	@Query("SELECT DISTINCT ar.kurohonQuestionId FROM AnswerRecordsEntity ar WHERE ar.kurohonQuestionId IN :ids")
	Set<String> pickUsedKurohonQuestionIds(@Param("ids") Collection<String> ids);

	@Query("SELECT DISTINCT ar.attemptSessionId FROM AnswerRecordsEntity ar WHERE ar.attemptSessionId IN :ids")
	Set<String> pickUsedAttemptSessionIds(@Param("ids") Collection<String> ids);

	@Query("""
		SELECT ar
		  FROM AnswerRecordsEntity ar
		  JOIN ChapterEntity ch ON ch.id = ar.chapterId
		  JOIN KurohonQuestionsEntity kq ON kq.id = ar.kurohonQuestionId
		 WHERE ar.attemptSessionId = :attemptSessionId
		 ORDER BY ch.no ASC, kq.questionNo ASC
		""")
	List<AnswerRecordsEntity> findAllByAttemptSessionIdOrderByChapterNoAscQuestionNoAsc(
		@Param("attemptSessionId") String attemptSessionId);

	/** 管理向け：全件を「セッションID → 章No → 問題No」で安定ソートして取得。 */
	@Query("""
		SELECT ar
		  FROM AnswerRecordsEntity ar
		  JOIN ChapterEntity ch ON ch.id = ar.chapterId
		  JOIN KurohonQuestionsEntity kq ON kq.id = ar.kurohonQuestionId
		 ORDER BY ar.attemptSessionId ASC, ch.no ASC, kq.questionNo ASC
		""")
	List<AnswerRecordsEntity> findAllOrderBySessionAscChapterNoAscQuestionNoAsc();

	/* ===== defaultユーティリティ：DB既定/トリガ値を即反映 ===== */

	default AnswerRecordsEntity saveAndReload(final AnswerRecordsEntity e, final EntityManager em) {
		final AnswerRecordsEntity saved = this.save(e);
		em.flush(); // ID/外部キー整合を先に確定
		em.refresh(saved); // answered_at / attempt_no を即時反映
		return saved;
	}

	default List<AnswerRecordsEntity> saveAllAndReload(
		final Iterable<AnswerRecordsEntity> entities,
		final EntityManager em) {
		final List<AnswerRecordsEntity> saved = this.saveAll(entities);
		em.flush();
		// refreshは List 化して順に
		final List<AnswerRecordsEntity> reloaded = new ArrayList<>(saved.size());
		for (final AnswerRecordsEntity e : saved) {
			em.refresh(e);
			reloaded.add(e);
		}
		return reloaded;
	}
}
