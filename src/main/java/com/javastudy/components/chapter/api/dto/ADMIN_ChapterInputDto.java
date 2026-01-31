/*
 * ADMIN_ChapterInputDto.java（変更なし/再掲）
 * 目的: InputDto.no は "01".."99" の二桁文字列
 */
package com.javastudy.components.chapter.api.dto;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ADMIN_ChapterInputDto(
	String viewId,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) @Size(min = 2, max = 2, message = ErrorProp.COMMON_SIZE) String no,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) @Size(max = 50, message = ErrorProp.COMMON_MAX) String name,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) String bookViewId) {
}
