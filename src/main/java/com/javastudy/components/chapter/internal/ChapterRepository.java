// com.javastudy.components.chapter.internal.ChapterRepository
package com.javastudy.components.chapter.internal;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.EntityManager;

interface ChapterRepository
	extends
	JpaRepository<ChapterEntity, String>,
	JpaSpecificationExecutor<ChapterEntity> {

	/* ===== [exists/find：noは "01".."99" の二桁文字列] ===== */
	Optional<ChapterEntity> findByNo(String no);

	boolean existsByNo(String no);

	/* ★同一参考書(sankouBookId)内での重複判定／取得／並び */
	boolean existsBySankouBookIdAndNo(String sankouBookId, String no);

	boolean existsBySankouBookIdAndNoAndIdNot(String sankouBookId, String no, String id);

	/* ▼ 追加：外部キー（SankouBooks）存在判定：true=使用中=削除不可 */
	boolean existsBySankouBookId(String sankouBookId);

	@Override
	boolean existsById(String id);

	Optional<ChapterEntity> findBySankouBookIdAndNo(String sankouBookId, String no);

	List<ChapterEntity> findAllBySankouBookIdOrderByNoAsc(String sankouBookId);

	/* admin向け：全件を安定ソート（book→no） */
	List<ChapterEntity> findAllByOrderBySankouBookIdAscNoAsc();

	/** 引数の本ID集合のうち、Chapterに実際に使われているIDだけを返す（distinct）。 */
	@Query("SELECT DISTINCT c.sankouBookId FROM ChapterEntity c WHERE c.sankouBookId IN :ids")
	Set<String> pickUsedSankouBookIds(@Param("ids") Collection<String> ids);

	/** 章ID集合の本体を一括取得（安定ソート不要）。 */
	@Query("SELECT c FROM ChapterEntity c WHERE c.id IN :ids")
	List<ChapterEntity> findAllByIdIn(@Param("ids") Collection<String> ids);

	/* 一括更新（no / name / sankouBookId） */
	@Modifying
	@Query("""
		UPDATE ChapterEntity c
		   SET c.no = :no,
		       c.name = :name,
		       c.sankouBookId = :sankouBookId
		 WHERE c.id = :id
		""")
	int updateById(
		@Param("id") String id,
		@Param("no") String no,
		@Param("name") String name,
		@Param("sankouBookId") String sankouBookId);

	/* DEFAULT/トリガ対応：save→flush→refresh */
	default ChapterEntity saveAndReload(final ChapterEntity e, final EntityManager em) {
		final ChapterEntity saved = this.save(e);
		em.flush();
		em.refresh(saved);
		return saved;
	}
}
