package com.cape.platform.module.search.indexer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;

import com.cape.platform.module.search.bean.PopKey;
import com.cape.platform.module.search.util.Constants;

/**
 * <p>
 * Title:相关搜索关键字索引
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:北京金航数码科技有限责任公司
 * </p>
 * 注：如果有性能问题，可将本查询改为RAMDirectory
 * @author liyg
 * @version 1.0
 * @date 2009-12-28 下午04:58:39
 */
public class RelationIndexer extends AbstractIndexer implements IIndexer {
	
	/**
	 * 查询sql，带默认值
	 */
	private String selectSql = "select keyword, frequence from sys_search_keyword order by frequence asc";
	
	/**
	 * 热门关键字存储类
	 */
	private PopFileData popFileData;
	
	@Override
	protected void processIndex(IndexWriter iwriter, Connection conn)
			throws SQLException, IOException {
		//存放所有热门关键字 FIXME 但有可能造成OOM，如果出现OOM请把这个数据结构存储于数据库
		Map<String, PopKey> cache = new HashMap<String, PopKey>();
		// TODO 考虑将这里重构，由DAO提供数据
		PreparedStatement ps = conn.prepareStatement(selectSql);
		ResultSet rs = ps.executeQuery();
		// FIXME 这里将频率字段改成NumericField，不知道是否有其他影响？
		NumericField frequence = new NumericField(Constants.RELATION_INDEX_FREQUENCE);
		while (rs.next()) {
			Document doc = new Document();
			// FIXME 可以将目前的根据frequence排序改为：将frequence根据一定算法计入score中
			// (由keyword.setBoost())，这样最终结果就不需要排序，即可以提高效率，又可以提高准确度
			// (即最终的相关搜索关键字由frequence和匹配结果综合决定)
			String key = rs.getString(1);
			int count = rs.getInt(2);
			Fieldable keyword = new Field(Constants.RELATION_INDEX_KEYWORD, 
					key, Store.YES, Index.ANALYZED);
			frequence.setLongValue(count);
			//Fieldable frequence = new Field(Constants.RELATION_INDEX_FREQUENCE, rs
			//		.getString(2), Store.NO, Index.NOT_ANALYZED);
			doc.add(keyword);
			doc.add(frequence);    
			iwriter.addDocument(doc);
			String[] keys = key.split(Constants.PATTERN_BLANK);
			for (String tmp : keys) {
				if (cache.containsKey(tmp)) {
					cache.get(tmp).add(count);
				} else {
					cache.put(tmp, new PopKey(tmp, count));
				}
			}
		}
		// 避免oracle数据库报错：java.sql.SQLException: ORA-01000: 超出打开游标的最大数
		rs.close();
		ps.close();
		
		// 这里将热门关键字进行排序，取出前10个存储到文件系统
		Collection<PopKey> popkeys = cache.values();
		PopKey[] pops = popkeys.toArray(new PopKey[0]);
		Arrays.sort(pops, new Comparator<PopKey>(){
			public int compare(PopKey o1, PopKey o2) {
				return o1.getCount() - o2.getCount();
			}
		});
		// 将热门关键字保存
		popFileData.savePopKeywords(pops);
		
	}

	public String getSelectSql() {
		return selectSql;
	}

	public void setSelectSql(String selectSql) {
		this.selectSql = selectSql;
	}

	public void setPopFileData(PopFileData popFileData) {
		this.popFileData = popFileData;
	}

}
