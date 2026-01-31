/*
 * AuthorityFormParam.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.authority.internal
 * Author  : shu-kundeath
 * Created : 2025/10/26 13:20:25
 *
 * 目的:
 * - Authorityフォームの画面契約（フォーム名 / フィールド名 / 制約値）を一元管理
 *
 * 注意:
 * - 画面に露出しない internal の package-private
 */

package com.javastudy.components.authority.internal;

/* ===== [public/protected] START ===== */
final class AuthorityFormParam {

	/* 機能：フォーム名 */
	static final String FORM = "authorityForm";

	/* 機能：フィールド名 */
	static final String NAME = "systemName";

	/* 制約値（BeanValidationと整合） */
	// ROLE_ + 1..15 => 全体最大20
	static final int NAME_MIN = 1; // 例: A（最小）
	static final int NAME_MAX = 15;

	/* 生成禁止（用途限定のためデフォルト可視性で十分） */
	private AuthorityFormParam() {
	}
}
/* ===== [public/protected] END ===== */
