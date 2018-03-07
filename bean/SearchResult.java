package com.cape.platform.module.search.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:查询结果信息</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-7 上午11:50:51
 */
public class SearchResult {
	
	// 查询结果总数
	private long total;
	
	// 查询实际使用时间
	private long time;
	
	// 实际返回的结果列表
	private List<Map<String, String>> models = new ArrayList<Map<String, String>>();
	
	// 相关搜索关键字
	//private List<String> relations = Collections.emptyList();

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<Map<String, String>> getModels() {
		return models;
	}

	public void setModels(List<Map<String, String>> models) {
		this.models = models;
	}
	
	public void addModel(Map<String, String> model) {
		this.models.add(model);
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

//	public List<String> getRelations() {
//		return relations;
//	}
//
//	public void setRelations(List<String> relations) {
//		this.relations = relations;
//	}
	
}
