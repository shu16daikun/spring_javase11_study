/*
 * AttemptSessionRefLookUpImpl.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.attempt_session.internal
 * Author  : shu-kundeath
 * Created : 2025/11/01 18:29:46
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.attempt_session.internal;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.attempt_session.api.service.AttemptSessionRefLookUp;
import com.util.type.MyType;

import lombok.AllArgsConstructor;

/**
 * AttemptSessionRefLookUpImpl
 * 目的: TODO
 *
 * 公開契約:
 * - 例外は userCode のみ外部に出す（内部構造は伏せる）
 *
 * 備考:
 * - DTO は record を用いる
 */
@Service
@AllArgsConstructor
public class AttemptSessionRefLookUpImpl implements AttemptSessionRefLookUp {

	private final AttemptSessionRepository repository;

	@Override
	@Transactional(readOnly = true)
	public boolean existsByUserId(final String userId) {
		if (MyType.isBlank(userId))
			return false;
		return this.repository.existsByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsBySankouBookId(final String sankouBookId) {
		if (MyType.isBlank(sankouBookId))
			return false;
		return this.repository.existsBySankouBookId(sankouBookId);
	}

	/* ===== 追加：IN一括 ===== */
	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedUserIds(final Collection<String> userIds) {
		return (userIds == null || userIds.isEmpty())
			? Set.of()
			: this.repository.pickUsedUserIds(userIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedSankouBookIds(final Collection<String> sankouBookIds) {
		return (sankouBookIds == null || sankouBookIds.isEmpty())
			? Set.of()
			: this.repository.pickUsedSankouBookIds(sankouBookIds);
	}
}
