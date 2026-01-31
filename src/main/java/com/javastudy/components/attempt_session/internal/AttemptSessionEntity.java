package com.javastudy.components.attempt_session.internal;

import com.javastudy.components.attempt_session.internal.AttemptSessionDB.AttemptSessionColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

/* 機能：解答セッション Entity（画面非公開） */
@Entity
@Table(name = AttemptSessionDB.TABLE)
@Getter
@DynamicInsert // null列をINSERTから外し、DB DEFAULT(started_at)を尊重
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AttemptSessionEntity {

	/* 主キー（AS + 10桁、アプリ採番：DbIdSequence） */
	@Id
	@Column(name = AttemptSessionColumn.ID, nullable = false, unique = true, length = 12)
	private String id;

	/* ユーザーID（他モジュール Entity を直参照しない） */
	@Column(name = AttemptSessionColumn.USER_ID, nullable = false, length = 12)
	private String userId;

	/* 参考書ID */
	@Column(name = AttemptSessionColumn.SANKOU_BOOK_ID, nullable = false, length = 12)
	private String sankouBookId;

	/* 開始時刻（DB DEFAULT now() を使用。INSERT時はアプリから送らない） */
	@Column(name = AttemptSessionColumn.STARTED_AT, nullable = false, insertable = false, updatable = false)
	private LocalDateTime startedAt;

	/* 終了時刻（JPQLで CURRENT_TIMESTAMP を設定） */
	@Column(name = AttemptSessionColumn.FINISHED_AT)
	private LocalDateTime finishedAt;

	/* ユーティリティ */
	public final boolean isFinished() {
		return finishedAt != null;
	}

	public final void finishNow() {
		this.finishedAt = LocalDateTime.now();
	}
}
