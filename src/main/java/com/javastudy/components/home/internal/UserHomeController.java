package com.javastudy.components.home.internal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.util.security.role.RoleUtil;

/* 機能：ユーザートップ */
@Controller
@RequestMapping(AppPath.USER_ROOT)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class UserHomeController {

	/* 機能：ホーム */
	@GetMapping
	public String getHome() {
		return TempPath.USER;
	}
}
