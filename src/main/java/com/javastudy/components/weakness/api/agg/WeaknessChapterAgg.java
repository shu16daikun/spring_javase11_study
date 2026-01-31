// com.javastudy.components.weakness.api.agg.WeaknessChapterAgg
package com.javastudy.components.weakness.api.agg;

/**
 * 弱点分析：章（Chapter）単位の集計結果プロジェクション。
 *
 * <p>
 * “画面に見せるもの”用の概要値をまとめて取得する。
 */
public interface WeaknessChapterAgg {

	/**
	 * 集計対象の章ID（EntityId）。
	 *
	 * @return chapter の EntityId
	 */
	String getChapterId();

	/**
	 * 試行回数（正誤合計）。null は 0 と同義の扱い想定。
	 *
	 * @return 総試行回数
	 */
	Long getTotalAttempts();

	/**
	 * 正解数。null は 0 と同義の扱い想定。
	 *
	 * @return 正解数
	 */
	Long getCorrectCount();

	/**
	 * 不正解数。null は 0 と同義の扱い想定。
	 *
	 * @return 不正解数
	 */
	Long getWrongCount();
}
