/*
 * ValidBookColorName.java
 * Project : spring_javase11_study
 * Package : com.javastudy.validation.book_color_name
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:29:23
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.validation.book_color_name;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* ===== [import] START ===== */
// import は明示指定（ワイルドカード禁止）
/* ===== [import] END ===== */

/**
 * ValidBookColorName 目的: TODO
 *
 * <p>
 * 公開契約: - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@Documented
@Constraint(validatedBy = BookColorNamePolicyValidator.class)
@Target({
	ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBookColorName {

	/**
	 * メッセージキー（properties参照）
	 *
	 * @return メッセージキー
	 */
	String message() default ErrorProp.SBC_NAME_PATTERN;

	/**
	 * Bean Validation 標準属性
	 *
	 * @return グループ
	 */
	Class<?>[] groups() default {};

	/**
	 * Bean Validation 標準属性
	 *
	 * @return ペイロード
	 */
	Class<? extends Payload>[] payload() default {};
}
