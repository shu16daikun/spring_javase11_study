/*
 * ADMIN_UsersForm.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.users.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 11:23:11
 *
 * 目的:
 * - 管理画面のユーザー作成/更新フォーム（Webバインド専用）
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */
package com.javastudy.components.users.internal;

import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.validation.username.ValidUsername;
import com.login.components.user.api.dto.MyUsersInputDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* ===== [import] END ===== */

/* ===== [public/protected] START ===== */
/**
 * ADMIN_UsersForm 目的: 画面→UseCase 橋渡し（Form ←→ InputDto）
 *
 * <p>
 * 公開契約: - Form はサービス非依存（純データ）
 *
 * <p>
 * 備考: - DTO は record を用いる
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ADMIN_UsersForm {

	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [field] START ===== */
	/** ユーザー名（注: 形式は @ValidUsername に委譲） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(max = 30, message = ErrorProp.COMMON_MAX)
	@ValidUsername
	private String username;

	/** 権限の画面用ID（AU0001 等）— フォームはフラットにバインド */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	private String authorityViewId;

	/** 権限の画面用ID（AU0001 等） */
	private ADMIN_AuthorityViewDto authorityViewDto;

	/* ===== [field] END ===== */

	/* ===== [public/protected] START ===== */
	public MyUsersInputDto toCreateInputDto() {
		return new MyUsersInputDto(null, this.trim(this.username), this.authorityViewId);
	}

	public MyUsersInputDto toUpdateInputDto(final String viewId) {
		return new MyUsersInputDto(
			viewId,
			this.trim(this.username),
			this.authorityViewId);
	}

	public static ADMIN_UsersForm fromViewDto(final ADMIN_UsersViewDto v) {
		return ADMIN_UsersForm.builder()
			.username(v.username())
			.authorityViewId(v.authorityViewDto().viewId())
			.authorityViewDto(v.authorityViewDto())
			.build();
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	private String trim(final String s) {
		return s == null ? null : s.trim();
	}
	/* ===== [private] END ===== */
}
/* ===== [public/protected] END ===== */
