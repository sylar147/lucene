package com.cape.platform.module.search.parser;

import java.util.Map;

import com.cape.platform.framework.spring.SpringFactory;

/**
 * <p>Title:reader工厂</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-4 下午04:32:35
 */
public class ParserFactory{
	
	// 装载文件类型和对应解析IReader
	private Map<String, IParser> mapParser;
	
	/**
	 * 获取IReader
	 * @param name
	 * @return
	 */
	public IParser getParser(String name) {
		for (Map.Entry<String, IParser> entry : mapParser.entrySet()) {
			if (name.toLowerCase().endsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * 获取ParserFactory
	 * @return
	 */
	public static ParserFactory getInstance() {
		return (ParserFactory) SpringFactory.getBean("parserFactory");
	}

	public Map<String, IParser> getMapParser() {
		return mapParser;
	}

	public void setMapParser(Map<String, IParser> mapParser) {
		this.mapParser = mapParser;
	}
	
}
