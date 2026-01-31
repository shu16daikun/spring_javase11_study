package com.javastudy.config.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.util.security.id.ViewIdUtil;
import com.util.type.MyType;

import jakarta.annotation.PostConstruct;

@Component
public class ViewIdSecretInitializer {

	private static final Path SECRET_PATH = Path.of("config", "viewid.secret");
	private static final int SECRET_BYTES = 32;

	@Value("${app.viewid.secret:}")
	private String secretFromProperty;

	@PostConstruct
	public void init() {
		// 1) application.properties 由来があれば最優先
		if (MyType.isNotBlank(this.secretFromProperty)) {
			ViewIdUtil.registerSecretFromSpring(this.secretFromProperty.trim());
			return;
		}

		// 2) 無ければファイルから読み、無ければ生成して保存
		final String secret = loadOrCreateSecretFile();
		ViewIdUtil.registerSecretFromSpring(secret);
	}

	private String loadOrCreateSecretFile() {
		try {
			if (Files.exists(SECRET_PATH)) {
				final String s = Files.readString(SECRET_PATH, StandardCharsets.UTF_8).trim();
				if (MyType.isNotBlank(s)) {
					return s;
				}
			}

			Files.createDirectories(SECRET_PATH.getParent());

			final String generated = generateSecret();
			Files.writeString(SECRET_PATH, generated + System.lineSeparator(),
				StandardCharsets.UTF_8);
			// ※Mac/Linuxなら `chmod 600 config/viewid.secret` を一回やると安心
			return generated;

		} catch (final IOException e) {
			// ファイル保存が無理なら、最後の手段として“起動中だけ有効な秘密鍵”で続行
			// （ただし再起動でViewIdが無効化される）
			return generateSecret();
		}
	}

	private String generateSecret() {
		final byte[] b = new byte[SECRET_BYTES];
		new SecureRandom().nextBytes(b);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
	}
}
