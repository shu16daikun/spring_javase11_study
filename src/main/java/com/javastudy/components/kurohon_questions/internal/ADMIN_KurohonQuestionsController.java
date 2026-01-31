/*
 * ADMIN_KurohonQuestionsController.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.kurohon_questions.internal
 * Author  : shu-kundeath
 *
 * 目的:
 * - KurohonQuestions の管理画面（設定ホーム／追加／更新）と削除・更新 POST の入口（最小）
 */

package com.javastudy.components.kurohon_questions.internal;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exception.util.param.MyExceptionParam;
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.chapter.api.param.ChapterObjParam;
import com.javastudy.components.chapter.api.service.ChapterService;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.kurohon_questions.api.param.KurohonQuestionsObjParam;
import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsService;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.param.SankouBooksObjParam;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.util.security.browser_guard.BrowserGuard;
import com.util.security.role.RoleUtil;
import com.util.type.MyConst;
import com.util.type.MyType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@AllArgsConstructor
public class ADMIN_KurohonQuestionsController {

	private final KurohonQuestionsService kurohonQuestionsService;
	private final SankouBooksService sankouBooksService;
	private final ChapterService chapterService;
	private final ValidationMessageUtil msg;

	/* ===== [public/protected] START ===== */

	/** 設定ホーム（一覧など） */
	@GetMapping(AppPath.ADMIN_KUROHON_QUESTIONS)
	public String getSetting(final Model model) {
		final List<ADMIN_KurohonQuestionsViewDto> viewList = this.kurohonQuestionsService
			.getAdminViewDtoList();
		model.addAttribute(KurohonQuestionsObjParam.VIEW_DTO_LIST, viewList);
		return TempPath.ADMIN_KUROHON_QUESTIONS;
	}

	/* ---------- 追加画面：エントリ（/insert/browser-guard） ---------- */

	/**
	 * 追加画面へのエントリポイント。
	 * POST(/admin/kurohonQuestions/insert/browser-guard)
	 * → redirect:/admin/kurohonQuestions/insert
	 */
	@PostMapping(AppPath.ADMIN_KUROHON_QUESTIONS_INSERT_ENTRY)
	public String postInsertEntry(final RedirectAttributes redirect) {
		// 正常フローとして OK を付与
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_KUROHON_QUESTIONS_INSERT;
	}

	/** 追加画面（GET） */
	@GetMapping(AppPath.ADMIN_KUROHON_QUESTIONS_INSERT)
	public String getInsert(final Model model, final RedirectAttributes redirect) {

		// BrowserGuard チェック（直叩き / 戻る・進む）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		// 2) OK 以外（NONE 含む）は不正フローとして一覧へ退避
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN;
		}

		// Form 未設定なら新規
		if (!model.containsAttribute(KurohonQuestionsFormParam.FORM)) {
			model.addAttribute(KurohonQuestionsFormParam.FORM, new ADMIN_KurohonQuestionsForm());
		}
		// select 用一覧（Flash にあれば優先）
		if (!model.containsAttribute(SankouBooksObjParam.VIEW_DTO_LIST)) {
			model.addAttribute(
				SankouBooksObjParam.VIEW_DTO_LIST,
				this.sankouBooksService.getAdminViewDtoList());
		}
		if (!model.containsAttribute(ChapterObjParam.VIEW_DTO_LIST)) {
			model.addAttribute(
				ChapterObjParam.VIEW_DTO_LIST,
				this.chapterService.getAdminViewDtoList());
		}

