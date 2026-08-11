package com.javastudy.components.weakness.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.weakness.api.agg.WeaknessChapterAgg;
import com.javastudy.components.weakness.api.dto.USER_WeaknessViewDto.USER_WeaknessChapterViewDto;
import com.my.util.type.MyType;

@Component
public class ToUSER_WeaknessChapterViewDtoMapper {

	public USER_WeaknessChapterViewDto fromAgg(
		final WeaknessChapterAgg a,
		final USER_ChapterViewDto chapterViewDto) {
		final int attempts = a.getTotalAttempts().intValue();
		final int correct = a.getCorrectCount().intValue();
		final int wrong = a.getWrongCount().intValue();
		final double rate = (attempts == 0) ? 0.0 : MyType.round1(100.0 * correct / attempts);

		return new USER_WeaknessChapterViewDto(
			chapterViewDto,
			rate,
			attempts,
			correct,
			wrong);
	}
}
