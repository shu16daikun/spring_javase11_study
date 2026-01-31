package com.javastudy.components.weakness.internal;

import com.javastudy.components.weakness.internal.WeaknessDB.WeaknessColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 機能：弱点スナップショット Entity（画面非公開） */
@Entity
@Table(name = WeaknessDB.TABLE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class WeaknessEntity {

	/* 主キー（WE + 8桁、DB側で採番も可だが本Entityは参照中心） */
	@Id
	@Column(name = WeaknessColumn.ID, nullable = false, unique = true, length = 10)
	private String id;

	/* 参照ID */
	@Column(name = WeaknessColumn.USER_ID, nullable = false, length = 6)
	private String userId;

	@Column(name = WeaknessColumn.SANKOU_BOOK_ID, nullable = false, length = 7)
	private String sankouBookId;

	@Column(name = WeaknessColumn.CHAPTER_ID, nullable = false, length = 10)
	private String chapterId;

	@Column(name = WeaknessColumn.KQ_ID, nullable = false, length = 10)
	private String kurohonQuestionId;

	/* 集計値（DB既定/トリガで更新） */
	@Column(name = WeaknessColumn.TOTAL_ATTEMPTS, nullable = false)
	private int totalAttempts;

	@Column(name = WeaknessColumn.CORRECT_COUNT, nullable = false)
	private int correctCount;

	@Column(name = WeaknessColumn.WRONG_COUNT, nullable = false)
	private int wrongCount;

	/* 正答率は DB の GENERATED ALWAYS 列。アプリからは送らない/更新しない */
	@Column(name = WeaknessColumn.CORRECT_RATE, nullable = false, insertable = false, updatable = false)
	private double correctRate;

	/* 直近回答情報（DBトリガで更新。NULL可） */
	@Column(name = WeaknessColumn.LAST_ANSWERED_AT, nullable = true)
	private LocalDateTime lastAnsweredAt;

	@Column(name = WeaknessColumn.LAST_SELECTED_OPTION, nullable = true, length = 20)
	private String lastSelectedOption;

	@Column(name = WeaknessColumn.LAST_IS_CORRECT, nullable = true)
	private Boolean lastIsCorrect;
}
