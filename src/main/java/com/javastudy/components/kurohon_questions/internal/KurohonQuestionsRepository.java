// com.javastudy.components.kurohon_questions.internal.KurohonQuestionsRepository
package com.javastudy.components.kurohon_questions.internal;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.EntityManager;

/**
 * 黒本：問題の永続化（画面非公開）。 - SQL は JPQL のみ（native 禁止） - DEFAULT/トリガ反映は
 * save→flush→refresh を default に集約
 */
interface KurohonQuestionsRepository
	extends
	JpaRepository<KurohonQuestionsEntity, String>,
	JpaSpecificationExecutor<KurohonQuestionsEntity> {

	/* ===== 参照 ===== */

	/* ▼ 追加：外部キー存在判定（true=使用中=削除不可） */
	boolean existsBySankouBookId(String sankouBookId);

	boolean existsByChapterId(String chapterId);

	@Override
	boolean existsById(String id);

	/* 参考書ID × 章ID で取得（呼び出し側で Sort 指定） */
	List<KurohonQuestionsEntity> findAllBySankouBookIdAndChapterId(
		String sankouBookId,
		String chapterId,
		Sort sort);

	/* 管理：全件の安定ソート（book→chapter→questionNo） */
	List<KurohonQuestionsEntity> findAllByOrderBySankouBookIdAscChapterIdAscQuestionNoAsc();

	/* 参考書ID × 章ID × 問題No で1件 */
	Optional<KurohonQuestionsEntity> findBySankouBookIdAndChapterIdAndQuestionNo(
		String sankouBookId,
		String chapterId,
		String questionNo);

	/* 同一章内のNoユニーク判定 */
	boolean existsByChapterIdAndQuestionNo(String chapterId, String questionNo);

	boolean existsByChapterIdAndQuestionNoAndIdNot(String chapterId, String questionNo, String id);

	/* ===== ★ 追加：IN最適化ユーティリティ ===== */

	/** 対象本ID集合のうち、実際に KurohonQuestions に使われているIDのみ返す。 */
	@Query("SELECT DISTINCT k.sankouBookId FROM KurohonQuestionsEntity k WHERE k.sankouBookId IN :ids")
	Set<String> pickUsedSankouBookIds(@Param("ids") Collection<String> ids);

	/** 対象章ID集合のうち、実際に KurohonQuestions に使われているIDのみ返す。 */
	@Query("SELECT DISTINCT k.chapterId FROM KurohonQuestionsEntity k WHERE k.chapterId IN :ids")
	Set<String> pickUsedChapterIds(@Param("ids") Collection<String> ids);

	/** 問題ID集合の本体を一括取得。 */
	@Query("SELECT k FROM KurohonQuestionsEntity k WHERE k.id IN :ids")
	List<KurohonQuestionsEntity> findAllByIdIn(@Param("ids") Collection<String> ids);
	/* ===== 更新（JPQL UPDATE） ===== */

	@Modifying
	@Query("""
		UPDATE KurohonQuestionsEntity k
		   SET k.sankouBookId   = :sankouBookId,
		       k.chapterId      = :chapterId,
		       k.questionNo     = :questionNo,
		       k.questionHtml   = :questionHtml,
		       k.correctOption  = :correctOption,
		       k.explanationHtml= :explanationHtml,
		       k.answerCountMax = :answerCountMax,
		       k.optionCount    = :optionCount
		 WHERE k.id = :id
		""")
	int updateById(
		@Param("id") String id,
		@Param("sankouBookId") String sankouBookId,
		@Param("chapterId") String chapterId,
		@Param("questionNo") String questionNo,
		@Param("questionHtml") String questionHtml,
		@Param("correctOption") String correctOption,
		@Param("explanationHtml") String explanationHtml,
		@Param("answerCountMax") int answerCountMax,
		@Param("optionCount") int optionCount);

	/* ===== ランダム取得（アプリ側シャッフル） ===== */
	default List<KurohonQuestionsEntity> findAllRandomBySankouBookIdAndChapterId(
		final String sankouBookId,
		final String chapterId) {
		final List<KurohonQuestionsEntity> src = this.findAllBySankouBookIdAndChapterId(
			sankouBookId,
			chapterId, Sort.unsorted());
		if (src == null || src.isEmpty())
			return List.of();
		final List<KurohonQuestionsEntity> copy = new ArrayList<>(src);
		Collections.shuffle(copy, new SecureRandom());
		return copy;
	}

	/* ===== DEFAULT/トリガ対応 ===== */
	default KurohonQuestionsEntity saveAndReload(
		final KurohonQuestionsEntity e,
		final EntityManager em) {
		final KurohonQuestionsEntity saved = this.save(e);
		em.flush();
		em.refresh(saved);
		return saved;
	}
}
