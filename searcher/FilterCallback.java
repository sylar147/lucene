package com.cape.platform.module.search.searcher;

import org.apache.lucene.search.Filter;

/**
 * <p>Title:过滤器接口(TODO 将来主要用来缓存结果，记得考虑刷新索引之后清空filter缓存)</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-1-6 下午07:26:07
 */
public interface FilterCallback {

	/**
	 * 构造自定义过滤器
	 * @return
	 */
	Filter customFilter();
}
