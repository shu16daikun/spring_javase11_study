package com.javastudy.components.answer_records.internal;

import com.util.security.id.IdBridge;

/* 機能：AnswerRecords IDブリッジ */
final class AnswerRecordsIdBridge {

	private static final String PREFIX = AnswerRecordsIdConstants.getPrefix();
	private static final int TOTAL = AnswerRecordsIdConstants.getTotalLength();

	private AnswerRecordsIdBridge() {
	}

	/* 機能：EntityID→擬似ID */
	static final String toViewId(final String entityId) {
		return IdBridge.toViewId(entityId);
	}

	/* 機能：擬似ID→EntityID候補 */
	static final String toEntityId(final String viewId) {
		return IdBridge.toEntityId(viewId, PREFIX, TOTAL);
	}

	/* 機能：照合（改ざん検知） */
	static final void assertMatches(final String viewId, final String entityId) {
		IdBridge.assertMatches(viewId, entityId);
	}
}
