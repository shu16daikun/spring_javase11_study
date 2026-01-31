package com.javastudy.components.chapter.internal;

import com.javastudy.components.chapter.api.dto.ADMIN_ChapterInputDto;
import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.util.zero_padding.ZeroPadding;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ADMIN_ChapterForm {

	@NotNull(message = ErrorProp.COMMON_NOT_BLANK)
	@Min(value = ChapterFormParam.NO_MIN, message = ErrorProp.COMMON_MIN)
	@Max(value = ChapterFormParam.NO_MAX, message = ErrorProp.COMMON_MAX)
	private Integer no;

	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(max = ChapterFormParam.TITLE_MAX, message = ErrorProp.COMMON_MAX)
	private String title;

	/** 外部キー：参考書（バインドは viewId） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	private String sankouBooksViewId;

	/** 表示補助（任意） */
	private ADMIN_SankouBooksViewDto sankouBooksViewDto;

	public ADMIN_ChapterInputDto toCreateInputDto() {
		return new ADMIN_ChapterInputDto(
			null,
			this.pad2(this.no),
			this.trim(this.title),
			this.sankouBooksViewId);
	}

	public ADMIN_ChapterInputDto toUpdateInputDto(final String viewId) {
		return new ADMIN_ChapterInputDto(
			viewId,
			this.pad2(this.no),
			this.trim(this.title),
			this.sankouBooksViewId);
	}

	public static ADMIN_ChapterForm fromViewDto(final ADMIN_ChapterViewDto v) {
		final Integer no = ZeroPadding.parseLeftPaddedInt(v.no());
		return ADMIN_ChapterForm.builder()
			.no(no)
			.title(v.name())
			.sankouBooksViewId(
				v.sankouBooksViewDto() != null ? v.sankouBooksViewDto().viewId() : null)
			.sankouBooksViewDto(v.sankouBooksViewDto())
			.build();
	}

	private String trim(final String s) {
		return s == null ? null : s.trim();
	}

	/** 二桁ゼロ埋め（1→"01", 11→"11"） */
	private String pad2(final Integer n) {
		if (n == null) {
			return null;
		}
		return ZeroPadding.leftPadDigits(String.valueOf(n), 2);
	}
}
