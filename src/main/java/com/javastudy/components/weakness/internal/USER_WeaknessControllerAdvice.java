/*
 * USER_WeaknessControllerAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.weakness.internal
 * Author  : shu-kundeath
 * Created : 2025/10/23 17:20:05
 *
 * 目的:
 * - 弱点分析画面でパンくず用の参考書 ViewDto を投入
 *
 * 注意:
 * - 文字列定数は private static final String を用いる（本クラスでは未使用）
 * - import は明示指定（ワイルドカード禁止）
 * - クラスは AOP 方針により public 非final
 */

package com.javastudy.components.weakness.internal;

/* ===== [import] START ===== */
import com.javastudy.components.sankou_books.api.param.SankouBooksObjParam;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.util.path.AppPath;
import com.util.security.role.RoleUtil;
import com.util.type.MyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

/* ===== [import] END ===== */

/**
 * <|diff_marker|> ADD A1480 USER_WeaknessControllerAdvice
 *
 * <p>
 * 目的: - パスの bookViewId を基に参考書 ViewDto を Model へ設定
 *
 * <p>
 * 責務: - bookViewId が空なら null、あれば Service から取得
 *
 * <p>
 * 公開契約: - 例外ハンドリングは GlobalAppExceptionAdvice で集約
 */
@ControllerAdvice(assignableTypes = USER_WeaknessController.class)
@RequiredArgsConstructor
@Order(20)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_WeaknessControllerAdvice { // public 非final（AOP）

	/* ===== [private] START ===== */
	private final SankouBooksService sankouBooksService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	@ModelAttribute
	public void addBookDto(
		final Model model,
		@PathVariable(value = AppPath.PARAM_BOOK_ID, required = false) final String bookViewId) {

		if (MyType.isBlank(bookViewId)) {
			model.addAttribute(SankouBooksObjParam.VIEW_DTO, null);
			return;
		}
		model.addAttribute(
			SankouBooksObjParam.VIEW_DTO,
			sankouBooksService.getUserViewDtoByViewId(bookViewId));
	}
	/* ===== [public/protected] END ===== */
}
