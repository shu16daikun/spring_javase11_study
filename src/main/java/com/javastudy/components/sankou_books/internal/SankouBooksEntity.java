package com.javastudy.components.sankou_books.internal;

import com.javastudy.components.sankou_books.internal.SankouBooksDB.SankouBooksColumn;
import com.my.util.type.MyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 機能：参考書 Entity（画面非公開） */
@Entity
@Table(name = SankouBooksDB.TABLE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SankouBooksEntity {

	/* 主キー（SA + 5桁、アプリ採番：DbIdSequence） */
	@Id
	@Column(name = SankouBooksColumn.ID, nullable = false, unique = true, length = 7)
	private String id;

	/* 名称（varchar(100) / UNIQUE） */
	@Column(name = SankouBooksColumn.NAME, nullable = false, unique = true, length = 100)
	private String name;

	/* 参照ID（カラー：varchar(5)／NULL許容だが本実装では必須扱い） */
	@Column(name = SankouBooksColumn.COLOR_ID, nullable = true, length = 5)
	private String colorId;

	/* 保存前補正（NOT NULL防御の最低限。Service層でBLANKを弾いている想定） */
	@PrePersist
	private void onPrePersist() {
		if (isNull(name)) {
			name = MyType.EMPTY;
		}
	}

	private static boolean isNull(final Object o) {
		return o == null;
	}
}
