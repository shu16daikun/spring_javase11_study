package com.javastudy;

import com.javastudy.util.path.PackagePath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
	PackagePath.MAIN, // Main モジュール
	PackagePath.MY_LOGIN, // my_login モジュール
	PackagePath.MY_EXCEPTION, // my_exception モジュール
	PackagePath.MY_UTIL
})
public class SpringJavase11StudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJavase11StudyApplication.class, args);
	}
}
