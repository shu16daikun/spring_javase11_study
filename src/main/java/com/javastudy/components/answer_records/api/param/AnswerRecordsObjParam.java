/** components/answer_records/api/param/AnswerRecordsObjParam.java */
package com.javastudy.components.answer_records.api.param;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/* 機能：Model属性キー（画面受け渡し用） */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnswerRecordsObjParam {
	public static final String VIEW_DTO = "answerRecordsViewDto";
	public static final String VIEW_DTO_LIST = "answerRecordsViewDtoList";
	public static final String STATS_VIEW_DTO = "answerRecordsStatsViewDto"; // ★ サマリ用（追加）
}
