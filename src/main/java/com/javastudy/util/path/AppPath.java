package com.javastudy.util.path;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * システムで使用する URL/パス定数と、画面遷移用のビルダーメソッド群。
 *
 * <p>
 * redirect 文字列もここで一元管理する。
 */
public final class AppPath {

	/* ===== [public/protected] START ===== */
	/** redirect: プレフィックス（固定） */
	public static final String REDIRECT = "redirect:";

	/** pathToken */
	public static final String PAGE_TOKEN = "pageToken";
	// --- 認証系 ---
	public static final String LOGIN = "/login";

	public static final String LOGIN_PROCESSING = LOGIN;
	public static final String LOGIN_FAILURE = "/login?error=true";
	public static final String LOGIN_SUCCESS = "/loginSuccess";
	public static final String LOGIN_ERROR = "/login/error";
	public static final String LOGIN_ERROR_ALL = "/login/error/**";
	public static final String LOGIN_ERROR_LOGS = "/login/error/logs";
	public static final String LOGOUT = "/logout";
	public static final String LOGOUT_SUCCESS = "/login?logout=true";
	public static final String PASS_SET = "/login/passwordSet";
	// --- 認証後のルート ---
	public static final String USER_ROOT = "/user";

	public static final String ADMIN_ROOT = "/admin";
	// --- 復元・共通 ---
	public static final String ERROR = "/error";

	public static final String ERROR_LOGS = "/error/logs";
	public static final String USER_ERROR = USER_ROOT + ERROR;
	public static final String USER_ERROR_LOGS = USER_ROOT + ERROR_LOGS;
	public static final String ADMIN_ERROR = ADMIN_ROOT + ERROR;
	public static final String ADMIN_ERROR_LOGS = ADMIN_ROOT + ERROR_LOGS;
	// --- 静的配下・その他 ---
	public static final String CSS_ALL = "/css/**";

	public static final String JS_ALL = "/js/**";
	public static final String IMG_ALL = "/img/**";
	public static final String PSFM_ALL = "/psfm/**";
	public static final String TEST_ALL = "/test/**";
	public static final String PSFM_ROOT = "/psfm";
	public static final String TEST_ROOT = "/test";
	public static final String WEBJARS_ALL = "/webjars/**";
	public static final String FAVICON = "/favicon.ico";
	// --- パターン ---
	public static final String ADMIN_ALL = "/admin/**";

	public static final String USER_ALL = "/user/**";
	// --- 参考書／章（ユーザー） ---
	public static final String SANKOU_BOOKS = "/sankouBooks";

	public static final String USER_SANKOU_BOOKS = USER_ROOT + SANKOU_BOOKS;
	public static final String PARAM_BOOK_ID = "book_view_id";
	public static final String BOOK_ID = "/{" + PARAM_BOOK_ID + "}";
	public static final String USER_SANKOU_BOOKS_ID = USER_ROOT + SANKOU_BOOKS + BOOK_ID;
	public static final String PARAM_CHAPTER_NO = "chapter_no";
	public static final String CHAPTER_NO = "/{" + PARAM_CHAPTER_NO + "}";
	// 章配下のアクション（ユーザー）
	public static final String CHAPTER_ENTRY = CHAPTER_NO + "/browser-guard";
	public static final String NEXT_CHAPTER = CHAPTER_NO + "/next";
	public static final String ANSWER_FINISH = CHAPTER_NO + "/finish";
	public static final String RESULT = CHAPTER_NO + "/result";
	public static final String WEAKNESS = "/weakness";
	// Controller 用セグメント（ユーザー系）
	public static final String ACCOUNT_SETTING = "/account/setting";
	public static final String ACCOUNT_SETTING_ENTRY = ACCOUNT_SETTING + "/browser-guard";
	public static final String ACCOUNT_UPDATE = "/account/update";
	public static final String ACCOUNT_PASSWORD_RESET = "/account/passwordReset";
	// Users（アカウント設定）
	public static final String USER_ACCOUNT_SETTING = USER_ROOT + ACCOUNT_SETTING;
	public static final String USER_ACCOUNT_SETTING_ENTRY = USER_ROOT + ACCOUNT_SETTING_ENTRY;

