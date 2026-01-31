// com.javastudy.config.EntityAndRepositoryConfig
package com.javastudy.config;

import com.javastudy.util.path.PackagePath;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA の Entity/Repository スキャン境界を宣言する設定クラス。
 * 役割：複数モジュールに跨るパッケージを明示し、“どこを見るか”の地図化。 注意：Bean
 * 定義は持たない（宣言専用）。
 */
@Configuration
@EnableJpaRepositories(basePackages = {
	PackagePath.LOGIN_COMPONENTS_BASE,
	PackagePath.MAIN_COMPONENTS_BASE
})
@EntityScan(basePackages = {
	PackagePath.LOGIN_COMPONENTS_BASE, PackagePath.MAIN_COMPONENTS_BASE
})
public class EntityAndRepositoryConfig { // public 非final（AOP方針）
	/* ===== [public/protected] START ===== */
	// 宣言専用のため公開メソッドなし
	/* ===== [public/protected] END ===== */

	/* ===== [private] START ===== */
	// フィールドなし
	/* ===== [private] END ===== */
}
