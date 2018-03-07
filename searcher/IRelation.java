package com.cape.platform.module.search.searcher;

import java.util.List;

/**
 * <p>Title:相关搜索关键字接口</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-28 下午02:06:27
 */
public interface IRelation {
	
	/**
	 * 根据已有关键字生成相关搜索关键字
	 * @param keyword
	 * @return
	 */
	List<String> findRelationKeywords(String keyword);
	
	/**
	 * 根据已有关键字生成指定个数的(如果能找到的话)相关搜索关键字
	 * @param keyword
	 * @param count
	 * @return
	 */
	List<String> findRelationKeywords(String keyword, int count);
	
}
