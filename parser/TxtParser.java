package com.cape.platform.module.search.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title:txt文本文件读取</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-4 下午03:37:44
 */
public class TxtParser implements IParser{
	private final static Log logger = LogFactory.getLog(TxtParser.class);
	
	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.parser.IParser#getContent(java.lang.String, java.io.InputStream)
	 */
	public String getContent(String name, InputStream is) {
		InputStreamReader reader = new InputStreamReader(is);
		BufferedReader buffReader = new BufferedReader(reader);
		try {
			StringBuilder sb = new StringBuilder();
			String content = buffReader.readLine();
			while (content != null) {
				sb.append(content);
				content = buffReader.readLine();
			}
			//FIXME BufferedReader need to close?
			return sb.toString();
		} catch (Throwable e) {
			logger.error("★ERROR★：文档【" + name + "】解析出错，具体原因是：" + e.getMessage());
		}
		return "";
	}
}
