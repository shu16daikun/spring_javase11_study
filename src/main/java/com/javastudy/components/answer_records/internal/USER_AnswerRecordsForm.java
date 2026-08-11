package com.javastudy.components.answer_records.internal;

import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsInputDto;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.my.util.type.MyType;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* 機能：解答登録フォーム（画面バインド専用） */
@Getter
@Setter
@NoArgsConstructor
public class USER_AnswerRecordsForm {

	@NotEmpty(message = ErrorProp.COMMON_NOT_BLANK)
	private List<String> selectedOption = new ArrayList<>();

	private List<String> values = new ArrayList<>();

	private String questionViewId;
	private String sankouBookViewId;
	private String chapterViewId;
	private int answerCountMax;
	private int optionCount;
	private String correctOption;

	private transient USER_KurohonQuestionsViewDto kurohonQuestions;

	/* 初期表示：ViewDto → Form */
	static USER_AnswerRecordsForm fromViewDto(final USER_KurohonQuestionsViewDto v) {
		final USER_AnswerRecordsForm f = new USER_AnswerRecordsForm();
		f.kurohonQuestions = v;
		f.questionViewId = MyType.orEmpty(v.viewId());
		f.sankouBookViewId = MyType.orEmpty(v.sankouBooksViewDto().viewId());
		f.chapterViewId = MyType.orEmpty(v.chapterViewDto().viewId());
		f.answerCountMax = v.answerCountMax();
		f.optionCount = v.optionCount();
		f.correctOption = MyType.orEmpty(v.correctOption());
		f.createFormElementsByOptionCount(); // ★ここで A/B/C... を生成
		return f;
	}

	/* ==== ここから差し替え ==== */
	private void createFormElementsByOptionCount() {
		if (optionCount <= 0) {
			return;
		}
		values.clear();
		for (int i = 0; i < optionCount; i++) {
			values.add(indexToLetters(i)); // A, B, ... Z, AA, AB, ...
		}
	}

	private static String indexToLetters(final int index) {
		int n = index + 1;
		final StringBuilder sb = new StringBuilder();
		while (n > 0) {
			final int rem = (n - 1) % 26;
			sb.insert(0, (char) ('A' + rem));
			n = (n - 1) / 26;
		}
		return sb.toString();
	}

	final USER_AnswerRecordsInputDto toInputDto() {
		final String norm = normalizeOptions(this.selectedOption, this.answerCountMax);
		return new USER_AnswerRecordsInputDto(
			MyType.orEmpty(this.questionViewId),
			MyType.orEmpty(this.sankouBookViewId),
			MyType.orEmpty(this.chapterViewId),
			norm,
			MyType.orEmpty(this.correctOption));
	}

	/* 「UNKNOWN」単独化＋重複排除＋昇順連結＋上限カット */
	private String normalizeOptions(final List<String> src, final int max) {
		if (MyType.isEmpty(src)) {
			return MyType.EMPTY;
		}
		if (src.contains("UNKNOWN")) {
			return "UNKNOWN";
		}
		final List<String> filtered = src.stream()
			.filter(MyType::isNotBlank)
			.distinct()
			.sorted()
			.limit(Math.max(0, max))
			.toList();
		return String.join("", filtered);
	}
}
