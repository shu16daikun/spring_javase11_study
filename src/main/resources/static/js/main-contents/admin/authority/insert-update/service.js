// /js/main-contents/admin/authority/insert-update/service.js
import { isBlank } from "/psfm/js/common/utils.js";
import { CONST } from "./const.js";
import { getLoger, endAndReturn } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.authority.insertUpdate.service");

const formatLocal = (messages = {}, key, label = "", ...args) => {
	let t = messages && typeof messages[key] === "string" ? messages[key] : "";
	if (!t) return label ? `${label}の入力に誤りがあります。` : "入力に誤りがあります。";
	args.forEach((a, i) => { t = t.replace(`{${i + 1}}`, String(a)); });
	return t.replace("{0}", label);
};

export class AuthorityInsertUpdateService {
	constructor(messages) {
		const span = LOG.logStart("AuthorityInsertUpdateService#constructor", { level: "TRACE", duration: false });
		this.messages = messages || CONST.MESSAGES();
		span.end();
	}

	validate(values) {
		const span = LOG.logStart("AuthorityInsertUpdateService#validate", { level: "TRACE", duration: false });

		const errors = {};
		const name = (values?.name ?? "").trim();

		// 必須・桁数
		if (isBlank(name)) {
			errors.systemName = formatLocal(this.messages, CONST.KEY.ERROR.COMMON_NOT_BLANK, CONST.LABEL.NAME);
		} else if (name.length < CONST.LIMIT.NAME_MIN || name.length > CONST.LIMIT.NAME_MAX) {
			errors.systemName = formatLocal(
				this.messages,
				CONST.KEY.ERROR.COMMON_SIZE,
				CONST.LABEL.NAME,
				CONST.LIMIT.NAME_MIN,
				CONST.LIMIT.NAME_MAX
			);
		}

		// パターン（サーバ設定があれば適用）
		const raw = String(this.messages[CONST.KEY.REGEX.AUTH_SYSTEM_NAME] || "").trim();
		if (!errors.systemName && raw) {
			try {
				const pattern = new RegExp(raw);
				if (!pattern.test(name)) {
					errors.systemName = formatLocal(
						this.messages,
						CONST.KEY.ERROR.AUTH_SYSTEM_NAME_PATTERN,
						CONST.LABEL.NAME
					);
					errors._invalidSet = ["systemName"];
				}
			} catch (_e) {
				// 無効な正規表現は無視（気づきたいなら WARN だけ出す）
				LOG.warn("Invalid regex ignored: {0}", raw);
			}
		}

		return endAndReturn(span, errors);
	}
}
