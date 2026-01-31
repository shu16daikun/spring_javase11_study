// 選択肢ポリシー
package com.javastudy.validation.selected_option;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 選択肢形式ポリシー注釈（Bean Validation）。
 *
 * <ul>
 * <li>対象：フィールド
 * <li>バリデータ：{@link SelectedOptionPolicyValidator}
 * </ul>
 */
@Documented
@Constraint(validatedBy = SelectedOptionPolicyValidator.class)
@Target({
	ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
/* ===== [public/protected] START ===== */
public @interface ValidSelectedOption {

	/**
	 * メッセージキー（properties参照）
	 *
	 * @return メッセージキー
	 */
	String message() default ErrorProp.ANSWER_OPT_PATTERN;

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
