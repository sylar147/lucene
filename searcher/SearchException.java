package com.cape.platform.module.search.searcher;

/**
 * <p>Title:查询异常</p>
 * <p>Description:FIXME 应该设计为一个非运行时异常？</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-7 上午12:19:44
 */
public class SearchException extends RuntimeException {

	private static final long serialVersionUID = 1548775193192238878L;

	public SearchException() {
	}

	public SearchException(String message) {
		super(message);
	}

	public SearchException(Throwable cause) {
		super(cause);
	}

	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}

}
