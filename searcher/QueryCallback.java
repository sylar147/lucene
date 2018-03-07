package com.cape.platform.module.search.searcher;

import org.apache.lucene.search.Query;

/**
 * <p>Title:查询生成Query回调类，主要由用户定义一些隐式的查询条件，例如密级、共享部门、共享用户等</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * <p>注意：此接口可能需要重构，即使用Filter来代替</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-15 下午01:01:03
 */
public interface QueryCallback {
	
	/**
	 * 用户自定义查询Query
	 * @return
	 */
	Query customQuery();
}
