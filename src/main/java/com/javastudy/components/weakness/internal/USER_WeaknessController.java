// com.javastudy.components.weakness.internal.USER_WeaknessController
package com.javastudy.components.weakness.internal;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto.USER_WeaknessChapterViewDto;
import com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto.USER_WeaknessQuestionsViewDto;
import com.javastudy.components.weakness.api.service.WeaknessService;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.login.components.user.api.dto.MyUsersViewDto;
import com.login.components.user.api.service.MyUsersService;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;

import lombok.AllArgsConstructor;

/**
 * ユーザー向け：弱点分析 画面コントローラ。
 *
 * <p>
 * 章別と問題別の弱点リストを同画面に表示。
 */
@Controller
@AllArgsConstructor
@RequestMapping(AppPath.USER_SANKOU_BOOKS_ID) // /user/sankouBooks/{book_view_id}
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class USER_WeaknessController {

	/* ===== [private] START ===== */
	/** 問題別で最低限の試行回数（この回数未満は除外） */
	private static final int MIN_ATTEMPTS = 3;

	/** Model属性キー：章別リスト */
	private static final String ATTR_WC_LIST = "weaknessChapterViewDtoList";

	/** Model属性キー：問題別リスト */
	private static final String ATTR_WQ_LIST = "weaknessQuestionsViewDtoList";

	private final WeaknessService weaknessService;
	private final MyUsersService usersService;

	/* ===== [private] END ===== */

	/**
	 * 弱点分析画面（章別＋問題別）を表示。
	 *
	 * <p>
	 * Service側で並び順（弱い順）・丸め等は済み想定。
	 *
	 * @param model
	 *            Model
	 * @param bookViewId
	 *            対象参考書の ViewId
	 * @return テンプレートパス
	 */
	@GetMapping(AppPath.WEAKNESS) // -> /weakness
	public String getWeakness(
		final Model model,
		@PathVariable(AppPath.PARAM_BOOK_ID) final String bookViewId) {

		if (MyType.isBlank(bookViewId)) {
			// ルーティング外だが保険としてユーザーRootへ
			return AppPath.R_USER;
		}

		final MyUsersViewDto login = usersService.getLoginUser();

		// 章別（加重平均・弱い順）
		final List<USER_WeaknessChapterViewDto> wcList = weaknessService
			.listWeakChapters(login.systemId(), bookViewId);

		// 問題別（MIN_ATTEMPTS 以上・弱い順・上限0=無制限）
		final List<USER_WeaknessQuestionsViewDto> wqList = weaknessService
			.listWeakQuestions(login.systemId(), bookViewId, MIN_ATTEMPTS, 0);

		model.addAttribute(ATTR_WC_LIST, wcList);
		model.addAttribute(ATTR_WQ_LIST, wqList);
		return TempPath.WEAKNESS;
	}
}
