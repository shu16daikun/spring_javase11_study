// path: com/javastudy/components/weakness/internal/WeaknessRefLookupImpl.java
package com.javastudy.components.weakness.internal;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.weakness.api.service.WeaknessRefLookUp;
import com.util.type.MyType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeaknessRefLookupImpl implements WeaknessRefLookUp {

	private final WeaknessRepository repository;

	@Override
	@Transactional(readOnly = true)
	public boolean existsByUserId(final String userId) {
		return !MyType.isBlank(userId) && this.repository.existsByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsBySankouBookId(final String id) {
		return !MyType.isBlank(id) && this.repository.existsBySankouBookId(id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByChapterId(final String id) {
		return !MyType.isBlank(id) && this.repository.existsByChapterId(id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByKurohonQuestionId(final String id) {
		return !MyType.isBlank(id) && this.repository.existsByKurohonQuestionId(id);
	}

	/* ===== 追加：IN 一括判定 ===== */

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedUserIds(final Collection<String> userIds) {
		if (userIds == null || userIds.isEmpty())
			return Set.of();
		return this.repository.pickUsedUserIds(userIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedSankouBookIds(final Collection<String> sankouBookIds) {
		if (sankouBookIds == null || sankouBookIds.isEmpty())
			return Set.of();
		return this.repository.pickUsedSankouBookIds(sankouBookIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedChapterIds(final Collection<String> chapterIds) {
		if (chapterIds == null || chapterIds.isEmpty())
			return Set.of();
		return this.repository.pickUsedChapterIds(chapterIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedKurohonQuestionIds(final Collection<String> questionIds) {
		if (questionIds == null || questionIds.isEmpty())
			return Set.of();
		return this.repository.pickUsedKurohonQuestionIds(questionIds);
	}
}
