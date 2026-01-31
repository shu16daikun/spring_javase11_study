// SankouBookColorEnum.java
package com.javastudy.components.sankou_book_color.api.domain;

/* 参考書カラーEnum */
public enum SankouBookColorEnum {
	BRONZE("BRONZE"),
	SILVER("SILVER"),
	GOLD("GOLD");

	/* テーマCSS */
	private static final String THEME_BRONZE = "my-theme-java-bronze";
	private static final String THEME_SILVER = "my-theme-java-silver";
	private static final String THEME_GOLD = "my-theme-java-gold";

	/* メッセージ */
	private static final String ERR_UNKNOWN_COLOR = "Unknown color: ";

	private final String name;

	private SankouBookColorEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	/* テーマCSSクラス名取得 */
	public String getThemeClass() {
		switch (this) {
		case BRONZE:
			return THEME_BRONZE;
		case SILVER:
			return THEME_SILVER;
		case GOLD:
			return THEME_GOLD;
		default:
			throw new IllegalStateException(ERR_UNKNOWN_COLOR + this);
		}
	}
}
