// com.javastudy.components.users.internal.UsersValidationMessages.java
package com.javastudy.components.users.internal;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.validation_messages.ValidationMessagesContributor;

/** Users 画面で使うキー追加。AOP方針により public 非final。 */
@Component
public class UsersValidationMessages implements ValidationMessagesContributor {

	@Override
	public Set<String> messageCodes() {
		// regex.username は BaseContributor ですでに積まれるので、ここではエラー系だけ宣言
		return Set.of(
			PropKey.ErrorProp.USER_USERNAME_PATTERN);
	}
}
