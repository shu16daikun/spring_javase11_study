package com.javastudy.components.kurohon_questions.api.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsInputDto;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;

/**
 * 【機能】黒本：問題ユースケース公開API
 *
 * <p>
 * 目的：問題の参照/作成/更新/削除を外部へ提供。内部IDは隠蔽。
 *
 * <h2>公開契約</h2>
 *
 * <ul>
 * <li>一覧取得は0件なら空リスト。
 * </ul>
 */
public interface KurohonQuestionsService {

	/* ===== [query: user] ===== */
	List<USER_KurohonQuestionsViewDto> getUserViewDtoListFilter(String bookViewId, String no);

	USER_KurohonQuestionsViewDto getUserViewDtoById(String id);

	USER_KurohonQuestionsViewDto getUserViewDtoByViewId(String viewId);

	/* ===== [query: admin] ===== */
	ADMIN_KurohonQuestionsViewDto getAdminViewDtoById(String id);

	ADMIN_KurohonQuestionsViewDto getAdminViewDtoByViewId(String viewId);

	/** 管理画面向け：全件取得（sankouBookId ASC, chapterId ASC, questionNo ASC）。空なら空リスト。 */
	List<ADMIN_KurohonQuestionsViewDto> getAdminViewDtoList();

	/* ===== [helper] ===== */
	String getEntityId(String viewId);

	/* ===== [command] ===== */
	void deleteByViewId(String viewId);

	void update(ADMIN_KurohonQuestionsInputDto input);

	void create(ADMIN_KurohonQuestionsInputDto input);

	boolean isUse(String id);

	/* ★ 追加：問題ID集合 → ADMIN_ViewDto を一括取得（IN最適化） */
	Map<String, ADMIN_KurohonQuestionsViewDto> getAdminViewDtoMapByIds(Set<String> ids);
}
