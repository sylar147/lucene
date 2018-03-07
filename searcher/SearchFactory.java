package com.cape.platform.module.search.searcher;

import com.cape.platform.framework.spring.SpringFactory;

/**
 * <p>Title:查询工厂类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-28 下午06:27:26
 */
public class SearchFactory {
	
	// 默认搜索bean
	public static final String DEFAULT_SEARCH_BEAN = "luceneSearcher";
	public static final String DEFAULT_RELATION_BEAN = "relationSearcher";
	public static final String DEFAULT_POP_BEAN = "popSearcher";
	
	private SearchFactory() {
		//拒绝外部初始化
	}
	
	/**
	 * 获取热门搜索服务
	 * @param beanname
	 * @return
	 */
	public static IPop getPop(String beanname) {
		return (IPop) SpringFactory.getBean(beanname);
	}
	
	/**
	 * 获取热门搜索服务
	 * @return
	 */
	public static IPop getPop() {
		return getPop(DEFAULT_POP_BEAN);
	}
	
	/**
	 * 获取相关搜索服务
	 * @return
	 */
	public static IRelation getRelation() {
		return getRelation(DEFAULT_RELATION_BEAN);
	}
	
	/**
	 * 获取相关搜索服务
	 * @param beanname
	 * @return
	 */
	public static IRelation getRelation(String beanname) {
		return (IRelation)SpringFactory.getBean(beanname);
	}
	
	/**
	 * 默认从spring获取ISearcher
	 * @return
	 */
	public static ISearcher getSearcher() {
		return getSearcher(DEFAULT_SEARCH_BEAN);
	}
	
	/**
	 * 获取ISearcher
	 * @param beanname
	 * @return
	 */
	public static ISearcher getSearcher(String beanname) {
		return (ISearcher)SpringFactory.getBean(beanname);
	}
}
