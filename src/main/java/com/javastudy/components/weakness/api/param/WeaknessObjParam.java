// com.javastudy.components.weakness.api.param.WeaknessObjParam
package com.javastudy.components.weakness.api.param;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Weakness 画面連携用：Model 属性キー定数。
 *
 * <p>
 * “画面に見せるもの”のキー名のみ。ログ専用キーは持たない。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WeaknessObjParam {
	/* ===== [public/protected] START ===== */
	public static final String VIEW_DTO = "weaknessViewDto";
	public static final String VIEW_DTO_LIST = "weaknessViewDtoList";
	/* ===== [public/protected] END ===== */
}
