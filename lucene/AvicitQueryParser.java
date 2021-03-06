package com.cape.platform.module.search.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import com.cape.platform.module.search.lucene.boost.BoostWords;

/**
 * <p>Title:Query构造器，主要为部分关键字设置单独boost使用<</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-12-24 下午01:09:45
 */
public class AvicitQueryParser extends QueryParser {

	public AvicitQueryParser(CharStream arg0) {
		super(arg0);
	}

	public AvicitQueryParser(QueryParserTokenManager arg0) {
		super(arg0);
	}

	public AvicitQueryParser(Version matchVersion, String f, Analyzer a) {
		super(matchVersion, f, a);
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