	public static final String USER_ACCOUNT_UPDATE = USER_ROOT + ACCOUNT_UPDATE;
	public static final String USER_ACCOUNT_PASSWORD_RESET = USER_ROOT + ACCOUNT_PASSWORD_RESET;
	// PRG redirect（ユーザー）
	public static final String R_USER_ACCOUNT_SETTING = REDIRECT + USER_ACCOUNT_SETTING;

	// 固定 redirect（共通）
	public static final String R_LOGIN = REDIRECT + LOGIN;

	public static final String R_LOGIN_ERROR = REDIRECT + LOGIN_ERROR;
	public static final String R_USER = REDIRECT + USER_ROOT;
	public static final String R_ADMIN = REDIRECT + ADMIN_ROOT;
	public static final String R_USER_ERROR = REDIRECT + USER_ERROR;
	public static final String R_ADMIN_ERROR = REDIRECT + ADMIN_ERROR;
	public static final String R_LOGOUT = REDIRECT + LOGOUT_SUCCESS;
	public static final String R_PASS_SET = REDIRECT + PASS_SET;
	public static final String R_SANKOU_BOOKS = REDIRECT + USER_SANKOU_BOOKS;
	// --- Authority ---
	public static final String ADMIN_AUTHORITY = ADMIN_ROOT + "/authority";

	// ===== 管理者：各コンポ汎用 =====

	public static final String ADMIN_AUTHORITY_INSERT = ADMIN_AUTHORITY + "/insert";
	public static final String ADMIN_AUTHORITY_INSERT_ENTRY = ADMIN_AUTHORITY_INSERT
		+ "/browser-guard";
	public static final String PARAM_AUTHORITY_VIEW_ID = "authority_view_id";
	public static final String AUTHORITY_VIEW_ID_SEG = "/{" + PARAM_AUTHORITY_VIEW_ID + "}";
	public static final String ADMIN_AUTHORITY_ID = ADMIN_AUTHORITY + AUTHORITY_VIEW_ID_SEG;
	public static final String ADMIN_AUTHORITY_ID_ENTRY = ADMIN_AUTHORITY_ID + "/browser-guard";
	public static final String ADMIN_AUTHORITY_UPDATE = ADMIN_AUTHORITY_ID + "/update";
	public static final String ADMIN_AUTHORITY_DELETE = ADMIN_AUTHORITY_ID + "/delete";
	// redirect
	public static final String R_ADMIN_AUTHORITY = REDIRECT + ADMIN_AUTHORITY;
	public static final String R_ADMIN_AUTHORITY_INSERT = REDIRECT + ADMIN_AUTHORITY_INSERT;
	public static final String R_ADMIN_AUTHORITY_ID = REDIRECT + ADMIN_AUTHORITY + "/";
	// --- Users（+ password_reset） ---
	public static final String ADMIN_USERS = ADMIN_ROOT + "/users";
	public static final String ADMIN_USERS_INSERT = ADMIN_USERS + "/insert";
	public static final String ADMIN_USERS_INSERT_ENTRY = ADMIN_USERS_INSERT + "/browser-guard";

	public static final String PARAM_USERS_VIEW_ID = "users_view_id";

	public static final String USERS_VIEW_ID_SEG = "/{" + PARAM_USERS_VIEW_ID + "}";
	public static final String ADMIN_USERS_ID = ADMIN_USERS + USERS_VIEW_ID_SEG;
	public static final String ADMIN_USERS_ID_ENTRY = ADMIN_USERS_ID + "/browser-guard";
	public static final String ADMIN_USERS_UPDATE = ADMIN_USERS_ID + "/update";
	public static final String ADMIN_USERS_DELETE = ADMIN_USERS_ID + "/delete";
	public static final String ADMIN_USERS_PASSWORD_RESET = ADMIN_USERS_ID + "/passwordReset";
	// redirect
	public static final String R_ADMIN_USERS = REDIRECT + ADMIN_USERS;
	public static final String R_ADMIN_USERS_INSERT = REDIRECT + ADMIN_USERS_INSERT;
	public static final String R_ADMIN_USERS_ID = REDIRECT + ADMIN_USERS + "/";
	// --- sankouBookColor ---
	public static final String ADMIN_SANKOU_BOOK_COLOR = ADMIN_ROOT + "/sankouBookColor";
	public static final String ADMIN_SANKOU_BOOK_COLOR_INSERT = ADMIN_SANKOU_BOOK_COLOR + "/insert";
	public static final String ADMIN_SANKOU_BOOK_COLOR_INSERT_ENTRY = ADMIN_SANKOU_BOOK_COLOR_INSERT
		+ "/browser-guard";
	public static final String PARAM_SANKOU_BOOK_COLOR_VIEW_ID = "sankou_book_color_view_id";

