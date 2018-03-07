package com.cape.platform.module.search.indexer;

/**
 * <p>Title:解析配置文件异常</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-8 下午03:57:46
 */
public class ConfigParserException extends RuntimeException {

	private static final long serialVersionUID = -6196967373323465371L;

	public ConfigParserException() {
		super();
	}

	public ConfigParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigParserException(String message) {
		super(message);
	}

	public ConfigParserException(Throwable cause) {
		super(cause);
	}
	
}
