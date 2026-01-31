// /js/util/csrf.js
import { getLoger, endAndReturn, endAndThrow } from "/psfm/js/common/loger.js";

const LOG = getLoger("csrf");

/** Headers / Object を雑にマージして Headers にする（nullish は捨てる） */
function mergeHeaders(a, b) {
	const out = new Headers(a || {});
	if (b && typeof b === "object") {
		for (const [k, v] of Object.entries(b)) {
			if (v == null) continue;
			out.set(k, String(v));
		}
	}
	return out;
}

export function csrfMeta() {
	const span = LOG.logStart("csrfMeta");

	const token = document.querySelector('meta[name="_csrf"]')?.content || "";
	const headerName =
		document.querySelector('meta[name="_csrf_header"]')?.content || "X-CSRF-TOKEN";
	const param = document.querySelector('meta[name="_csrf_parameter"]')?.content || "_csrf";

	return endAndReturn(span, { token, headerName, param });
}

export function csrfHeader() {
	const span = LOG.logStart("csrfHeader");

	const { token, headerName } = csrfMeta();
	const headers = token ? { [headerName]: token } : {};

	return endAndReturn(span, headers);
}

export function appendCsrfToForm(form) {
	const span = LOG.logStart("appendCsrfToForm");

	const { token, param } = csrfMeta();
	if (!form) {
		LOG.warn("appendCsrfToForm: form is null");
		span.end();
		return;
	}
	if (!token) {
		LOG.debug("appendCsrfToForm: csrf token is blank");
		span.end();
		return;
	}

	const exists = form.querySelector(`input[name="${param}"]`);
	if (exists) {
		exists.value = token;
		span.end();
		return;
	}

	const input = document.createElement("input");
	input.type = "hidden";
	input.name = param;
	input.value = token;
	form.appendChild(input);

	span.end();
}

export function withCsrf(init = {}) {
	const span = LOG.logStart("withCsrf");

	const headers = mergeHeaders(init.headers, csrfHeader());
	return endAndReturn(span, { ...init, headers });
}

export function postJson(url, body, init = {}) {
	const label = `postJson ${String(url || "")}`;
	const span = LOG.logStart(label, { level: "INFO", duration: true });

	const reqInit = withCsrf(init);
	const headers = mergeHeaders(reqInit.headers, { "Content-Type": "application/json" });

	LOG.debug("{0} request", label);

	return fetch(url, {
		method: "POST",
		headers,
		body: JSON.stringify(body),
		...reqInit
	})
		.then((res) => {
			LOG.info("{0} response status={1}", label, res?.status);
			return res;
		})
		.catch((err) => {
			LOG.error("{0} failed", err);
			return endAndThrow(span, err);
		})
		.finally(() => {
			span.end();
		});
}
