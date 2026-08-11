// com.javastudy.components.error.internal.ErrorReportService
package com.javastudy.components.error.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.my.util.io.MyCsvExporter;

/**
 * traceId に紐づく近傍行を、複数ログ（本体＋client-js）から横断抽出して CSV 化。
 * 出力: source, lineNo, raw
 * 失敗時もヘッダのみの CSV を返す（DL体験優先）。
 */
@Service
public class ErrorReportService { // public 非final（AOP方針）

	/* ===== [private] START ===== */
	private static final int CONTEXT = 5;
	private static final String HEADER_SOURCE = "source";
	private static final String HEADER_LINE_NO = "lineNo";
	private static final String HEADER_RAW = "raw";

	/** カンマ区切りでログファイル群を明示指定（任意） */
	@Value("${error.report.log-files:}")
	private String logFilesProp;

	/** LOG_DIR の既定（logback と揃える） */
	@Value("${LOG_DIR:logs}")
	private String logDir;

	/** APP 名（logback の springProperty と揃える） */
	@Value("${spring.application.name:java-study}")
	private String appName;

	/** Spring の logging.file.name（最優先候補に含める） */
	@Value("${logging.file.name:}")
	private String loggingFileName;

	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final ZoneId ASIA_TOKYO = ZoneId.of("Asia/Tokyo");
	/* ===== [private] END ===== */

	/* ===== [public/protected] START ===== */
	public byte[] exportCsvForTrace(final String traceId) {
		final List<Path> candidates = this.resolveLogFiles();
		final List<String[]> rows = new ArrayList<>();
		rows.add(new String[] {
			HEADER_SOURCE, HEADER_LINE_NO, HEADER_RAW
		});

		for (final Path p : candidates) {
			final List<String[]> chunk = this.collectAround(p, traceId);
			if (!chunk.isEmpty()) {
				rows.addAll(chunk);
			}
		}
		return MyCsvExporter.toCsv(rows);
	}
	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	private List<Path> resolveLogFiles() {
		final List<Path> list = new ArrayList<>();

		// 明示指定があれば最優先（カンマ区切り）
		if (StringUtils.hasText(this.logFilesProp)) {
			Arrays.stream(this.logFilesProp.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.map(Paths::get)
				.forEach(list::add);
		}

		// Spring 側の logging.file.name も候補に含める
		if (StringUtils.hasText(this.loggingFileName)) {
			list.add(Paths.get(this.loggingFileName));
		}

		// 既定の候補（logback のデフォルト設計と対応）
		list.add(Paths.get(this.logDir, this.appName + ".log"));
		list.add(Paths.get(this.logDir, "client-js.log"));

		// 日次ローテーション名（当日分）
		final String today = LocalDate.now(ASIA_TOKYO).format(DATE);
		list.add(Paths.get(this.logDir, this.appName + "." + today + ".log"));
		list.add(Paths.get(this.logDir, "client-js." + today + ".log"));

		// 重複除去（順序保持）
		final Map<String, Path> uniq = new LinkedHashMap<>();
		for (final Path p : list) {
			uniq.put(p.toAbsolutePath().normalize().toString(), p);
		}
		return new ArrayList<>(uniq.values());
	}

	private List<String[]> collectAround(final Path path, final String traceId) {
		final List<String[]> out = new ArrayList<>();
		final List<String> all;
		try {
			all = Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			return out; // 読めない/存在しない場合は黙ってスキップ
		}
		for (int i = 0; i < all.size(); i++) {
			if (all.get(i).contains(traceId)) {
				final int from = Math.max(0, i - CONTEXT);
				final int to = Math.min(all.size() - 1, i + CONTEXT);
				for (int j = from; j <= to; j++) {
					out.add(new String[] {
						path.getFileName().toString(),
						String.valueOf(j + 1),
						all.get(j)
					});
				}
			}
		}
		return out;
	}
	/* ===== [private] END ===== */
}
