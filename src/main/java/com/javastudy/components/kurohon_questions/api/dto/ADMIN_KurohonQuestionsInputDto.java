/*
 * ADMIN_KurohonQuestionsInputDto.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.kurohon_questions.api.dto
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:24:06
 *
 * 目的:
 * - 管理画面の入力DTO（更新時のみ viewId を使用）
 * - 外部キーは viewId 文字列で受ける（Form で ViewDto→viewId に変換済）
 * - questionNo は "001".."999" の3桁文字列
 */
package com.javastudy.components.kurohon_questions.api.dto;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* ===== [import] END ===== */

/* ===== [public/protected] START ===== */
public record ADMIN_KurohonQuestionsInputDto(
	String viewId,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) String sankouBookViewId,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) String chapterViewId,

	/* "001".."999" の数字3桁 */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) @Size(min = 3, max = 3, message = ErrorProp.COMMON_SIZE) @Pattern(regexp = RegexProp.QUESTION_NO, message = ErrorProp.COMMON_BAD_REQUEST) // messageKeyは共通を流用
	String questionNo,

	/* HTML系は任意。空や空白は許容（DB DEFAULTに任せる） */
	String questionHtml,
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK) @Size(max = 20, message = ErrorProp.COMMON_MAX) String correctOption,
	String explanationHtml,
	@NotNull(message = ErrorProp.COMMON_NOT_BLANK) @Min(value = 1, message = ErrorProp.COMMON_MIN) Integer answerCountMax,
	@NotNull(message = ErrorProp.COMMON_NOT_BLANK) @Min(value = 0, message = ErrorProp.COMMON_MIN) Integer optionCount) {
}
/* ===== [public/protected] END ===== */
