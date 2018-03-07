package com.cape.platform.module.search.util;

import java.util.Locale;

import com.cape.platform.framework.spring.SpringFactory;

/**
 * <p>Title:资源文件类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-1-7 下午06:46:45
 */
public class MessageUtil {
	
	private MessageUtil() {
		
	}

	public static String getMessage(String key) {
		return getMessage(key, null);
	}

	public static String getMessage(String key, Object[] args) {
		return getMessage(key, args, null);
	}

	public static String getMessage(String key, Object[] args, Locale locale) {
		return getMessage(key, args, "", locale);
	}

	public static String getMessage(String key, Object[] args,
			String defaultMessage, Locale locale) {
		return SpringFactory.getAppContext().getMessage(key, args,
				defaultMessage, locale);
	}

}
