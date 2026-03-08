package com.impacsys.core.common.utils.httprequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 */
public class CommonHttpRequestUtil {

	/**
	 * Returns <code>HttpServletRequest</code>.
	 */
	public static HttpServletRequest getHttpServletRequest() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return attrs != null ? attrs.getRequest() : null;
	}

	private CommonHttpRequestUtil() {

	}
}
