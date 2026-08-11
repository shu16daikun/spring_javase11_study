/*
 * ADMIN_AttemptSessionController.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.attempt_session.internal
 * Author  : shu-kundeath
 * Created : 2025/10/29 18:36:22
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.attempt_session.internal;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.javastudy.components.attempt_session.api.dto.ADMIN_AttemptSessionViewDto;
import com.javastudy.components.attempt_session.api.param.AttemptSessionObjParam;
import com.javastudy.components.attempt_session.api.service.AttemptSessionService;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.my.util.security.role.RoleUtil;

import lombok.AllArgsConstructor;

/**
 * ADMIN_AttemptSessionController
 * 目的: TODO
 *
 * 公開契約:
 * - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * 備考:
 * - DTO は record を用いる
 */
@Controller
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@AllArgsConstructor
public class ADMIN_AttemptSessionController {
	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [field] START ===== */
	private final AttemptSessionService sessionService;
	/* ===== [field] END ===== */

	/* ===== [public/protected] START ===== */
	/** 設定ホーム（一覧など） */
	@GetMapping(AppPath.ADMIN_ATTEMPT_SESSION)
	public String getSetting(final Model model) {
		final List<ADMIN_AttemptSessionViewDto> sessionViewDtoList = this.sessionService
			.getAdminViewDtoList();
		model.addAttribute(AttemptSessionObjParam.VIEW_DTO_LIST, sessionViewDtoList);
		return TempPath.ADMIN_ATTEMPT_SESSION;
	}
	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/* ===== [private] END ===== */
}
