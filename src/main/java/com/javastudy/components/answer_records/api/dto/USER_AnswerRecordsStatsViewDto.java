package com.javastudy.components.answer_records.api.dto;

import java.math.BigDecimal;

/* 機能：結果サマリ（回答数・正答数・正答率%） */
public record USER_AnswerRecordsStatsViewDto(
	int totalCount,
	int correctCount,
	BigDecimal accuracy) {
}
