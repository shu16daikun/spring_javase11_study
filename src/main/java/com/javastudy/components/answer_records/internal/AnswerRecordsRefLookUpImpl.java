package com.javastudy.components.answer_records.internal;

/* ===== [import] START ===== */
import java.util.Collection;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.answer_records.api.service.AnswerRecordsRefLookUp;
import com.util.type.MyType;

import lombok.AllArgsConstructor;
/* ===== [import] END ===== */

@Service
@AllArgsConstructor
public class AnswerRecordsRefLookUpImpl implements AnswerRecordsRefLookUp {

	private final AnswerRecordsRepository repository;

	@Override
	@Transactional(readOnly = true)
	public boolean existsByUserId(final String userId) {
		return !MyType.isBlank(userId) && this.repository.existsByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsBySankouBookId(final String sankouBookId) {
		return !MyType.isBlank(sankouBookId) && this.repository.existsBySankouBookId(sankouBookId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByChapterId(final String chapterId) {
		return !MyType.isBlank(chapterId) && this.repository.existsByChapterId(chapterId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByKurohonQuestionId(final String kurohonQuestionId) {
		return !MyType.isBlank(kurohonQuestionId)
			&& this.repository.existsByKurohonQuestionId(kurohonQuestionId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByAttemptSessionId(final String attemptSessionId) {
		return !MyType.isBlank(attemptSessionId)
			&& this.repository.existsByAttemptSessionId(attemptSessionId);
	}

	/* ===== 追加：IN一括 ===== */

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedUserIds(final Collection<String> userIds) {
		return (userIds == null || userIds.isEmpty()) ? Set.of()
			: this.repository.pickUsedUserIds(userIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedSankouBookIds(final Collection<String> sankouBookIds) {
		return (sankouBookIds == null || sankouBookIds.isEmpty()) ? Set.of()
			: this.repository.pickUsedSankouBookIds(sankouBookIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedChapterIds(final Collection<String> chapterIds) {
		return (chapterIds == null || chapterIds.isEmpty()) ? Set.of()
			: this.repository.pickUsedChapterIds(chapterIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedKurohonQuestionIds(final Collection<String> questionIds) {
		return (questionIds == null || questionIds.isEmpty()) ? Set.of()
			: this.repository.pickUsedKurohonQuestionIds(questionIds);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findUsedAttemptSessionIds(final Collection<String> sessionIds) {
		return (sessionIds == null || sessionIds.isEmpty()) ? Set.of()
			: this.repository.pickUsedAttemptSessionIds(sessionIds);
	}
}
