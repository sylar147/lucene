package com.cape.platform.module.search.bean.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>Title:xml文件主表映射</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-6 下午08:48:22
 */
public class Table extends AbstractTable {
	
	// 名称
	//private String name;
	
	// 类型显示名称
	//private String showName;
	
	// 配置url
	private String url;
	
	// 是否工作流相关
	private boolean isWorkflow;
	
	// 子表信息
	private List<SubTable> subTables = new ArrayList<SubTable>();
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

//	public String getName() {
//		return name;
//	}

//	public void setName(String name) {
//		this.name = name;
//	}

	public List<SubTable> getSubTables() {
		return subTables;
	}
	
	public void addSubTable(SubTable subTable) {
		this.subTables.add(subTable);
	}
	
	public void addSubtables(Collection<SubTable> subTables) {
		this.subTables.addAll(subTables);
	}

//	public String getShowName() {
//		return showName;
//	}
//
//	public void setShowName(String showName) {
//		this.showName = showName;
//	}

	public boolean isWorkflow() {
		return isWorkflow;
	}

	public void setWorkflow(boolean isWorkflow) {
		this.isWorkflow = isWorkflow;
	}

}
