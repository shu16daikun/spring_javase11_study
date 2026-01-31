package com.javastudy.components.chapter.internal;

import com.javastudy.components.chapter.internal.ChapterDB.ChapterColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

/* 機能：章 Entity（画面非公開） */
@Entity
@Table(name = ChapterDB.TABLE)
@Getter
@DynamicInsert // null列をINSERTから外し、DB DEFAULTを尊重
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ChapterEntity {

	/* 主キー（CH + 8桁、アプリ採番：DbIdSequence） */
	@Id
	@Column(name = ChapterColumn.ID, nullable = false, unique = true, length = 10)
	private String id;

	/* 章番号（varchar(2)：例 1, 2, …） */
	@Column(name = ChapterColumn.NO, nullable = false, length = 2)
	private String no;

	/* 章名（varchar(50)） */
	@Column(name = ChapterColumn.NAME, nullable = false, length = 50)
	private String name;

	/* 参照ID（参考書：varchar(7)） */
	@Column(name = ChapterColumn.SANKOU_BOOK_ID, nullable = false, length = 7)
	private String sankouBookId;
}
