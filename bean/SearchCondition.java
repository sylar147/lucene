package com.cape.platform.module.search.bean;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.SortField;

import com.cape.platform.module.search.searcher.FilterCallback;
import com.cape.platform.module.search.searcher.QueryCallback;

/**
 * <p>Title:查询条件</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-7 下午06:00:46
 */
public class SearchCondition {
	
	/**
	 * 查询关键字，此关键字可包含lucene提供的基本查询中可使用的查询语言 比如 AND OR等
	 */
	private String keyword;
	
	/**
	 * 起始记录(从1开始，依次2.3.4......等)
	 */
	private int start;
	
	/**
	 * 结束记录
	 */
	private int end;
	
	/**
	 * 是否记录热门搜索，默认为true；只有那些后台代码调用搜索引擎的时候需要传false
	 */
	private boolean logKeyword = true;
	
	/**
	 * 查询类型的集合，可以不传
	 */
	private Set<String> names = new HashSet<String>();
	
	/**
	 * 查询生成query的回调函数，即用户可以自定义一部分查询隐含条件(过滤条件)
	 */
	private QueryCallback queryCallback;
	
	/**
	 * 查询Filter
	 */
	private FilterCallback filterCallback;
	
	/**
	 * 排序字段
	 */
	private Set<SortField> sortfields = new HashSet<SortField>();;
	
	/**
	 * 空构造函数
	 */
	public SearchCondition() {
		
	}

	/**
	 * 构造函数
	 * @param keyword
	 * @param start
	 * @param end
	 */
	public SearchCondition(String keyword, int start, int end) {
		setKeyword(keyword);
		setStart(start);
		setEnd(end);
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}
	
	/**
	 * 增加查询类型
	 * @param name
	 */
	public void addName(String name) {
		// 如果是空的话则什么都不做
		if (StringUtils.isBlank(name)) {
			return;
		}
		this.names.add(name);
	}
	
	/**
	 * 一次增加多个查询类型
	 * @param names
	 */
	public void addNams(Collection<String> names){
		this.names.addAll(names);
	}

	public Set<String> getNames() {
		return this.names;
	}

	public QueryCallback getQueryCallback() {
		return this.queryCallback;
	}

	public void setQueryCallback(QueryCallback queryCallback) {
		this.queryCallback = queryCallback;
	}

	public FilterCallback getFilterCallback() {
		return this.filterCallback;
	}

	public void setFilterCallback(FilterCallback filterCallback) {
		this.filterCallback = filterCallback;
	}
	
	/**
	 * 增加排序字段
	 * @param field 字段名称
	 * @param type 字段类型，请参考com.cape.platform.module.search.util.Constants.STRING
	 * @param reverse 是否逆序
	 */
	public void addSortField(String field, int type, boolean reverse) {
		SortField sf = new SortField(field, type, reverse);
		this.sortfields.add(sf);
	}
	
	/**
	 * 增加查询排序字段
	 * @param field 字段名称
	 * @param type 字段类型，请参考com.cape.platform.module.search.util.Constants.STRING
	 */
	public void addSortField(String field, int type) {
		addSortField(field, type, false);
	}
	
	public boolean isLogKeyword() {
		return logKeyword;
	}

	public void setLogKeyword(boolean logKeyword) {
		this.logKeyword = logKeyword;
	}

	public Set<SortField> getSortfields() {
		return sortfields;
	}
}
