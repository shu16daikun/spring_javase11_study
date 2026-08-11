/*
 * KurohonQuestionsRefLookUp.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.kurohon_questions.internal
 * Author  : shu-kundeath
 * Created : 2025/11/01 18:34:43
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.kurohon_questions.internal;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsRefLookUp;
import com.my.util.type.MyType;

import lombok.AllArgsConstructor;

/**
 * KurohonQuestionsRefLookUp
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
public class KurohonQuestionsRefLookUpImpl implements KurohonQuestionsRefLookUp {

	private final KurohonQuestionsRepository repository;

	@Override
	@Transactional(readOnly = true)
	public boolean existsBySankouBookId(final String sankouBookId) {
		if (MyType.isBlank(sankouBookId))
			return false;
		return this.repository.existsBySankouBookId(sankouBookId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByChapterId(final String chapterId) {
		if (MyType.isBlank(chapterId))
			return false;
		return this.repository.existsByChapterId(chapterId);
	}

	/* ★ 追加：バルク */

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedSankouBookIds(final Collection<String> sankouBookIds) {
		return (sankouBookIds == null || sankouBookIds.isEmpty())
			? Set.of()
			: this.repository.pickUsedSankouBookIds(sankouBookIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedChapterIds(final Collection<String> chapterIds) {
		return (chapterIds == null || chapterIds.isEmpty())
			? Set.of()
			: this.repository.pickUsedChapterIds(chapterIds);
	}
}
