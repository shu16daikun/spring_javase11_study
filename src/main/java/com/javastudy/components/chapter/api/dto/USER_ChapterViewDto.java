package com.javastudy.components.chapter.api.dto;

import com.javastudy.components.sankou_books.api.dto.USER_SankouBooksViewDto;

/* 機能：章ビューDTO（ユーザー） */
public record USER_ChapterViewDto(
	String viewId,
	String no,
	String name,
	USER_SankouBooksViewDto sankouBooksViewDto) {
}
