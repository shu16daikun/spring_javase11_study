package com.javastudy.components.answer_records.internal;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exception.util.param.MyExceptionParam;
import com.javastudy.components.answer_records.api.dto.USER_AnswerRecordsStatsViewDto;
import com.javastudy.components.answer_records.api.param.AnswerRecordsObjParam;
import com.javastudy.components.answer_records.api.service.AnswerRecordsService;
import com.javastudy.components.attempt_session.api.dto.USER_AttemptSessionViewDto;
import com.javastudy.components.attempt_session.api.param.AttemptSessionObjParam;
import com.javastudy.components.attempt_session.api.service.AttemptSessionService;
import com.javastudy.components.chapter.api.param.ChapterObjParam;
import com.javastudy.components.chapter.api.service.ChapterService;
import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsService;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.param.SankouBooksObjParam;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.validation_messages.ValidationMessageUtil;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.login.components.user.api.service.MyUsersService;
import com.util.security.browser_guard.BrowserGuard;
import com.util.security.role.RoleUtil;
import com.util.type.MyConst;
import com.util.type.MyType;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping(AppPath.USER_SANKOU_BOOKS_ID)
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_AnswerRecordsController {

	/** Spring Validation のコードをプロジェクトのキーへ寄せる小ヘルパ */
	private static String mapToProjectCode(final String springCode) {
		if (springCode == null || springCode.isBlank()) {
			return null;
		}
		return switch (springCode) {
		case "NotBlank", "NotNull", "NotEmpty" -> ErrorProp.COMMON_NOT_BLANK;
		case "Size" -> ErrorProp.COMMON_SIZE;
		// 必要に応じて追加
		default -> springCode.startsWith("error.") ? springCode : null;
		};
	}

	private final SankouBooksService sankouBooksService;
	private final AnswerRecordsService answerRecordsService;
	private final AttemptSessionService attemptSessionService;
	private final ChapterService chapterService;
	private final KurohonQuestionsService questionsService;
	private final MyUsersService usersService;

	private final ValidationMessageUtil msg;

	// ** ★ フィールドエラーを「問{no} …」に整形（改行区切り） */
	private List<String> buildFieldErrorMessages(
		final USER_AnswerRecordsFormList formList,
		final BindingResult result) {

		final var idxPattern = Pattern.compile("\\[(\\d+)]"); // e.g. forms[3].selectedOption
		final var ordered = new LinkedHashSet<String>(); // 重複排除＋順序保持

		result.getFieldErrors().forEach(fe -> {
			// 配列indexを取り出す
			int idx = -1;
			final var m = idxPattern.matcher(fe.getField());
			if (m.find()) {
				try {
					idx = Integer.parseInt(m.group(1));
				} catch (final NumberFormatException ignore) {
					// noop
				}
			}

			// 問番号を取得（安全側フォールバック）
			String qno = "?";
			if (idx >= 0 && idx < formList.getFormItems().size()) {
				final String qViewId = formList.getFormItems().get(idx).getQuestionViewId();
				final var qDto = questionsService.getUserViewDtoByViewId(qViewId);
				if (qDto != null && qDto.questionNo() != null) {
					qno = qDto.questionNo();
				}
			}

			// Springコード→プロジェクトキー（error.common.*）に寄せる
			final String springCode = fe.getCode(); // NotBlank/Size/…
			final String projectCode = mapToProjectCode(springCode);

			// 文言解決（{0}に「問{qno}」を入れる想定）
			final String text = (projectCode != null)
				? msg.getMessageWithLabel("問" + qno, projectCode) // error.common.notblank 等
				: "問"
					+ qno
					+ " "
					+ ((fe.getDefaultMessage() == null || fe.getDefaultMessage().isBlank())
						? "入力に誤りがあります。"
						: fe.getDefaultMessage());

			ordered.add(text);
		});

		if (ordered.isEmpty()) {
			return List.of("入力に誤りがあります。");
		}
		return new ArrayList<>(ordered).stream().sorted().collect(Collectors.toList());
	}

	/* ========== [章解答画面：BrowserGuard エントリ] ========== */

	/**
	 * 章解答画面へのエントリポイント。
	 * POST(/user/sankouBooks/{book_view_id}/{chapter_no}/browser-guard)
	 * → redirect:/user/sankouBooks/{book_view_id}/{chapter_no}
	 */
	@PostMapping(AppPath.CHAPTER_ENTRY)
	public String postChapterEntry(
		@PathVariable(AppPath.PARAM_BOOK_ID) final String bookViewId,
		@PathVariable(AppPath.PARAM_CHAPTER_NO) final String chapterNo,
		final RedirectAttributes redirect) {

		// 正常フローとして OK を付与
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.rChapter(bookViewId, chapterNo);
	}

	/* 参考書有り解答表示（attempt_session の開始/継続） */
	@GetMapping(AppPath.CHAPTER_NO)
	public String getChapter(
		final Model model,
		final HttpSession session,
		final RedirectAttributes redirect,
		@PathVariable(AppPath.PARAM_BOOK_ID) final String bookViewId,
		@PathVariable(AppPath.PARAM_CHAPTER_NO) final String chapterNo) {

		// 0) BrowserGuard チェック
		final Object rawGuard = model.getAttribute(BrowserGuard.PARAM);
		final String guardCode = BrowserGuard.resolveCode(rawGuard);
		if (MyType.isNotEqual(guardCode, BrowserGuard.ok())) {
			// 直接叩き・戻る／進むなどは書籍トップへ退避
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_USER;
		}

		// attempt_session の開始/継続だけやる（ViewDto/FormsはAdviceが投入済み）
		String asViewId = (String) session.getAttribute(AttemptSessionObjParam.VIEW_ID);
		if (asViewId == null) {
			final MyUsersViewDto loginUser = usersService.getLoginUser();
			final USER_SankouBooksViewDto vBook = (USER_SankouBooksViewDto) model
				.getAttribute(SankouBooksObjParam.VIEW_DTO);
			final USER_AttemptSessionViewDto as = attemptSessionService.createNew(loginUser, vBook);
			asViewId = as.viewId();
			session.setAttribute(AttemptSessionObjParam.VIEW_ID, asViewId);
		} else {
			// 存在チェック（無ければ例外 → Advice 側でエラー画面へ）
			attemptSessionService.getUserViewDtoByViewId(asViewId);
		}

		// ★ 次章の存在可否をモデルに積む
		final String nextNo = String.format("%02d", Integer.parseInt(chapterNo) + 1);
		final boolean hasNextChapter = chapterService.existsByNo(bookViewId, nextNo);
		model.addAttribute(ChapterObjParam.HAS_NEXT_CHAPTER, hasNextChapter);

		// 正常表示の場合は OK を前面に出しておく（フロントJS用）
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());

		return TempPath.ANSWER;
	}

	/* 結果表示（章→問題 昇順）／表示後は Session の attempt_session は除去 */
	@GetMapping(AppPath.RESULT)
	public String getResult(
		final HttpSession session,
		final Model model,
		final RedirectAttributes redirect,
		@PathVariable(AppPath.PARAM_BOOK_ID) final String bookViewId,
		@PathVariable(AppPath.PARAM_CHAPTER_NO) final String chapterNo) {

		final String asViewId = (String) session.getAttribute(AttemptSessionObjParam.VIEW_ID);
		if (asViewId == null) {
			// attemptSession 無しは不正フロー寄りなので invalidFlow に寄せて章へ戻す
			redirect.addFlashAttribute(MyExceptionParam.ALERT_DANGER, MyConst.TRUE);
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.invalidFlow());
			return AppPath.R_USER;
		}

		// 一覧
		model.addAttribute(
			AnswerRecordsObjParam.VIEW_DTO_LIST,
			answerRecordsService.getUserViewDtoListByAttemptSession(asViewId));

		// ★ 集計
		final USER_AnswerRecordsStatsViewDto statsViewDto = answerRecordsService
			.getStatsByAttemptSession(asViewId);
		model.addAttribute(AnswerRecordsObjParam.STATS_VIEW_DTO, statsViewDto);

		// 結果画面自体は「閲覧」なので、一旦 OK に寄せておく
		model.addAttribute(BrowserGuard.PARAM, BrowserGuard.ok());

		// セッションは結果表示後に破棄
		session.removeAttribute(AttemptSessionObjParam.VIEW_ID);
		return TempPath.RESULT;
	}

	@PostMapping(AppPath.ANSWER_FINISH)
	public String postFinish(
		final HttpSession session,
		@PathVariable(AppPath.PARAM_BOOK_ID) final String bookViewId,
		@PathVariable(AppPath.PARAM_CHAPTER_NO) final String chapterNo,
		@Valid @ModelAttribute(AnswerRecordsFormParam.FORM_LIST) final USER_AnswerRecordsFormList formList,
		final BindingResult result,
		final Model model,
		final RedirectAttributes redirect) {

		if (result.hasErrors()) {
			// hasErrors() 時（next/finish 共通）
			final List<String> messages = buildFieldErrorMessages(formList, result);
			// PRG用（1回表示）
			redirect.addFlashAttribute(PropKey.ERROR_MESSAGE, messages);
			// 粘着用（再GETやリロードでも残す）
			session.setAttribute(PropKey.ERROR_MESSAGE, messages);
			// 入力復元
			redirect.addFlashAttribute(AnswerRecordsFormParam.FORM_LIST, formList);
			// フローとしては正しいので OK に寄せて戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.rChapter(bookViewId, chapterNo);
		}

		// attemptSession 確保
		String asViewId = (String) session.getAttribute(AttemptSessionObjParam.VIEW_ID);
		if (asViewId == null) {
			final USER_SankouBooksViewDto vBook = sankouBooksService
				.getUserViewDtoByViewId(bookViewId);
			final String newAsViewId = attemptSessionService
				.createNew(usersService.getLoginUser(), vBook)
				.viewId();
			session.setAttribute(AttemptSessionObjParam.VIEW_ID, newAsViewId);
			asViewId = newAsViewId;
		}
		final String ensuredAsViewId = (String) session
			.getAttribute(AttemptSessionObjParam.VIEW_ID);

		answerRecordsService.saveAllWithAttempt(ensuredAsViewId, formList.toInputDto());
		attemptSessionService.atFinished(ensuredAsViewId);

		// ★成功したのでメッセージを掃除
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		// 結果画面へも「正常フロー」として OK を持っていく
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.rResult(bookViewId, chapterNo);
	}

	/* 次章へ（保存＋attempt_session 継続） */
	@PostMapping(AppPath.NEXT_CHAPTER)
	public String postNextChapter(
		final Model model,
		final HttpSession session,
		@PathVariable(AppPath.PARAM_BOOK_ID) final String bookViewId,
		@PathVariable(AppPath.PARAM_CHAPTER_NO) final String chapterNo,
		@Valid @ModelAttribute(AnswerRecordsFormParam.FORM_LIST) final USER_AnswerRecordsFormList formList,
		final BindingResult result,
		final RedirectAttributes redirect) {

		if (result.hasErrors()) {
			final List<String> message = buildFieldErrorMessages(formList, result); // ★集約
			redirect.addFlashAttribute(PropKey.ERROR_MESSAGE, message); // PRG用
			session.setAttribute(PropKey.ERROR_MESSAGE, message); // 粘着用

			// 入力復元
			redirect.addFlashAttribute(AnswerRecordsFormParam.FORM_LIST, formList);

			// フローは正しいので OK として章に戻す
			redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
			return AppPath.rChapter(bookViewId, chapterNo);
		}

		// attemptSession 確保
		String asViewId = (String) session.getAttribute(AttemptSessionObjParam.VIEW_ID);
		final String ensuredAsViewId = (asViewId != null)
			? asViewId
			: attemptSessionService
				.createNew(
					usersService.getLoginUser(),
					sankouBooksService.getUserViewDtoByViewId(bookViewId))
				.viewId();
		session.setAttribute(AttemptSessionObjParam.VIEW_ID, ensuredAsViewId);

		answerRecordsService.saveAllWithAttempt(ensuredAsViewId, formList.toInputDto());

		// ★成功したのでメッセージを掃除
		session.removeAttribute(PropKey.ERROR_MESSAGE);

		final String nextNo = String.format("%02d", Integer.parseInt(chapterNo) + 1);

		// 次章も「通常遷移」として OK を渡す
		redirect.addFlashAttribute(BrowserGuard.PARAM, BrowserGuard.ok());
		return AppPath.rChapter(bookViewId, nextNo);
	}
}
