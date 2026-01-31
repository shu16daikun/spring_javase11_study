// com.javastudy.exception_module.api.service.ErrorStateService
package com.javastudy.components.error.api.service;

import com.javastudy.components.error.api.dto.ErrorViewDto;
import java.util.Optional;

/**
 * エラーページ表示状態の短期保存・復元サービス（画面復元用の最小情報のみ扱う）。
 *
 * <p>
 * “画面に見せるもの”と“ログ専用”の線引きを厳守し、内部構造は保持しない。
 */
public interface ErrorStateService {

	/* ===== [public/protected] START ===== */

	/**
	 * エラービュー状態を traceId をキーに短期保存する。 実装側でTTL等の有効期限管理を行う（必要に応じて上書き許可）。
	 *
	 * @param traceId
	 *            復元用のトレースID（URLにも付与される想定）
	 * @param state
	 *            表示用のエラー状態（ErrorViewDto）
	 */
	void put(String traceId, ErrorViewDto state);

	/**
	 * traceId に対応するエラービュー状態を返す。 状態が無い／期限切れ等の場合は {@code Optional.empty()} を返す。
	 *
	 * @param traceId
	 *            復元用のトレースID
	 * @return 見つかった場合は状態、無ければ {@code Optional.empty()}
	 */
	Optional<ErrorViewDto> get(String traceId);

	/**
	 * traceId に紐づく状態を削除する。 明示的に破棄したい場合や、復元後の掃除に利用する。
	 *
	 * @param traceId
	 *            復元用のトレースID
	 */
	void remove(String traceId);

	/* ===== [public/protected] END ===== */
}
