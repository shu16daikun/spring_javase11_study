/*
 * ADMIN_ChapterControllerAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.chapter.internal
 * Author  : shu-kundeath
 * Created : 2025/11/07 18:09:35
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.chapter.internal;

import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.javastudy.util.param.prop_key.PropKey;
import com.util.security.role.RoleUtil;
import com.util.type.MyType;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@ControllerAdvice(assignableTypes = {
	ADMIN_ChapterController.class
})
@Order(20)
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@Component
@AllArgsConstructor
public class ADMIN_ChapterControllerAdvice {
	private final ADMIN_ChapterFormValidator validator;

	/** ADMIN chapter 用の Validator を適用。 */
	@InitBinder(ChapterFormParam.FORM)
	public void initBinder(final WebDataBinder binder) {
		binder.addValidators(this.validator);
	}

	/** Sticky な ERROR_MESSAGE の復元。 */
	@ModelAttribute
	public void restoreStickyError(final Model model, final HttpSession session) {
		if (model.containsAttribute(PropKey.ERROR_MESSAGE))
			return;
		final Object sticky = session.getAttribute(PropKey.ERROR_MESSAGE);
		if (sticky instanceof final String s && MyType.isNotBlank(s)) {
			model.addAttribute(PropKey.ERROR_MESSAGE, s);
		}
	}
}
