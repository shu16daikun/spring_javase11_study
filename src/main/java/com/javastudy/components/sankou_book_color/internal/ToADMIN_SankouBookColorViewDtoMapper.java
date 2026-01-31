package com.javastudy.components.sankou_book_color.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.sankou_book_color.api.domain.SankouBookColorEnum;
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorViewDto;

@Component
public class ToADMIN_SankouBookColorViewDtoMapper {

	/** 純粋変換：外部解決が必要な `isUse` は呼び出し側で渡す。 */
	public ADMIN_SankouBookColorViewDto fromEntity(
		final SankouBookColorEnum colorEnum,
		final SankouBookColorEntity entity,
		final boolean isUse) {
		final String viewId = SankouBookColorIdBridge.toViewId(entity.getId());
		return new ADMIN_SankouBookColorViewDto(
			entity.getId(),
			viewId,
			entity.getName(),
			colorEnum,
			isUse);
	}
}
