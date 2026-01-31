package com.javastudy.components.sankou_books.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.sankou_book_color.api.domain.SankouBookColorEnum;
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorViewDto;
import com.javastudy.components.sankou_books.api.dto.ADMIN_SankouBooksViewDto;

@Component
public class ToADMIN_SankouBooksViewDtoMapper {

	public ADMIN_SankouBooksViewDto fromEntity(
		final SankouBooksEntity entity,
		final SankouBookColorEnum colorEnum,
		final ADMIN_SankouBookColorViewDto colorV,
		final boolean isUse) {
		final String viewId = SankouBooksIdBridge.toViewId(entity.getId());
		return new ADMIN_SankouBooksViewDto(
			entity.getId(),
			viewId,
			entity.getName(),
			colorEnum,
			colorV,
			isUse);
	}
}
