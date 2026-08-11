/*
 * ADMIN_UsersControllerAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.users.internal
 * Author  : shu-kundeath
 * Created : 2025/11/07 18:17:46
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.users.internal;

import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.javastudy.util.param.prop_key.PropKey;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

/**
 * ADMIN_UsersControllerAdvice
 * 目的: TODO
 *
 * 公開契約:
 * - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * 備考:
 * - DTO は record を用いる
 */
@ControllerAdvice(assignableTypes = {
	ADMIN_UsersController.class
})
@Order(20)
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@Component
@AllArgsConstructor
public class ADMIN_UsersControllerAdvice {
	private final ADMIN_UsersFormValidator validator;

	/** ADMIN books 用の Validator を適用。 */
	@InitBinder(UsersFormParam.FORM)
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
