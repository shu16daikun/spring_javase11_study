/*
 * SankouBooksRefLookUpImpl.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.sankou_books.internal
 * Author  : shu-kundeath
 * Created : 2025/11/01 18:37:30
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.sankou_books.internal;

import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.sankou_books.api.service.SankouBooksRefLookUp;
import com.util.type.MyType;

import lombok.AllArgsConstructor;

/**
 * SankouBooksRefLookUpImpl
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
public class SankouBooksRefLookUpImpl implements SankouBooksRefLookUp {

	private final SankouBooksRepository repository;

	@Transactional(readOnly = true)
	@Override
	public boolean existsByColorId(final String colorId) {
		if (MyType.isBlank(colorId))
			return false;
		return this.repository.existsByColorId(colorId);
	}

	/* ★追加：Color 使用中 ID を一括取得 */
	@Transactional(readOnly = true)
	@Override
	public Set<String> findUsedColorIds(final Collection<String> colorIds) {
		if (colorIds == null || colorIds.isEmpty())
			return Set.of();
		return this.repository.pickUsedColorIds(colorIds);
	}
}
