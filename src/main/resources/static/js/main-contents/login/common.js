// /js/main-contents/login/common.js

import { setModal } from "/psfm/js/fragment/modal.js";
import { setBrowserGuard } from "/psfm/js/common/browser-guard.js";

import { getLoger, endAndThrow } from "/psfm/js/common/loger.js";

const LOG = getLoger("login.common");

/* CSRF ヘッダ自動付与（meta または hidden input から取得） */
const setupCsrf = function() {
	const span = LOG.logStart("setupCsrf");

	let token = $("meta[name='_csrf']").attr("content") || null;
	let header = $("meta[name='_csrf_header']").attr("content") || null;

	if (!token) {
		const $inp = $("input[type='hidden'][name]")
			.filter(function() {
				return String(this.name).toLowerCase().indexOf("csrf") >= 0;
			})
			.first();
		if ($inp.length) {
			token = $inp.val();
			header = header || "X-CSRF-TOKEN";
		}
	}

	// 多重バインド防止
	$(document).off("ajaxSend.loginCsrf").on("ajaxSend.loginCsrf", function(_e, xhr) {
		if (token) {
			xhr.setRequestHeader(header || "X-CSRF-TOKEN", token);
		}
	});

	span.end();
};

/**
 * ログイン画面用：ブラウザ戻る／進む／リロード時にモーダルを開く
 * - 対象モーダル：#loginBrowserNavModal
 * - イベント：psfm:browserBackOrForward / psfm:browserReload
 */
const setupLoginBrowserNavModal = function() {
	const span = LOG.logStart("setupLoginBrowserNavModal");

	const MODAL_SELECTOR = "#loginBrowserNavModal";

	const openLoginBrowserNavModal = () => {
		const openSpan = LOG.logStart("openLoginBrowserNavModal");
		const $modal = $(MODAL_SELECTOR);
		if (!$modal.length) {
			LOG.warn("loginBrowserNavModal not found: {0}", MODAL_SELECTOR);
			openSpan.end();
			return;
		}
		$modal.addClass("open");
		openSpan.end();
	};

	// 多重バインド防止
	$(window).off(".loginBrowserNav");

	$(window).on(
		"psfm:browserBackOrForward.loginBrowserNav psfm:browserReload.loginBrowserNav",
		(_event, _detail) => {
			LOG.info("browser nav detected -> open modal");
			openLoginBrowserNavModal();
		}
	);

	span.end();
};

/**
 * setCommonReady
 * - ログイン画面共通の初期化をここで完結させる
 */
export const setCommonReady = function(pageInit) {
	$(document).ready(function() {
		const span = LOG.logStart("setCommonReady(document.ready)", { level: "INFO", duration: true });

		try {
			setupCsrf();
			setBrowserGuard();
			setModal();
			setupLoginBrowserNavModal();

			if (typeof pageInit === "function") {
				const pi = LOG.logStart("pageInit()", { duration: true });
				try {
					pageInit();
				} catch (e) {
					LOG.error("pageInit failed", e);
					return endAndThrow(pi, e);
				}
				pi.end();
			}

			span.end();
		} catch (e) {
			LOG.error("setCommonReady failed", e);
			return endAndThrow(span, e);
		}
	});
};
