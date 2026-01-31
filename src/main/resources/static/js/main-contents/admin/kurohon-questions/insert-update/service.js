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

export class KurohonQuestionsInsertUpdateService {
	constructor(messages = CONST.MESSAGES()) {
		this.messages = messages;
		this.ERROR = CONST.KEY.ERROR;
		this.LABEL = CONST.LABEL;
		this.L = CONST.LIMIT;
	}

	validate(values) {
		const errors = {};

		const book = (values?.sankouBooksViewId ?? "").trim();
		const chapter = (values?.chapterViewId ?? "").trim();
		const qnoRaw = (values?.questionNo ?? "").trim();
		const correct = (values?.correctOption ?? "").trim();
		const ansMaxRaw = (values?.answerCountMax ?? "").trim();
		const optCntRaw = (values?.optionCount ?? "").trim();

		// 参考書：必須
		if (isBlank(book)) {
			errors.sankouBooksViewId = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.BOOK);
		}

		// 章：必須
		if (isBlank(chapter)) {
			errors.chapterViewId = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.CHAPTER);
		}

		// 問題番号：必須・整数・範囲
		if (isBlank(qnoRaw)) {
			errors.questionNo = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.QUESTION_NO);
		} else {
			const n = Number(qnoRaw);
			if (!Number.isInteger(n)) {
				errors.questionNo = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.QUESTION_NO);
			} else if (n < this.L.QUESTION_NO_MIN) {
				errors.questionNo = format(this.messages, this.ERROR.COMMON_MIN, this.LABEL.QUESTION_NO, this.L.QUESTION_NO_MIN);
			} else if (n > this.L.QUESTION_NO_MAX) {
				errors.questionNo = format(this.messages, this.ERROR.COMMON_MAX, this.LABEL.QUESTION_NO, this.L.QUESTION_NO_MAX);
			}
		}

		// 正答：必須・最大長
		if (isBlank(correct)) {
			errors.correctOption = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.CORRECT_OPTION);
		} else if (correct.length > this.L.CORRECT_OPTION_MAX) {
			errors.correctOption = format(this.messages, this.ERROR.COMMON_MAX, this.LABEL.CORRECT_OPTION, this.L.CORRECT_OPTION_MAX);
		}

		// 最大回答数：必須・整数・下限
		if (isBlank(ansMaxRaw)) {
			errors.answerCountMax = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.ANSWER_COUNT_MAX);
		} else {
			const n = Number(ansMaxRaw);
			if (!Number.isInteger(n) || n < this.L.ANSWER_COUNT_MAX_MIN) {
				errors.answerCountMax = format(this.messages, this.ERROR.COMMON_MIN, this.LABEL.ANSWER_COUNT_MAX, this.L.ANSWER_COUNT_MAX_MIN);
			}
		}

		// 選択肢数：必須・整数・下限0
		if (isBlank(optCntRaw)) {
			errors.optionCount = format(this.messages, this.ERROR.COMMON_NOT_BLANK, this.LABEL.OPTION_COUNT);
		} else {
			const n = Number(optCntRaw);
			if (!Number.isInteger(n) || n < this.L.OPTION_COUNT_MIN) {
				errors.optionCount = format(this.messages, this.ERROR.COMMON_MIN, this.LABEL.OPTION_COUNT, this.L.OPTION_COUNT_MIN);
			}
		}

		return errors;
	}
}
