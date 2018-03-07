package com.cape.platform.module.search.parser;

import java.io.InputStream;

/**
 * <p>Title:lucene读取文件接口</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-4 下午03:26:02
 */
public interface IParser {
	
	/**
	 * 读取文件文本内容
	 * @param name 文件名(传该字段主要是为打印出错信息提供)
	 * @param is 文件流
	 * @return
	 */
	String getContent(String name, InputStream is);
}
