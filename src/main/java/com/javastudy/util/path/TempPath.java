package com.javastudy.util.path;

/**
 * Thymeleaf 等のテンプレート論理パスの定数化。
 *
 * <p>
 * View 解決で使用する論理名をここに集約する。
 */
public final class TempPath {

	/* ===== [private] START ===== */
	/** コンストラクタ封印 */
	private TempPath() {
	}
	/* ===== [private] END ===== */

	/* ===== [private] START ===== */
	// ベース
	private static final String BASE_LOGIN = "login/main-contents";
	private static final String BASE_USER = "user/main-contents";
	private static final String BASE_ADMIN = "admin/main-contents";
	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	// Login
	public static final String LOGIN = BASE_LOGIN + "/login";
	public static final String PASS_SET = BASE_LOGIN + "/passwordSet";
	public static final String LOGIN_ERROR = BASE_LOGIN + "/error";

	// User
	public static final String USER = BASE_USER + "/home";
	public static final String USER_ERROR = BASE_USER + "/error";
	public static final String SANKOU_BOOKS = BASE_USER + "/sankouBooks";
	public static final String WEAKNESS = BASE_USER + "/weakness";
	public static final String CHAPTER = BASE_USER + "/chapter";
	public static final String ANSWER = BASE_USER + "/answer";
	public static final String ACCOUNT_SETTING = BASE_USER + "/accountSetting";
	public static final String RESULT = BASE_USER + "/result";

	// Admin（ホーム／エラー）
	public static final String ADMIN_HOME = BASE_ADMIN + "/home";
	/** 互換用途（従来の ADMIN を残す） */
	public static final String ADMIN = ADMIN_HOME;
	public static final String ADMIN_ERROR = BASE_ADMIN + "/error";

	// Admin：Authority（設定／追加／更新）
	public static final String ADMIN_AUTHORITY = BASE_ADMIN + "/authority/setting";
	public static final String ADMIN_AUTHORITY_INSERT = BASE_ADMIN + "/authority/insert";
	public static final String ADMIN_AUTHORITY_UPDATE = BASE_ADMIN + "/authority/update";

	// Admin：Users（設定／追加／更新）
	public static final String ADMIN_USERS = BASE_ADMIN + "/users/setting";
	public static final String ADMIN_USERS_INSERT = BASE_ADMIN + "/users/insert";
	public static final String ADMIN_USERS_UPDATE = BASE_ADMIN + "/users/update";

	// Admin：SankouBooks（設定／追加／更新）
	public static final String ADMIN_SANKOU_BOOKS = BASE_ADMIN + "/sankou-books/setting";
	public static final String ADMIN_SANKOU_BOOKS_INSERT = BASE_ADMIN + "/sankou-books/insert";
	public static final String ADMIN_SANKOU_BOOKS_UPDATE = BASE_ADMIN + "/sankou-books/update";

	// Admin：SankouBookColor（設定／追加／更新）
	public static final String ADMIN_SANKOU_BOOK_COLOR = BASE_ADMIN + "/sankou-book-color/setting";
	public static final String ADMIN_SANKOU_BOOK_COLOR_INSERT = BASE_ADMIN
		+ "/sankou-book-color/insert";
	public static final String ADMIN_SANKOU_BOOK_COLOR_UPDATE = BASE_ADMIN
		+ "/sankou-book-color/update";

	// Admin：Chapter（設定／追加／更新）
	public static final String ADMIN_CHAPTER = BASE_ADMIN + "/chapter/setting";
	public static final String ADMIN_CHAPTER_INSERT = BASE_ADMIN + "/chapter/insert";
	public static final String ADMIN_CHAPTER_UPDATE = BASE_ADMIN + "/chapter/update";

	// Admin：KurohonQuestions（設定／追加／更新）
	public static final String ADMIN_KUROHON_QUESTIONS = BASE_ADMIN + "/kurohon-questions/setting";
	public static final String ADMIN_KUROHON_QUESTIONS_INSERT = BASE_ADMIN
		+ "/kurohon-questions/insert";
	public static final String ADMIN_KUROHON_QUESTIONS_UPDATE = BASE_ADMIN
		+ "/kurohon-questions/update";

	// Admin：AnswerRecords（設定のみ）
	public static final String ADMIN_ANSWER_RECORDS = BASE_ADMIN + "/answer-records/setting";

	// Admin：AttemptSession（設定のみ）
	public static final String ADMIN_ATTEMPT_SESSION = BASE_ADMIN + "/attempt-session/setting";

	// Admin：Weakness（設定のみ）
	public static final String ADMIN_WEAKNESS = BASE_ADMIN + "/weakness/setting";
	/* ===== [public/protected] END ===== */
}
