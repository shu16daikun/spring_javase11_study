/*
 * SankouBooksFormParam.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_books.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 13:47:34
 *
 * 目的:
 * - 画面契約の定数（フォーム名／フィールド名／制約値／Model属性キー）を一元管理
 *
 * 注意:
 * - internal 配下の package-private（外部公開しない）
 */

package com.javastudy.components.sankou_books.internal;

/* ===== [public/protected] START ===== */
final class SankouBooksFormParam {

	/* ===== [form/model keys] START ===== */
	static final String FORM = "sankouBooksForm";
	static final String ATTR_CONSTRAINTS = "constraints";
	static final String ATTR_REGEX = "regex";
	static final String ATTR_COLORS = "colors"; // 参照候補（List<ADMIN_SankouBookColorViewDto>）
	/* ===== [form/model keys] END ===== */

	/* ===== [field names] START ===== */
	static final String NAME = "name";
	static final String COLOR = "color";
	/* ===== [field names] END ===== */

	/* ===== [constraints] START ===== */
	static final int NAME_MIN = 1;
	static final int NAME_MAX = 50; // DB/要件に合わせて調整可

	/* ===== [constraints] END ===== */

	private SankouBooksFormParam() {
	}
}
/* ===== [public/protected] END ===== */
