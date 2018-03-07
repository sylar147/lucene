package com.cape.platform.module.search.searcher;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RemoteCachingWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryTermScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.util.Version;

import com.cape.platform.module.search.bean.CombineQuery;
import com.cape.platform.module.search.bean.SearchCondition;
import com.cape.platform.module.search.bean.SearchResult;
import com.cape.platform.module.search.indexer.IndexConfigInfo;
import com.cape.platform.module.search.lucene.AvicitMultiFieldQueryParser;
import com.cape.platform.module.search.util.Constants;
import com.cape.platform.module.search.util.PlaceholderResolverUtils;

/**
 * <p>
 * Title:Lucene查询器，线程安全
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:北京金航数码科技有限责任公司
 * </p>
 * TODO 目前搜索只是简单的全文查询，需要考虑搜索结果的排序、范围搜索(比如日期)等，以及匹配结果得分等
 * 
 * @author liyg
 * @version 1.0
 * @date 2009-12-7 下午12:41:17
 */
public class LuceneSearcher extends AbstractSearcher implements ISearcher {

	// 类型
	public static final String TYPE = LuceneSearcher.class.getSimpleName();

	// 日志记录
	private Log logger = LogFactory.getLog(this.getClass());

	// 是否需要高亮显示
	private boolean needHighLight = true;

	// 显示字数
	private int fragmenters = 100;

	// 高亮显示颜色
	private String color = "red";

	// 索引配置
	private IndexConfigInfo indexConfigInfo;

	// 生成相关搜索关键字的服务
	//private IRelation relation;
	
	private KeywordCache keywordCache;

