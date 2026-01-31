// src/main/java/com/javastudy/config/web/NoStoreInterceptor.java
package com.javastudy.config.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class NoStoreInterceptor implements HandlerInterceptor {

	@Override
	public void postHandle(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final Object handler,
		final ModelAndView modelAndView) {

		if (response == null) {
			return;
		}
		// ★ キャッシュ禁止ヘッダ
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
	}
}
