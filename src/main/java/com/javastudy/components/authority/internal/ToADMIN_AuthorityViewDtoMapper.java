/*
 * ToADMIN_AuthorityViewDtoMapper.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.authority.internal
 * Author  : shu-kundeath
 * Created : 2025/10/25 10:38:29
 *
 * 目的:
 * - MyAuthorityViewDto（login側）→ ADMIN_AuthorityViewDto（main側）への純粋変換
 *
 * 注意:
 * - 依存注入しない（純粋変換）。外部解決が必要な値は引数で渡す。
 */

package com.javastudy.components.authority.internal;

import org.springframework.stereotype.Component;

/* ===== [import] START ===== */
import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.login.components.authority.api.dto.MyAuthorityViewDto;
/* ===== [import] END ===== */

@Component
public class ToADMIN_AuthorityViewDtoMapper {

	/**
	 * MyAuthorityViewDto → ADMIN_AuthorityViewDto（純粋変換）。
	 *
	 * @param myViewDto
	 *            loginモジュールの権限ViewDto
	 * @param entityId
	 *            権限の物理ID（呼び出し側で解決済み）
	 * @param isUse
	 *            使用有無（呼び出し側で判定済み）
	 */
	public ADMIN_AuthorityViewDto fromMyViewDto(
		final MyAuthorityViewDto myViewDto,
		final String entityId,
		final boolean isUse) {

		final String viewId = myViewDto.systemId();
		return new ADMIN_AuthorityViewDto(
			entityId,
			viewId,
			myViewDto.systemName(),
			isUse);
	}
}
