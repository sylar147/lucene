package com.cape.platform.module.search.serivce;

import java.util.List;

import com.cape.platform.module.search.bean.PopKey;
import com.cape.platform.module.search.bean.SearchCondition;
import com.cape.platform.module.search.bean.SearchResult;
import com.cape.platform.module.search.searcher.QueryCallback;

/**
 * <p>Title:全文检索服务类，供其他服务类使用</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-9-17 下午04:24:08
 */
public interface FulltextSearchService {

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @param pageIndex
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @return
	 */
	SearchResult search(String keyword, int pageIndex, int pageSize);

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @param pageIndex
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @param queryCallback
	 *            查询回调
	 * @return
	 */
	SearchResult search(String keyword, int pageIndex, int pageSize,
			QueryCallback queryCallback);

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @param pageIndex
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @param name
	 *            查询名称，对应luceneIndexer.xml中<table>或者<directory>的name属性
	 * @return
	 */
	SearchResult search(String keyword, int pageIndex, int pageSize, String name);

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @param pageIndex
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @param name
	 *            查询名称，对应luceneIndexer.xml中<table>或者<directory>的name属性
	 * @param queryCallback
	 *            查询回调
	 * @return
	 */
	SearchResult search(String keyword, int pageIndex, int pageSize,
			String name, QueryCallback queryCallback);

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @param pageIndex
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @param logKeyword
	 *            是否记录搜索关键字：如果是后台代码调用，需要传false
	 * @return
	 */
	SearchResult search(String keyword, int pageIndex, int pageSize,
			boolean logKeyword);

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @param pageIndex
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @param queryCallback
	 *            查询回调
	 * @param logKeyword
	 *            是否记录搜索关键字：如果是后台代码调用，需要传false
	 * @return
	 */
	SearchResult search(String keyword, int pageIndex, int pageSize,
			QueryCallback queryCallback, boolean logKeyword);

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @param pageIndex
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @param name
	 *            查询名称，对应luceneIndexer.xml中<table>或者<directory>的name属性
	 * @param logKeyword
	 *            是否记录搜索关键字：如果是后台代码调用，需要传false
	 * @return
	 */
	SearchResult search(String keyword, int pageIndex, int pageSize,
			String name, boolean logKeyword);

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param keyword
	 *            关键字
	 * @param pageIndex
	 *            当前页数
	 * @param pageSize
	 *            每页记录数
	 * @param name
	 *            查询名称，对应luceneIndexer.xml中<table>或者<directory>的name属性
	 * @param queryCallback
	 *            查询回调
	 * @param logKeyword
	 *            是否记录搜索关键字：如果是后台代码调用，需要传false
	 * @return
	 */
	SearchResult search(String keyword, int pageIndex, int pageSize,
			String name, QueryCallback queryCallback, boolean logKeyword);

	/**
	 * 根据相关条件对全文检索进行搜索
	 * 
	 * @param condition
	 * @return
	 */
	SearchResult search(SearchCondition condition);

	/**
	 * 获取相关搜索
	 * 
	 * @param keyword
	 *            搜索关键字
	 * @param count
	 *            最多需要返回多少个相关搜索
	 * @return
	 */
	List<String> findRelationKeywords(String keyword, int count);

	/**
	 * 获取热门搜索关键字列表
	 * 
	 * @param top
	 *            要获取多少个最热门关键字，程序最多只能提供100个(过多的热门搜索关键字用处不大)
	 * @return
	 */
	List<PopKey> getPopKeys(int top);

	/**
	 * 获取所有缓存的热门关键字
	 * 
	 * @return
	 */
	List<PopKey> getPopKeys();

}
