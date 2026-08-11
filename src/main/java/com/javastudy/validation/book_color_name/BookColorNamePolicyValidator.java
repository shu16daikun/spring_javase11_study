/*
 * BookColorNamePolicyValidator.java
 * Project : spring_javase11_study
 * Package : com.javastudy.validation.book_color_name
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:28:59
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.validation.book_color_name;

import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.my.util.type.MyType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/* ===== [import] START ===== */
// import は明示指定（ワイルドカード禁止）
/* ===== [import] END ===== */

/**
 * BookColorNamePolicyValidator 目的: TODO
 *
 * <p>
 * 公開契約: - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@Component
@AllArgsConstructor
public class BookColorNamePolicyValidator
	implements
	ConstraintValidator<ValidBookColorName, String> {
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
		final String regex = msg.getMessage(RegexProp.BOOK_COLOR_NAME);
		return value.matches(regex);
	}
}
