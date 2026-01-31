// /js/main-contents/login/service.js
// 機能：ログインフォーム検証（PropKey依存なし）
import { CONST } from "./const.js";

/* ---- formatMsg が無い場合のフォールバック ---- */
const _formatFallback = (messages = {}, key, label = "", ...args) => {
	let t = messages?.[key] || "";
	if (!t) return label ? `${label}の入力に誤りがあります。` : "入力に誤りがあります。";
	args.forEach((a, i) => { t = t.replace(`{${i + 1}}`, String(a)); });
	return t.replace("{0}", label);
};
const format = globalThis?.formatMsg ? globalThis.formatMsg : _formatFallback;

const isBlank = (s) => s == null || String(s).trim() === "";

export class LoginService {
	constructor(messages = CONST.MESSAGES()) {
		this.messages = messages;
		this.K = CONST.KEY.ERROR;
		this.LABEL = CONST.LABEL;
	}

	validate(values) {
		const errors = {};
		const username = values?.username ?? "";
		const password = values?.password ?? "";

		if (isBlank(username)) {
			errors.username = format(this.messages, this.K.COMMON_NOT_BLANK, this.LABEL.USERNAME);
		}
		if (isBlank(password)) {
			errors.password = format(this.messages, this.K.COMMON_NOT_BLANK, this.LABEL.PASSWORD);
		}
		return errors;
	}
}
