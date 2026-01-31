// =======================================
// main: API
// com.javastudy.components.authority.api.service.AuthorityService
// =======================================
package com.javastudy.components.authority.api.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.login.components.authority.api.domain.MyAuthorityEnum;
import com.login.components.authority.api.dto.MyAuthorityInputDto;
import com.login.components.authority.api.dto.MyAuthorityViewDto;
import com.login.components.user.api.dto.MyUsersViewDto;

/**
 * 【統合ファサード】Authority（main→login 橋渡し一本化）
 *
 * <p>
 * UI/他ドメインは本インターフェースのみ参照し、login 側の MyAuthorityService に直接依存しない。
 */
public interface AuthorityService {

	/* ===== Admin向け View 取得 ===== */
	ADMIN_AuthorityViewDto getAdminViewDtoById(String id);

	ADMIN_AuthorityViewDto getAdminViewDtoByViewId(String viewId);

	List<ADMIN_AuthorityViewDto> getAdminViewDtoList();

	/* ===== パススルー（login機能の面を main に集約） ===== */
	String getEntityId(String viewId);

	String getNameById(String id);

	boolean hasRole(MyAuthorityEnum role, MyUsersViewDto userDto);

	boolean isUse(String authorityId);

	/* ===== 検索 ===== */
	Page<ADMIN_AuthorityViewDto> searchByName(String likeName, Pageable pageable);

	/* ===== 変更系（戻りは Admin用View に正規化） ===== */
	ADMIN_AuthorityViewDto create(MyAuthorityInputDto input);

	ADMIN_AuthorityViewDto update(MyAuthorityInputDto input);

	void updateName(String viewId, String newName);

	void deleteByViewId(String viewId);

	/** login側のViewDto → main側のAdmin用ViewDto に変換（null安全） */
	ADMIN_AuthorityViewDto toAdminViewDto(MyAuthorityViewDto myViewDto);

	Map<String, ADMIN_AuthorityViewDto> getAdminViewDtoMapByIds(Set<String> ids);
}
