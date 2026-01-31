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

export class SankouBooksInsertUpdateService {
	constructor(messages = CONST.MESSAGES()) {
		this.messages = messages;
		this.MAX = CONST.LIMIT.NAME_MAX;
		this.ERROR = CONST.KEY.ERROR;
		this.LABEL = CONST.LABEL;
	}

	validate(values) {
		const errors = {};
		const name = (values?.name ?? "").trim();
		const colorViewId = (values?.colorViewId ?? "").trim();

		// 参考書名：必須・最大長
		if (isBlank(name)) {
			errors.name = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.NAME);
		} else if (this.MAX && name.length > this.MAX) {
			errors.name = format(this.messages, this.ERROR.COMMON_MAX, this.LABEL.NAME, this.MAX);
		}

		// カラー：必須
		if (isBlank(colorViewId)) {
			errors.colorViewId = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.COLOR);
		}

		return errors;
	}
}
