// /js/main-contents/login/password_set/service.js
/* 機能：パスワード設定フォーム検証（PropKey依存なし） */
import { isBlank, isNotEquals } from "/psfm/js/common/utils.js";
import { CONST } from "./const.js";

/* ---- formatMsg が無い場合のフォールバック ---- */
const _formatFallback = (messages = {}, key, label = "", ...args) => {
	let t = messages?.[key] || "";
	if (!t) return label ? `${label}の入力に誤りがあります。` : "入力に誤りがあります。";
	args.forEach((a, i) => { t = t.replace(`{${i + 1}}`, String(a)); });
	return t.replace("{0}", label);
};
const format = globalThis?.formatMsg ? globalThis.formatMsg : _formatFallback;

export class PasswordSetService {
	constructor(messages = CONST.MESSAGES()) {
		this.messages = messages;
		this.MIN = CONST.LIMIT.PASSWORD_MIN;
		this.ERROR = CONST.KEY.ERROR;
		this.REGEX = CONST.KEY.REGEX;
		this.LABEL = CONST.LABEL;
	}

	validate(values) {
		const errors = {};
		const password = values?.password ?? "";
		const passwordCheck = values?.passwordCheck ?? "";

		/* password（必須・長さ） */
		if (isBlank(password)) {
			errors.password = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.PASSWORD);
		} else if (password.length < this.MIN) {
			errors.password = format(this.messages, this.ERROR.COMMON_MIN, this.LABEL.PASSWORD, this.MIN);
		}

		/* passwordCheck（必須・長さ・一致） */
		if (isBlank(passwordCheck)) {
			errors.passwordCheck = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.PASSWORD_CHECK);
		} else if (passwordCheck.length < this.MIN) {
			errors.passwordCheck = format(this.messages, this.ERROR.COMMON_MIN, this.LABEL.PASSWORD_CHECK, this.MIN);
		} else if (isNotEquals(password, passwordCheck)) {
			errors.passwordCheck = format(this.messages, this.ERROR.USER_PASSWORD_MISMATCH, this.LABEL.PASSWORD_CHECK);
		}

		/* 正規表現：グローバルのみ表示。フィールドは invalid 固定（行内テキストは出さない） */
		const regexRaw = this.messages?.[this.REGEX.PASSWORD] ?? this.messages?.["regex.password"];
		if (!errors.password && typeof regexRaw === "string" && regexRaw.length > 0) {
			try {
				const pattern = new RegExp(regexRaw);
				if (!pattern.test(password)) {
					const msg = format(this.messages, this.ERROR.USER_PASSWORD_PATTERN, this.LABEL.PASSWORD);
					errors.global = msg; // 上部アラートへ
					errors._invalidSet = ["password", "passwordCheck"]; // invalid 固定
				}
			} catch (_e) {
				/* 無効な正規表現は無視 */
			}
		}
		return errors;
	}
}
