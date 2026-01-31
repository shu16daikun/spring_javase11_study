package com.javastudy.components.sankou_books.internal;

import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksInputDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;

import jakarta.validation.constraints.NotBlank;
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
public class ADMIN_SankouBooksForm {

	/** 参考書名 */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(max = SankouBooksFormParam.NAME_MAX, message = ErrorProp.COMMON_MAX)
	private String name;

	/** 外部キー：カラー（バインド用は viewId） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	private String colorViewId;

	/** 表示補助用に保持（任意） */
	private ADMIN_SankouBookColorViewDto colorViewDto;

	public ADMIN_SankouBooksInputDto toCreateInputDto() {
		return new ADMIN_SankouBooksInputDto(null, this.trim(this.name), this.colorViewId);
	}

	public ADMIN_SankouBooksInputDto toUpdateInputDto(final String viewId) {
		return new ADMIN_SankouBooksInputDto(viewId, this.trim(this.name), this.colorViewId);
	}

	public static ADMIN_SankouBooksForm fromViewDto(final ADMIN_SankouBooksViewDto v) {
		return ADMIN_SankouBooksForm.builder()
			.name(v.name())
			// ※ API が colorVierDto() で提供されている前提（既存コード準拠）
			.colorViewId(v.colorViewDto() != null ? v.colorViewDto().viewId() : null)
			.colorViewDto(v.colorViewDto())
			.build();
	}

	private String trim(final String s) {
		return s == null ? null : s.trim();
	}
}
