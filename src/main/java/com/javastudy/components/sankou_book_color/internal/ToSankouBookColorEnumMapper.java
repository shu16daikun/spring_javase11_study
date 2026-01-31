// com/javastudy/components/sankou_book_color/internal/ToSankouBookColorEnumMapper.java
package com.javastudy.components.sankou_book_color.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.sankou_book_color.api.domain.SankouBookColorEnum;
import com.javastudy.components.sankou_book_color.api.exception.SankouBookColorException;
import com.javastudy.components.sankou_book_color.internal.SankouBookColorErrorCode.SankouBookColorDbgMsg;
import com.util.mapper.ToEnumMapper;

@Component
public class ToSankouBookColorEnumMapper
	implements ToEnumMapper<SankouBookColorEntity, SankouBookColorEnum> {

	private boolean isSameName(final SankouBookColorEnum e, final String other) {
		return e.getName().equals(other);
	}

	/* 名称→Enum */
	public SankouBookColorEnum fromName(final String name) {
		for (final SankouBookColorEnum e : SankouBookColorEnum.values()) {
			if (this.isSameName(e, name)) {
				return e;
			}
		}
		// IllegalArgumentException → SankouBookColorException(NOT_ENTITY) に変更
		throw new SankouBookColorException(
			SankouBookColorErrorCode.NOT_ENTITY,
			SankouBookColorDbgMsg.notEntity("name", name));
	}

	@Override
	public SankouBookColorEnum fromEntity(final SankouBookColorEntity entity) {
		return this.fromName(entity.getName());
	}
}
