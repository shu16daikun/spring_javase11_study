package com.javastudy.components.sankou_book_color.api.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.javastudy.components.sankou_book_color.api.domain.SankouBookColorEnum;
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorInputDto;
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorViewDto;

/**
 * 【機能】参考書カラー公開サービス
 *
 * <p>
 * 目的：カラーの参照/更新/削除を公開。内部IDは隠蔽。
 */
public interface SankouBookColorService {

	String getEntityId(String viewId);

	ADMIN_SankouBookColorViewDto getAdminViewDtoById(String id);

	ADMIN_SankouBookColorViewDto getAdminViewDtoByViewId(String viewId);

	SankouBookColorEnum getEnumByViewId(String viewId);

	SankouBookColorEnum getEnumById(String id);

	void update(ADMIN_SankouBookColorInputDto input);

	void deleteByViewId(String viewId);

	/** 管理画面向け：全件取得（name ASC）。空なら空リスト。 */
	List<ADMIN_SankouBookColorViewDto> getAdminViewDtoList();

	boolean isUse(String id);

	Map<String, ADMIN_SankouBookColorViewDto> getAdminViewDtoByIds(Set<String> idSet);

	void create(ADMIN_SankouBookColorInputDto input);

}
