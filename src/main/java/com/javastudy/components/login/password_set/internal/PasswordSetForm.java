// PasswordSetForm.java
package com.javastudy.components.login.password_set.internal;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.validation.password.ValidPassword;
import com.login.components.user.api.dto.MyPasswordSetInputDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * パスワード再設定フォーム。
 *
 * <p>
 * BeanValidation：必須／長さ／ポリシー（@ValidPassword）の基本チェックを担当。
 */
@Getter
@Setter
@NoArgsConstructor
public class PasswordSetForm {

	/* ===== [public/protected] START ===== */
	/** 新パスワード（画面入力） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(min = PasswordSetFormParam.MIN, max = PasswordSetFormParam.MAX, message = ErrorProp.COMMON_MIN)
	@ValidPassword
	private String password;

	/** 確認用パスワード（画面入力） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(min = PasswordSetFormParam.MIN, max = PasswordSetFormParam.MAX, message = ErrorProp.COMMON_MIN)
	@ValidPassword
	private String passwordCheck;

	/**
	 * Service層入力DTOへ変換（Controller → Service 受け渡しの境界）。
	 *
	 * @return MyPasswordSetInputDto（password / passwordCheck）
	 */
	MyPasswordSetInputDto toInputDto() {
		return new MyPasswordSetInputDto(password, passwordCheck);
	}
	/* ===== [public/protected] END ===== */
}
