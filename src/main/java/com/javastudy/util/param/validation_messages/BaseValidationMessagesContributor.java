// com.javastudy.util.param.BaseValidationMessagesContributor.java
package com.javastudy.util.param.validation_messages;

import com.javastudy.util.param.prop_key.PropKey;
import java.util.Set;
import org.springframework.stereotype.Component;

/** 全画面で欲しい最低限のキー束。AOP方針により public 非final。 */
@Component
public class BaseValidationMessagesContributor implements ValidationMessagesContributor {

	@Override
	public Set<String> messageCodes() {
		return Set.of(
			// Common error.*
			PropKey.ErrorProp.COMMON_NOT_BLANK,
			PropKey.ErrorProp.COMMON_MIN,
			PropKey.ErrorProp.COMMON_MAX,
			PropKey.ErrorProp.COMMON_SIZE,
			// Regex（JS側の便宜用）
			PropKey.RegexProp.USERNAME,
			PropKey.RegexProp.PASSWORD,
			PropKey.RegexProp.OPTION,
			PropKey.RegexProp.BOOK_COLOR_NAME,
			PropKey.RegexProp.QUESTION_NO);
	}
}