	public static final String SANKOU_BOOK_COLOR_VIEW_ID_SEG = "/{"
		+ PARAM_SANKOU_BOOK_COLOR_VIEW_ID + "}";

	public static final String ADMIN_SANKOU_BOOK_COLOR_ID = ADMIN_SANKOU_BOOK_COLOR
		+ SANKOU_BOOK_COLOR_VIEW_ID_SEG;
	public static final String ADMIN_SANKOU_BOOK_COLOR_ID_ENTRY = ADMIN_SANKOU_BOOK_COLOR_ID
		+ "/browser-guard";

	public static final String ADMIN_SANKOU_BOOK_COLOR_UPDATE = ADMIN_SANKOU_BOOK_COLOR_ID
		+ "/update";
	public static final String ADMIN_SANKOU_BOOK_COLOR_DELETE = ADMIN_SANKOU_BOOK_COLOR_ID
		+ "/delete";
	// redirect
	public static final String R_ADMIN_SANKOU_BOOK_COLOR = REDIRECT + ADMIN_SANKOU_BOOK_COLOR;
	public static final String R_ADMIN_SANKOU_BOOK_COLOR_INSERT = REDIRECT
		+ ADMIN_SANKOU_BOOK_COLOR_INSERT;
	public static final String R_ADMIN_SANKOU_BOOK_COLOR_ID = REDIRECT + ADMIN_SANKOU_BOOK_COLOR
		+ "/";
	// --- SankouBooks（管理側） ---
	public static final String ADMIN_SANKOU_BOOKS = ADMIN_ROOT + "/sankouBooks";
	public static final String ADMIN_SANKOU_BOOKS_INSERT = ADMIN_SANKOU_BOOKS + "/insert";
	public static final String ADMIN_SANKOU_BOOKS_INSERT_ENTRY = ADMIN_SANKOU_BOOKS_INSERT
		+ "/browser-guard";
	public static final String PARAM_SANKOU_BOOKS_VIEW_ID = "sankou_books_view_id";
	public static final String SANKOU_BOOKS_VIEW_ID_SEG = "/{" + PARAM_SANKOU_BOOKS_VIEW_ID + "}";
	public static final String ADMIN_SANKOU_BOOKS_ID = ADMIN_SANKOU_BOOKS
		+ SANKOU_BOOKS_VIEW_ID_SEG;
	public static final String ADMIN_SANKOU_BOOKS_ID_ENTRY = ADMIN_SANKOU_BOOKS_ID
		+ "/browser-guard";

	public static final String ADMIN_SANKOU_BOOKS_UPDATE = ADMIN_SANKOU_BOOKS_ID + "/update";

	public static final String ADMIN_SANKOU_BOOKS_DELETE = ADMIN_SANKOU_BOOKS_ID + "/delete";

	// redirect
	public static final String R_ADMIN_SANKOU_BOOKS = REDIRECT + ADMIN_SANKOU_BOOKS;
	public static final String R_ADMIN_SANKOU_BOOKS_INSERT = REDIRECT + ADMIN_SANKOU_BOOKS_INSERT;
	public static final String R_ADMIN_SANKOU_BOOKS_ID = REDIRECT + ADMIN_SANKOU_BOOKS + "/";
	// --- Chapter（管理側） ---
	public static final String ADMIN_CHAPTER = ADMIN_ROOT + "/chapter";
	public static final String ADMIN_CHAPTER_INSERT = ADMIN_CHAPTER + "/insert";
	public static final String ADMIN_CHAPTER_INSERT_ENTRY = ADMIN_CHAPTER_INSERT + "/browser-guard";
	public static final String PARAM_CHAPTER_VIEW_ID = "chapter_view_id";
	public static final String CHAPTER_VIEW_ID_SEG = "/{" + PARAM_CHAPTER_VIEW_ID + "}";
	public static final String ADMIN_CHAPTER_ID = ADMIN_CHAPTER + CHAPTER_VIEW_ID_SEG;
	public static final String ADMIN_CHAPTER_ID_ENTRY = ADMIN_CHAPTER_ID + "/browser-guard";
	public static final String ADMIN_CHAPTER_UPDATE = ADMIN_CHAPTER_ID + "/update";
	public static final String ADMIN_CHAPTER_DELETE = ADMIN_CHAPTER_ID + "/delete";

