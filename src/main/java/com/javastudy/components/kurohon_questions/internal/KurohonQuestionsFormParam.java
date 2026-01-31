/*
 * KurohonQuestionsFormParam.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.kurohon_questions.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 14:47:43
 *
 * 目的:
 * - 画面契約の定数（フォーム名／フィールド名／制約値／Model属性キー）を一元管理
 */
package com.javastudy.components.kurohon_questions.internal;

/* ===== [public/protected] START ===== */
final class KurohonQuestionsFormParam {

	/* フォーム/Modelキー */
	static final String FORM = "kurohonQuestionsForm";
	static final String ATTR_CONSTRAINTS = "constraints";
	static final String ATTR_BOOKS = "books"; // List<ADMIN_SankouBooksViewDto>
	static final String ATTR_CHAPTERS = "chapters"; // List<ADMIN_ChapterViewDto>（選択中Bookで絞る）

	/* フィールド名 */
	static final String SANKOU_BOOK = "sankouBook";
	static final String CHAPTER = "chapter";
	static final String QUESTION_NO = "questionNo";
	static final String QUESTION_HTML = "questionHtml";
	static final String CORRECT_OPTION = "correctOption";
	static final String EXPLANATION_HTML = "explanationHtml";
	static final String ANSWER_COUNT_MAX = "answerCountMax";
	static final String OPTION_COUNT = "optionCount";

	/* 制約値 */
	static final int QUESTION_NO_MIN = 1;
	static final int QUESTION_NO_MAX = 999;
	static final int CORRECT_OPTION_MAX = 20;

	// answerCountMax: 1..（上限は要件なし）
	// optionCount : 0..（上限は要件なし）

	private KurohonQuestionsFormParam() {
	}
}
/* ===== [public/protected] END ===== */
