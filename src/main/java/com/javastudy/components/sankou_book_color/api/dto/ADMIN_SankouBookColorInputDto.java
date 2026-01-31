/*
 * ADMIN_SankouBookColorInputDto.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_book_color.api.dto
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:21:25
 *
 * 目的:
 * - 管理画面の入力DTO（viewId は更新時のみ使用）
 *
 * 注意:
 * - 文字列は private static final String
 */

package com.javastudy.components.sankou_book_color.api.dto;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/* ===== [import] END ===== */

/* ===== [public/protected] START ===== */
public record ADMIN_SankouBookColorInputDto(
	String viewId,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) @Size(max = 20, message = ErrorProp.COMMON_MAX) String name) {
}
/* ===== [public/protected] END ===== */
