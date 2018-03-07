package com.cape.platform.module.search.searcher;

import com.cape.platform.module.search.bean.SearchCondition;
import com.cape.platform.module.search.bean.SearchResult;

/**
 * <p>Title:查询器接口</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-7 上午12:20:23
 */
public interface ISearcher {

	/**
	 * 根据查询条件查询出结果
	 * @param confition
	 * @return
	 * @throws SearchException
	 */
	SearchResult search(SearchCondition condition);
	
}
