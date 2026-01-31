package com.javastudy.util.path;

import lombok.NoArgsConstructor;

/** パッケージ名の定数化と、複数モジュールの base package 一括指定ユーティリティ。 */
@NoArgsConstructor
/* ===== [public/protected] START ===== */
public class PackagePath {

	// 依存パッケージ（スキャン・参照ベース）
	public static final String MAIN = "com.javastudy";
	public static final String MY_LOGIN = "com.login";
	public static final String MY_EXCEPTION = "com.exception";
	public static final String MY_UTIL = "com.util";

	// コンポーネントの base
	public static final String MAIN_COMPONENTS_BASE = "com.javastudy.components";
	public static final String LOGIN_COMPONENTS_BASE = "com.login.components";

	/** Spring の @ComponentScan 等でまとめて設定するための配列 */
	public static final String[] MODULE_PACKAGES = {
		LOGIN_COMPONENTS_BASE, MAIN_COMPONENTS_BASE
	};
}
/* ===== [public/protected] END ===== */
