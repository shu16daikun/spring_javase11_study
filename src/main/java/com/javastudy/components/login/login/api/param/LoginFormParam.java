// com.javastudy.login_module.login.api.param.LoginFormParam
package com.javastudy.components.login.login.api.param;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** ログインフォームの“定数ひとまとめ”。画面との契約名と簡易バリデーション閾値を集約。 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoginFormParam {

	/* ===== [public/protected] START ===== */
	/** フォーム属性名（Model/Binding用） */
	public static final String FORM = "loginForm";

	/** フィールド名：ユーザー名 */
	public static final String USERNAME = "username";

	/** フィールド名：パスワード */
	public static final String PASSWORD = "password";

	/** 検証閾値：ユーザー名最小桁 */
	public static final int USERNAME_MIN = 8;

	/** 検証閾値：ユーザー名最大桁 */
	public static final int USERNAME_MAX = 20;

	/** 検証閾値：パスワード最小桁 */
	public static final int PASS_MIN = 8;
	/* ===== [public/protected] END ===== */
}
