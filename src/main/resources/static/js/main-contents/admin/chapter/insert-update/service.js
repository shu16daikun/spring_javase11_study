// /js/main-contents/admin/chapter/insert-update/service.js
import { isBlank } from "/psfm/js/common/utils.js";
import { getLoger, endAndReturn } from "/psfm/js/common/loger.js";
import { CONST } from "./const.js";

const LOG = getLoger("admin.chapter.insert-update.service");

/* formatMsg 無い場合の保険 */
const _formatFallback = (messages = {}, key, label = "", ...args) => {
	let t = messages?.[key] || "";
	if (!t) return label ? `${label}の入力に誤りがあります。` : "入力に誤りがあります。";
	args.forEach((a, i) => {
		t = t.replace(`{${i + 1}}`, String(a));
	});
	return t.replace("{0}", label);
};
const format = globalThis?.formatMsg ? globalThis.formatMsg : _formatFallback;

export class ChapterInsertUpdateService {
	constructor(messages = CONST.MESSAGES()) {
		const span = LOG.logStart("ChapterInsertUpdateService#constructor", { level: "TRACE", duration: false });

		this.messages = messages;
		this.MIN = CONST.LIMIT.NO_MIN;
		this.MAX = CONST.LIMIT.NO_MAX;
		this.TITLE_MAX = CONST.LIMIT.TITLE_MAX;
		this.ERROR = CONST.KEY.ERROR;
		this.LABEL = CONST.LABEL;

		span.end();
	}

	validate(values) {
		const span = LOG.logStart("ChapterInsertUpdateService#validate", { level: "DEBUG", duration: true });

		const errors = {};
		const noRaw = String(values?.no ?? "").trim();
		const title = (values?.title ?? "").trim();
		const bookId = (values?.sankouBooksViewId ?? "").trim();

		// 章番号：必須・数値・範囲
		if (isBlank(noRaw)) {
			errors.no = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.NO);
		} else {
			const n = Number(noRaw);
			if (!Number.isInteger(n)) {
				errors.no = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.NO);
			} else if (n < this.MIN) {
				errors.no = format(this.messages, this.ERROR.COMMON_MIN, this.LABEL.NO, this.MIN);
			} else if (n > this.MAX) {
				errors.no = format(this.messages, this.ERROR.COMMON_MAX, this.LABEL.NO, this.MAX);
			}
		}

		// タイトル：必須・最大長
		if (isBlank(title)) {
			errors.title = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.TITLE);
		} else if (this.TITLE_MAX && title.length > this.TITLE_MAX) {
			errors.title = format(this.messages, this.ERROR.COMMON_MAX, this.LABEL.TITLE, this.TITLE_MAX);
		}

		// 参考書：必須
		if (isBlank(bookId)) {
			errors.sankouBooksViewId = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.BOOK);
		}

		return endAndReturn(span, errors);
	}
}
