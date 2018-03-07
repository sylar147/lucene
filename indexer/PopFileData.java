package com.cape.platform.module.search.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cape.platform.module.search.bean.PopKey;

/**
 * <p>Title:热门关键字服务类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-11-16 下午03:53:23
 */
public class PopFileData {

	/**
	 * 日志记录器
	 */
	private Log logger = LogFactory.getLog(getClass());

	/**
	 * 缓存的热门关键字
	 */
	private List<PopKey> cachePops = new ArrayList<PopKey>();
	
	/**
	 * 是否初始化
	 */
	private boolean init = false;
	
	/**
	 * 保存的热门关键字个数
	 */
	private int maxPop = 20;
	
	/**
	 * 关键字文件存储基本路径
	 */
	protected String basePath;
	
	/**
	 * 获取热门关键字文件全路径
	 * @return
	 */
	private String getPopFile() {
		String path = basePath;
		if (!basePath.endsWith(File.separator)) {
			path += File.separator;
		}
		return path + getClass().getSimpleName() + ".dat";
	}

	/**
	 * 初始化热门关键字缓存
	 */
	private synchronized void init() {
		if (!init) {
			init = true;
			File file = new File(getPopFile());
			if (!file.exists()) {
				return;
			}
			cachePops.clear();
			// 从文件读取热门关键字
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String data = null;
				while ((data = br.readLine()) != null) {
					int spilt = data.lastIndexOf("=");
					String keyword = data.substring(0, spilt);
					int count = Integer.valueOf(data.substring(spilt + 1));
					cachePops.add(new PopKey(keyword, count));
				}
				br.close();
			} catch (IOException e) {
				logger.error("从文件系统初始化热门关键字时报错!", e);
			}
		}
	}

	/**
	 * 保存热门关键字
	 * @param arrPops
	 */
	public synchronized void savePopKeywords(PopKey[] arrPops) {
		// 保存之后会使用最新数据，可以认为缓存已经初始化过了
		init = true;
		cachePops.clear();
		int end = arrPops.length > maxPop ? maxPop : arrPops.length;
		try {
			// 先存
			File file = new File(getPopFile());
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			int size = arrPops.length;
			for (int i = size; i > size - end; i--) {
				PopKey popKey = arrPops[i-1];
				fw.write(popKey.getKeyword() + "=" + popKey.getCount()
								+ "\r\n");
				cachePops.add(popKey);
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			logger.error("保存热门关键字时报错!", e);
		}
	}

	/**
	 * 加载热门关键字
	 * @return
	 */
	public List<PopKey> getPopKeys() {
		if (!init) {
			init();
		}
		return new ArrayList<PopKey>(cachePops);
	}

	/**
	 * 加载热门关键字
	 * @param top
	 * @return
	 */
	public List<PopKey> getPopKeys(int top) {
		if (!init) {
			init();
		}
		if (cachePops.size() > top) {
			return cachePops.subList(0, top);
		}
		return getPopKeys();
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setMaxPop(int maxPop) {
		this.maxPop = maxPop;
	}
	
}
