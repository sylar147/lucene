package com.cape.platform.module.search.bean.directory;

import com.cape.platform.module.search.bean.BaseModule;

/**
 * <p>Title:操作系统文件目录</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-10-19 上午10:52:12
 */
public class Directory extends BaseModule {
	/**
	 * 要索引的文件路径（可以是绝对路径，也可以时相对路径）
	 */
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