	// redirect
	public static final String R_ADMIN_CHAPTER = REDIRECT + ADMIN_CHAPTER;

	public static final String R_ADMIN_CHAPTER_INSERT = REDIRECT + ADMIN_CHAPTER_INSERT;

	public static final String R_ADMIN_CHAPTER_ID = REDIRECT + ADMIN_CHAPTER + "/";
	// --- attemptSession（設定ホームのみ） ---
	public static final String ADMIN_ATTEMPT_SESSION = ADMIN_ROOT + "/attemptSession";
	public static final String R_ADMIN_ATTEMPT_SESSION = REDIRECT + ADMIN_ATTEMPT_SESSION;
	// --- KurohonQuestions（管理側） ---
	public static final String ADMIN_KUROHON_QUESTIONS = ADMIN_ROOT + "/kurohonQuestions";
	public static final String ADMIN_KUROHON_QUESTIONS_INSERT = ADMIN_KUROHON_QUESTIONS + "/insert";
	public static final String ADMIN_KUROHON_QUESTIONS_INSERT_ENTRY = ADMIN_KUROHON_QUESTIONS_INSERT
		+ "/browser-guard";
	public static final String PARAM_KUROHON_QUESTIONS_VIEW_ID = "kurohon_questions_view_id";
	public static final String KUROHON_QUESTIONS_VIEW_ID_SEG = "/{"
		+ PARAM_KUROHON_QUESTIONS_VIEW_ID + "}";
	public static final String ADMIN_KUROHON_QUESTIONS_ID = ADMIN_KUROHON_QUESTIONS
		+ KUROHON_QUESTIONS_VIEW_ID_SEG;
	public static final String ADMIN_KUROHON_QUESTIONS_ID_ENTRY = ADMIN_KUROHON_QUESTIONS_ID
		+ "/browser-guard";
	public static final String ADMIN_KUROHON_QUESTIONS_UPDATE = ADMIN_KUROHON_QUESTIONS_ID
		+ "/update";
	public static final String ADMIN_KUROHON_QUESTIONS_DELETE = ADMIN_KUROHON_QUESTIONS_ID
		+ "/delete";

	// redirect
	public static final String R_ADMIN_KUROHON_QUESTIONS = REDIRECT + ADMIN_KUROHON_QUESTIONS;

	public static final String R_ADMIN_KUROHON_QUESTIONS_INSERT = REDIRECT
		+ ADMIN_KUROHON_QUESTIONS_INSERT;

	public static final String R_ADMIN_KUROHON_QUESTIONS_ID = REDIRECT + ADMIN_KUROHON_QUESTIONS
		+ "/";
	// --- AnswerRecords（設定ホームのみ） ---
	public static final String ADMIN_ANSWER_RECORDS = ADMIN_ROOT + "/answerRecords";

	public static final String R_ADMIN_ANSWER_RECORDS = REDIRECT + ADMIN_ANSWER_RECORDS;
	// --- Weakness（設定ホームのみ） ---
	public static final String ADMIN_WEAKNESS = ADMIN_ROOT + "/weakness";
	public static final String R_ADMIN_WEAKNESS = REDIRECT + ADMIN_WEAKNESS;

	// builder（※GET更新画面 `/{id}` を返す。旧 Id 系メソッドは廃止）
	public static String pAdminAuthorityUpdate(final String viewId) {
		return ADMIN_AUTHORITY + "/" + seg(viewId);
	}

