// com.javastudy.exception_module.internal.LoginErrorController
package com.javastudy.components.error.internal;

import com.javastudy.components.error.api.service.ErrorStateService;
import com.javastudy.util.param.prop_key.PropKey;
import com.javastudy.util.path.AppPath;
import com.javastudy.util.path.TempPath;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ログイン領域のエラー画面コントローラ。
 *
 * <p>
 * “画面に見せるもの”：ステータス・コード・メッセージ・traceId。
 *
 * <p>
 * “ログ専用”：本クラスでは行わない。
 */
@Controller
@RequiredArgsConstructor
public class LoginErrorController { // public 非final（AOP方針）

	/* ===== [private] START ===== */
	private final ErrorStateService errorStateService;
	private final ErrorReportService reportService;

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/** ログイン用汎用エラー画面の表示。 */
	@GetMapping(AppPath.LOGIN_ERROR)
	public String error(
		@RequestParam(value = PropKey.TRACE_ID, required = false) final String traceId,
		final Model model) {
		if (traceId != null && !model.containsAttribute(PropKey.STATUS)) {
			this.errorStateService
				.get(traceId)
				.ifPresent(
					v -> {
						model.addAttribute(PropKey.STATUS, v.status());
						model.addAttribute(PropKey.ERROR_CODE, v.errorCode());
						model.addAttribute(PropKey.ERROR_MESSAGE, v.errorMessage());
					});
		}
		if (traceId != null) {
			model.addAttribute(PropKey.TRACE_ID, traceId);
		}
		return TempPath.LOGIN_ERROR;
	}

	/**
	 * ログイン領域のエラーログCSVダウンロード。
	 *
	 * @param traceId
	 *            トレースID
	 */
	@GetMapping(AppPath.LOGIN_ERROR_LOGS)
	public ResponseEntity<Resource> downloadCsv(
		@RequestParam(PropKey.TRACE_ID) final String traceId) {
		final byte[] csv = this.reportService.exportCsvForTrace(traceId);
		final String filename = "error-login-" + traceId + ".csv";

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
		headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));

		return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(csv));
	}
	/* ===== [public/protected] END ===== */
}
