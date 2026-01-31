package com.javastudy.components.sankou_books.api.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksInputDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;

/**
 * 【機能】参考書公開サービス
 *
 * <p>
 * 目的：参考書の参照/作成/更新/削除を公開。
 */
public interface SankouBooksService {

	List<USER_SankouBooksViewDto> getUserViewDtoList();

	USER_SankouBooksViewDto getUserViewDtoByViewId(String viewId);

	USER_SankouBooksViewDto getUserViewDtoById(String Id);

	ADMIN_SankouBooksViewDto getAdminViewDtoByViewId(String viewId);

	ADMIN_SankouBooksViewDto getAdminViewDtoById(String Id);

	String getEntityId(String viewId);

	void deleteByViewId(String viewId);

	void update(ADMIN_SankouBooksInputDto input);

	void create(ADMIN_SankouBooksInputDto input);

	/** 管理画面向け：全件取得（name ASC）。空なら空リスト。 */
	List<ADMIN_SankouBooksViewDto> getAdminViewDtoList();

	boolean isUse(String sankouBookId);

	/* ★追加：ID集合→ADMIN_ViewDto を一括取得（IN最適化用） */
	Map<String, ADMIN_SankouBooksViewDto> getAdminViewDtoMapByIds(Set<String> ids);
}
