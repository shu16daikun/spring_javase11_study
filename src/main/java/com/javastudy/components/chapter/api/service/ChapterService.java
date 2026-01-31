package com.javastudy.components.chapter.api.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.javastudy.components.chapter.api.dto.ADMIN_ChapterInputDto;
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;

/**
 * 【機能】章ユースケース公開API
 *
 * <p>
 * 目的：章の参照/作成/更新/削除を外部へ提供。内部IDは隠蔽。
 *
 * <h2>公開契約</h2>
 *
 * <ul>
 * <li>一覧取得は0件なら空リスト。
 * <li>例外は userCode（ErrorCode）で提示、内部構造は出さない。
 * </ul>
 *
 * <h2>注意/備考</h2>
 *
 * <ul>
 * <li>DTOは record（Java 21）。importは明示。文字列定数は private final String。
 * </ul>
 */
public interface ChapterService {

	/* ===== [query: user] ===== */
	List<USER_ChapterViewDto> getUserViewDtoListFilterSankouBooks(String sankouBooksId);

	USER_ChapterViewDto getUserViewDtoByNo(String bookId, String no);

	USER_ChapterViewDto getUserViewDtoByViewId(String viewId);

	USER_ChapterViewDto getUserViewDtoById(String id);

	/* ===== [query: admin] ===== */
	ADMIN_ChapterViewDto getAdminViewDtoByViewId(String viewId);

	ADMIN_ChapterViewDto getAdminViewDtoById(String id);

	/** 管理画面向け：全件取得（sankouBookId ASC, no ASC）。空なら空リスト。 */
	List<ADMIN_ChapterViewDto> getAdminViewDtoList();

	/* ===== [helper] ===== */
	String getEntityId(String viewId);

	boolean existsByNo(String bookViewId, String no);

	/* ===== [command] ===== */
	void create(ADMIN_ChapterInputDto input);

	void update(ADMIN_ChapterInputDto input);

	void deleteByViewId(String viewId);

	boolean isUse(String id);

	/* ★ 追加：章ID集合 → ADMIN_ViewDto を一括取得（IN最適化） */
	Map<String, ADMIN_ChapterViewDto> getAdminViewDtoMapByIds(Set<String> ids);

}
