// ユーザー名ポリシー
package com.javastudy.validation.username;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ユーザー名形式ポリシー注釈（Bean Validation）。
 *
 * <ul>
 * <li>対象：フィールド
 * <li>バリデータ：{@link UsernamePolicyValidator}
 * </ul>
 */
@Documented
@Constraint(validatedBy = UsernamePolicyValidator.class)
@Target({
	ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
/* ===== [public/protected] START ===== */
public @interface ValidUsername {

	/**
	 * メッセージキー（properties参照）
	 *
	 * @return メッセージキー
	 */
	String message() default ErrorProp.USER_USERNAME_PATTERN;

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
/* ===== [public/protected] END ===== */
