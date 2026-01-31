// com/javastudy/components/sankou_book_color/internal/SankouBookColorServiceImpl.java
package com.javastudy.components.sankou_book_color.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javastudy.components.sankou_book_color.api.domain.SankouBookColorEnum;
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorInputDto;
import com.javastudy.components.sankou_book_color.api.dto.ADMIN_SankouBookColorViewDto;
import com.javastudy.components.sankou_book_color.api.exception.SankouBookColorException;
import com.javastudy.components.sankou_book_color.api.service.SankouBookColorService;
import com.javastudy.components.sankou_book_color.internal.SankouBookColorDB.SankouBookColorIdParam;
import com.javastudy.components.sankou_book_color.internal.SankouBookColorErrorCode.SankouBookColorDbgMsg;
import com.javastudy.components.sankou_books.api.service.SankouBooksRefLookUp;
import com.util.security.id.DbIdSequence;
import com.util.type.MyType;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;

/* 機能：参考書カラー取得サービス（ユーザー向け） */
@Service
@AllArgsConstructor
public class SankouBookColorServiceImpl implements SankouBookColorService {

	private final SankouBookColorRepository repository;
	private final ToSankouBookColorEnumMapper toEnumMapper;
	private final ToADMIN_SankouBookColorViewDtoMapper toViewDtoMapper;
	private final DbIdSequence idSeq; // 採番器（アプリ側）

	// 参照チェックは RefLookup に委譲（Books 直接依存なし）
	private final SankouBooksRefLookUp sankouBooksRefLookup;

	@PersistenceContext
	private final EntityManager em;

	private String trim(final String s) {
		return s == null ? null : s.trim();
	}

	/* ===== IDユーティリティ ===== */

	/** 新規 Color 用IDを採番（シーケンスと既存IDの衝突を自動回避） */
	String allocateNewId() {
		return this.idSeq.nextIdAvoidCollision(
			SankouBookColorIdParam.SEQUENCE,
			SankouBookColorIdParam.PREFIX,
			SankouBookColorIdParam.PAD,
			SankouBookColorDB.TABLE_FQN,
			SankouBookColorDB.SankouBookColorColumn.ID);
	}

	/* ===== 参照系 ===== */

	@Override
	@Transactional(readOnly = true)
	public Map<String, ADMIN_SankouBookColorViewDto> getAdminViewDtoByIds(final Set<String> idSet) {
		if (idSet == null || idSet.isEmpty()) {
			return Map.of();
		}
		final List<SankouBookColorEntity> colors = this.repository.findAllByIdIn(idSet);
		final Set<String> usedColorIds = this.sankouBooksRefLookup.findUsedColorIds(idSet);

		return colors.stream().collect(Collectors.toMap(
			SankouBookColorEntity::getId,
			entity -> {
				final SankouBookColorEnum e = this.toEnumMapper.fromEntity(entity);
				final boolean used = usedColorIds.contains(entity.getId());
				return this.toViewDtoMapper.fromEntity(e, entity, used);
			}));
	}

