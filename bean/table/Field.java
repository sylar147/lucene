package com.cape.platform.module.search.bean.table;

import java.util.Collections;
import java.util.List;

/**
 * <p>Title:字段信息</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-1-7 下午05:42:47
 */
public class Field extends AbstractField{
	
	// 存储类型
	private String store;
	
	// 索引类型
	private String index;
	
	// 分组向量类型
	private String termvertor;
	
	// 是否通用查询
	private boolean query;
	
	// 子字段
	private List<SubField> subFields = Collections.emptyList();
	
	/**
	 * 记录各个字段数据库字段值之和
	 * 注意：使用完之和需要手工clear，否则可能造成内存泄露
	 */
	private StringBuilder sbContent = new StringBuilder();
	
	public StringBuilder getSbContent() {
		return sbContent;
	}
	

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getTermvertor() {
		return termvertor;
	}

	public void setTermvertor(String termvertor) {
		this.termvertor = termvertor;
	}

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

	public List<SubField> getSubFields() {
		return subFields;
	}

	public void setSubFields(List<SubField> subFields) {
		this.subFields = subFields;
	}
}