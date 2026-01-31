// ValidationMessageUtil.java
package com.javastudy.util.param.validation_messages;

import com.javastudy.util.param.prop_key.PropKey.ErrorProp;
import com.javastudy.util.param.prop_key.PropKey.RegexProp;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * バリデーション／画面文言の取得ユーティリティ。
 *
 * <ul>
 * <li>MessageSource を介してコードからローカライズされた文言を取得
 * <li>画面向けの代表バリデーション文言バンドルを提供
 * <li>“画面に見せるもの”のみ返し、内部構造は出さない
 * </ul>
 */
@Component
@RequiredArgsConstructor
/* ===== [public/protected] START ===== */
public class ValidationMessageUtil {

	/* ===== [private] START ===== */
	/** 区切り（Form名.フィールド名 用） */
	private static final String DOT = ".";

	/** メッセージソース（DI） */
	private final MessageSource messageSource;

	/* ===== [private] END ===== */

	/**
	 * 現在のリクエストコンテキストにおける Locale を返す。
	 *
	 * @return Locale
	 */
	protected Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	/**
	 * メッセージコードから文言を取得する（なければコードをそのまま返す）。
	 *
	 * @param code
	 *            メッセージコード
	 * @param args
	 *            置換引数
	 * @return ローカライズ済み文言
	 */
	public String getMessage(final String code, final Object... args) {
		return this.messageSource.getMessage(code, args, code, this.getLocale());
	}

	/**
	 * Form クラス名とフィールド名をラベルとして先頭に渡して文言取得。
	 *
	 * @param formClassName
	 *            Form クラスの単純名
	 * @param fieldName
	 *            フィールド名
	 * @param code
	 *            メッセージコード
	 * @param args
	 *            置換引数
	 * @return ラベル埋め込み済みの文言
	 */
	public String getMessageWithLabel(
		final String formClassName,
		final String fieldName,
		final String code,
		final Object... args) {
		final String labelKey = formClassName + DOT + fieldName;
		final String label = this.messageSource.getMessage(labelKey, args, fieldName,
			this.getLocale());
		final Object[] argsWithLabel = this.prependArg(label, args);
		return this.messageSource.getMessage(code, argsWithLabel, code, this.getLocale());
	}

	/**
	 * フィールド名のラベルのみを先頭に渡して文言取得。
	 *
	 * @param fieldName
	 *            フィールド名
	 * @param code
	 *            メッセージコード
	 * @param args
	 *            置換引数
	 * @return ラベル埋め込み済みの文言
	 */
	public String getMessageWithLabel(
		final String fieldName,
		final String code,
		final Object... args) {
		final String label = this.messageSource.getMessage(fieldName, args, fieldName,
			this.getLocale());
		final Object[] argsWithLabel = this.prependArg(label, args);
		return this.messageSource.getMessage(code, argsWithLabel, code, this.getLocale());
	}

	/**
	 * 代表的なバリデーションメッセージをまとめて返す（Model へ搭載想定）。
	 *
	 * <p>
	 * 新体系キー（error.common.*）を主として返却。Regex も同一マップに載せて JS 側で即参照可能にする。
	 *
	 * @return code → message の順序付き Map
	 */
	public Map<String, String> setModelValidationMessages() {
		final Map<String, String> vm = new LinkedHashMap<>();

		/* --- error.common.*（新体系・画面共通） --- */
		this.put(vm, ErrorProp.COMMON_NOT_BLANK);
		this.put(vm, ErrorProp.COMMON_MIN);
		this.put(vm, ErrorProp.COMMON_MAX);
		this.put(vm, ErrorProp.COMMON_SIZE);
		this.put(vm, ErrorProp.COMMON_BAD_REQUEST);
		this.put(vm, ErrorProp.COMMON_FORBIDDEN);
		this.put(vm, ErrorProp.COMMON_NOT_FOUND);
		this.put(vm, ErrorProp.COMMON_NOT_FOUND_GENERIC);
		this.put(vm, ErrorProp.COMMON_UNEXPECTED);

		/* --- regex.*（フロント検証用：JSへそのまま渡す） --- */
		this.put(vm, RegexProp.USERNAME);
		this.put(vm, RegexProp.PASSWORD);
		this.put(vm, RegexProp.OPTION);
		this.put(vm, RegexProp.BOOK_COLOR_NAME);
		this.put(vm, RegexProp.QUESTION_NO);

		return vm;
	}

	/** 指定コード群をまとめて解決して Map を返す（UIテスト等の補助）。 */
	public Map<String, String> resolveAll(final Iterable<String> codes) {
		final Map<String, String> vm = new LinkedHashMap<>();
		if (codes == null)
			return vm;
		for (final String code : codes) {
			vm.put(code, this.getMessage(code));
		}
		return vm;
	}

	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	/**
	 * 先頭に 1 要素追加した引数配列を返す。
	 *
	 * @param head
	 *            先頭に追加する要素
	 * @param tail
	 *            既存配列（null 可）
	 * @return 新配列（head + tail）
	 */
	private Object[] prependArg(final Object head, final Object[] tail) {
		final int len = (tail == null) ? 0 : tail.length;
		final Object[] merged = new Object[len + 1];
		merged[0] = head;
		if (len > 0) {
			System.arraycopy(tail, 0, merged, 1, len);
		}
		return merged;
	}

	/**
	 * Map に code→message を追加する小ユーティリティ。
	 *
	 * @param map
	 *            追加先
	 * @param code
	 *            メッセージコード
	 * @param args
	 *            置換引数
	 */
	private void put(final Map<String, String> map, final String code, final Object... args) {
		map.put(code, this.getMessage(code, args));
	}
	/* ===== [private] END ===== */
}
