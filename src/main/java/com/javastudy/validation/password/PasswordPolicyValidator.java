package com.javastudy.validation.password;

import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.util.type.MyType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * パスワードポリシー検証。
 *
 * <ul>
 * <li>Regex は {@link ValidationMessageUtil} 経由で {@link RegexProp#PASSWORD} を参照
 * <li>判定のみ。可観測出力（画面・ログ）は行わない
 * </ul>
 */
@Component
@AllArgsConstructor
/* ===== [public/protected] START ===== */
public class PasswordPolicyValidator implements ConstraintValidator<ValidPassword, String> {
	/* ===== [private] START ===== */
	/** メッセージ／正規表現解決ユーティリティ（DI） */
	private final ValidationMessageUtil msg;

	/* ===== [private] END ===== */

	/**
	 * 値の妥当性検証（null/空白は不正、正規表現一致で妥当）
	 *
	 * @param value
	 *            入力値
	 * @param context
	 *            検証コンテキスト（未使用）
	 * @return true: 妥当 / false: 不正
	 */
	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (MyType.isBlank(value)) {
			return false;
		}
		final String regex = msg.getMessage(RegexProp.PASSWORD);
		return value.matches(regex);
	}
}
/* ===== [public/protected] END ===== */
