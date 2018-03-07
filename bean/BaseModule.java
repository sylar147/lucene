package com.cape.platform.module.search.bean;

/**
 * <p>Title:基本索引模块pojo</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-10-19 上午10:46:29
 */
public abstract class BaseModule {
	
	/**
	 * 唯一标示本模块的名称，一般的模块比如阅批件/收文/人员等等..
	 */
	private String name;
	
	/**
	 * 显示名称，主要用于将来在显示查询结果时使用
	 */
	private String showName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}
}
