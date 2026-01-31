// com.javastudy.config.logging.advice.GlobalPropKeysAdvice.java
package com.javastudy.util.param.prop_key;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** 全画面へ window.PropKey として渡すための元データを Model に搭載。 AOP方針により public 非final。 */
@Component
@ControllerAdvice
@RequiredArgsConstructor
@Order(0) // ValidationMessages より先でも後でもOK。0→先に積む。
public class GlobalPropKeysAdvice { // public 非final（AOP）

	/* ===== [private] START ===== */
	private final PropKeyViewUtil builder;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	@ModelAttribute
	public void addPropKey(final Model model) {
		if (!model.containsAttribute(PropKey.PROP_KEY)) {
			final Map<String, Object> obj = builder.build();
			model.addAttribute(PropKey.PROP_KEY, obj);
		}
	}
	/* ===== [public/protected] END ===== */
}
