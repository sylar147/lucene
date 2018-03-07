package com.cape.platform.module.search.serivce.impl;

import java.util.List;

import com.cape.platform.module.search.bean.PopKey;
import com.cape.platform.module.search.bean.SearchCondition;
import com.cape.platform.module.search.bean.SearchResult;
import com.cape.platform.module.search.searcher.EmptyQueryCallback;
import com.cape.platform.module.search.searcher.QueryCallback;
import com.cape.platform.module.search.searcher.SearchFactory;
import com.cape.platform.module.search.serivce.FulltextSearchService;

/**
 * <p>Title:全文检索服务实现类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-9-17 下午04:24:38
 */
public class FulltextSearchServiceImpl implements FulltextSearchService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(java.lang.String,
	 *      int, int)
	 */
	public SearchResult search(String keyword, int pageIndex, int pageSize) {
		return search(keyword, pageIndex, pageSize, new EmptyQueryCallback());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(java.lang.String,
	 *      int, int, com.cape.platform.module.search.searcher.QueryCallback)
	 */
	public SearchResult search(String keyword, int pageIndex, int pageSize,
			QueryCallback queryCallback) {
		return search(keyword, pageIndex, pageSize, null, queryCallback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(java.lang.String,
	 *      int, int, java.lang.String)
	 */
	public SearchResult search(String keyword, int pageIndex, int pageSize,
			String name) {
		return search(keyword, pageIndex, pageSize, name,
				new EmptyQueryCallback());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(java.lang.String,
	 *      int, int, java.lang.String,
	 *      com.cape.platform.module.search.searcher.QueryCallback)
	 */
	public SearchResult search(String keyword, int pageIndex, int pageSize,
			String name, QueryCallback queryCallback) {
		return search(keyword, pageIndex, pageSize, name, queryCallback, true);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(java.lang.String,
	 *      int, int)
	 */
	public SearchResult search(String keyword, int pageIndex, int pageSize, boolean logKeyword) {
		return search(keyword, pageIndex, pageSize, new EmptyQueryCallback(), logKeyword);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(java.lang.String,
	 *      int, int, com.cape.platform.module.search.searcher.QueryCallback)
	 */
	public SearchResult search(String keyword, int pageIndex, int pageSize,
			QueryCallback queryCallback, boolean logKeyword) {
		return search(keyword, pageIndex, pageSize, null, queryCallback, logKeyword);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(java.lang.String,
	 *      int, int, java.lang.String)
	 */
	public SearchResult search(String keyword, int pageIndex, int pageSize,
			String name, boolean logKeyword) {
		return search(keyword, pageIndex, pageSize, name,
				new EmptyQueryCallback(), logKeyword);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(java.lang.String,
	 *      int, int, java.lang.String,
	 *      com.cape.platform.module.search.searcher.QueryCallback)
	 */
	public SearchResult search(String keyword, int pageIndex, int pageSize,
			String name, QueryCallback queryCallback, boolean logKeyword) {
		SearchCondition condition = new SearchCondition();
		condition.setKeyword(keyword);
		condition.addName(name);
		int start = pageSize * (pageIndex - 1) + 1;
		int end = pageSize * pageIndex;
		condition.setStart(start);
		condition.setEnd(end);
		condition.setQueryCallback(queryCallback);
		condition.setLogKeyword(logKeyword);
		return search(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.serivce.SearchService#search(com.cape.platform.module.search.bean.SearchCondition)
	 */
	public SearchResult search(SearchCondition condition) {
		return SearchFactory.getSearcher().search(condition);
	}

	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.serivce.FulltextSearchService#findRelationKeywords(java.lang.String, int)
	 */
	public List<String> findRelationKeywords(String keyword, int count) {
		return SearchFactory.getRelation().findRelationKeywords(keyword, count);
	}

	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.serivce.FulltextSearchService#getPopKeys(int)
	 */
	public List<PopKey> getPopKeys(int top) {
		return SearchFactory.getPop().getPopKeys(top);
	}
	
	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.serivce.FulltextSearchService#getPopKeys()
	 */
	public List<PopKey> getPopKeys() {
		return SearchFactory.getPop().getPopKeys();
	}

}
