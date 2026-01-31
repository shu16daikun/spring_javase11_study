package com.javastudy.components.users.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 【機能】ユーザー更新入力DTO（ユーザー自身）
 *
 * <p>
 * 目的：ユーザー名更新の入力を受け付ける不変DTO（Java21 record）。
 *
 * <h2>契約（入力制約）</h2>
 *
 * <ul>
 * <li><code>username</code>：必須、最大30文字（<code>@NotBlank</code>,
 * <code>@Size(max=30)</code>）。
 * </ul>
 *
 * <h2>設計メモ</h2>
 *
 * <ul>
 * <li>UIのバリデーションは補助であり、最終判定は本DTOのBean Validationで行う。
 * <li>内部実装やエンティティ構造は公開しない（DTOは境界オブジェクト）。
 * </ul>
 */
public record USER_UsersInputDto(@NotBlank @Size(max = 30) String username) {
}
