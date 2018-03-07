package com.cape.platform.module.search.bean;

import org.apache.lucene.search.Query;

/**
 * <p>Title:Query对象的组合</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-12-22 下午02:14:33
 */
public class CombineQuery {
	/**
	 * 核心查询Query
	 */
	private Query baseQuery;
	/**
	 * 最终查询Query
	 */
	private Query finalQuery;
	
	/**
	 * 空构造函数
	 */
	public CombineQuery() {
		
	}
	
	/**
	 * 构造函数
	 * @param baseQuery
	 * @param finalQuery
	 */
	public CombineQuery(Query baseQuery, Query finalQuery) {
		this.baseQuery = baseQuery;
		this.finalQuery = finalQuery;
	}
	
	public Query getBaseQuery() {
		return baseQuery;
	}
	public void setBaseQuery(Query baseQuery) {
		this.baseQuery = baseQuery;
	}
	public Query getFinalQuery() {
		return finalQuery;
	}
	public void setFinalQuery(Query finalQuery) {
		this.finalQuery = finalQuery;
	}
}
