package com.cape.platform.module.search.indexer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.cape.platform.module.search.searcher.AbstractSearcher;

/**
 * <p>Title:创建索引抽象类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-28 下午04:55:03
 */
public abstract class AbstractIndexer{
	// 日志记录器
	protected Log logger = LogFactory.getLog(getClass());

	// 索引存储位置
	// protected String basePath;

	// 合并因子(lucene默认为10)
	protected int mergeFactor;

	// 最大缓存文档对象数(lucene默认为10000)
	protected int maxBufferedDocs;

	// 分词程序
	protected Analyzer analyzer;

	// 数据源
	protected DataSource dataSource;

	// 对应的searcher
	protected AbstractSearcher searcher;

	// 创建索引类 IndexWriter/IndexReader都是线程安全的，可以全局共享
	// FIXME 但是考虑目前只是定时刷新索引，所以每次进行索引的时候都进行新创建，避免全局的IndexWriter对象一直存在
	// 另外一点，其实可以将不同类型的知识进行分类建索引，搜索的时候进行MultiSearcher/ParallelMultiSearcher
	// private IndexWriter iwriter;

	/**
	 * 真正处理索引，即建索引，由之类负责实现
	 * @param iwriter
	 * @param conn
	 * @throws Exception
	 */
	abstract protected void processIndex(IndexWriter iwriter, Connection conn) 
			throws SQLException, IOException;

	/**
	 * 创建索引 FIXME 目前我们都是将所有的被检索文件存储在索引文件，方便之后的高亮显示等其他处理，但是这样会引起
	 * 索引文件太大，导致效率低下，尤其当索引文件达到几百兆之后效率很慢。所以之后可以考虑将内容操作方便的文件(
	 * 比如txt和htm/html文件)甚至全部文件，放在查询之后单独进行高亮显示等其它处理。
	 * 
	 * @return
	 * @throws IndexerFailureException
	 */
	public void index() throws IndexerFailureException {
		logger.info("【开始重建索引........】");
		long startTime = new Date().getTime();
		
		Connection conn = null;
		IndexWriter iwriter = null;
		try {

			// 创建IndexWriter
			Directory directory = FSDirectory.open(new File(searcher
					.getIndexPath()));
			iwriter = new IndexWriter(directory, analyzer, true,
					MaxFieldLength.UNLIMITED);
			// 优化 增大mergeFactor
			iwriter.setMergeFactor(mergeFactor);
			iwriter.setMaxBufferedDocs(maxBufferedDocs);
			conn = getDataSource().getConnection();

			// 真正的处理索引
			processIndex(iwriter, conn);

			// 优化
			iwriter.optimize();
		} catch (Exception e) {
			logger.error("【索引创建失败......】", e);
			throw new IndexerFailureException("索引创建失败!", e);
		} finally {
			closeIfNecessary(conn, iwriter);
		}

		long endTime = new Date().getTime();
		logger.info("【重建索引完成，共耗时：" + (endTime - startTime) / 1000
				+ " 秒........】");
		// FIXME 这里耦合比较大，可以考虑aop或者注入一个IndexSearcher/IndexReader重新打开一下索引
		// 或者通过事件机制/消息机制
		searcher.reopen();
	}

	/**
	 * @param conn
	 * @param iwriter
	 */
	private void closeIfNecessary(Connection conn, IndexWriter iwriter) {
		// 关闭数据库连接
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.warn("关闭数据库连接时出错！", e);
			}
		}

		// 关闭索引文件
		if (iwriter != null) {
			try {
				iwriter.close();
			} catch (IOException e) {
				logger.warn("关闭索引文件时出错！", e);
			}
		}
	}

	/**
	 * 检查索引路径
	 * 
	 * @throws Exception
	 */
	public static void checkpath(String path) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				throw new IllegalArgumentException("lucene索引文件存放路径【" + path
						+ "】是一个文件而不是文件夹，请确认！");
			}
		} else {
			boolean created = file.mkdir();
			if (!created) {
				throw new IOException("创建索引【" + path + "】目录失败");
			}
		}

		if (!file.canWrite()) {
			throw new IllegalArgumentException("lucene索引文件存放路径【" + path
					+ "】不能进行写操作，请确认是否有相关权限！");
		}

	}

	public int getMergeFactor() {
		return mergeFactor;
	}

	public void setMergeFactor(int mergeFactor) {
		this.mergeFactor = mergeFactor;
	}

	public int getMaxBufferedDocs() {
		return maxBufferedDocs;
	}

	public void setMaxBufferedDocs(int maxBufferedDocs) {
		this.maxBufferedDocs = maxBufferedDocs;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public AbstractSearcher getSearcher() {
		return searcher;
	}

	public void setSearcher(AbstractSearcher searcher) {
		this.searcher = searcher;
	}

}
