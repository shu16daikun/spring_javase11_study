package com.javastudy.components.chapter.internal;

/* ===== [import] START ===== */
import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.chapter.api.service.ChapterRefLookUp;
import com.my.util.type.MyType;

import lombok.AllArgsConstructor;
/* ===== [import] END ===== */

@Service
@AllArgsConstructor
public class ChapterRefLookUpImpl implements ChapterRefLookUp {

	private final ChapterRepository repository;

	@Override
	@Transactional(readOnly = true)
	public boolean existsBySankouBookId(final String sankouBookId) {
		return !MyType.isBlank(sankouBookId) && this.repository.existsBySankouBookId(sankouBookId);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedSankouBookIds(final Collection<String> sankouBookIds) {
		return (sankouBookIds == null || sankouBookIds.isEmpty())
			? Set.of()
			: this.repository.pickUsedSankouBookIds(sankouBookIds);
	}
}