	// builder
	public static String pAdminChapterUpdate(final String viewId) {
		return ADMIN_CHAPTER + "/" + seg(viewId);
	}

	// builder
	public static String pAdminKurohonQuestionsUpdate(final String viewId) {
		return ADMIN_KUROHON_QUESTIONS + "/" + seg(viewId);
	}

	// builder
	public static String pAdminSankouBookColorUpdate(final String viewId) {
		return ADMIN_SANKOU_BOOK_COLOR + "/" + seg(viewId);
	}

	// builder
	public static String pAdminSankouBooksUpdate(final String viewId) {
		return ADMIN_SANKOU_BOOKS + "/" + seg(viewId);
	}

	// builder
	public static String pAdminUsersUpdate(final String viewId) {
		return ADMIN_USERS + "/" + seg(viewId);
	}

	/** 書籍ページのプレーンパスを生成する。 */
	public static String pBook(final String bookViewId) {
		return USER_SANKOU_BOOKS + "/" + seg(bookViewId);
	}

	/** 章ページのプレーンパスを生成する。 */
	public static String pChapter(final String bookViewId, final String chapterNo) {
		return pBook(bookViewId) + "/" + seg(chapterNo);
	}

	/** 次の章 */
	public static String pChapterNext(final String bookViewId, final String chapterNo) {
		return pChapter(bookViewId, chapterNo) + "/next";
	}

	/** 終了 */
	public static String pFinish(final String bookViewId, final String chapterNo) {
		return pChapter(bookViewId, chapterNo) + "/finish";
	}

	/** 結果 */
	public static String pResult(final String bookViewId, final String chapterNo) {
		return pChapter(bookViewId, chapterNo) + "/result";
	}

	/** 苦手ページ（章非依存） */
	public static String pWeakness(final String bookViewId) {
		return pBook(bookViewId) + "/weakness";
	}

	public static String rAdminAuthorityUpdate(final String viewId) {
		return REDIRECT + pAdminAuthorityUpdate(viewId);
	}

	// ===== 参考書／章（ユーザー）ビルダー =====

	public static String rAdminChapterUpdate(final String viewId) {
		return REDIRECT + pAdminChapterUpdate(viewId);
	}

	public static String rAdminKurohonQuestionsUpdate(final String viewId) {
		return REDIRECT + pAdminKurohonQuestionsUpdate(viewId);
	}

	public static String rAdminSankouBookColorUpdate(final String viewId) {
		return REDIRECT + pAdminSankouBookColorUpdate(viewId);
	}

	public static String rAdminSankouBooksUpdate(final String viewId) {
		return REDIRECT + pAdminSankouBooksUpdate(viewId);
	}

	public static String rAdminUsersUpdate(final String viewId) {
		return REDIRECT + pAdminUsersUpdate(viewId);
	}

	/** redirect 版：書籍 */
	public static String rBook(final String bookViewId) {
		return REDIRECT + pBook(bookViewId);
	}

	/** redirect 版：章 */
	public static String rChapter(final String bookViewId, final String chapterNo) {
		return REDIRECT + pChapter(bookViewId, chapterNo);
	}

	/** redirect 版：次の章 */
	public static String rChapterNext(final String bookViewId, final String chapterNo) {
		return REDIRECT + pChapterNext(bookViewId, chapterNo);
	}

	/** redirect 版：終了 */
	public static String rFinish(final String bookViewId, final String chapterNo) {
		return REDIRECT + pFinish(bookViewId, chapterNo);
	}

	/** redirect 版：結果 */
	public static String rResult(final String bookViewId, final String chapterNo) {
		return REDIRECT + pResult(bookViewId, chapterNo);
	}

	/** redirect 版：苦手 */
	public static String rWeakness(final String bookViewId) {
		return REDIRECT + pWeakness(bookViewId);
	}

	/* ===== [private] START ===== */
	/** URL セグメント用のエンコード（UTF-8）。 */
	private static String seg(final String v) {
		return URLEncoder.encode(String.valueOf(v), StandardCharsets.UTF_8);
	}
	/* ===== [private] END ===== */

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/** インスタンス化禁止 */
	private AppPath() {
	}
	/* ===== [private] END ===== */
}
