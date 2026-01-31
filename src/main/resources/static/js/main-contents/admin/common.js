// /js/main-contents/admin/common.js

import { setNavbarAdmin } from "/psfm/js/fragment/navbar.js";
import { setModal } from "/psfm/js/fragment/modal.js";
import { setBrowserGuard } from "/psfm/js/common/browser-guard.js";

import { getLoger, endAndReturn, endAndThrow } from "/psfm/js/common/loger.js";

const LOG = getLoger("admin.common");

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

	// 多重バインド防止（この関数が複数回呼ばれた場合に備える）
	$(document).off("ajaxSend.adminCsrf").on("ajaxSend.adminCsrf", function(_e, xhr) {
		if (token) {
			xhr.setRequestHeader(header || "X-CSRF-TOKEN", token);
		}
	});

	span.end();
};

/**
 * 管理者画面用：ブラウザ戻る／進む／リロード時にモーダルを開く
 * - 対象モーダル：#adminBrowserNavModal
 * - イベント：psfm:browserBackOrForward / psfm:browserReload
 */
const setupAdminBrowserNavModal = function() {
	const span = LOG.logStart("setupAdminBrowserNavModal");

	const MODAL_SELECTOR = "#adminBrowserNavModal";

	const openAdminBrowserNavModal = () => {
		const openSpan = LOG.logStart("openAdminBrowserNavModal");
		const $modal = $(MODAL_SELECTOR);
		if (!$modal.length) {
			LOG.warn("adminBrowserNavModal not found: {0}", MODAL_SELECTOR);
			openSpan.end();
			return;
		}
		$modal.addClass("open");
		openSpan.end();
	};

	// 多重バインド防止
	$(window).off(".adminBrowserNav");

	// 戻る／進むとリロードで同じ挙動
	$(window).on(
		"psfm:browserBackOrForward.adminBrowserNav psfm:browserReload.adminBrowserNav",
		(_event, _detail) => {
			LOG.info("browser nav detected -> open modal");
			openAdminBrowserNavModal();
		}
	);

	span.end();
};

/**
 * setCommonReady
 * - 管理者画面共通の初期化をここで完結させる
 */
export const setCommonReady = function(pageInit) {
	$(document).ready(function() {
		const span = LOG.logStart("setCommonReady(document.ready)", { level: "INFO", duration: true });

		try {
			setupCsrf();
			setBrowserGuard();
			setModal();
			setNavbarAdmin();
			setupAdminBrowserNavModal();

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

// 好きなら setReady でも呼べるようにエイリアス
export const setReady = setCommonReady;