		// 正常表示なので OK を前面に
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return TempPath.ADMIN_KUROHON_QUESTIONS_INSERT;
	}

	/** 追加（POST）→ 成功: 設定ホーム / 失敗: 入力復元してGETへ */
	@PostMapping(AppPath.ADMIN_KUROHON_QUESTIONS_INSERT)
	public String postInsert(
		@ModelAttribute(KurohonQuestionsFormParam.FORM) @Valid final ADMIN_KurohonQuestionsForm form,
		final BindingResult result,
		@ModelAttribute(SankouBooksObjParam.VIEW_DTO_LIST) final List<ADMIN_SankouBooksViewDto> sankouBooksViewDtoList,
		@ModelAttribute(ChapterObjParam.VIEW_DTO_LIST) final List<ADMIN_ChapterViewDto> chapterViewDtoList,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		if (result.hasErrors()) {
			// エラーメッセージを Sticky（セッション）へ
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			// 入力維持 + セレクト再描画用一覧
			redirect.addFlashAttribute(KurohonQuestionsFormParam.FORM, form);
			redirect.addFlashAttribute(SankouBooksObjParam.VIEW_DTO_LIST, sankouBooksViewDtoList);
			redirect.addFlashAttribute(ChapterObjParam.VIEW_DTO_LIST, chapterViewDtoList);
			// フロー自体は正しいので OK として戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			// PRG
			return AppPath.R_ADMIN_KUROHON_QUESTIONS_INSERT;
		}

		this.kurohonQuestionsService.create(form.toCreateInputDto());
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		// 設定ホーム側も通常フローとして OK に寄せておく
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_KUROHON_QUESTIONS;
	}

	/* ---------- 更新画面：エントリ（/{id}/browser-guard） ---------- */

	/**
	 * 更新画面へのエントリポイント。
	 * POST(/admin/kurohonQuestions/{kurohon_questions_view_id}/browser-guard)
	 * → redirect:/admin/kurohonQuestions/{kurohon_questions_view_id}
	 */
	@PostMapping(AppPath.ADMIN_KUROHON_QUESTIONS_ID_ENTRY)
	public String postUpdateEntry(
		@PathVariable(AppPath.PARAM_KUROHON_QUESTIONS_VIEW_ID) final String viewId,
		final RedirectAttributes redirect) {

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.rAdminKurohonQuestionsUpdate(viewId);
	}

	/** 更新画面（GET, 詳細） */
	@GetMapping(AppPath.ADMIN_KUROHON_QUESTIONS_ID)
	public String getUpdate(
		@PathVariable(AppPath.PARAM_KUROHON_QUESTIONS_VIEW_ID) final String viewId,
		final Model model,
		final RedirectAttributes redirect) {

		// BrowserGuard チェック（直叩き / 戻る・進む）
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		// 2) OK 以外（NONE 含む）は不正フローとして一覧へ退避
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_ADMIN;
		}

		// Form 未設定（初回表示）のときだけ ViewDto から生成
		if (!model.containsAttribute(KurohonQuestionsFormParam.FORM)) {
			final ADMIN_KurohonQuestionsViewDto v = this.kurohonQuestionsService
				.getAdminViewDtoByViewId(viewId);
			model.addAttribute(
				KurohonQuestionsFormParam.FORM,
				ADMIN_KurohonQuestionsForm.fromViewDto(v));
		}
		// select 用一覧
		if (!model.containsAttribute(SankouBooksObjParam.VIEW_DTO_LIST)) {
			model.addAttribute(
				SankouBooksObjParam.VIEW_DTO_LIST,
				this.sankouBooksService.getAdminViewDtoList());
		}
		if (!model.containsAttribute(ChapterObjParam.VIEW_DTO_LIST)) {
			model.addAttribute(
				ChapterObjParam.VIEW_DTO_LIST,
				this.chapterService.getAdminViewDtoList());
		}

		// viewId（テンプレ用）
		model.addAttribute(AppPath.PARAM_KUROHON_QUESTIONS_VIEW_ID, viewId);
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return TempPath.ADMIN_KUROHON_QUESTIONS_UPDATE;
	}

	/** 更新（POST）→ 成功: 設定ホーム / 失敗: 入力復元してGETへ */
	@PostMapping(AppPath.ADMIN_KUROHON_QUESTIONS_UPDATE)
	public String postUpdate(
		@PathVariable(AppPath.PARAM_KUROHON_QUESTIONS_VIEW_ID) final String viewId,
		@ModelAttribute(KurohonQuestionsFormParam.FORM) @Valid final ADMIN_KurohonQuestionsForm form,
		final BindingResult result,
		@ModelAttribute(SankouBooksObjParam.VIEW_DTO_LIST) final List<ADMIN_SankouBooksViewDto> sankouBooksViewDtoList,
		@ModelAttribute(ChapterObjParam.VIEW_DTO_LIST) final List<ADMIN_ChapterViewDto> chapterViewDtoList,
		final HttpSession session,
		final RedirectAttributes redirect,
		final Model model) {

		if (result.hasErrors()) {
			session.setAttribute(PropKey.ERROR_MESSAGE, this.toOneLineMessage(result));
			redirect.addFlashAttribute(KurohonQuestionsFormParam.FORM, form);
			// 再描画時にセレクトが空にならないよう一覧も付与
			redirect.addFlashAttribute(SankouBooksObjParam.VIEW_DTO_LIST, sankouBooksViewDtoList);
			redirect.addFlashAttribute(ChapterObjParam.VIEW_DTO_LIST, chapterViewDtoList);
			// フローは正常なので OK で戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.rAdminKurohonQuestionsUpdate(viewId);
		}

		this.kurohonQuestionsService.update(form.toUpdateInputDto(viewId));
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.R_ADMIN_KUROHON_QUESTIONS;
	}

	/** 削除（POST）→ 設定ホーム */
	@PostMapping(AppPath.ADMIN_KUROHON_QUESTIONS_DELETE)
	public String postDelete(
		@PathVariable(AppPath.PARAM_KUROHON_QUESTIONS_VIEW_ID) final String viewId,
		final HttpSession session) {
		this.kurohonQuestionsService.deleteByViewId(viewId);
		session.removeAttribute(PropKey.ERROR_MESSAGE);
		return AppPath.R_ADMIN_KUROHON_QUESTIONS;
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/** BindingResult → 代表メッセージ（重複排除 / 連結） */
	private String toOneLineMessage(final BindingResult result) {
		return result.getAllErrors().stream()
			.map(ObjectError::getDefaultMessage) // 例: "error.common.max"
			.map(this.msg::getMessage) // -> 実メッセージへ解決
			.distinct()
			.collect(Collectors.joining(" ／ "));
	}
	/* ===== [private] END ===== */
}