	@Override
	public String getType() {
		return TYPE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.searcher.ISearcher#search(com.cape.platform.module.search.bean.SearchCondition)
	 */
	public SearchResult search(SearchCondition condition) {
		if (StringUtils.isBlank(condition.getKeyword())) {
			throw new IllegalArgumentException("查询关键字不能为空！");
		}
		if (condition.getStart() <= 0 || condition.getStart() > condition.getEnd()) {
			throw new IllegalArgumentException("请输入正确的分页信息！");
		}
		
		long startTime = System.currentTimeMillis();
		// 返回结果
		SearchResult result = new SearchResult();
		try {
			// 初始化
			initIfNecessary();
			// 多种方式构造CombineQuery
			CombineQuery cQuery = makeQuery(condition);
			// 过滤器
			Filter filter = null;
			if (condition.getFilterCallback() != null) {
				// TODO 目前所有的查询都是通过Query，并且没有缓存结果
				// 将来可考虑将部分过滤条件(例如密级)改为Filter，然后进行缓存
				// To cache a result you must do something like
				Filter customFilter = condition.getFilterCallback()
						.customFilter();
				CachingWrapperFilter cwFilter = new CachingWrapperFilter(
						customFilter);
				filter = new RemoteCachingWrapperFilter(
						cwFilter);
			} 
			// 查询结果
			TopDocs topDocs = null;
			if (condition.getSortfields().isEmpty()) {
				topDocs = iSearcher.search(cQuery.getFinalQuery(), filter, condition.getEnd());
			} else {
				topDocs = iSearcher.search(cQuery.getFinalQuery(), filter, condition.getEnd(), 
					new Sort(condition.getSortfields().toArray(new SortField[]{})));
			}
			// 生成查询结果
			buildResult(cQuery.getBaseQuery(), topDocs, condition, result);
			// 如果需要记录关键字，并且是第一页则：缓存查询关键字，并由定时服务进行持久化
			if (condition.isLogKeyword() && condition.getStart() == 1) {
				keywordCache.cache(condition.getKeyword());
			}
		} catch (Exception e) {
			logger.error("查询出错，请联系系统管理员！", e);
			throw new SearchException(e);
		} finally {
			//
		}
		long endTime = System.currentTimeMillis();
		result.setTime(endTime - startTime);
		return result;
	}

	/**
	 * 生成查询query
	 * 
	 * @param condition
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private CombineQuery makeQuery(SearchCondition condition) throws ParseException,
			IOException {
		// 1、先构造一个主的Boolean查询
		BooleanQuery mainQuery = new BooleanQuery();
		Set<String> tableNames = new HashSet<String>();
		Set<String> dirNames = new HashSet<String>();
		if (condition.getNames().isEmpty()) {
			tableNames.addAll(indexConfigInfo.getTableNames());
			dirNames.addAll(indexConfigInfo.getDirectoryNames());
		} else {
			tableNames.addAll(condition.getNames());
			for (String name : tableNames) {
				if (indexConfigInfo.isDirectory(name)) {
					dirNames.add(name);
				}
			}
			tableNames.removeAll(dirNames);
		}
		
		// 先处理directory查询类型
		BooleanQuery dirQuery = new BooleanQuery();
		if (!dirNames.isEmpty()) {
			for (String dirName : dirNames) {
				Query nameQuery = new TermQuery(new Term(Constants.INDEX_NAME,
						dirName));
				dirQuery.add(nameQuery, Occur.SHOULD);
				tableNames.remove(dirName);
			}
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, 
					Constants.INDEX_FILE_CONTENT, analyzer);
			//parser.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
			Query contentQuery = parser.parse(condition.getKeyword());
			dirQuery.add(contentQuery , Occur.MUST);
			mainQuery.add(dirQuery, Occur.SHOULD);
		}
		
		
		
		// 2、先将类型加上，如果有的话
		BooleanQuery tableQuery = new BooleanQuery();
		BooleanQuery nameQuery = new BooleanQuery();
		for (String name : tableNames) {
			nameQuery.add(new TermQuery(new Term(Constants.INDEX_NAME, name)), 
					Occur.SHOULD);
		}
		tableQuery.add(nameQuery, Occur.MUST);

		// 3、先将用户自定义查询条件加上
		if (condition.getQueryCallback() != null) {
			Query customQuery = condition.getQueryCallback().customQuery();
			if (customQuery != null) {
				tableQuery.add(customQuery, Occur.MUST);
			}
		}

		// TODO 考虑日期类型的查询，即范围RangeQuery 或 ConstantScoreRangeQuery
		// 另外日期也可以通过前缀查询 PrefixQuery(如果用户仅输入年份等)
		// 4、构造其他所有域上的查询，即关键字的真实查询
		Set<String> fields = indexConfigInfo.getAllQueryField(tableNames);
		
		// 还可以直接在查询串中直接写域名查询，例如：author:liyg OR attache:liyg
		String[] aryField = fields.toArray(new String[0]);
		
		// 构造多域或查询
		//MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_CURRENT, aryField, analyzer);
		AvicitMultiFieldQueryParser parser = new AvicitMultiFieldQueryParser(
				Version.LUCENE_CURRENT, aryField, analyzer);
		parser.setDefaultOperator(QueryParser.OR_OPERATOR);
		//parser.setEnablePositionIncrements(true);
		// 为了高亮显示，否则可以直接用MultiFieldQueryParser.parse方法构造多域查询
		//parser.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
		Query multiQuery = parser.parse(condition.getKeyword());
		
		tableQuery.add(multiQuery, Occur.MUST);
		mainQuery.add(tableQuery, Occur.SHOULD);
		logger.info(mainQuery.toString());
		CombineQuery combine = new CombineQuery(multiQuery, mainQuery);
		return combine;
	}

	/**
	 * 生成带高亮显示的结果
	 * 
	 * @param query
	 * @param topDocs
	 * @param condition
	 * @return
	 * @throws IOException
	 */
	protected void buildResult(Query query, TopDocs topDocs,
			SearchCondition condition, SearchResult result) throws IOException {
		result.setTotal(topDocs.totalHits);
		// 全部结果 取的结果是top end
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		if (scoreDocs.length <= 0) {
			return;
		}
		Highlighter highlighter = getHighLight(query);

		// 从start开始取(start起始为1)
		int indexStart = condition.getStart() - 1;
		// 取实际结果和end的最大值(因为scoreDocs.length <= end)
		int indexEnd = condition.getEnd();
		if (indexEnd > scoreDocs.length) {
			indexEnd = scoreDocs.length;
		}
		for (int i = indexStart; i < indexEnd; i++) {
			Document doc = iSearcher.doc(scoreDocs[i].doc);
			// 获取本条查询结果所属的类型
			String name = doc.get(Constants.INDEX_NAME);

			// 遍历所有字段，将每条查询记录封装为Map
			Map<String, String> map = new HashMap<String, String>();
			for (Object obj : doc.getFields()) {
				Field field = (Field) obj;
				String value = field.stringValue();
				// 如果字段值为空，则不返回该字段值
				if (StringUtils.isBlank(value)) {
					continue;
				}
				// 处理高亮显示
				if (indexConfigInfo.isDirectory(name)) {
					if (Constants.INDEX_FILE_CONTENT.equals(field.name())) {
						value = processHighLight(field, scoreDocs[i].doc, highlighter);
					}
				} else {
					if (indexConfigInfo.isQueryField(name, field.name())) {
						value = processHighLight(field, scoreDocs[i].doc, highlighter);
					}
				}
				if (value == null) {
					value = showSubString(field);
				}
				map.put(field.name(), value);
			}
			// 放入查询结果的默认url
			// TODO 将这里的url位置进行处理一下
			String url = indexConfigInfo.getDefaultURL(name);
			if (indexConfigInfo.isDirectory(name)) {
				url = "platform/module/search/getFileContent.action?path=" 
					+ URLEncoder.encode(map.get(Constants.INDEX_FILE_PATH), "UTF-8");
			} else {
				url = PlaceholderResolverUtils.parseStringValue(url, map);
			}
			//url = URLEncoder.encode(url, "UTF-8");
			map.put(Constants.SEARCH_RESULT_URL, url);
			// 放入类型
			map.put(Constants.SEARCH_RESULT_TYPE, name);
			// 放入显示名称
			map.put(Constants.SEARCH_RESULT_NAME, indexConfigInfo.getShowName(name));
			result.addModel(map);

			// 相关搜索
			// likeSearch(scoreDocs[i].doc, analyzer);
		}
	}
	
	private String processHighLight(Field field, int docId,
			Highlighter highlighter) throws IOException {
		String value = null;
		if (highlighter == null) {
			return value;
		}
		TokenStream tokenStream = TokenSources.getAnyTokenStream(iSearcher
				.getIndexReader(), docId, field.name(), analyzer);
		try {
			// FIXME 这里可以改用获取所有的Fragment进行处理加工结果
			value = highlighter.getBestFragment(tokenStream, field
					.stringValue());
		} catch (InvalidTokenOffsetsException e) {
			logger.warn("注意：高亮处理出现问题", e);
		}
		// 特殊处理，如果为空的话，则显示原值
		if (value == null) {
			value = showSubString(field);
		}
		return value;
	}

	/**
	 * @param query
	 * @param highlighter
	 * @return
	 * @throws IOException
	 */
	private Highlighter getHighLight(Query query)
			throws IOException {
		Highlighter highlighter = null;
		if (needHighLight) {
			Formatter simpleHTMLFormatter = new SimpleHTMLFormatter(
					"<b><font color=" + color + ">", "</font></b>");
			Query qsQuery = query.rewrite(iSearcher.getIndexReader());
			Scorer scorer = new QueryTermScorer(qsQuery);
			//Scorer scorer = new QueryScorer(qsQuery);
			highlighter = new Highlighter(simpleHTMLFormatter, scorer);
			// 设置高亮附近的字数
			highlighter.setTextFragmenter(new SimpleFragmenter(fragmenters));
		}
		return highlighter;
	}
	
	/**
	 * 如果字太多，只显示配置数
	 * 
	 * @param field
	 * @return
	 */
	private String showSubString(Field field) {
		String tmp = field.stringValue();
		String value = tmp.substring(0,
				tmp.length() > fragmenters ? fragmenters : tmp.length());
		return value;
	}

	public boolean isNeedHighLight() {
		return needHighLight;
	}

	public void setNeedHighLight(boolean needHighLight) {
		this.needHighLight = needHighLight;
	}

//	public IRelation getRelation() {
//		return relation;
//	}
//
//	public void setRelation(IRelation relation) {
//		this.relation = relation;
//	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getFragmenters() {
		return fragmenters;
	}

	public void setFragmenters(int fragmenters) {
		this.fragmenters = fragmenters;
	}

	public IndexConfigInfo getIndexConfigInfo() {
		return indexConfigInfo;
	}

	public void setIndexConfigInfo(IndexConfigInfo indexConfigInfo) {
		this.indexConfigInfo = indexConfigInfo;
	}

	public void setKeywordCache(KeywordCache keywordCache) {
		this.keywordCache = keywordCache;
	}
}