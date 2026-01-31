// com.javastudy.components.answer_records.internal.AnswerRecordsValidationMessages.java
package com.javastudy.components.answer_records.internal;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import com.javastudy.util.param.validation_messages.ValidationMessagesContributor;

/** AnswerRecords 画面で使うキー追加。AOP方針により public 非final。 */
@Component
public class AnswerRecordsValidationMessages implements ValidationMessagesContributor {

	@Override
	public Set<String> messageCodes() {
		return Set.of(
			// error.*
			ErrorProp.ANSWER_OPT_PATTERN,
			ErrorProp.ANSWER_OPT_UNKNOWN,
			PropKey.ErrorProp.ANSWER_OPT_MAX, // ※選択数上限エラーを使う場合は活かす
			// regex.*
			RegexProp.OPTION);
	}
}
