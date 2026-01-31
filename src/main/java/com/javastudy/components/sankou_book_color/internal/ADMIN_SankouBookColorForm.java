/*
 * ADMIN_SankouBookColorForm.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_book_color.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:18:51
 *
 * 目的:
 * - 管理画面の参考書カラー作成/更新フォーム（Webバインド専用）
 *
 * 注意:
 * - 文字列は private static final String
 */

package com.javastudy.components.sankou_book_color.internal;

/* ===== [import] START ===== */
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorInputDto;
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorViewDto;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* ===== [import] END ===== */

/**
 * ADMIN_SankouBookColorForm 目的: 画面→UseCase 橋渡し（Form ←→ InputDto）
 *
 * <p>
 * 公開契約: - Form はサービス非依存（純データ） - viewId は保持しない（更新時は引数で受ける）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ADMIN_SankouBookColorForm {

	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [field] START ===== */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(max = SankouBookColorFormParam.NAME_MAX, message = ErrorProp.COMMON_MAX)
	private String name;

	/* ===== [field] END ===== */

	/* ===== [public/protected] START ===== */
	public ADMIN_SankouBookColorInputDto toCreateInputDto() {
		return new ADMIN_SankouBookColorInputDto(null, this.trim(this.name));
	}

	public ADMIN_SankouBookColorInputDto toUpdateInputDto(final String viewId) {
		return new ADMIN_SankouBookColorInputDto(viewId, this.trim(this.name));
	}

	public static ADMIN_SankouBookColorForm fromViewDto(final ADMIN_SankouBookColorViewDto v) {
		return ADMIN_SankouBookColorForm.builder()
			.name(v.name())
			.build();
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	private String trim(final String s) {
		return s == null ? null : s.trim();
	}
	/* ===== [private] END ===== */
}
