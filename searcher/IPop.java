package com.cape.platform.module.search.searcher;

import java.util.List;

import com.cape.platform.module.search.bean.PopKey;

/**
 * <p>Title:热门搜索服务接口</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-11-11 下午01:31:37
 */
public interface IPop {
	/**
	 * 获取热门搜索关键字列表
	 * 
	 * @param top
	 *            要获取多少个最热门关键字，程序最多只能提供100个
	 * @return
	 */
	List<PopKey> getPopKeys(int top);
	
	/**
	 * 获取所有热门关键字
	 * @return
	 */
	List<PopKey> getPopKeys();
}
