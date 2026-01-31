package com.javastudy.components.sankou_books.internal;

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

/**
 * 参考書の永続化（画面非公開）。 - 実装クラスは作らない（インターフェース一本化／defaultメソッド可） - SQLはJPQLのみ（native禁止）
 * - DB DEFAULT/トリガ反映は
 * save→flush→refresh を default に集約
 */
interface SankouBooksRepository
	extends
	JpaRepository<SankouBooksEntity, String>,
	JpaSpecificationExecutor<SankouBooksEntity> {

	Optional<SankouBooksEntity> findByName(String name);

	boolean existsByName(String name);

	/* ★更新時の「自分以外で同名」検出 */
	boolean existsByNameAndIdNot(String name, String id);

	/* ▼ 追加：外部キー（Color）存在判定：true=使用中=削除不可 */
	boolean existsByColorId(String colorId);

	@Override
	boolean existsById(String id);

	/** 管理画面：名称昇順で全件取得 */
	List<SankouBooksEntity> findAllByOrderByNameAsc();

	/* ★名称・カラーの一括更新（戻り値：更新件数） */
	@Modifying
	@Query("UPDATE SankouBooksEntity b SET b.name = :name, b.colorId = :colorId WHERE b.id = :id")
	int updateNameAndColorById(
		@Param("id") String id,
		@Param("name") String name,
		@Param("colorId") String colorId);

	/* ★追加：ID 一括取得（IN 句） */
	@Query("SELECT b FROM SankouBooksEntity b WHERE b.id IN :ids")
	List<SankouBooksEntity> findAllByIdIn(@Param("ids") Collection<String> ids);

	/* ★追加：Color 使用中 ID を一括で拾う（distinct） */
	@Query("SELECT DISTINCT b.colorId FROM SankouBooksEntity b WHERE b.colorId IN :colorIds")
	Set<String> pickUsedColorIds(@Param("colorIds") Collection<String> colorIds);

	/* ===== defaultユーティリティ（将来のDEFAULT/トリガ追加にも対応） ===== */
	default SankouBooksEntity saveAndReload(final SankouBooksEntity e, final EntityManager em) {
		final SankouBooksEntity saved = this.save(e);
		em.flush();
		em.refresh(saved);
		return saved;
	}
}
