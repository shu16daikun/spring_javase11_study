package com.javastudy.components.weakness.internal;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.javastudy.components.weakness.api.agg.WeaknessChapterAgg;

import jakarta.persistence.EntityManager;

/**
 * 弱点スナップショットの永続化（画面非公開）。
 *
 * <ul>
 * <li>実装クラスは作らない（インターフェース一本化 / defaultメソッド可）
 * <li>SQLはJPQLのみ（native禁止）
 * <li>必要に応じて save→flush→refresh を default に集約
 * </ul>
 */
interface WeaknessRepository
	extends
	JpaRepository<WeaknessEntity, String>,
	JpaSpecificationExecutor<WeaknessEntity> {

	// ▼ 追加：外部キー存在判定（参照元：Weakness）
	boolean existsByUserId(String userId);

	boolean existsBySankouBookId(String sankouBookId);

	boolean existsByChapterId(String chapterId);

	boolean existsByKurohonQuestionId(String kurohonQuestionId);

	/* ▼ 追加：使用中IDを IN 句で一括取得（distinct） */
	@Query("select distinct w.userId from WeaknessEntity w where w.userId in :ids")
	Set<String> pickUsedUserIds(@Param("ids") Collection<String> ids);

	@Query("select distinct w.sankouBookId from WeaknessEntity w where w.sankouBookId in :ids")
	Set<String> pickUsedSankouBookIds(@Param("ids") Collection<String> ids);

	@Query("select distinct w.chapterId from WeaknessEntity w where w.chapterId in :ids")
	Set<String> pickUsedChapterIds(@Param("ids") Collection<String> ids);

	@Query("select distinct w.kurohonQuestionId from WeaknessEntity w where w.kurohonQuestionId in :ids")
	Set<String> pickUsedKurohonQuestionIds(@Param("ids") Collection<String> ids);

	/* 機能：ユーザー×参考書の弱点（並びはサービス層で制御） */
	@Query("""
		select w
		  from WeaknessEntity w
		 where w.userId = :userId
		   and w.sankouBookId = :bookId
		   and w.totalAttempts >= :minAttempts
		""")
	List<WeaknessEntity> findWeakQuestions(
		@Param("userId") String userId,
		@Param("bookId") String bookId,
		@Param("minAttempts") int minAttempts);

	/* 機能：章単位の合算投影（総試行・正答・誤答） */
	@Query("""
		select w.chapterId as chapterId,
		       sum(w.totalAttempts) as totalAttempts,
		       sum(w.correctCount)  as correctCount,
		       sum(w.wrongCount)    as wrongCount
		  from WeaknessEntity w
		 where w.userId = :userId
		   and w.sankouBookId = :bookId
		 group by w.chapterId
		""")
	List<WeaknessChapterAgg> aggregateByChapter(
		@Param("userId") String userId,
		@Param("bookId") String bookId);

	/* ===== defaultユーティリティ（将来のDEFAULT/トリガ列追加に備える） ===== */
	default WeaknessEntity saveAndReload(final WeaknessEntity e, final EntityManager em) {
		final WeaknessEntity saved = this.save(e);
		em.flush();
		em.refresh(saved);
		return saved;
	}
}
