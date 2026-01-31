package com.javastudy.components.weakness.internal;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.chapter.api.dto.ADMIN_ChapterViewDto;
import com.javastudy.components.chapter.api.service.ChapterService;
import com.javastudy.components.kurohon_questions.api.dto.ADMIN_KurohonQuestionsViewDto;
import com.javastudy.components.kurohon_questions.api.service.KurohonQuestionsService;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;
import com.javastudy.components.sankou_books.api.service.SankouBooksService;
import com.javastudy.components.users.api.dto.ADMIN_UsersViewDto;
import com.javastudy.components.users.api.service.UsersService;
import com.javastudy.components.weakness.api.dto.ADMIN_WeaknessViewDto;
import com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto.USER_WeaknessChapterViewDto;
import com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto.USER_WeaknessQuestionsViewDto;
import com.javastudy.components.weakness.api.exception.WeaknessException;
import com.javastudy.components.weakness.api.service.WeaknessService;
import com.javastudy.components.weakness.internal.WeaknessErrorCode.WeaknessDbgMsg;
import com.util.security.role.RoleUtil;
import com.util.type.MyType;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@PreAuthorize(RoleUtil.HAS_ROLE_USER)
public class WeaknessServiceImpl implements WeaknessService {

	/* 依存 */
	private final WeaknessRepository repository;
	private final ToUSER_WeaknessQuestionsViewDtoMapper qMapper;
	private final ToUSER_WeaknessChapterViewDtoMapper cMapper;
	private final ToADMIN_WeaknessViewDtoMapper toAdminMapper;
	private final UsersService usersService; // ID変換／AdminView構築
	private final SankouBooksService booksService; // AdminView構築
	private final ChapterService chapterService; // AdminView構築／UserView構築
	private final KurohonQuestionsService questionsService; // AdminView構築／UserView構築

