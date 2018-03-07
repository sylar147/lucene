package com.cape.platform.module.search.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * <p>Title:解决lucene3.0版本之前中文搜索缺陷，详见lucene-2458</p>
 * <p>Description:
 * TODO 升级到lucene3.1版本之后，本类和<code>com.cape.platform.module.search.lucene.PositionFilter</code>都将废弃
 * </p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-12-20 上午10:52:54
 */
public class PositionHackAnalyzerWrapper extends Analyzer {
	
	/**
	 * 被包装Analyzer
	 */
	private Analyzer wrapped;

	/**
	 * 构造函数
	 * @param wrapped 被包装Analyzer
	 */
	public PositionHackAnalyzerWrapper(Analyzer wrapped) {
		this.wrapped = wrapped;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String, java.io.Reader)
	 */
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = wrapped.tokenStream(fieldName, reader);
		return new PositionFilter(ts);
	}

}
