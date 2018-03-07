package com.cape.platform.module.search.searcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.util.Version;

import com.cape.platform.module.search.util.Constants;

/**
 * <p>
 * Title:默认的相关搜索关键字服务，如果有需要可以替换
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:北京金航数码科技有限责任公司
 * </p>
 * 
 * @author liyg
 * @version 1.0
 * @date 2009-12-28 下午02:22:49
 */
public class RelationSearcher extends AbstractSearcher implements IRelation {

	// 类型
	public static final String TYPE = RelationSearcher.class.getSimpleName();

	// 默认的显示个数
	private int defaultCount;

	// 排序字段
	private volatile Sort sort;

	@Override
	protected String getType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.searcher.IRelation#findRelationKeywords(java.lang.String)
	 */
	public List<String> findRelationKeywords(String keyword) {
		return findRelationKeywords(keyword, defaultCount);
	}

	/**
	 * 初始化排序
	 */
	private void initSortIfNecessary() {
		// double check
		if (sort == null) {
			synchronized (this) {
				if (sort == null) {
					SortField sf = new SortField(
							Constants.RELATION_INDEX_FREQUENCE, SortField.LONG,
							true);
					sort = new Sort(sf);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cape.platform.module.search.searcher.IRelation#findRelationKeywords(java.lang.String,
	 *      int)
	 */
	public List<String> findRelationKeywords(String keyword, int count) {
		if (StringUtils.isBlank(keyword)) {
			throw new IllegalArgumentException("查询关键字不能为空！");
		}
		if (count <= 0) {
			throw new IllegalArgumentException("请输入正确的相关关键字个数！");
		}
		
		List<String> relations = new ArrayList<String>();
		try {
			// 初始化，如果需要的话
			initIfNecessary();
			initSortIfNecessary();

			// 先查询当前关键字的相关搜索关键字 FIXME 这里应该根据keyword分词之后，
			// 在Constants.RELATION_INDEX_KEYWORD上构建query
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT,
					Constants.RELATION_INDEX_KEYWORD, analyzer);
			Query query = parser.parse(keyword);
			//logger.info(query);
			// 取count + 1个匹配结果，有可能要过滤掉自身
			TopFieldDocs topFieldDocs = iSearcher.search(query, null,
					count + 1, sort);
			ScoreDoc[] scoreDocs = topFieldDocs.scoreDocs;
			for (ScoreDoc scoreDoc : scoreDocs) {
				Document doc = iSearcher.doc(scoreDoc.doc);
				String rKeyword = doc.get(Constants.RELATION_INDEX_KEYWORD);
				// 排除和自己相同的关键字
				if (keyword.equals(rKeyword)) {
					continue;
				}
				relations.add(rKeyword);
				// 如果找到count了，则退出
				if (relations.size() == count) {
					break;
				}
				// logger.info(rKeyword + " : " +
				// doc.get(Constants.RELATION_INDEX_FREQUENCE));
			}
		} catch (Exception e) {
			logger.error("[" + keyword + "]的相关搜索关键字获取出错，请联系系统管理员！", e);
		}
		return relations;
	}

	public int getDefaultCount() {
		return defaultCount;
	}

	public void setDefaultCount(int defaultCount) {
		this.defaultCount = defaultCount;
	}


	public Sort getSort() {
		return sort;
	}

}
