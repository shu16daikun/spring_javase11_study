/*
 * SankouBooksRefLookUp.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_books.api.service
 * Author  : shu-kundeath
 * Created : 2025/11/01 18:36:50
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.sankou_books.api.service;

import java.util.Collection;
import java.util.Set;

/**
 * SankouBooksRefLookUp
 * 目的: TODO
 *
 * 公開契約:
 * - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * 備考:
 * - DTO は record を用いる
 */

public interface SankouBooksRefLookUp {
	boolean existsByColorId(String colorId);

	/* ★追加：Color 使用中 ID を一括取得（IN 最適化用） */
	Set<String> findUsedColorIds(Collection<String> colorIds);

}
