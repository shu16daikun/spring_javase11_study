/*
 * KurohonQuestionsRefLookUp.java
 * Project : spring_javase11_study
 * Package : com.javastudy.components.kurohon_questions.api.service
 * Author  : shu-kundeath
 * Created : 2025/11/01 18:34:04
 *
 * 目的:
 * - TODO
 *
 * 注意:
 * - 定数/文字列の扱いは規約に従う（文字列は private static final String）
 */

package com.javastudy.components.kurohon_questions.api.service;

import java.util.Collection;
import java.util.Set;

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

public interface KurohonQuestionsRefLookUp {
	/** SankouBooks の entity側ID が KurohonQuestions に参照されているか（true=使用中） */
	boolean existsBySankouBookId(String sankouBookId);

	/** Chapters の entity側ID が KurohonQuestions に参照されているか（true=使用中） */
	boolean existsByChapterId(String chapterId);

	/* ★ 追加：バルク問い合わせ */
	Set<String> findUsedSankouBookIds(Collection<String> sankouBookIds);

	Set<String> findUsedChapterIds(Collection<String> chapterIds);

}
