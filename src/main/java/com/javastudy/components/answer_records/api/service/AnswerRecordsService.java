// components/answer_records/api/service/AnswerRecordsService.java（追記）
package com.javastudy.components.answer_records.api.service;

import java.util.List;

import com.javastudy.components.answer_records.api.dto.ADMIN_AnswerRecordsViewDto;
import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsInputDto;
import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsStatsViewDto;
import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsViewDto;
import com.javastudy.components.answer_records.internal.AnswerRecordsEntity;

public interface AnswerRecordsService {
	List<USER_AnswerRecordsViewDto> getUserViewDtoListByAttemptSession(String attemptSessionViewId);

	List<AnswerRecordsEntity> saveAllWithAttempt(
		String sessionViewId,
		List<USER_AnswerRecordsInputDto> dtoList);

	/** 管理向け：全件取得（attemptSessionId→chapter.no→questionNo）。空なら空リスト。 */
	List<ADMIN_AnswerRecordsViewDto> getAdminViewDtoList();

	ADMIN_AnswerRecordsViewDto getAdminViewDtoById(String id);

	ADMIN_AnswerRecordsViewDto getAdminViewDtoByViewId(String viewId);

	String getEntityId(String viewId);

	// ★ 集計を返す
	USER_AnswerRecordsStatsViewDto getStatsByAttemptSession(String attemptSessionViewId);
}
