/*
 * ADMIN_AuthorityForm.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.authority.internal
 * Author  : shu-kundeath
 * Created : 2025/10/24 17:09:42
 *
 * 目的:
 * - 管理画面での権限作成/更新フォーム（Webバインド用）
 *
 * 注意:
 * - 文字列定数は private static final String（規約）
 */

package com.javastudy.components.authority.internal;

import com.javastudy.components.authority.api.dto.ADMIN_AuthorityViewDto;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.validation.authority_system_name.ValidAuthoritySystemName;
import com.login.components.authority.api.dto.MyAuthorityInputDto;
import com.util.security.role.RoleUtil;
import com.util.type.MyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* ===== [import] END ===== */

/**
 * ADMIN_AuthorityForm 目的: 画面→Service の橋渡し（Form ←→ ViewDto／Form → InputDto）
 *
 * <p>
 * 公開契約: - フォームは可変 & public getter/setter 必須
 *
 * <p>
 * 備考: - DTO は record を用いる（返却/引数）
 */
@Getter
@Setter
@NoArgsConstructor
public class ADMIN_AuthorityForm {

	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [public/protected] START ===== */
	/** 権限名（ROLE_ から開始／最大20） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(max = 15, message = ErrorProp.COMMON_MAX)
	@ValidAuthoritySystemName
	private String systemName;

	/* ===== [public/protected] END ===== */

	/* ===== [factory/static] START ===== */
	public static ADMIN_AuthorityForm fromViewDto(final ADMIN_AuthorityViewDto v) {
		final ADMIN_AuthorityForm f = new ADMIN_AuthorityForm();
		if (MyType.isNotNull(v)) {
			f.setSystemName(v.systemName());
		}
		return f;
	}

	/* ===== [factory/static] END ===== */

	/* ===== [public/protected] START ===== */
	/** 作成用 InputDto を生成（id は null → 先方 Service/DB で採番） */
	public MyAuthorityInputDto toCreateInputDto() {
		final String name = RoleUtil.toDbRole(this.systemName);
		return new MyAuthorityInputDto(
			null,
			this.trim(name));
	}

	/**
	 * 更新用 InputDto を生成（entityId は呼び手で変換して渡す） - 例: entityId =
	 * myAuthorityService.getEntityId(form.getViewId())
	 */
	public MyAuthorityInputDto toUpdateInputDto(final String viewId) {
		final String name = RoleUtil.toDbRole(this.systemName);
		return new MyAuthorityInputDto(
			viewId,
			this.trim(name));
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	private String trim(final String s) {
		return MyType.isNull(s) ? null : s.trim();
	}
	/* ===== [private] END ===== */
}
