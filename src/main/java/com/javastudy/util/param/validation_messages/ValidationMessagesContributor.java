// com.javastudy.util.param.ValidationMessagesContributor.java
package com.javastudy.util.param.validation_messages;

import java.util.Set;

/**
 * 画面へ渡す Validation メッセージの「キー」を宣言する拡張ポイント。 - 戻り値は messages.properties の
 * key（error.* / regex.* 等） -
 * 実体の解決は GlobalValidationMessagesAdvice が行う
 */
public interface ValidationMessagesContributor {
	Set<String> messageCodes();
}
