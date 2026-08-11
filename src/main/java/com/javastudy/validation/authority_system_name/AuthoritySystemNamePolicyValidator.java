/*
 * AuthorityNamePolicyValidator.java
 * Project : spring_javase11_study
 * Package : com.javastudy.validation.authority_name
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:59:33
 *
 * 目的:
 * - 権限名の入力をポリシーで検証（ROLE_ 前置、許可文字、最大長）
 *
 * 注意:
 * - 文字列定数は private static final String（規約）
 */

package com.javastudy.validation.authority_system_name;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/* ===== [import] END ===== */

/**
 * AuthorityNamePolicyValidator 目的: ValidAuthorityName の実装本体。
 *
 * <p>
 * 公開契約: - 空/Null は true を返す（@NotBlank と組み合わせて使う前提）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
//com.javastudy.validation.authority_system_name.AuthoritySystemNamePolicyValidator

@Component
public class AuthoritySystemNamePolicyValidator
	implements ConstraintValidator<ValidAuthoritySystemName, String> {

	private static final int MAX_LEN = 15;

	// ★ @Value は ${...} でプロパティ解決。: の後ろはデフォルト値（プロパティ未設定時）
	@Value("${regex.authority.systemname:^[A-Z0-9_]{1,15}$}")
	private String configuredRegex;

	private Pattern pattern;

	@Override
	public void initialize(final ValidAuthoritySystemName anno) {
		// ★ ここで確実にコンパイル（null/空はデフォルトで補填）
		final String rx = (this.configuredRegex == null || this.configuredRegex.trim().isEmpty())
			? "^[A-Z0-9_]{1,15}$"
			: this.configuredRegex.trim();
		this.pattern = Pattern.compile(rx);
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext ctx) {
		if (com.my.util.type.MyType.isBlank(value)) {
			return true; // @NotBlank と組み合わせ前提
		}
		final String v = value.trim();

		if (v.length() > MAX_LEN) {
			return false;
		}
		// ★ ここは initialize 済みの pattern を使う
		return this.pattern.matcher(v).matches();
	}
}
