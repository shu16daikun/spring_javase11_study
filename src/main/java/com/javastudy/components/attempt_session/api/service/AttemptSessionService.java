/** */
package com.javastudy.components.attempt_session.api.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.javastudy.components.attempt_session.api.dto.ADMIN_AttemptSessionViewDto;
import com.javastudy.components.attempt_session.api.dto.USER_AttemptSessionViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.login.components.user.api.dto.MyUsersViewDto;

/** */
public interface AttemptSessionService {

	/**
	 * @param viewId
	 * @return
	 */
	USER_AttemptSessionViewDto getUserViewDtoByViewId(String viewId);

	/**
	 * @param viewId
	 * @return
	 */
	USER_AttemptSessionViewDto getUserViewDtoById(String Id);

	/**
	 * @param viewId
	 * @return
	 */
	ADMIN_AttemptSessionViewDto getAdminViewDtoByViewId(String viewId);

	/**
	 * @param viewId
	 * @return
	 */
	ADMIN_AttemptSessionViewDto getAdminViewDtoById(String Id);

	/** 管理向け：全件取得（最新開始順）。空なら空リスト。 */
	List<ADMIN_AttemptSessionViewDto> getAdminViewDtoList();

	/**
	 * @param viewId
	 * @return
	 */
	String getEntityId(String viewId);

	/**
	 * @param users
	 * @param books
	 */
	USER_AttemptSessionViewDto createNew(MyUsersViewDto users, USER_SankouBooksViewDto books);

	/**
	 * @param viewId
	 */
	void atFinished(String viewId);

	/* ▼ 追加：ID集合→ADMIN_ViewDto の一括取得（IN最適化） */
	Map<String, ADMIN_AttemptSessionViewDto> getAdminViewDtoMapByIds(Set<String> ids);

}
