/*
 * AttemptSessionRefLookUp.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.attempt_session.api.service
 * Author  : shu-kundeath
 * Created : 2025/11/01 18:29:09
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.attempt_session.api.service;

import java.util.Collection;
import java.util.Set;

/**
 * AttemptSessionRefLookUp
 * 目的: TODO
 *
 * 公開契約:
 * - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * 備考:
 * - DTO は record を用いる
 */

public interface AttemptSessionRefLookUp {
	/** Users の entity側ID が AttemptSession に存在するか（true=使用中=削除不可） */
	boolean existsByUserId(String userId);

	/** SankouBooks の entity側ID が AttemptSession に存在するか（true=使用中） */
	boolean existsBySankouBookId(String sankouBookId);

	/* ▼ 追加：IN一括で“存在したIDだけ”返す */
	Set<String> findUsedUserIds(Collection<String> userIds);

	Set<String> findUsedSankouBookIds(Collection<String> sankouBookIds);

}
