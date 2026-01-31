/*
 * ToADMIN_UsersViewDtoMapper.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.users.internal
 * Author  : shu-kundeath
 * Updated : 2025/11/01
 *
 * 目的:
 * - MyUsersViewDto → ADMIN_UsersViewDto の純粋変換（DIなし）
 *
 * 注意:
 * - 外部解決が必要な値（entityId / authorityViewDto / isUse）は呼び出し側で解決し、引数で渡す。
 */

package com.javastudy.components.users.internal;

import org.springframework.stereotype.Component;

/* ===== [import] START ===== */
import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.login.components.user.api.dto.MyUsersViewDto;
/* ===== [import] END ===== */

@Component
public class ToADMIN_UsersViewDtoMapper {
	/**
	 * 純粋変換：外部解決が必要な値は呼び出し側から受け取る。
	 */
	final ADMIN_UsersViewDto fromMyViewDto(
		final MyUsersViewDto myViewDto,
		final String entityId,
		final ADMIN_AuthorityViewDto authorityViewDto,
		final boolean isUse) {
		return new ADMIN_UsersViewDto(
			entityId,
			myViewDto.viewId(),
			myViewDto.username(),
			authorityViewDto,
			myViewDto.isFirstLogin(),
			isUse);
	}
}
