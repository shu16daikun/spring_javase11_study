// /js/main-contents/admin/users/insert-update/service.js
import { isBlank } from "/psfm/js/common/utils.js";
import { CONST } from "./const.js";

/* formatMsg 無い場合の保険 */
const _formatFallback = (messages = {}, key, label = "", ...args) => {
	let t = messages?.[key] || "";
	if (!t) return label ? `${label}の入力に誤りがあります。` : "入力に誤りがあります。";
	args.forEach((a, i) => { t = t.replace(`{${i + 1}}`, String(a)); });
	return t.replace("{0}", label);
};
const format = globalThis?.formatMsg ? globalThis.formatMsg : _formatFallback;

export class UsersInsertUpdateService {
	constructor(messages = CONST.MESSAGES()) {
		this.messages = messages;
		this.MIN = CONST.LIMIT.USERNAME_MIN;
		this.MAX = CONST.LIMIT.USERNAME_MAX;
		this.ERROR = CONST.KEY.ERROR;
		this.REGEX = CONST.KEY.REGEX;
		this.LABEL = CONST.LABEL;
	}

	validate(values) {
		const errors = {};
		const username = (values?.username ?? "").trim();
		const authorityViewId = (values?.authorityViewId ?? "").trim();

		/* ユーザー名：必須・桁数 */
		if (isBlank(username)) {
			errors.username = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.USERNAME);
		} else if (username.length < this.MIN || username.length > this.MAX) {
			errors.username = format(this.messages, this.ERROR.COMMON_SIZE, this.LABEL.USERNAME, this.MIN, this.MAX);
		}

		/* ユーザー名：パターン（NG なら invalid 固定） */
		const raw = String(this.messages[this.REGEX.USERNAME] || "").trim();
		if (!errors.username && raw) {
			try {
				const pattern = new RegExp(raw);
				if (!pattern.test(username)) {
					errors.username = format(this.messages, this.ERROR.USER_USERNAME_PATTERN, this.LABEL.USERNAME);
					errors._invalidSet = ["username"];
				}
			} catch (_e) {
				/* 無効な正規表現はスキップ */
			}
		}

		/* 権限：必須 */
		if (isBlank(authorityViewId)) {
			errors.authorityViewId = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.AUTHORITY);
		}

		return errors;
	}
}