	@Override
	@Transactional(readOnly = true)
	public SankouBookColorEnum getEnumByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.BLANK_ID,
				SankouBookColorDbgMsg.blankId("viewId"));
		}
		final String id = this.getEntityId(viewId);
		final SankouBookColorEntity entity = this.getEntityById(id);
		return this.toEnumMapper.fromEntity(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public SankouBookColorEnum getEnumById(final String id) {
		if (MyType.isBlank(id)) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.BLANK_ID,
				SankouBookColorDbgMsg.blankId("id"));
		}
		final SankouBookColorEntity entity = this.getEntityById(id);
		return this.toEnumMapper.fromEntity(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public String getEntityId(final String viewId) {
		return SankouBookColorIdBridge.toEntityId(viewId);
	}

	@Override
	@Transactional(readOnly = true)
	public ADMIN_SankouBookColorViewDto getAdminViewDtoById(final String id) {
		final SankouBookColorEntity entity = this.getEntityById(id);
		final SankouBookColorEnum e = this.getEnumById(entity.getId());
		return this.toViewDtoMapper.fromEntity(e, entity, this.isUse(entity.getId()));
	}

	@Override
	@Transactional(readOnly = true)
	public ADMIN_SankouBookColorViewDto getAdminViewDtoByViewId(final String viewId) {
		final String id = SankouBookColorIdBridge.toEntityId(viewId);
		final SankouBookColorEntity entity = this.getEntityById(id);
		final SankouBookColorEnum e = this.getEnumById(entity.getId());
		return this.toViewDtoMapper.fromEntity(e, entity, this.isUse(entity.getId()));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ADMIN_SankouBookColorViewDto> getAdminViewDtoList() {
		final List<SankouBookColorEntity> rows = this.repository.findAllByOrderByNameAsc();
		if (rows.isEmpty()) {
			return List.of();
		}
		final Set<String> colorIds = rows.stream()
			.map(SankouBookColorEntity::getId)
			.collect(Collectors.toSet());

		final Set<String> usedColorIds = this.sankouBooksRefLookup.findUsedColorIds(colorIds);

		return rows.stream()
			.map(entity -> this.toViewDtoMapper.fromEntity(
				this.toEnumMapper.fromEntity(entity),
				entity,
				usedColorIds.contains(entity.getId())))
			.toList();
	}

	/* ===== 変更系 ===== */

	@Override
	@Transactional
	public void create(final ADMIN_SankouBookColorInputDto input) {
		if (MyType.isBlank(input.name())) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.BLANK_NAME,
				SankouBookColorDbgMsg.blankName());
		}
		if (this.repository.existsByName(input.name())) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.DUPLICATE_NAME,
				SankouBookColorDbgMsg.duplicateName(input.name()));
		}
		this.repository.saveAndReload(this.toEntityNew(input), this.em);
	}

	@Override
	@Transactional
	public void update(final ADMIN_SankouBookColorInputDto input) {
		if (MyType.isNull(input) || MyType.isBlank(input.viewId())) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.BLANK_ID,
				SankouBookColorDbgMsg.blankId("viewId"));
		}
		final String name = this.trim(input.name());
		if (MyType.isBlank(name)) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.BLANK_NAME,
				SankouBookColorDbgMsg.blankName());
		}

		final String id = SankouBookColorIdBridge.toEntityId(input.viewId());
		if (this.repository.existsByNameAndIdNot(name, id)) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.DUPLICATE_NAME,
				SankouBookColorDbgMsg.duplicateName(name));
		}

		this.repository.save(this.toEntityUpdate(input));
	}

	@Override
	@Transactional
	public void deleteByViewId(final String viewId) {
		if (MyType.isBlank(viewId)) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.BLANK_ID,
				SankouBookColorDbgMsg.blankId("viewId"));
		}
		final String id = SankouBookColorIdBridge.toEntityId(viewId);

		if (!this.repository.existsById(id)) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.NOT_ENTITY,
				SankouBookColorDbgMsg.notEntity(id));
		}
		if (this.isUse(id)) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.IN_USE,
				SankouBookColorDbgMsg.inUse(id));
		}
		this.repository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isUse(final String colorId) {
		if (MyType.isBlank(colorId))
			return false;
		return this.sankouBooksRefLookup.existsByColorId(colorId);
	}

	/* ===== private helpers ===== */

	private SankouBookColorEntity toEntityNew(final ADMIN_SankouBookColorInputDto input) {
		final String newId = this.allocateNewId(); // ★ 衝突回避つき
		if (this.isExist(newId)) {
			throw new SankouBookColorException(
				SankouBookColorErrorCode.DUPLICATE_ID,
				SankouBookColorDbgMsg.duplicateId(newId));
		}
		return SankouBookColorEntity.builder()
			.id(newId) // SC + 3桁
			.name(input.name().trim())
			.build();
	}

	private SankouBookColorEntity toEntityUpdate(final ADMIN_SankouBookColorInputDto input) {
		final String id = SankouBookColorIdBridge.toEntityId(input.viewId());
		return SankouBookColorEntity.builder()
			.id(id) // SC + 3桁
			.name(this.trim(input.name()))
			.build();
	}

	private boolean isExist(final String id) {
		return this.repository.existsById(id);
	}

	private SankouBookColorEntity getEntityById(final String id) {
		return this.repository
			.findById(id)
			.orElseThrow(() -> new SankouBookColorException(
				SankouBookColorErrorCode.NOT_ENTITY,
				SankouBookColorDbgMsg.notEntity(id)));
	}
}
