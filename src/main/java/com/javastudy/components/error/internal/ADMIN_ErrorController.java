// com.javastudy.exception_module.internal.ADMIN_ErrorController
package com.javastudy.components.error.internal;

import com.javastudy.components.error.api.service.ErrorStateService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import com.my.util.security.role.RoleUtil;
import com.my.util.type.MyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 管理者領域のエラー画面コントローラ（表示のみ）。
 *
 * <p>
 * “画面に見せるもの”：テンプレート遷移。
 */
@Controller
@RequiredArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_ADMIN)
@RequestMapping(AppPath.ADMIN_ROOT)
public class ADMIN_ErrorController { // public 非final（AOP方針）

	/* ===== [private] START ===== */
	private final ErrorReportService reportService;
	private final ErrorStateService errorStateService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/**
	 * エラー表示。Flashが無い場合は traceId をキーに直近状態を復元する。
	 *
	 * @param model
	 *            画面モデル
	 * @param traceId
	 *            例外ハンドラで払い出したトレースID（任意）
	 * @return ユーザー領域のエラーテンプレート
	 */
	@GetMapping(AppPath.ERROR)
	public String getUserError(
		final Model model,
		@RequestParam(value = PropKey.TRACE_ID, required = false) final String traceId) {

		// Flash が無い（=リロード等）場合は traceId から復元
		if (MyType.isNotBlank(traceId) && !model.containsAttribute(PropKey.STATUS)) {
			this.errorStateService
				.get(traceId)
				.ifPresent(
					s -> {
						model.addAttribute(PropKey.STATUS, s.status());
						model.addAttribute(PropKey.ERROR_CODE, s.errorCode());
						model.addAttribute(PropKey.ERROR_MESSAGE, s.errorMessage());
					});
		}
		// CSV ボタン用に traceId は常に返す
		if (MyType.isNotBlank(traceId)) {
			model.addAttribute(PropKey.TRACE_ID, traceId);
		}
		return TempPath.ADMIN_ERROR;
	}

	/**
	 * エラーログ（周辺行付き）CSVのダウンロード。
	 *
	 * @param traceId
	 *            トレースID
	 * @return CSVレスポンス（UTF-8）
	 */
	@GetMapping(AppPath.ERROR_LOGS)
	public ResponseEntity<Resource> downloadCsv(
		@RequestParam(PropKey.TRACE_ID) final String traceId) {
		final byte[] csv = this.reportService.exportCsvForTrace(traceId);
		final String filename = "error-admin-" + traceId + ".csv";

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
		headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));

		return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(csv));
	}
	/* ===== [public/protected] END ===== */
}