	/* 画面用ID → 物理ID */
	@Transactional
	@Override
	public String getEntityId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new WeaknessException(
				WeaknessErrorCode.BLANK_ID,
				WeaknessDbgMsg.blankId("viewId"));
		}
		return WeaknessIdBridge.toEntityId(viewId);
	}

	/* 問題単位：物理ID指定 → ViewDto */
	@Transactional
	@Override
	public USER_WeaknessQuestionsViewDto getQuestionsViewDtoById(final String id) {
		if (MyType.isBlank(id)) {
			throw new WeaknessException(WeaknessErrorCode.BLANK_ID, WeaknessDbgMsg.blankId("id"));
		}
		final WeaknessEntity entity = this.getEntity(id);
		return this.qMapper.fromEntity(
			entity,
			this.chapterService.getUserViewDtoById(entity.getChapterId()),
			this.questionsService.getUserViewDtoById(entity.getKurohonQuestionId()));
	}

	/* 問題単位：画面用ID指定 → ViewDto */
	@Transactional
	@Override
	public USER_WeaknessQuestionsViewDto getQuestionsViewDtoByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new WeaknessException(
				WeaknessErrorCode.BLANK_ID,
				WeaknessDbgMsg.blankId("viewId"));
		}
		return this.getQuestionsViewDtoById(WeaknessIdBridge.toEntityId(viewId));
	}

	/* ===== 管理向け：全件取得（高速化版） ===== */
	@Transactional(readOnly = true)
	@Override
	public List<ADMIN_WeaknessViewDto> getAdminViewDtoList() {
		// ① 本体を一括取得
		final List<WeaknessEntity> rows = this.repository.findAll();
		if (rows.isEmpty()) {
			return List.of();
		}

		// ② 参照IDをセットに集約（重複排除）
		final Set<String> userIds = rows.stream()
			.map(WeaknessEntity::getUserId)
			.collect(Collectors.toSet());

		final Set<String> bookIds = rows.stream()
			.map(WeaknessEntity::getSankouBookId)
			.collect(Collectors.toSet());

		final Set<String> chapterIds = rows.stream()
			.map(WeaknessEntity::getChapterId)
			.collect(Collectors.toSet());

		final Set<String> questionIds = rows.stream()
			.map(WeaknessEntity::getKurohonQuestionId)
			.collect(Collectors.toSet());

		// ③ 参照Viewをまとめて解決（Service側で IN 最適化してる想定）
		final Map<String, ADMIN_UsersViewDto> usersMap = this.usersService
			.getViewDtoMapByIds(userIds);

		final Map<String, ADMIN_SankouBooksViewDto> booksMap = this.booksService
			.getAdminViewDtoMapByIds(bookIds);

		final Map<String, ADMIN_ChapterViewDto> chaptersMap = this.chapterService
			.getAdminViewDtoMapByIds(chapterIds);

		final Map<String, ADMIN_KurohonQuestionsViewDto> questionsMap = this.questionsService
			.getAdminViewDtoMapByIds(questionIds);

		// ④ 並び順は今までと同じ：userId → bookId → chapterId → totalAttempts(desc)
		return rows.stream()
			.sorted(
				Comparator
					.comparing(WeaknessEntity::getUserId, Comparator.nullsLast(String::compareTo))
					.thenComparing(WeaknessEntity::getSankouBookId,
						Comparator.nullsLast(String::compareTo))
					.thenComparing(WeaknessEntity::getChapterId,
						Comparator.nullsLast(String::compareTo))
					.thenComparing(
						Comparator.comparingInt(WeaknessEntity::getTotalAttempts).reversed()))
			.map(e -> this.toAdminMapper.fromEntity(
				e,
				usersMap.get(e.getUserId()),
				booksMap.get(e.getSankouBookId()),
				chaptersMap.get(e.getChapterId()),
				questionsMap.get(e.getKurohonQuestionId())))
			.toList();
	}

	/* 問題単位：物理ID指定 → ViewDto */
	@Transactional
	@Override
	public ADMIN_WeaknessViewDto getAdminViewDtoById(final String id) {
		if (MyType.isBlank(id)) {
			throw new WeaknessException(WeaknessErrorCode.BLANK_ID, WeaknessDbgMsg.blankId("id"));
		}
		final WeaknessEntity e = this.getEntity(id);
		return this.toAdminMapper.fromEntity(
			e,
			this.usersService.getAdminViewDtoById(e.getUserId()),
			this.booksService.getAdminViewDtoById(e.getSankouBookId()),
			this.chapterService.getAdminViewDtoById(e.getChapterId()),
			this.questionsService.getAdminViewDtoById(e.getKurohonQuestionId()));
	}

	/* 問題単位：画面用ID指定 → ViewDto */
	@Transactional
	@Override
	public ADMIN_WeaknessViewDto getAdminViewDtoByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new WeaknessException(
				WeaknessErrorCode.BLANK_ID,
				WeaknessDbgMsg.blankId("viewId"));
		}
		final String id = WeaknessIdBridge.toEntityId(viewId);
		final WeaknessEntity e = this.getEntity(id);
		return this.toAdminMapper.fromEntity(
			e,
			this.usersService.getAdminViewDtoById(e.getUserId()),
			this.booksService.getAdminViewDtoById(e.getSankouBookId()),
			this.chapterService.getAdminViewDtoById(e.getChapterId()),
			this.questionsService.getAdminViewDtoById(e.getKurohonQuestionId()));
	}

	/** 問題単位の弱点一覧（弱い順）＝ 正答率↑ → 試行回数↓ */
	@Transactional
	@Override
	public List<USER_WeaknessQuestionsViewDto> listWeakQuestions(
		final String userViewId,
		final String bookViewId,
		final int minAttempts,
		final int limit) {

		if (MyType.isBlank(userViewId) || MyType.isBlank(bookViewId)) {
			throw new WeaknessException(
				WeaknessErrorCode.BLANK_ID,
				WeaknessDbgMsg.blankId("userViewId/bookViewId"));
		}
		final String userId = this.usersService.getEntityId(userViewId);
		final String bookId = this.booksService.getEntityId(bookViewId);

		final var entities = this.repository.findWeakQuestions(userId, bookId, minAttempts);

		final var sorted = entities.stream()
			.map(e -> this.qMapper.fromEntity(
				e,
				this.chapterService.getUserViewDtoById(e.getChapterId()),
				this.questionsService.getUserViewDtoById(e.getKurohonQuestionId())))
			.sorted(
				Comparator.comparingDouble(USER_WeaknessQuestionsViewDto::correctRate)
					.thenComparing(
						Comparator.comparingInt(USER_WeaknessQuestionsViewDto::totalAttempts)
							.reversed()))
			.toList();

		return (limit > 0 && sorted.size() > limit) ? sorted.subList(0, limit) : sorted;
	}

	/** 章単位の弱点一覧（弱い順）＝ 正答率↑ → 試行回数↓ */
	@Transactional
	@Override
	public List<USER_WeaknessChapterViewDto> listWeakChapters(
		final String userViewId,
		final String bookViewId) {

		if (MyType.isBlank(userViewId) || MyType.isBlank(bookViewId)) {
			throw new WeaknessException(
				WeaknessErrorCode.BLANK_ID,
				WeaknessDbgMsg.blankId("userViewId/bookViewId"));
		}
		final String userId = this.usersService.getEntityId(userViewId);
		final String bookId = this.booksService.getEntityId(bookViewId);

		return this.repository.aggregateByChapter(userId, bookId).stream()
			.map(a -> this.cMapper.fromAgg(a,
				this.chapterService.getUserViewDtoById(a.getChapterId())))
			.sorted(
				Comparator.comparingDouble(USER_WeaknessChapterViewDto::correctRate)
					.thenComparing(
						Comparator.comparingInt(USER_WeaknessChapterViewDto::totalAttempts)
							.reversed()))
			.toList();
	}

	/* Repoヘルパ：Id → Entity（404に変換） */
	@Transactional
	WeaknessEntity getEntity(final String id) {
		if (MyType.isBlank(id)) {
			throw new WeaknessException(WeaknessErrorCode.BLANK_ID, WeaknessDbgMsg.blankId("id"));
		}
		return this.repository
			.findById(id)
			.orElseThrow(() -> new WeaknessException(
				WeaknessErrorCode.NOT_ENTITY,
				WeaknessDbgMsg.notEntity(id)));
	}

}
