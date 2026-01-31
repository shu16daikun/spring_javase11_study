package com.javastudy.components.chapter.internal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javastudy.components.chapter.api.param.ChapterObjParam;
import com.javastudy.components.chapter.api.service.ChapterService;
import com.javastudy.components.sankou_books.api.param.SankouBooksObjParam;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.util.security.role.RoleUtil;

import lombok.AllArgsConstructor;

/* 機能：章（ユーザー） */
@Controller
@AllArgsConstructor
@RequestMapping(AppPath.USER_SANKOU_BOOKS)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_ChapterController {

	/* 機能：依存 */
	private final ChapterService chapterService;
	private final SankouBooksService sankouBooksService;

	/* 機能：章一覧表示 */
	@GetMapping(AppPath.BOOK_ID)
	public String getChapter(
		final Model model,
		@PathVariable(AppPath.PARAM_BOOK_ID) final String bookViewId) {
		model.addAttribute(
			ChapterObjParam.VIEW_DTO_LIST,
			this.chapterService.getUserViewDtoListFilterSankouBooks(bookViewId));
		model.addAttribute(
			SankouBooksObjParam.VIEW_DTO,
			this.sankouBooksService.getUserViewDtoByViewId(bookViewId));
		return TempPath.CHAPTER;
	}
}
