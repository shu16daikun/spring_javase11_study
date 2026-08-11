package com.javastudy.components.sankou_books.internal;

import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.my.util.security.role.RoleUtil;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/* 機能：参考書（ユーザー） */
@Controller
@AllArgsConstructor
@RequestMapping(AppPath.USER_ROOT)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_SankouBooksController {

	/* 画面：一覧 */
	@GetMapping(AppPath.SANKOU_BOOKS)
	public String getSankouBooks() {
		return TempPath.SANKOU_BOOKS;
	}
}
