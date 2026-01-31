/*
 * USER_AnswerRecordsControllerAdvice.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.answer_records.internal
 * Author  : shu-kundeath
 * Created : 2025/10/23 17:20:01
 *
 * 目的:
 * - 解答履歴画面向けの共通処理（Validation 追加／パンくず DTO／FormList 初期化／Sticky 復元）
 *
 * 注意:
 * - 文字列定数は private static final String を用いる（本クラスでは未使用）
 * - import は明示指定（ワイルドカード禁止）
 * - クラスは AOP 方針により public 非final
 */

package com.javastudy.components.answer_records.internal;

import com.javastudy.components.chapter.api.param.ChapterObjParam;
import com.javastudy.components.chapter.api.service.ChapterService;
import com.javastudy.components.kurohon_questions.api.dto.USER_KurohonQuestionsViewDto;
import com.javastudy.components.kurohon_questions.api.param.KurohonQuestionsObjParam;
import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsService;
import com.javastudy.components.sankou_books.api.param.SankouBooksObjParam;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.path.AppPath;
import com.util.security.role.RoleUtil;
import com.util.type.MyType;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

/* ===== [import] END ===== */

/**
 * USER_AnswerRecordsControllerAdvice
 *
 * <p>
 * 目的: - 解答履歴 UI の初期化(パンくず/問題一覧→FormList)と Validation メッセージ追加
 *
 * <p>
 * 責務: - Form/FormList の Validator 適用 - 参考書/章の ViewDto と、問題→FormList 変換を Model
 * へ投入 - Sticky な
 * ERROR_MESSAGE をセッションから復元
 *
 * <p>
 * 公開契約: - 例外ハンドリングは GlobalAppExceptionAdvice で集約
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@ControllerAdvice(assignableTypes = {
	USER_AnswerRecordsController.class
})
@RequiredArgsConstructor
@Order(20)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_AnswerRecordsControllerAdvice { // public 非final（AOP）

	private final USER_AnswerRecordsFormValidator formValidator;
	private final USER_AnswerRecordsFormListValidator formListValidator;
	private final SankouBooksService sankouBooksService;
	private final ChapterService chapterService;
	private final KurohonQuestionsService kurohonQuestionsService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	@InitBinder(AnswerRecordsFormParam.FORM)
	public void initBinderForm(final WebDataBinder binder) {
		final Object target = binder.getTarget();
		if (MyType.isNotNull(target) && this.formValidator.supports(target.getClass())) {
			binder.addValidators(this.formValidator);
		}
	}

	@InitBinder(AnswerRecordsFormParam.FORM_LIST)
	public void initBinderFormList(final WebDataBinder binder) {
		final Object target = binder.getTarget();
		if (MyType.isNotNull(target) && this.formListValidator.supports(target.getClass())) {
			binder.addValidators(this.formListValidator);
		}
	}

	/** 参考書/章の ViewDto（パンくず用）。 */
	@ModelAttribute
	public void addDtos(
		final Model model,
		@PathVariable(value = AppPath.PARAM_BOOK_ID, required = false) final String bookViewId,
		@PathVariable(value = AppPath.PARAM_CHAPTER_NO, required = false) final String chapterNo) {
		if (MyType.isBlank(bookViewId) || MyType.isBlank(chapterNo)) {
			model.addAttribute(SankouBooksObjParam.VIEW_DTO, null);
			model.addAttribute(ChapterObjParam.VIEW_DTO, null);
			return;
		}
		model.addAttribute(
			SankouBooksObjParam.VIEW_DTO,
			this.sankouBooksService.getUserViewDtoByViewId(bookViewId));
		model.addAttribute(
			ChapterObjParam.VIEW_DTO,
			this.chapterService.getUserViewDtoByNo(bookViewId, chapterNo));
	}

	/** 問題一覧→FormList（未設定なら構築）。 */
	@ModelAttribute
	public void addQuestionsAndFormList(
		final Model model,
		@PathVariable(value = AppPath.PARAM_BOOK_ID, required = false) final String bookViewId,
		@PathVariable(value = AppPath.PARAM_CHAPTER_NO, required = false) final String chapterNo) {
		if (MyType.isBlank(bookViewId) || MyType.isBlank(chapterNo)) {
			model.addAttribute(KurohonQuestionsObjParam.VIEW_DTO_LIST, List.of());
			return;
		}
		final List<USER_KurohonQuestionsViewDto> questions = this.kurohonQuestionsService
			.getUserViewDtoListFilter(bookViewId, chapterNo);
		model.addAttribute(KurohonQuestionsObjParam.VIEW_DTO_LIST, questions);

		if (!model.containsAttribute(AnswerRecordsFormParam.FORM_LIST)) {
			final List<USER_AnswerRecordsForm> forms = questions.stream()
				.map(USER_AnswerRecordsForm::fromViewDto).toList();
			model.addAttribute(AnswerRecordsFormParam.FORM_LIST,
				new USER_AnswerRecordsFormList(forms));
		}
	}

	/** Sticky な ERROR_MESSAGE の復元。 */
	@ModelAttribute
	public void restoreStickyError(final Model model, final HttpSession session) {
		if (model.containsAttribute(PropKey.ERROR_MESSAGE)) {
			return;
		}
		final Object sticky = session.getAttribute(PropKey.ERROR_MESSAGE);
		if (sticky instanceof final String s && MyType.isNotBlank(s)) {
			model.addAttribute(PropKey.ERROR_MESSAGE, s);
		}
	}
	/* ===== [public/protected] END ===== */
}
