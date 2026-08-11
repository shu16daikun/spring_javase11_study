// com.javastudy.components.authority.internal.ADMIN_AuthorityControllerAdvice.java
package com.javastudy.components.authority.internal;

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

@ControllerAdvice(assignableTypes = {
	ADMIN_AuthorityController.class
})
@Order(20)
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@Component
public class ADMIN_AuthorityControllerAdvice { // public 非final（AOP）

	private final ADMIN_AuthorityFormValidator validator;

	public ADMIN_AuthorityControllerAdvice(final ADMIN_AuthorityFormValidator validator) {
		this.validator = validator;
	}

	/** ADMIN Authority 用の Validator を適用。 */
	@InitBinder(AuthorityFormParam.FORM)
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
