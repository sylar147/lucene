package com.cape.platform.module.search.indexer;

import java.util.Collections;
import java.util.List;

import com.cape.platform.framework.spring.SpringFactory;

/**
 * <p>Title:索引服务类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-28 下午05:16:33
 */
public class IndexRefresher {

	public static final String INDEX_REFRESHER_BEAN = "indexRefresher";
	// IIndexer集合
	private List<IIndexer> lstIndexer = Collections.emptyList();

	public static IndexRefresher getInstance() {
		return (IndexRefresher) SpringFactory.getBean(INDEX_REFRESHER_BEAN);
	}

	/**
	 * 刷新所有的索引
	 * 
	 * @throws IndexerFailureException
	 */
	public void indexAll() throws IndexerFailureException {
		for (IIndexer indexer : lstIndexer) {
			indexer.index();
		}
	}
	
	/**
	 * 为多拉多页面专门增加的刷新方法
	 * @param parameters
	 * @throws IndexerFailureException
	 */
	public void indexAll(Object parameters) throws IndexerFailureException {
		indexAll();
	}

	// /**
	// * 根据类型刷新指定的索引，比如 <code>LuceneIndexer</code>
	// * @param clazz
	// * @throws IndexerFailureException
	// */
	// public void refresh(Class<? extends IIndexer> clazz)
	// throws IndexerFailureException {
	// refresh(clazz.getName());
	// for (IIndexer indexer : lstIndexer) {
	// if (!clazz.isInterface() && clazz.isInstance(indexer)) {
	// indexer.index();
	// }
	// }
	// }

	/**
	 * 根据类名刷新
	 * 
	 * @param classname
	 * @throws IndexerFailureException
	 */
	public void index(String classname) throws IndexerFailureException {
		for (IIndexer indexer : lstIndexer) {
			if (indexer.getClass().getName().equals(classname)) {
				indexer.index();
			}
		}
	}

	public List<IIndexer> getLstIndexer() {
		return lstIndexer;
	}

	public void setLstIndexer(List<IIndexer> lstIndexer) {
		this.lstIndexer = lstIndexer;
	}

}
