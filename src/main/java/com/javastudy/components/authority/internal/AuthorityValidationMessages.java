/*
 * ADMIN_AuthorityValidationMessages.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.authority.internal
 * Author  : shu-kundeath
 * Created : 2025/10/27 12:00:00
 *
 * 目的:
 * - Authority 画面で使う Validation メッセージ“キー”の宣言（実体解決は GlobalValidationMessagesAdvice）
 *
 * 注意:
 * - import は明示指定（ワイルドカード禁止）
 * - クラスは AOP 方針により public 非final
 */

package com.javastudy.components.authority.internal;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import com.javastudy.util.param.validation_messages.ValidationMessagesContributor;

/* ===== [import] END ===== */

/** Authority 画面のメッセージキー宣言。 */
@Component
public class AuthorityValidationMessages
	implements
	ValidationMessagesContributor { // public 非final（AOP）

	@Override
	public Set<String> messageCodes() {
		return Set.of(
			// error.*
			ErrorProp.AUTH_NAME_PATTERN,
			ErrorProp.AUTH_SYSTEM_NAME_PATTERN,
			// regex.*
			RegexProp.AUTH_NAME,
			RegexProp.AUTH_SYSTEM_NAME);
	}
}
