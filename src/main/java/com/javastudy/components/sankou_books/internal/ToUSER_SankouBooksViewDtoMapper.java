package com.javastudy.components.sankou_books.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.sankou_book_color.api.domain.SankouBookColorEnum;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.util.type.MyType;

@Component
public class ToUSER_SankouBooksViewDtoMapper {

	USER_SankouBooksViewDto fromEntity(
		final SankouBooksEntity entity,
		final SankouBookColorEnum colorEnum) {
		final String vId = SankouBooksIdBridge.toViewId(MyType.orEmpty(entity.getId()));
		final String vName = MyType.orEmpty(entity.getName());
		return new USER_SankouBooksViewDto(vId, vName, colorEnum);
	}
}
