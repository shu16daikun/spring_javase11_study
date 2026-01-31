package com.javastudy.components.attempt_session.api.dto;

import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;
import com.login.components.user.api.dto.MyUsersViewDto;

/* 機能：解答セッション表示DTO（ユーザー） */
public record USER_AttemptSessionViewDto(
	String viewId,
	MyUsersViewDto usersViewDto,
	USER_SankouBooksViewDto sankouBooksViewDto) {
}
