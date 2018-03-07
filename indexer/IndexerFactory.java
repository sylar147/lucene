package com.cape.platform.module.search.indexer;

import com.cape.platform.framework.spring.SpringFactory;

/**
 * <p>Title:刷新索引工厂类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-28 下午06:28:37
 */
public class IndexerFactory {
	
	// 默认bean名称
	public static final String DEFAULT_INDEXER_BENA = "luceneIndexer";
	
	/**
	 * 构造函数
	 */
	private IndexerFactory() {
		// 拒绝外部初始化
	}

	/**
	 * 获取默认IIndexer
	 * @return
	 */
	public static IIndexer getIndexer() {
		return getIndexer(DEFAULT_INDEXER_BENA);
	}
	
	/**
	 * 获取IIndexer
	 * @param beanname
	 * @return
	 */
	public static IIndexer getIndexer(String beanname) {
		return (IIndexer) SpringFactory.getBean(beanname);
	}
}
