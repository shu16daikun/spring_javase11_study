/*
 * ChapterFormParam.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.chapter.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 14:06:57
 */

package com.javastudy.components.chapter.internal;

/* ===== [public/protected] START ===== */
final class ChapterFormParam {

	/* フォーム/Modelキー */
	static final String FORM = "chapterForm";
	static final String ATTR_CONSTRAINTS = "constraints";
	static final String ATTR_BOOKS = "books";

	/* フィールド名 */
	static final String NO = "no";
	static final String NAME = "name";
	static final String BOOKS_VIEW_DTO = "sankouBooksViewDto";

	/* 制約値 */
	static final int NO_MIN = 1;
	static final int NO_MAX = 99; // ★ 二桁運用に合わせて 99
	static final int TITLE_MIN = 1;
	static final int TITLE_MAX = 50;

	private ChapterFormParam() {
	}
}
/* ===== [public/protected] END ===== */
