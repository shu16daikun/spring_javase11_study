package com.javastudy.components.sankou_book_color.internal;

/* ===== [import] START ===== */
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.EntityManager;
/* ===== [import] END ===== */

/**
 * 参考書カラーの永続化（画面非公開）。
 * - 実装クラスは作らない（インターフェース一本化 / defaultメソッド可）
 * - SQL は JPQL のみ（native 禁止）
 * - DEFAULT/トリガ反映は save→flush→refresh を default に集約
 */
interface SankouBookColorRepository
	extends
	JpaRepository<SankouBookColorEntity, String>,
	JpaSpecificationExecutor<SankouBookColorEntity> {

	/* 機能：名称一致検索（ユニーク） */
	Optional<SankouBookColorEntity> findByName(String name);

	/* 機能：名称の存在確認（ユニーク制約の事前チェック） */
	boolean existsByName(String name);

	/* ★追加：自分以外で同名が存在するか（更新用） */
	boolean existsByNameAndIdNot(String name, String id);

	@Override
	boolean existsById(String id);

	List<SankouBookColorEntity> findAllByOrderByNameAsc();

	@Modifying
	@Query("UPDATE SankouBookColorEntity c SET c.name = :name WHERE c.id = :id")
	int updateNameById(@Param("id") String id, @Param("name") String name);

	/* ★追加：ID 一括取得（IN 句） */
	@Query("select c from SankouBookColorEntity c where c.id in :ids")
	List<SankouBookColorEntity> findAllByIdIn(@Param("ids") Collection<String> ids);

	/* ===== defaultユーティリティ（DEFAULT/トリガ対応） ===== */
	default SankouBookColorEntity saveAndReload(
		final SankouBookColorEntity e,
		final EntityManager em) {
		final SankouBookColorEntity saved = this.save(e);
		em.flush();
		em.refresh(saved);
		return saved;
	}
}
