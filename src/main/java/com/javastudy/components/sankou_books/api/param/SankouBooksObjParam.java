// com.javastudy.components.sankou_books.api.param.SankouBooksObjParam
package com.javastudy.components.sankou_books.api.param;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 【機能】SankouBooks 画面系 Model 属性キー定数
 *
 * <p>
 * 目的：View とのキー名契約を一元管理し、散在/齟齬を防ぐ。
 *
 * <h2>画面に見せるもの／ログ専用</h2>
 *
 * <ul>
 * <li>画面：キー名は安定運用（互換性を重視）。
 * <li>ログ：キーの中身（値）の内部構造は出さない。
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SankouBooksObjParam {

	/* ===== [public/protected] START ===== */

	/** 単体表示用 ViewDto の Model 属性キー。 */
	public static final String VIEW_DTO = "sankouBooksViewDto";

	/** 一覧表示用 ViewDto の Model 属性キー。 */
	public static final String VIEW_DTO_LIST = "sankouBooksViewDtoList";

	/* ===== [public/protected] END ===== */
	/* ===== [private] START ===== */
	// なし
	/* ===== [private] END ===== */
}
