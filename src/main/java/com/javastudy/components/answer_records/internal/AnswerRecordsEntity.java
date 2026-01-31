package com.javastudy.components.answer_records.internal;

import com.javastudy.components.answer_records.internal.AnswerRecordsDB.AnswerRecordsColumn;
import com.util.type.MyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

/* 機能：解答履歴 */
@Entity
@Table(name = AnswerRecordsDB.TABLE)
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AnswerRecordsEntity {

	/* 主キー：AR + 8桁（Service 側で事前採番してセット） */
	@Id
	@Column(name = AnswerRecordsColumn.ID, nullable = false, unique = true, length = 10)
	private String id;

	@Column(name = AnswerRecordsColumn.USER_ID, nullable = false, length = 6)
	private String userId;

	@Column(name = AnswerRecordsColumn.QUESTION_ID, nullable = false, length = 10)
	private String kurohonQuestionId;

	@Column(name = AnswerRecordsColumn.CHAPTER_ID, nullable = false, length = 10)
	private String chapterId;

	@Column(name = AnswerRecordsColumn.SANKOU_BOOK_ID, nullable = false, length = 7)
	private String sankouBookId;

	@Column(name = AnswerRecordsColumn.ATTEMPT_SESSION_ID, length = 12)
	private String attemptSessionId;

	@Column(name = AnswerRecordsColumn.SELECTED_OPTION, nullable = false, length = 20)
	private String selectedOption;

	@Column(name = AnswerRecordsColumn.IS_CORRECT, nullable = false)
	private boolean isCorrect;

	/* ▼ DB DEFAULT/トリガに完全委譲。アプリからは送らない（取得は refresh） */
	@Column(name = AnswerRecordsColumn.ANSWERED_AT, nullable = false, insertable = false, updatable = false)
	private LocalDateTime answeredAt;

	@Column(name = AnswerRecordsColumn.ATTEMPT_NO, nullable = false, insertable = false, updatable = false)
	private Integer attemptNo;

	/* NOT NULL 対策（最低限の防御） */
	@PrePersist
	private void onPrePersist() {
		if (MyType.isNull(selectedOption)) {
			selectedOption = MyType.EMPTY;
		}
	}
}
