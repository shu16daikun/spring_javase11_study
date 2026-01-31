// com.javastudy.util.param.prop_key.PropKey
package com.javastudy.util.param.prop_key;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** 画面／バリデーションで用いるメッセージキー・Label 等の定数集約。 “画面に見せるもの（キー）”のみ公開し、内部構造は出さない。 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropKey {

	/** JS 側スキーマ同期用（frond / server でズレ検知に使用） */
	public static final String VALIDATION_SCHEMA_VERSION = "2025-11-06_01";

	/** View に渡す代表キー群（Model 属性名など） */
	public static final String VALIDATION_MESSAGES = "validationMessages";

	public static final String STATUS = "status";
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String TRACE_ID = "traceId";

	/** JS へ渡す PropKey の Model 属性名 */
	public static final String PROP_KEY = "propKey";

	/** エラーメッセージ用キーの定義（properties の message key を SSOT 化）。 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class ErrorProp {

		// ===== Common =====
		public static final String COMMON_NOT_BLANK = "error.common.notblank";
		public static final String COMMON_MIN = "error.common.min";
		public static final String COMMON_MAX = "error.common.max";
		public static final String COMMON_SIZE = "error.common.size";
		public static final String COMMON_NOT_FOUND = "error.common.notfound";
		public static final String COMMON_UNEXPECTED = "error.common.unexpected";
		public static final String COMMON_BAD_REQUEST = "error.common.badrequest";
		public static final String COMMON_FORBIDDEN = "error.common.forbidden";
		public static final String COMMON_NOT_FOUND_GENERIC = "error.common.notfound.generic";
		public static final String COMMON_DUPLICATE_ID = "error.common.duplicateid";

		// ===== Login =====
		public static final String LOGIN_MISMATCH = "error.login.mismatch";
		public static final String LOGIN_LOGOUT_SUCCESS = "error.login.logout.success";

		// ===== Users / Account =====
		public static final String USER_USERNAME_PATTERN = "error.user.username.pattern";
		public static final String USER_PASSWORD_PATTERN = "error.user.password.pattern";
		public static final String USER_PASSWORD_MISMATCH = "error.user.password.mismatch";

		// ===== Authority =====
		public static final String AUTH_NAME_PATTERN = "error.authority.name.pattern";
		public static final String AUTH_SYSTEM_NAME_PATTERN = "error.authority.systemname.pattern";

		// ===== SankouBookColor =====
		public static final String SBC_NAME_BLANK = "error.sankoubookcolor.name.blank";
		public static final String SBC_NAME_DUPLICATE = "error.sankoubookcolor.name.duplicate";
		public static final String SBC_NAME_PATTERN = "error.sankoubookcolor.name.pattern";
		public static final String SBC_IN_USE = "error.sankoubookcolor.inuse";

		// ===== SankouBooks =====
		public static final String SB_NAME_BLANK = "error.sankoubooks.name.blank";
		public static final String SB_COLOR_BLANK = "error.sankoubooks.color.blank";
		public static final String SB_NAME_DUPLICATE = "error.sankoubooks.name.duplicate";
		public static final String SB_IN_USE = "error.sankoubooks.inuse";

		// ===== 黒本問題 / Kurohon Questions =====
		public static final String KQ_NOT_FOUND = "error.kurohon.questions.notfound";
		public static final String KQ_DUPLICATE_NO = "error.kurohon.questions.duplicateno";
		public static final String KQ_CORRECT_INVALID = "error.kurohon.questions.correct.invalid";
		public static final String KQ_OPTIONCOUNT_INVALID = "error.kurohon.questions.optioncount.invalid";
		public static final String KUROHON_DTO_MISMATCH = "error.kurohon.questions.dto.mismatch";
		public static final String KQ_IN_USE = "error.kurohon.questions.inuse";

		// ===== 回答 / Selected Option =====
		public static final String ANSWER_OPT_PATTERN = "error.answer.selectedoption.pattern";
		public static final String ANSWER_OPT_UNKNOWN = "error.answer.selectedoption.unknown";
		public static final String ANSWER_OPT_MAX = "error.answer.selectedoption.max";

		// ===== Chapter =====
		public static final String CH_NOT_FOUND = "error.chapter.notfound";
		public static final String CH_DUPLICATE_NO = "error.chapter.duplicateno";
		public static final String CH_IN_USE = "error.chapter.inuse";

		// ===== Attempt Session =====
		public static final String AS_NOT_FOUND = "error.attemptsession.notfound";
		public static final String AS_ALREADY_FINISHED = "error.attemptsession.alreadyfinished";
		public static final String AS_IN_USE = "error.attemptsession.inuse";

		// ===== Weakness =====
		public static final String WEAKNESS_NOT_FOUND = "error.weakness.notfound";
	}

	/** 正規表現キーの定義（properties の regex.* を参照）。 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class RegexProp {
		public static final String BASE = "regex.";
		public static final String AUTH_NAME = BASE + "authority.name";
		public static final String AUTH_SYSTEM_NAME = BASE + "authority.systemname";
		public static final String USERNAME = BASE + "username";
		public static final String PASSWORD = BASE + "password";
		public static final String OPTION = BASE + "selectedoption";
		public static final String BOOK_COLOR_NAME = BASE + "bookcolor.name";
		public static final String QUESTION_NO = BASE + "questions.no";
	}

	/** ラベル系キーの定義（将来拡張用）。 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class LabelsProp {
		// ラベル系キーを増やす場合はここに集約
	}
}
