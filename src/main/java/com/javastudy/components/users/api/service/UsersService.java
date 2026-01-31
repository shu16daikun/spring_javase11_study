// =======================================
// main: API
// com.javastudy.components.users.api.service.UsersService
// =======================================
package com.javastudy.components.users.api.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.javastudy.components.users.api.dto.USER_UsersInputDto;
import com.login.components.user.api.dto.MyUsersInputDto;
import com.login.components.user.api.dto.MyUsersViewDto;

/**
 * 【統合ファサード】Users（main→login 橋渡し一本化）
 *
 * <p>
 * UI/他ドメインは本インターフェースのみ参照し、login 側の MyUsersService に直接依存しない。
 */
public interface UsersService {

	/* ===== User向け操作 ===== */
	void updateUsername(USER_UsersInputDto dto);

	void resetPassword();

	/* ===== Admin向け View ===== */
	ADMIN_UsersViewDto getAdminViewDtoById(String id);

	ADMIN_UsersViewDto getAdminViewDtoByViewId(String viewId);

	/* === 追加：ID集合→ViewDto の一括取得（IN最適化用） === */
	Map<String, ADMIN_UsersViewDto> getViewDtoMapByIds(Set<String> ids);

	List<ADMIN_UsersViewDto> getAdminViewDtoList();

	Page<ADMIN_UsersViewDto> findAll(Pageable pageable);

	Page<ADMIN_UsersViewDto> searchByUsername(String likeUsername, Pageable pageable);

	/* ===== パススルー（login機能の集約提供） ===== */
	String getEntityId(String viewId);

	MyUsersViewDto getLoginUser();

	/* ===== 変更系（戻りは Admin用View に正規化） ===== */
	void create(MyUsersInputDto input);

	void resetPassword(String viewId);

	void update(String currentViewId, MyUsersInputDto input);

	void update(MyUsersInputDto input);

	void updateAuthority(String userViewId, String authorityViewId);

	void changePassword(String userViewId, String rawPassword);

	void deleteByViewId(String viewId);

	/* ===== 参照整合チェック ===== */
	boolean isUse(String userId);

	/* ===== 補助（必要なら残す） ===== */
	ADMIN_UsersViewDto toAdminViewDto(MyUsersViewDto myViewDto);

}
