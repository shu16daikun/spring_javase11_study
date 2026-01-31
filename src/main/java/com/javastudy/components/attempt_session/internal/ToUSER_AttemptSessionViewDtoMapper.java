package com.javastudy.components.attempt_session.internal;

import org.springframework.stereotype.Component;

import com.javastudy.components.attempt_session.api.dto.USER_AttemptSessionViewDto;
import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.login.components.user.api.dto.MyUsersViewDto;

/**
 * AttemptSessionEntity → USER_AttemptSessionViewDto 変換（純粋変換：DI依存なし）。
 */
@Component
public class ToUSER_AttemptSessionViewDtoMapper {

	USER_AttemptSessionViewDto fromEntity(
		final AttemptSessionEntity entity,
		final MyUsersViewDto usersViewDto,
		final USER_SankouBooksViewDto booksViewDto) {

		final String viewId = AttemptSessionIdBridge.toViewId(entity.getId());
		return new USER_AttemptSessionViewDto(viewId, usersViewDto, booksViewDto);
	}
}
