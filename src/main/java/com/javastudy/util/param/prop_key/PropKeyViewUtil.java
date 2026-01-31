// com.javastudy.util.param.prop_key.PropKeyViewUtil.java
package com.javastudy.util.param.prop_key;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.prop_key.PropKey.RegexProp;

/** JS側へ渡す PropKey 構造を組み立てる（window.PropKey に載せる）。 */
@Component
public class PropKeyViewUtil {

	public Map<String, Object> build() {
		final Map<String, Object> root = new LinkedHashMap<>();

		// 上位キー
		root.put("VALIDATION_SCHEMA_VERSION", PropKey.VALIDATION_SCHEMA_VERSION);
		root.put("VALIDATION_MESSAGES", PropKey.VALIDATION_MESSAGES);
		root.put("STATUS", PropKey.STATUS);
		root.put("ERROR_CODE", PropKey.ERROR_CODE);
		root.put("ERROR_MESSAGE", PropKey.ERROR_MESSAGE);
		root.put("TRACE_ID", PropKey.TRACE_ID);

		/* ===== Error ===== */
		final Map<String, String> err = new LinkedHashMap<>();
		// Common
		err.put("COMMON_NOT_BLANK", ErrorProp.COMMON_NOT_BLANK);
		err.put("COMMON_MIN", ErrorProp.COMMON_MIN);
		err.put("COMMON_MAX", ErrorProp.COMMON_MAX);
		err.put("COMMON_SIZE", ErrorProp.COMMON_SIZE);
		err.put("COMMON_UNEXPECTED", ErrorProp.COMMON_UNEXPECTED);
		err.put("COMMON_BAD_REQUEST", ErrorProp.COMMON_BAD_REQUEST);
		err.put("COMMON_FORBIDDEN", ErrorProp.COMMON_FORBIDDEN);
		err.put("COMMON_NOT_FOUND", ErrorProp.COMMON_NOT_FOUND);
		err.put("COMMON_NOT_FOUND_GENERIC", ErrorProp.COMMON_NOT_FOUND_GENERIC);

		// Login
		err.put("LOGIN_MISMATCH", ErrorProp.LOGIN_MISMATCH);
		err.put("LOGIN_LOGOUT_SUCCESS", ErrorProp.LOGIN_LOGOUT_SUCCESS);

		// Users / Account
		err.put("USER_USERNAME_PATTERN", ErrorProp.USER_USERNAME_PATTERN);
		err.put("USER_PASSWORD_PATTERN", ErrorProp.USER_PASSWORD_PATTERN);
		err.put("USER_PASSWORD_MISMATCH", ErrorProp.USER_PASSWORD_MISMATCH);

		// Authority
		err.put("AUTH_NAME_PATTERN", ErrorProp.AUTH_NAME_PATTERN);
		err.put("AUTH_SYSTEM_NAME_PATTERN", ErrorProp.AUTH_SYSTEM_NAME_PATTERN); // ★ 追加

		// SankouBookColor
		err.put("SBC_NAME_BLANK", ErrorProp.SBC_NAME_BLANK);
		err.put("SBC_NAME_DUPLICATE", ErrorProp.SBC_NAME_DUPLICATE);
		err.put("SBC_NAME_PATTERN", ErrorProp.SBC_NAME_PATTERN);
		err.put("SBC_IN_USE", ErrorProp.SBC_IN_USE); // ★ 追加

		// SankouBooks
		err.put("SB_NAME_BLANK", ErrorProp.SB_NAME_BLANK);
		err.put("SB_COLOR_BLANK", ErrorProp.SB_COLOR_BLANK);
		err.put("SB_NAME_DUPLICATE", ErrorProp.SB_NAME_DUPLICATE);
		err.put("SB_IN_USE", ErrorProp.SB_IN_USE); // ★ 追加

		// Kurohon Questions
		err.put("KQ_NOT_FOUND", ErrorProp.KQ_NOT_FOUND);
		err.put("KQ_DTO_MISMATCH", ErrorProp.KUROHON_DTO_MISMATCH);
		err.put("KQ_DUPLICATE_NO", ErrorProp.KQ_DUPLICATE_NO);
		err.put("KQ_CORRECT_INVALID", ErrorProp.KQ_CORRECT_INVALID);
		err.put("KQ_OPTIONCOUNT_INVALID", ErrorProp.KQ_OPTIONCOUNT_INVALID);
		err.put("KQ_IN_USE", ErrorProp.KQ_IN_USE); // ★ 追加

		// Answer / Selected Option
		err.put("ANS_OPT_PATTERN", ErrorProp.ANSWER_OPT_PATTERN);
		err.put("ANS_OPT_UNKNOWN", ErrorProp.ANSWER_OPT_UNKNOWN);
		err.put("ANS_OPT_MAX", ErrorProp.ANSWER_OPT_MAX);
		// ※ error.answer.notfound は PropKey になければ後日追加

		// Chapter
		err.put("CH_NOT_FOUND", ErrorProp.CH_NOT_FOUND);
		err.put("CH_DUPLICATE_NO", ErrorProp.CH_DUPLICATE_NO);
		err.put("CH_IN_USE", ErrorProp.CH_IN_USE); // ★ 追加

		// Attempt Session
		err.put("AS_NOT_FOUND", ErrorProp.AS_NOT_FOUND);
		err.put("AS_ALREADY_FINISHED", ErrorProp.AS_ALREADY_FINISHED);
		err.put("AS_IN_USE", ErrorProp.AS_IN_USE); // ★ 追加

		root.put("Error", err);

		/* ===== Regex ===== */
		final Map<String, String> regex = new LinkedHashMap<>();
		regex.put("USERNAME", RegexProp.USERNAME);
		regex.put("PASSWORD", RegexProp.PASSWORD);
		regex.put("OPTION", RegexProp.OPTION);
		regex.put("BOOK_COLOR_NAME", RegexProp.BOOK_COLOR_NAME);
		regex.put("QUESTION_NO", RegexProp.QUESTION_NO);
		regex.put("AUTH_NAME", RegexProp.AUTH_NAME); // ★ 追加
		regex.put("AUTH_SYSTEM_NAME", RegexProp.AUTH_SYSTEM_NAME); // ★ 追加

		root.put("Regex", regex);

		return root;
	}
}
