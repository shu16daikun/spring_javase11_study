package com.javastudy.components.attempt_session.internal;

import com.util.security.id.IdBridge;

final class AttemptSessionIdBridge {

	private static final String PREFIX = AttemptSessionIdConstants.getPrefix();
	private static final int TOTAL = AttemptSessionIdConstants.getTotalLength();

	private AttemptSessionIdBridge() {
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
