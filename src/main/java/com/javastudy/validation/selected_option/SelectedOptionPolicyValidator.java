package com.javastudy.validation.selected_option;

import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.util.type.MyType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 選択肢ポリシー検証。
 *
 * <ul>
 * <li>Regex は {@link ValidationMessageUtil} 経由で {@link RegexProp#OPTION} を参照
 * <li>“画面に見せるもの”なし（判定のみ）。表示はアノテーション側の messageKey で行う
 * </ul>
 */
@Component
@AllArgsConstructor
/* ===== [public/protected] START ===== */
public class SelectedOptionPolicyValidator
	implements
	ConstraintValidator<ValidSelectedOption, String> {
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
		final String regex = msg.getMessage(RegexProp.OPTION);
		return value.matches(regex);
	}
}
/* ===== [public/protected] END ===== */
