package com.cape.platform.module.search.bean.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cape.platform.module.search.bean.BaseModule;

/**
 * <p>Title:抽象table类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-1-7 下午02:07:04
 */
public abstract class AbstractTable extends BaseModule{
	
	/**
	 * 字段类型集合，为快速访问而增加，当然这里记录的是字段的数据库类型
	 */
	private Map<String, Integer> types = new HashMap<String, Integer>();
	
	/**
	 * 查询sql
	 */
	private String sql;

	/**
	 * 主键
	 */
	private String PK;

	/**
	 * 字段列表
	 */
	private List<Field> fields = new ArrayList<Field>();
	//private Map<String, Field> fields = new HashMap<String, Field>();

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getPK() {
		return PK;
	}

	public void setPK(String pk) {
		this.PK = pk;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void addField(Field field) {
		this.fields.add(field);
	}
	
	public void addType(String column, int type) {
		types.put(column, type);
	}
	
	public Integer getType(String column) {
		return types.get(column);
	}
}