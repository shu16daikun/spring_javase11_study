// com.javastudy.exception_module.internal.ErrorStateServiceImpl
package com.javastudy.components.error.internal;

import com.javastudy.components.error.api.dto.ErrorViewDto;
import com.javastudy.components.error.api.service.ErrorStateService;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/**
 * エラー表示状態の短期保存ストア（再読込復元用）。
 *
 * <p>
 * 揮発性・TTLつき・メモリ内。永続化は不要想定。
 */
@Service
public class ErrorStateServiceImpl implements ErrorStateService { // public 非final（AOP方針）

	/* ===== [private] START ===== */
	private final ConcurrentHashMap<String, ErrorViewDto> store = new ConcurrentHashMap<>();
	private static final long TTL_MILLIS = TimeUnit.MINUTES.toMillis(30); // 期限（要件に応じて調整）

	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	/** {@inheritDoc} */
	@Override
	public void put(final String traceId, final ErrorViewDto state) {
		store.put(traceId, state);
	}

	/** {@inheritDoc} */
	@Override
	public Optional<ErrorViewDto> get(final String traceId) {
		final ErrorViewDto s = store.get(traceId);
		if (s == null) {
			return Optional.empty();
		}
		if (isExpired(s)) {
			store.remove(traceId);
			return Optional.empty();
		}
		return Optional.of(s);
	}

	/** {@inheritDoc} */
	@Override
	public void remove(final String traceId) {
		store.remove(traceId);
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/** 機能：TTL 超過判定 */
	private boolean isExpired(final ErrorViewDto s) {
		return System.currentTimeMillis() - s.createdAtEpochMilli() > TTL_MILLIS;
	}
	/* ===== [private] END ===== */
}
