/*
 * USER_SankouBooksControllerAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_books.internal
 * Author  : shu-kundeath
<|diff_marker|> ADD A1280
 * Created : 2025/10/23 17:20:03
 *
 * 目的:
 * - 参考書画面の初期表示データ投入
 *
 * 注意:
 * - 文字列定数は private static final String を用いる（本クラスでは未使用）
 * - import は明示指定（ワイルドカード禁止）
 * - クラスは AOP 方針により public 非final
 */

package com.javastudy.components.sankou_books.internal;

/* ===== [import] START ===== */
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.param.SankouBooksObjParam;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.my.util.security.role.RoleUtil;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/* ===== [import] END ===== */

/**
 * USER_SankouBooksControllerAdvice
 *
 * <p>
 * 目的: - 参考書一覧の ViewDto を Model に投入
 *
 * <p>
 * 責務: - SankouBooksService#getViewDtoList() の結果を VIEW_DTO_LIST へ設定
 *
 * <p>
 * 公開契約: <|diff_marker|> ADD A1320 - 例外ハンドリングは GlobalAppExceptionAdvice で集約
 */
@ControllerAdvice(assignableTypes = USER_SankouBooksController.class)
@RequiredArgsConstructor
@Order(10)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_SankouBooksControllerAdvice { // public 非final（AOP）

	/* ===== [private] START ===== */
	private final SankouBooksService sankouBooksService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	@ModelAttribute
	public void addCommonModel(final Model model) {
		final List<USER_SankouBooksViewDto> list = sankouBooksService.getUserViewDtoList();
		model.addAttribute(SankouBooksObjParam.VIEW_DTO_LIST, list);
	}
	/* ===== [public/protected] END ===== */
}
