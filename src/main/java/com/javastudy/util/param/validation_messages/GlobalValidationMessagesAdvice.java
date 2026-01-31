// com.javastudy.config.logging.advice.GlobalValidationMessagesAdvice.java
package com.javastudy.util.param.validation_messages;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.javastudy.util.param.prop_key.PropKey;

import lombok.RequiredArgsConstructor;

@Component
@ControllerAdvice
@RequiredArgsConstructor
@Order(2)
public class GlobalValidationMessagesAdvice { // public 非final（AOP）

	/* ===== [private] START ===== */
	private final ValidationMessageUtil msg;
	private final List<ValidationMessagesContributor> contributors;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/** 共通＋各画面の宣言分を一括解決して Model へ搭載（既存があればマージ）。 */
	@ModelAttribute
	@SuppressWarnings("unchecked")
	public void addBaseMessages(final Model model) {
		final Map<String, String> base = (Map<String, String>) model
			.getAttribute(PropKey.VALIDATION_MESSAGES);
		final Map<String, String> vm = (base != null)
			? base
			: new LinkedHashMap<>(this.msg.setModelValidationMessages());

		final Set<String> codes = new LinkedHashSet<>();
		if (this.contributors != null) {
			for (final ValidationMessagesContributor c : this.contributors) {
				codes.addAll(c.messageCodes());
			}
		}
		for (final String code : codes) {
			vm.putIfAbsent(code, this.msg.getMessage(code));
		}
		model.addAttribute(PropKey.VALIDATION_MESSAGES, vm);
	}
	/* ===== [public/protected] END ===== */
}
