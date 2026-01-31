// com.javastudy.components.users.internal.USER_UsersForm
package com.javastudy.components.users.internal;

import com.javastudy.components.users.api.dto.USER_UsersInputDto;
import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.validation.username.ValidUsername;
import com.login.components.user.api.dto.MyUsersViewDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 【機能】ユーザー更新フォーム（画面バインド専用）
 *
 * <p>
 * 目的：View と Controller の間のフォームオブジェクト（DTO/Entity とは分離）。
 *
 * <h2>契約（入力制約）</h2>
 *
 * <ul>
 * <li><code>username</code>：必須・8〜20文字・命名規則 {@link ValidUsername}。
 * </ul>
 *
 * <h2>実装メモ</h2>
 *
 * <ul>
 * <li>画面に見せるメッセージは labelKey を経由して人間可読に整形。
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
class USER_UsersForm {

	/* ===== [private] START ===== */
	/** 項目：ユーザー名（UI はラベル名で表示。内部値は出さない） */
	@NotBlank(message = ErrorProp.COMMON_NOT_BLANK)
	@Size(min = UsersFormParam.USERNAME_MIN, max = UsersFormParam.USERNAME_MAX, message = ErrorProp.COMMON_SIZE)
	@ValidUsername(message = ErrorProp.USER_USERNAME_PATTERN)
	private String username;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */

	/** 変換：入力DTOへ。 */
	final USER_UsersInputDto toInputDto() {
		return new USER_UsersInputDto(username);
	}

	/** 初期表示：ViewDto → Form。 */
	static USER_UsersForm fromViewDto(final MyUsersViewDto loginUser) {
		final USER_UsersForm f = new USER_UsersForm();
		f.setUsername(loginUser.username());
		return f;
	}

	/* ===== [public/protected] END ===== */
}
