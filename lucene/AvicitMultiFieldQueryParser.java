package com.cape.platform.module.search.lucene;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import com.cape.platform.module.search.lucene.boost.BoostWords;

/**
 * <p>Title:Query构造器，主要为部分关键字设置单独boost使用</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-12-24 下午01:09:04
 */
public class AvicitMultiFieldQueryParser extends MultiFieldQueryParser {

	public AvicitMultiFieldQueryParser(Version matchVersion, String[] fields,
			Analyzer analyzer) {
		super(matchVersion, fields, analyzer);
	}

	public AvicitMultiFieldQueryParser(Version matchVersion, String[] fields,
			Analyzer analyzer, Map<String, Float> boosts) {
		super(matchVersion, fields, analyzer, boosts);
	}
	
	@Override
	protected Query newTermQuery(Term term) {
		Query t = super.newTermQuery(term);
		t.setBoost(BoostWords.getInstance().getBoost(term.text()));
		return t;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.queryParser.QueryParser#newBooleanQuery(boolean)
	 * TODO 在升级lucene3.1之后删除本方法
	 */
	@Override
	protected BooleanQuery newBooleanQuery(boolean disableCoord) {
		return super.newBooleanQuery(false);
	}

}
