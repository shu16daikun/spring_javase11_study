package com.javastudy.components.kurohon_questions.internal;

import com.javastudy.components.kurohon_questions.internal.KurohonQuestionsDB.KurohonQuestionsColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

/* 機能：黒本：問題 Entity（画面非公開） */
@Entity
@Table(name = KurohonQuestionsDB.TABLE)
@Getter
@DynamicInsert // null列をINSERTから外し、DB DEFAULT（question_html / explanation_html の空文字）を尊重
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class KurohonQuestionsEntity {

	/* 主キー（KQ + 8桁、アプリ採番：DbIdSequence） */
	@Id
	@Column(name = KurohonQuestionsColumn.ID, nullable = false, unique = true, length = 10)
	private String id;

	/* 参照ID */
	@Column(name = KurohonQuestionsColumn.CHAPTER_ID, nullable = false, length = 10)
	private String chapterId;

	@Column(name = KurohonQuestionsColumn.SANKOU_BOOK_ID, nullable = false, length = 7)
	private String sankouBookId;

	/* 属性 */
	@Column(name = KurohonQuestionsColumn.QUESTION_NO, nullable = false, length = 3)
	private String questionNo;

	/* DB DEFAULT：空文字。@DynamicInsertで null 時はDBに任せる */
	@Column(name = KurohonQuestionsColumn.QUESTION_HTML)
	private String questionHtml;

	@Column(name = KurohonQuestionsColumn.CORRECT_OPTION, nullable = false, length = 20)
	private String correctOption;

	/* DB DEFAULT：空文字。@DynamicInsertで null 時はDBに任せる */
	@Column(name = KurohonQuestionsColumn.EXPLANATION_HTML)
	private String explanationHtml;

	@Column(name = KurohonQuestionsColumn.ANSWER_COUNT_MAX, nullable = false)
	private int answerCountMax;

	@Column(name = KurohonQuestionsColumn.OPTION_COUNT, nullable = false)
	private int optionCount;

	/* 保存前補正（NOT NULL防御のみ。文字列はDB DEFAULTに委譲） */
	@PrePersist
	private void onPrePersist() {
		if (isLessThanOne(answerCountMax)) {
			answerCountMax = 1;
		}
		if (isNegative(optionCount)) {
			optionCount = 0;
		}
	}

	/* 判定ユーティリティ */
	private static boolean isLessThanOne(final int n) {
		return n < 1;
	}

	private static boolean isNegative(final int n) {
		return n < 0;
	}
}
