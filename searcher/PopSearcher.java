package com.cape.platform.module.search.searcher;

import java.util.List;

import com.cape.platform.module.search.bean.PopKey;
import com.cape.platform.module.search.indexer.PopFileData;

/**
 * <p>Title:热门搜索服务</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-11-11 下午01:38:17
 */
public class PopSearcher implements IPop {
	
	private PopFileData popFileData;

	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.searcher.IPop#getPopKeys(int)
	 */
	public List<PopKey> getPopKeys(int top) {
		if (top <= 0) {
			throw new IllegalArgumentException("请输入正确的热门关键字个数！");
		}
		return popFileData.getPopKeys(top);
	}

	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.searcher.IPop#getPopKeys()
	 */
	public List<PopKey> getPopKeys() {
		return popFileData.getPopKeys();
	}

	public void setPopFileData(PopFileData popFileData) {
		this.popFileData = popFileData;
	}
}
