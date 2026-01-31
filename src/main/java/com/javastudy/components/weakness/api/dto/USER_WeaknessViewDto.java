// com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto
package com.javastudy.components.weakness.api.dto;

import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;
import java.time.LocalDateTime;

/**
 * 弱点分析：ユーザー向け表示DTO ルート。
 *
 * <p>
 * “画面に見せるもの”専用。ログ専用情報は持たせない。
 */
public final class USER_WeaknessViewDto {

	/**
	 * 弱点質問リスト用の行DTO。
	 *
	 * <p>
	 * 最終回答状況・正答率など、質問単位での指標を保持。
	 */
	public static record USER_WeaknessQuestionsViewDto(
		/* 画面表示：章の概要（公開ViewId等は chapterViewDto 側で保持） */
		USER_ChapterViewDto chapterViewDto,
		/* 画面表示：問題の概要（公開ViewId等は questionsViewDto 側で保持） */
		USER_KurohonQuestionsViewDto questionsViewDto,
		/* 正答率（小数。表示時に丸め／百分率化はView側で実施） */
		double correctRate,
		/* 総試行回数 */
		int totalAttempts,
		/* 直近の回答が正だったか */
		boolean lastIsCorrect,
		/* 直近回答時刻（ローカル時間） */
		LocalDateTime lastAnsweredAt) {
	}

	/**
	 * 弱点章リスト用の行DTO。
	 *
	 * <p>
	 * 章単位の集計（加重平均の正答率／正誤カウント）を保持。
	 */
	public static record USER_WeaknessChapterViewDto(
		/* 画面表示：章の概要 */
		USER_ChapterViewDto chapterViewDto,
		/* 章全体の正答率（小数。表示時に丸め） */
		double correctRate,
		/* 総試行回数 */
		int totalAttempts,
		/* 正解数 */
		int correctCount,
		/* 不正解数 */
		int wrongCount) {
	}
}
