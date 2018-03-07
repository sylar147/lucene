package com.cape.platform.module.search.indexer;

/**
 * <p>Title: 索引创建失败异常</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-6 下午10:48:20
 */
public class IndexerFailureException extends Exception {

	private static final long serialVersionUID = 7498271445616923494L;

	public IndexerFailureException() {
		super();
	}

	public IndexerFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public IndexerFailureException(String message) {
		super(message);
	}

	public IndexerFailureException(Throwable cause) {
		super(cause);
	}
	
}
