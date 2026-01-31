package com.javastudy.components.chapter.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.chapter.api.dto.USER_ChapterViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.util.type.MyType;

@Component
public class ToUSER_ChapterViewDtoMapper {

	USER_ChapterViewDto fromEntity(
		final ChapterEntity entity,
		final USER_SankouBooksViewDto booksViewDto) {
		final String vId = ChapterIdBridge.toViewId(MyType.orEmpty(entity.getId()));
		final String vNo = MyType.orEmpty(entity.getNo());
		final String vName = MyType.orEmpty(entity.getName());
		final USER_SankouBooksViewDto vBook = booksViewDto; // 呼び出し側で解決済み
		return new USER_ChapterViewDto(vId, vNo, vName, vBook);
	}
}
