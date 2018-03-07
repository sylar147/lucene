package com.cape.platform.module.search.dorado.view;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;

import com.bstek.dorado.common.HttpDoradoContext;
import com.bstek.dorado.data.AbstractDatasetListener;
import com.bstek.dorado.data.Dataset;
import com.bstek.dorado.data.ParameterSet;
import com.bstek.dorado.data.Record;
import com.cape.platform.framework.spring.SpringFactory;
import com.cape.platform.module.search.bean.SearchCondition;
import com.cape.platform.module.search.bean.SearchResult;
import com.cape.platform.module.search.bean.table.Table;
import com.cape.platform.module.search.indexer.IndexConfigInfo;
import com.cape.platform.module.search.searcher.QueryCallback;
import com.cape.platform.module.search.searcher.SearchFactory;
import com.cape.platform.module.search.util.Constants;
import com.cape.platform.module.system.sysuser.bo.SysUser;

/**
 * <p>
 * Title:
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
 * @date 2010-5-25 下午08:54:29
 * 马义 2010年7月16日 修复查询时只能从菜单等固定内容中查询的Bug。
 */
public class FulltextSearchListener extends AbstractDatasetListener {
	public boolean beforeLoadData(Dataset dataset) throws Exception {
		// refreshIndex();
		ParameterSet parameters = dataset.parameters();
		int pageIndex = dataset.getPageIndex();
		int pageSize = dataset.getPageSize();
		int start = pageSize * (pageIndex - 1) + 1;
		int end = pageSize * pageIndex;
		SearchCondition condition = new SearchCondition();
		condition.setStart(start);
		condition.setEnd(end);
		condition.setKeyword((String) parameters.getValue("keyword"));
		HttpDoradoContext context = (HttpDoradoContext)HttpDoradoContext.getContext();
		HttpSession session = context.getRequest().getSession();
		final SysUser sysuser = (SysUser)session.getAttribute("user");
		// TODO 将来可以加上需要搜索的类型，但是目前还没有这种需求，所以暂且不做了？
		final String securityLevel = sysuser.getKmsExpertLevel();
		final IndexConfigInfo indexConfigInfo = (IndexConfigInfo)SpringFactory.getBean("indexConfigInfo");
		condition.setQueryCallback(new QueryCallback() {
			public Query customQuery() {
				BooleanQuery mainQuery = new BooleanQuery();
				// 处理工作流相关模块（带密级和用户的模块，例如收文、阅批件)
				BooleanQuery wf = new BooleanQuery();
				// 将来需要去掉isworkflow属性，或者通过其他方式来避免应用程序直接访问IndexConfigInfo方法
				BooleanQuery wfName = new BooleanQuery();
				Map<String, Table> tables= indexConfigInfo.getTables();
				for (Table table : tables.values()) {
					if (table.isWorkflow()) {
						wfName.add(new TermQuery(new Term(Constants.INDEX_NAME,
							table.getName())), BooleanClause.Occur.SHOULD);
					} else {
						//处理普通模块(如菜单)
						mainQuery.add(new TermQuery(new Term(Constants.INDEX_NAME,
							table.getName())), BooleanClause.Occur.SHOULD);
					}
				}
				if (wfName.getClauses().length > 0) {
					wf.add(wfName, BooleanClause.Occur.MUST);
					// 先处理密级匹配，为范围查询 SECRET_LEVEL:[1 TO 3]
					if (!StringUtils.isBlank(securityLevel)) {
						// FIXME
						// 将这里的密级改为NumericRangeQuery，速度应该加快，但是需要在建Field的时候使用NumericField
						// Query secQuery = NumericRangeQuery.newIntRange(
						// "SECRET_LEVEL", Integer.valueOf(1), Integer
						// .valueOf(securityLevel), true, true);
						Query secQuery = new TermRangeQuery("SECRET_LEVEL", "1",
								securityLevel, true, true);
						wf.add(secQuery, BooleanClause.Occur.MUST);
					}
					// 用户
					Query readerUserQuery = new TermQuery(new Term("SYS_USER_IDS", 
							sysuser.getEmployeeId()));
					wf.add(readerUserQuery, BooleanClause.Occur.MUST);
					mainQuery.add(wf, BooleanClause.Occur.SHOULD);
				}
				
				return mainQuery;
			}
		});
		//String sortField = (String) parameters.getValue("sortField");
		//if (!"PPD".equals(sortField)) {
		//	Boolean sortMode = (Boolean) parameters.getValue("sortMode");
		//	SortField sf = new SortField(sortField, SortField.LONG, sortMode);
		//	condition.addSortFields(sf, SortField.FIELD_SCORE);
		//}
		SearchResult result = SearchFactory.getSearcher().search(condition);
		List<Map<String, String>> ls = result.getModels();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		for (Map<String, String> data : ls) {
			Record record = dataset.insertRecord();
			String name = data.get(Constants.SEARCH_RESULT_TYPE);
			for (Map.Entry<String, String> entry : data.entrySet()) {
				if (indexConfigInfo.isDirectory(name)) {
					if (entry.getKey().equals(Constants.INDEX_FILE_CONTENT)) {
						record.setValue("REMARK", entry.getValue());
					} else if (entry.getKey().equals(Constants.INDEX_FILE_NAME)) {
						record.setValue("TITLE", entry.getValue());
					}
					record.setValue(entry.getKey(), entry.getValue());
				} else {
					Integer type = indexConfigInfo.getTables().get(name).getType(entry.getKey());
					if (type != null && Types.DATE == type) {
						record.setValue(entry.getKey(), sdf.parse(entry.getValue()));
					} else {
						record.setValue(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		int pagecount = (int) Math.ceil((double) result.getTotal() / pageSize);
		dataset.setPageCount(pagecount);
		dataset.setPossibleRecordCount((int) result.getTotal());
		return true;
	}

}
