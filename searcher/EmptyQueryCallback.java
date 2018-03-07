package com.cape.platform.module.search.searcher;

import org.apache.lucene.search.Query;

/**
 * <p>Title:空的QueryCallback</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-9-17 下午04:50:19
 */
public class EmptyQueryCallback implements QueryCallback {

	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.searcher.QueryCallback#customQuery()
	 */
	public Query customQuery() {
		return null;
	}

}
