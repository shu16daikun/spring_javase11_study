// LoginForm.java
package com.javastudy.components.login.login.internal;

import com.javastudy.components.login.login.api.param.LoginFormParam;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.validation.password.ValidPassword;
import com.javastudy.validation.username.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ログインフォーム（UI入力の受け皿）。
 *
 * <p>
 * BeanValidation：必須／長さ／パターンの基本チェックを担当。
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginForm {

	/* ===== [public/protected] START ===== */
	/** ユーザー名（画面入力） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(min = LoginFormParam.USERNAME_MIN, max = LoginFormParam.USERNAME_MAX, message = ErrorProp.COMMON_SIZE)
	@ValidUsername
	String username;

	/** パスワード（画面入力） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(min = LoginFormParam.PASS_MIN, message = ErrorProp.COMMON_MIN)
	@ValidPassword
	String password;
	/* ===== [public/protected] END ===== */
}
