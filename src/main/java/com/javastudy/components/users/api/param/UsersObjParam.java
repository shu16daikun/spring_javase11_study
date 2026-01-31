package com.javastudy.components.users.api.param;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 【機能】Users画面系のModel属性キー定数
 *
 * <p>
 * 目的：View との「名前」契約を一元管理し、キー名の分散や齟齬を防ぐ。
 *
 * <h2>方針</h2>
 *
 * <ul>
 * <li>画面に見せるのは <em>値</em> であり、内部実装詳細は見せない。キー名は安定運用（破壊的変更は避ける）。
 * <li>本クラスはユーティリティのためインスタンス化不可（private コンストラクタ）。
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UsersObjParam {

	/* ===== [public/protected] START ===== */

	/** 単一表示用 ViewDto の Model 属性キー（UI契約）。 */
	public static final String VIEW_DTO = "usersViewDto";

	/** 一覧表示用 ViewDto の Model 属性キー（UI契約）。 */
	public static final String VIEW_DTO_LIST = "usersViewDtoList";

	/* ===== [public/protected] END ===== */
	/* ===== [private] START ===== */
	// なし
	/* ===== [private] END ===== */
}
