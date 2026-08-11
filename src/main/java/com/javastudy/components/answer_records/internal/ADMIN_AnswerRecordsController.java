/*
 * ADMIN_AnswerRecordsController.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.answer_records.internal
 * Author  : shu-kundeath
 * Created : 2025/10/29 8:52:15
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.answer_records.internal;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.javastudy.components.answer_records.api.dto.ADMIN_AnswerRecordsViewDto;
import com.javastudy.components.answer_records.api.param.AnswerRecordsObjParam;
import com.javastudy.components.answer_records.api.service.AnswerRecordsService;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.my.util.security.role.RoleUtil;

import lombok.AllArgsConstructor;

/**
 * ADMIN_AnswerRecordsController
 * 目的: TODO
 *
 * 公開契約:
 * - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * 備考:
 * - DTO は record を用いる
 */
@Controller
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@AllArgsConstructor
public class ADMIN_AnswerRecordsController {
	/* ===== [constants] START ===== */
	/* ===== [constants] END ===== */

	/* ===== [field] START ===== */
	private final AnswerRecordsService answerRecordsService;
	/* ===== [field] END ===== */

	/* ===== [public/protected] START ===== */
	/** 設定ホーム（一覧など） */
	@GetMapping(AppPath.ADMIN_ANSWER_RECORDS)
	public String getSetting(final Model model) {
		final List<ADMIN_AnswerRecordsViewDto> recordsViewDtoList = this.answerRecordsService
			.getAdminViewDtoList();
		model.addAttribute(AnswerRecordsObjParam.VIEW_DTO_LIST, recordsViewDtoList);
		return TempPath.ADMIN_ANSWER_RECORDS;
	}
	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/* ===== [private] END ===== */
}
