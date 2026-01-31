// com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto
package com.javastudy.components.sankou_books.api.dto;

import com.javastudy.components.sankou_book_color.api.domain.SankouBookColorEnum;

/**
 * 【機能】参考書ビューDTO（ユーザー向け出力）
 *
 * <p>
 * 目的：UI/他モジュールへ公開する不変の表示用DTO（Java21 record）。
 *
 * <h2>画面に見せるもの／ログ専用</h2>
 *
 * <ul>
 * <li>画面：<code>viewId</code> / <code>name</code> / <code>color</code> のみ。
 * <li>ログ：内部IDや構造は出さない（必要なら別層で）。
 * </ul>
 *
 * <h2>契約</h2>
 *
 * <ul>
 * <li><code>viewId</code>：UI契約の公開ID（形式・署名の仕様は IdBridge 側で管理）。
 * <li><code>name</code>：参考書名。
 * <li><code>color</code>：表示用の色カテゴリ。
 * </ul>
 */
public record USER_SankouBooksViewDto(String viewId, String name, SankouBookColorEnum color) {
}
