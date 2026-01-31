// com.javastudy.components.users.internal.UsersFormParam
package com.javastudy.components.users.internal;

/**
 * 【機能】Usersフォーム定数（画面契約の集約）
 *
 * <p>
 * 目的：フォーム名・フィールド名・制約値・Model属性キーを一元管理し、散在と齟齬を防止。
 *
 * <p>
 * 可視性：internal 配下／package-private（外部公開しない）。
 *
 * <h2>契約</h2>
 *
 * <ul>
 * <li><code>FORM</code>：モデル上のフォーム名。
 * <li><code>ATTR_*</code>：ControllerAdvice で積む Model 属性キー。
 * <li><code>VIEW_ID / USERNAME / AUTHORITY_VIEW_ID</code>：フィールド名。
 * <li><code>*_MIN/MAX</code>：画面側の入力制約（BeanValidation と整合）。
 * <li><code>REGEX_KEY_*</code>：プロパティ解決用の正規表現キー。
 * </ul>
 */
final class UsersFormParam {

	/* ===== [form/model keys] START ===== */
	static final String FORM = "usersForm";

	/** Model: 入力制約（例：{ usernameMin, usernameMax, viewIdMax }） */
	static final String ATTR_CONSTRAINTS = "constraints";

	/** Model: 正規表現（例：{ username }） */
	static final String ATTR_REGEX = "regex";

	/** Model: 権限候補（List&lt;MyAuthorityViewDto&gt;） */
	static final String ATTR_AUTHORITIES = "authorities";

	/* ===== [form/model keys] END ===== */

	/* ===== [field names] START ===== */
	static final String VIEW_ID = "viewId";
	static final String USERNAME = "username";
	static final String AUTHORITY_VIEW_ID = "authorityViewId";
	/* ===== [field names] END ===== */

	/* ===== [constraints] START ===== */
	// ViewId: "US" + 4桁想定
	static final int VIEW_ID_MAX = 6;

	// Username: 8..20（ValidUsername の Regex と一致）
	static final int USERNAME_MIN = 8;
	static final int USERNAME_MAX = 20;

	/* ===== [constraints] END ===== */

	/* ===== [ctor] START ===== */
	private UsersFormParam() {
		// インスタンス化禁止（用途限定）
	}
	/* ===== [ctor] END ===== */
}
