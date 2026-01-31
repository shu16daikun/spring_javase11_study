/*
 * ChapterRefLookUp.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.chapter.api.service
 * Author  : shu-kundeath
 * Created : 2025/11/01 18:31:50
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.chapter.api.service;

import java.util.Collection;
import java.util.Set;

/**
 * ChapterRefLookUp
 * 目的: TODO
 *
 * 公開契約:
 * - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * 備考:
 * - DTO は record を用いる
 */

public interface ChapterRefLookUp {
	/** SankouBooks の entity側ID が Chapter に存在するか（true=使用中=削除不可） */
	boolean existsBySankouBookId(String sankouBookId);

	/* ★ 追加：本ID集合のうち、実際に Chapter に使われているIDだけ返す */
	Set<String> findUsedSankouBookIds(Collection<String> sankouBookIds);

}
