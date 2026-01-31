/*
 * PasswordSetControllerAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.login_module.password_set.internal
 * Author  : shu-kundeath
 * Created : 2025/10/23 17:20:06
 *
 * 目的:
 * - パスワード設定画面の Binder 設定／Form/DTO 初期化／Validation 追加
 *
 * 注意:
 * - 文字列定数は private static final String を用いる（本クラスでは未使用）
 * - import は明示指定（ワイルドカード禁止）
 * - クラスは AOP 方針により public 非final
 */

package com.javastudy.components.login.password_set.internal;

import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.login.components.user.api.param.MyPasswordSetObjParam;
import com.login.components.user.api.service.MyPasswordSetService;
import com.login.components.user.api.service.MyUsersService;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

/* ===== [import] END ===== */

/**
 * PasswordSetControllerAdvice
 *
 * <p>
 * 目的: - 初回パスワード設定の画面用 Model 構築（Form/DTO）と Validation 追加
 *
 * <p>
 * 責務: - Binder へ PasswordSetFormValidator を登録 - PRG 後の GET で Form/BR が無い場合のみ新規
 * Form を投入 -
 * UsersViewDto を基に表示用 ViewDto を作成 - Validation の PASSWORD 系 Key を追記
 *
 * <p>
 * 公開契約: - 例外ハンドリングは GlobalAppExceptionAdvice で集約
 */
@ControllerAdvice(assignableTypes = PasswordSetController.class)
@RequiredArgsConstructor
@Order(10)
public class PasswordSetControllerAdvice { // public 非final（AOP）

	/* ===== [private] START ===== */
	private final PasswordSetFormValidator validator;
	private final ValidationMessageUtil msg;
	private final MyUsersService myUsersService;
	private final MyPasswordSetService passSetService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	@InitBinder(PasswordSetFormParam.FORM)
	public void initBinder(final WebDataBinder binder) {
		binder.addValidators(validator);
	}

	@ModelAttribute
	public void addFormAndDto(final Model model) {
		final String brKey = BindingResult.MODEL_KEY_PREFIX + PasswordSetFormParam.FORM;
		final boolean hasForm = model.containsAttribute(PasswordSetFormParam.FORM);
		final boolean hasBR = model.containsAttribute(brKey);

		if (!hasForm && !hasBR) {
			model.addAttribute(PasswordSetFormParam.FORM, new PasswordSetForm());
		}
		final MyUsersViewDto loginUserViewDto = myUsersService.getLoginUser();
		model.addAttribute(
			MyPasswordSetObjParam.VIEW_DTO,
			passSetService.getViewDtoFromUsersDto(loginUserViewDto));
	}

	@ModelAttribute
	public void addValidationMessages(final Model model) {
		@SuppressWarnings("unchecked")
		final Map<String, String> base = (Map<String, String>) model
			.getAttribute(PropKey.VALIDATION_MESSAGES);
		final Map<String, String> vm = (base != null)
			? base
			: new LinkedHashMap<>(msg.setModelValidationMessages());
		vm.put(ErrorProp.USER_PASSWORD_MISMATCH, msg.getMessage(ErrorProp.USER_PASSWORD_MISMATCH));
		vm.put(ErrorProp.USER_PASSWORD_PATTERN, msg.getMessage(ErrorProp.USER_PASSWORD_PATTERN));
		vm.put(RegexProp.PASSWORD, msg.getMessage(RegexProp.PASSWORD));
		model.addAttribute(PropKey.VALIDATION_MESSAGES, vm);
	}
	/* ===== [public/protected] END ===== */
}
