package com.javastudy.util;

import java.util.Objects;

import org.springframework.ui.Model;

public class Type {
	/* ===== [constants] START ===== */
	private Type() {
	}
	/* ===== [constants] END ===== */

	/* ===== [field] START ===== */
	/* ===== [field] END ===== */

	/* ===== [public/protected] START ===== */
	public static final boolean existsModelValue(Model model, String key, String value) {
		if (!model.containsAttribute(key)) {
			return false;
		}
		Object v = model.asMap().get(key);
		return Objects.equals(value, v);
	}
	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/* ===== [private] END ===== */
}
