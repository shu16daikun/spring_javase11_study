// com.javastudy.components.weakness.api.service.WeaknessService
package com.javastudy.components.weakness.api.service;

import java.util.List;

import com.javastudy.components.weakness.api.dto.ADMIN_WeaknessViewDto;
import com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto.USER_WeaknessChapterViewDto;
import com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto.USER_WeaknessQuestionsViewDto;

/**
 * 弱点分析 公開サービス（ユーザー向け集計の取得）。
 *
 * <p>
 * “画面に見せるもの”のDTOを返す。ログ専用値は返却しない。
 */
public interface WeaknessService {

	/**
	 * 擬似ID（ViewId）から内部の EntityId 候補を得る。
	 *
	 * <p>
	 * 外部キー挿入など内部参照にのみ使用。
	 *
	 * @param viewId
	 *            画面用の擬似ID
	 * @return EntityId 候補（規約に従い復元）
	 */
	String getEntityId(String viewId);

	/**
	 * 章単位の弱点サマリを取得する。
	 *
	 * @param userId
	 *            ログインユーザーの EntityId
	 * @param bookViewId
	 *            対象参考書の ViewId
	 * @return 章ごとの集計リスト（表示用）
	 */
	List<USER_WeaknessChapterViewDto> listWeakChapters(String userId, String bookViewId);

	/**
	 * 質問（問題）単位の弱点リストを取得する。
	 *
	 * @param userId
	 *            ログインユーザーの EntityId
	 * @param bookViewId
	 *            対象参考書の ViewId
	 * @param minAttempts
	 *            最低試行回数（この回数未満は除外）
	 * @param limit
	 *            取得上限件数（表示の都合）
	 * @return 弱点質問の一覧（表示用）
	 */
	List<USER_WeaknessQuestionsViewDto> listWeakQuestions(
		String userId,
		String bookViewId,
		int minAttempts,
		int limit);

	/**
	 * 質問ViewIdから表示DTOを取得する。
	 *
	 * @param viewId
	 *            質問の ViewId
	 * @return 質問の表示DTO
	 */
	USER_WeaknessQuestionsViewDto getQuestionsViewDtoByViewId(String viewId);

	/**
	 * 質問EntityIdから表示DTOを取得する。
	 *
	 * @param id
	 *            質問の EntityId
	 * @return 質問の表示DTO
	 */
	USER_WeaknessQuestionsViewDto getQuestionsViewDtoById(String id);

	/** 管理向け：全件取得（userId → sankouBookId → chapterId → totalAttempts 降順） */
	List<ADMIN_WeaknessViewDto> getAdminViewDtoList();

	/**
	 * 質問ViewIdから表示DTOを取得する。
	 *
	 * @param viewId
	 *            質問の ViewId
	 * @return 質問の表示DTO
	 */
	ADMIN_WeaknessViewDto getAdminViewDtoByViewId(String viewId);

	/**
	 * 質問EntityIdから表示DTOを取得する。
	 *
	 * @param id
	 *            質問の EntityId
	 * @return 質問の表示DTO
	 */
	ADMIN_WeaknessViewDto getAdminViewDtoById(String id);
}
