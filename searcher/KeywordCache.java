package com.cape.platform.module.search.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class KeywordCache {
	
	private Log logger = LogFactory.getLog(getClass());
	/**
	 * 数据源
	 */
	private DataSource dataSource;
	
	// 查询sql
	private String selectSql = "select frequence from sys_search_keyword where keyword = ? ";

	// 更新sql
	private String insertSql = "insert into sys_search_keyword(keyword, frequence) values(?, ?) ";

	// 插入sql
	private String updateSql = "update sys_search_keyword set frequence = ? where keyword = ? ";

	// 默认保存时间
	public static final long DEFAULT_SAVE_SLEEPTIME = 1000 * 60 * 10;

	// 缓存用户查询关键字，定时刷新到数据库
	private Map<String, Integer> cache = new HashMap<String, Integer>();

	// 定时保存线程
	private SaveKeywordTask saveTask = new SaveKeywordTask();;

	// 保存间隔时间
	private long sleeptime = DEFAULT_SAVE_SLEEPTIME;
	
	/**
	 * 缓存
	 * @param keyword
	 */
	public void cache(String keyword) {
		// 修改为异步方式，放在队列由单独的线程完成
		// saveOrUpdate(keyword, 1);
		synchronized (cache) {
			if (cache.containsKey(keyword)) {
				cache.put(keyword, cache.get(keyword) + 1);
			} else {
				cache.put(keyword, 1);
			}
		}
	}
	
	public KeywordCache() {
		// 设置为守护进程，并启动
		Thread rsThread = new Thread(saveTask, "KeywordCacheTask");
		rsThread.setDaemon(true);
		rsThread.start();
	}
	
	/**
	 * 保存或更新关键字频率
	 * 
	 * @param keyword
	 */
	private void saveOrUpdate(Map<String, Integer> map) {
		if (map.isEmpty()) {
			return;
		}
		Connection conn = null;
		PreparedStatement selectStat = null;
		PreparedStatement updateStat = null;
		PreparedStatement insertStat = null;
		ResultSet rs = null;
		try {
			// 获取连接
			conn = dataSource.getConnection();
			selectStat = conn.prepareStatement(selectSql);
			updateStat = conn.prepareStatement(updateSql);
			insertStat = conn.prepareStatement(insertSql);
			
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				String keyword = entry.getKey();
				Integer count = entry.getValue();
				// 先判断关键字是否存在
				selectStat.setString(1, entry.getKey());
				rs = selectStat.executeQuery();
				if (rs.next()) {
					// 更新
					updateStat.setLong(1, count + rs.getInt(1));
					updateStat.setString(2, keyword);
					updateStat.addBatch();
				} else {
					// 插入
					insertStat.setString(1, keyword);
					insertStat.setLong(2, count);
					insertStat.addBatch();
				}
				rs.close();
			}
			updateStat.executeBatch();
			insertStat.executeBatch();
			
			selectStat.close();
			updateStat.close();
			insertStat.close();
			
		} catch (Exception e) {
			logger.error("保存搜索关键字出错，请联系系统管理员！", e);
			throw new IllegalStateException(e);
		} finally {
			// 关闭连接
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.warn("关闭数据库连接出错！", e);
				}
			}
		}
	}
	
	protected class SaveKeywordTask implements Runnable {
		private boolean running = true;

		private Map<String, Integer> cacheCopy = new HashMap<String, Integer>();

		public void run() {
			while (running) {
				logger.debug("====正在保存用户查询关键字====");
				cacheCopy.clear();
				synchronized (cache) {
					if (!cache.isEmpty()) {
						// copy on write
						cacheCopy.putAll(cache);
						cache.clear();
					}
				}
				saveOrUpdate(cacheCopy);
				cacheCopy.clear();
				logger.debug("====保存用户关键字完成====");
				try {
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {
					logger.error("保存用户查询关键字报错");
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setSelectSql(String selectSql) {
		this.selectSql = selectSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}

	public void setSleeptime(long sleeptime) {
		this.sleeptime = sleeptime;
	}

}
