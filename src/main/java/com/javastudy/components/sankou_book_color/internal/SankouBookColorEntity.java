package com.javastudy.components.sankou_book_color.internal;

import com.javastudy.components.sankou_book_color.internal.SankouBookColorDB.SankouBookColorColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 機能：参考書カラー Entity（画面非公開） */
@Entity
@Getter
@Table(name = SankouBookColorDB.TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SankouBookColorEntity {

	/* 主キー（SC + 3桁、アプリ採番：DbIdSequence） */
	@Id
	@Column(name = SankouBookColorColumn.ID, nullable = false, unique = true, length = 5)
	private String id;

	/* 名称（varchar(20)） */
	@Column(name = SankouBookColorColumn.NAME, nullable = false, unique = true, length = 20)
	private String name;
}
