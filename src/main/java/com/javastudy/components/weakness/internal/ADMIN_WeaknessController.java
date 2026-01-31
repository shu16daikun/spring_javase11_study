/*
 * ADMIN_WeaknessController.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.weakness.internal
 * Author  : shu-kundeath
 * Created : 2025/10/29 19:19:48
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.weakness.internal;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.javastudy.components.weakness.api.dto.ADMIN_WeaknessViewDto;
import com.javastudy.components.weakness.api.param.WeaknessObjParam;
import com.javastudy.components.weakness.api.service.WeaknessService;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.util.security.role.RoleUtil;

import lombok.AllArgsConstructor;

/**
 * ADMIN_WeaknessController
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
public class ADMIN_WeaknessController {
	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [field] START ===== */
	private final WeaknessService weaknessService;
	/* ===== [field] END ===== */

	/* ===== [public/protected] START ===== */
	/** 設定ホーム（一覧など） */
	@GetMapping(AppPath.ADMIN_WEAKNESS)
	public String getSetting(final Model model) {
		final List<ADMIN_WeaknessViewDto> weaknessViewDtoList = this.weaknessService
			.getAdminViewDtoList();
		model.addAttribute(WeaknessObjParam.VIEW_DTO_LIST, weaknessViewDtoList);
		return TempPath.ADMIN_WEAKNESS;
	}
	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/* ===== [private] END ===== */
}
