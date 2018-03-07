package com.cape.platform.module.search.indexer;

/**
 * <p>Title:建索引</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-28 下午04:33:44
 */
public interface IIndexer {
	
	/**
	 * 建索引
	 * @throws IndexerFailureException
	 */
	void index() throws IndexerFailureException ;
}
