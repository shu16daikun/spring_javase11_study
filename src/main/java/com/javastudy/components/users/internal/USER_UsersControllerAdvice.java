/*
 * USER_UsersControllerAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.users.internal
 * Author  : shu-kundeath
 * Created : 2025/10/23 17:20:04
 *
 * 目的:
 * - アカウント設定画面の Binder 設定／初期 Form 投入／Validation 追加
 *
 * 注意:
 * - 文字列定数は private static final String を用いる（本クラスでは未使用）
 * - import は明示指定（ワイルドカード禁止）
 * - クラスは AOP 方針により public 非final
 */

package com.javastudy.components.users.internal;

import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.login.components.user.api.service.MyUsersService;
import com.my.util.security.role.RoleUtil;

import lombok.RequiredArgsConstructor;

/* ===== [import] END ===== */

/**
 * USER_UsersControllerAdvice
 *
 * <p>
 * 目的: - UsersForm への Validator 適用／初期 Form の組み立て - Validation メッセージの“追加分”投入
 *
 * <p>
 * 責務: - UsersForm 未設定時のみ、ログインユーザーから Form を構築 - PropKey.VALIDATION_MESSAGES に
 * USERNAME 系の Key を追記
 *
 * <p>
 * 公開契約: - 例外ハンドリングは GlobalAppExceptionAdvice で集約 <|diff_marker|> ADD A1400
 */
@ControllerAdvice(assignableTypes = USER_UsersController.class)
@RequiredArgsConstructor
@Order(10)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_UsersControllerAdvice { // public 非final（AOP）

	/* ===== [private] START ===== */
	private final USER_UsersFormValidator usersFormValidator;
	private final MyUsersService loginUserService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	@InitBinder(UsersFormParam.FORM)
	public void initBinder(final WebDataBinder binder) {
		binder.addValidators(this.usersFormValidator);
	}

	@ModelAttribute
	public void addUsersForm(final Model model) {
		if (!model.containsAttribute(UsersFormParam.FORM)) {
			model.addAttribute(
				UsersFormParam.FORM,
				USER_UsersForm.fromViewDto(this.loginUserService.getLoginUser()));
		}
	}

	/* ===== [public/protected] END ===== */
}
