/*
 * ValidAuthorityName.java
 * Project : spring_javase11_study
 * Package : com.javastudy.validation.authority_name
 * Author  : shu-kundeath
 * Created : 2025/10/26 12:01:03
 *
 * 目的:
 * - 権限名（例: ROLE_ADMIN）の表記ポリシーをサーバ側で厳格化する
 *
 * 注意:
 * - 文字列定数は private static final String（規約）
 */

package com.javastudy.validation.authority_system_name;

/* ===== [import] START ===== */
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;

/* ===== [import] END ===== */

/**
 * ValidAuthorityName 目的: 権限名のポリシー（ROLE_ から始まり英大字/数字/アンダースコア、全体20文字以内）を検証する。
 *
 * <p>
 * 公開契約: - メッセージは {error.authority.name.policy} を既定キーとして解決
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@Documented
@Constraint(validatedBy = AuthoritySystemNamePolicyValidator.class)
@Target({
	FIELD, PARAMETER, RECORD_COMPONENT
})
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface ValidAuthoritySystemName {

	/* ===== [contract] START ===== */
	String message() default ErrorProp.AUTH_NAME_PATTERN;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	/* ===== [contract] END ===== */
}
