// com.javastudy.exception_module.api.dto.ErrorViewDto
package com.javastudy.components.error.api.dto;

/**
 * 例外表示用のビューDTO。 用途：ログイン／ユーザー／管理画面の汎用エラーページで表示する最低限の情報を束ねる。
 * 注意：“画面に見せるもの”のみ保持し、内部構造・スタックトレースは持たない（ログ専用情報とは分離）。
 *
 * @param status
 *            HTTPステータスコード（表示用）
 * @param errorCode
 *            問い合わせ番号（短いコード。内部の詳細構造は出さない）
 * @param errorMessage
 *            ローカライズ済みメッセージ（画面表示OK）
 * @param createdAtEpochMilli
 *            生成時刻（epoch milli。復元／TTL判定等に利用）
 */
public record ErrorViewDto(
	int status,
	String errorCode,
	String errorMessage,
	long createdAtEpochMilli) {
}
