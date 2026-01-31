/*
 * SankouBookColorFormParam.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_book_color.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 13:33:06
 *
 * 目的:
 * - 画面契約の定数（フォーム名／フィールド名／制約値／Model属性キー）を一元管理
 *
 * 注意:
 * - internal 配下の package-private（外部公開しない）
 */

package com.javastudy.components.sankou_book_color.internal;

import com.javastudy.util.param.prop_key.PropKey.RegexProp;

/* ===== [import] END ===== */

/* ===== [public/protected] START ===== */
final class SankouBookColorFormParam {

	/* ===== [form/model keys] START ===== */
	static final String FORM = "sankouBookColorForm";
	static final String ATTR_CONSTRAINTS = "constraints";
	static final String ATTR_REGEX = "regex";
	/* ===== [form/model keys] END ===== */

	/* ===== [field names] START ===== */
	static final String NAME = "name";
	/* ===== [field names] END ===== */

	/* ===== [constraints] START ===== */
	static final int NAME_MIN = 1;
	static final int NAME_MAX = 20; // BeanValidation と整合
	/* ===== [constraints] END ===== */

	/* ===== [regex property keys] START ===== */
	// 直接リテラルではなく、PropKey を参照
	static final String REGEX_KEY_NAME = RegexProp.BOOK_COLOR_NAME;

	/* ===== [regex property keys] END ===== */

	private SankouBookColorFormParam() {
	}
}
/* ===== [public/protected] END ===== */
