package com.cape.platform.module.search.bean;

/**
 * <p>Title:热门搜索对象</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-11-11 下午01:34:11
 */
public class PopKey {
	
	/**
	 * 热门关键字
	 */
	private String keyword;
	/**
	 * 热门关键字被搜索次数
	 */
	private int count;
	
	/**
	 * 空构造函数
	 */
	public PopKey() {
	}
	
	/**
	 * 构造函数
	 * @param keyword
	 * @param count
	 */
	public PopKey(String keyword, int count) {
		this.keyword = keyword;
		this.count = count;
	}
	
	/**
	 * 次数叠加
	 */
	public void add(int add) {
		this.count += add;
	}
	
	/**
	 * 次数加一
	 */
	public void add() {
		this.count++;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
