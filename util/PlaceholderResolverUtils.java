package com.cape.platform.module.search.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:将配置文件中的${..}替换为实际数据库值
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:北京金航数码科技有限责任公司
 * </p>
 * 
 * @author liyg
 * @version 1.0
 * @date 2010-1-5 上午11:39:49
 */
public class PlaceholderResolverUtils {

	/**
	 * 内部获取解析值类
	 */
	private static class ValueResolver {
		public String resolve(String placeholder) {
			return null;
		}
	}

	/** Default placeholder prefix: "${" */
	public static final String PLACEHOLDER_PREFIX = "${";

	/** Default placeholder suffix: "}" */
	public static final String PLACEHOLDER_SUFFIX = "}";

	/**
	 * 内部解析
	 * @param strVal
	 * @param resolver
	 * @return
	 */
	private static String parseStringValue(String strVal, ValueResolver resolver) {
		if (strVal == null) {
			return null;
		}
		StringBuilder buf = new StringBuilder(strVal);
		int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
		while (startIndex != -1) {
			int endIndex = buf.indexOf(PLACEHOLDER_SUFFIX, startIndex
					+ PLACEHOLDER_PREFIX.length());
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex
						+ PLACEHOLDER_PREFIX.length(), endIndex);
				String value = resolver.resolve(placeholder);
				if (value != null) {
					buf = buf.replace(startIndex, endIndex
							+ PLACEHOLDER_SUFFIX.length(), value);
					startIndex = endIndex - value.length()
							- PLACEHOLDER_PREFIX.length();
				} else {
					startIndex = endIndex + PLACEHOLDER_SUFFIX.length();
				}
				startIndex = buf.indexOf(PLACEHOLDER_PREFIX, startIndex);
			} else {
				startIndex = -1;
			}
		}
		return buf.toString();
	}

	/**
	 * 根据ResultSet解析
	 * @param strVal
	 * @param rs
	 * @return
	 */
	public static String parseStringValue(String strVal, final ResultSet rs) {
		return parseStringValue(strVal, new ValueResolver() {
			@Override
			public String resolve(String placeholder) {
				try {
					int columnIndex = rs.findColumn(placeholder);
					if (columnIndex > 0) {
						return rs.getString(columnIndex);
					}
				} catch (SQLException e) {
					// 忽略
				}
				return null;
			}
		});
	}

	/**
	 * 根据map值解析
	 * @param strVal
	 * @param map
	 * @return
	 */
	public static String parseStringValue(String strVal, final Map<String, String> map) {
		return parseStringValue(strVal, new ValueResolver() {
			@Override
			public String resolve(String placeholder) {
				return map.get(placeholder);
			}
		});
	}
	
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "1001");
		map.put("key2", "1002");
		String str = PlaceholderResolverUtils.parseStringValue("/a/d=${key1}&b=${key2}&c=${key3}", map);
		System.out.println(str);
	}

}
