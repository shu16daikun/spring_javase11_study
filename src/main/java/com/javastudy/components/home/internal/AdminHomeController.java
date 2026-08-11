package com.javastudy.components.home.internal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.my.util.security.role.RoleUtil;

import lombok.AllArgsConstructor;

/* 機能：管理者トップ */
@Controller
@AllArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
public class AdminHomeController {

	/* 機能：ホーム */
	@GetMapping(AppPath.ADMIN_ROOT)
	public String getHome() {
		return TempPath.ADMIN;
	}
}
