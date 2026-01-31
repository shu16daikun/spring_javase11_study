package com.javastudy.components.sankou_book_color.internal;

import com.util.security.id.IdBridge;

/* 機能：SankouBookColor IDブリッジ */
final class SankouBookColorIdBridge {

	private static final String PREFIX = SankouBookColorIdConstants.getPrefix();
	private static final int TOTAL = SankouBookColorIdConstants.getTotalLength();

	private SankouBookColorIdBridge() {
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
