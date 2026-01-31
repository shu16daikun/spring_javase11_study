// PasswordSetFormParam.java
package com.javastudy.components.login.password_set.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** パスワード再設定フォームの“契約定数”集約（フォーム名／フィールド名／閾値）。 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PasswordSetFormParam {

	/* ===== [public/protected] START ===== */
	/* 機能：フォーム名 */
	static final String FORM = "passwordSetForm";
	/* 機能：フィールド名 */
	static final String PASSWORD = "password";
	static final String PASS_CHECK = "passwordCheck";

	/* 機能：制約値 */
	static final int MIN = 8;
	static final int MAX = 255;
	/* ===== [public/protected] END ===== */
}
