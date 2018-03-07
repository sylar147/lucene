package com.cape.platform.module.search.searcher;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import com.cape.platform.module.search.lucene.PositionHackAnalyzerWrapper;

public abstract class AbstractSearcher{
	
	// 日志记录
	protected Log logger = LogFactory.getLog(this.getClass());
	
	// 索引存储基础路径
	protected String basePath;
	
	// 查询器，全局共享
	//For performance reasons it is recommended to open only one IndexSearcher and use it for all of your searches
	// IndexSearcher其实只是对IndexReader的一个封装，来实现专门的查询接口
	// IndexSearcher.close()仅仅是如果需要的话，关闭其所拥有的IndexReader
	// 我们这里应该需要一个readonly的IndexReader
	protected volatile IndexSearcher iSearcher;
	
	// 分词程序
	protected Analyzer analyzer;
	
	/**
	 * 获取类型，由子类实现
	 * @return
	 */
	abstract protected String getType();
	
	/**
	 * 获取索引路径
	 * @return
	 */
	public String getIndexPath() {
		if (basePath.endsWith("/") || basePath.endsWith("\\")) {
			return basePath + getType();
		} else {
			return basePath + "/" + getType();
		}
	}
	
	/**
	 * 初始化
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	protected void initIfNecessary() throws IOException {
		// double check
		if (iSearcher == null) {
			synchronized (this) {
				if (iSearcher == null) {
					// IndexReader的readonly参数，指定为ture可提高性能
					iSearcher = new IndexSearcher(FSDirectory.open(new File(
							getIndexPath())));
				}
			}
		} /*
			 * else { if (!iSearcher.getIndexReader().isCurrent()) { reopen(); } }
			 */
	}
	
	/**
	 * 目前的刷新索引机制：重新创建索引之后，由程序刷新查询IndexSearcher
	 * FIXME 这里的刷新，也可以通过在每次查询之前，先进行IndexReader.isCurrent()方法判
	 * 断是否更新过索引，如果更新过则IndexReader.reopen()
	 * 注意：此方法同步
	 */
	public synchronized void reopen() {
		try {
			// 类似init方式创建，但是需要把先前的IndexReader关闭
			if (iSearcher != null) {
				// 通过reopen方法，在增量索引时对性能有提升
				IndexReader oldReader = iSearcher.getIndexReader();
				// 先关闭之前的查询器 reopen方法性能比重建IndexReader性能好
				IndexReader newReader = oldReader.reopen(true);
				if (oldReader != newReader) {
					oldReader.close();
					oldReader = null;
				}
				// 创建新的IndexSearcher
				iSearcher = new IndexSearcher(newReader);
				logger.info("已经重新打开索引！");
			}
		} catch (Exception e) {
			logger.error("重新打开索引失败！", e);
		}
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		// 为解决lucene3.0版本之前中文搜索缺陷(详见lucene-2458)，所以对分词器进行包装
		// TODO 待lucene升级到3.1版本之后，请掉PositionHackAnalyzerWrapper
		PositionHackAnalyzerWrapper wrapperAnalyzer = new PositionHackAnalyzerWrapper(
				analyzer);
		this.analyzer = wrapperAnalyzer;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}
