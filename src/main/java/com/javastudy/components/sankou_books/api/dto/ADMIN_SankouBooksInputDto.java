/*
 * ADMIN_SankouBooksInputDto.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_books.api.dto
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:21:52
 *
 * 目的:
 * - 管理画面の入力DTO（viewId は更新時のみ使用）
 * - 外部キーは viewId 文字列で受ける（Formで ViewDto→viewId に変換済）
 *
 * 注意:
 * - 文字列は private static final String
 */

package com.javastudy.components.sankou_books.api.dto;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/* ===== [import] END ===== */

/* ===== [public/protected] START ===== */
public record ADMIN_SankouBooksInputDto(
	String viewId,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) @Size(max = 50, message = ErrorProp.COMMON_MAX) String name,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) String colorViewId) {
}
/* ===== [public/protected] END ===== */
