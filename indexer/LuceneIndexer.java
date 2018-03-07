package com.cape.platform.module.search.indexer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;

import com.cape.platform.module.search.bean.directory.Directory;
import com.cape.platform.module.search.bean.table.AbstractField;
import com.cape.platform.module.search.bean.table.Field;
import com.cape.platform.module.search.bean.table.SubField;
import com.cape.platform.module.search.bean.table.SubTable;
import com.cape.platform.module.search.bean.table.Table;
import com.cape.platform.module.search.parser.IParser;
import com.cape.platform.module.search.parser.ParserFactory;
import com.cape.platform.module.search.util.Constants;
import com.cape.platform.module.search.util.MessageUtil;

/**
 * <p>
 * Title:索引服务类，线程安全
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:北京金航数码科技有限责任公司
 * </p>
 * 
 * @author liyg
 * @version 1.0
 * @date 2009-12-4 下午05:18:12
 */
public class LuceneIndexer extends AbstractIndexer implements IIndexer {

	private static final String FIELD_SPILT = " ";
	// 索引配置
	private IndexConfigInfo indexConfigInfo;

	// GMT时区
	// public final static TimeZone GMT = TimeZone.getTimeZone("GMT");
	public final static TimeZone GMT = DateUtils.UTC_TIME_ZONE;

	// 读取附件工厂
	private ParserFactory parserFactory;

	@Override
	protected void processIndex(IndexWriter iwriter, Connection conn)
			throws SQLException, IOException {
		// FIXME 重新加载配置文件，这里是否需要呢
		indexConfigInfo.reload();
		
		Map<String, Table> tables = indexConfigInfo.getTables();
		for (Entry<String, Table> entryTable : tables.entrySet()) {
			String name = entryTable.getKey();
			Table table = entryTable.getValue();
			PreparedStatement ps = conn.prepareStatement(table.getSql());
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				String id = rs.getString(table.getPK());
				logger.info(MessageUtil.getMessage("lucene.index.process",
						new Object[] { table.getShowName(), id }));
				Document document = new Document();
				
				//-------------------处理主表-------------------//
				// 全文搜索类型：知识/问题 等
				addDocumentField(document, Constants.INDEX_NAME, name,
						Store.YES, Index.NOT_ANALYZED, TermVector.NO);

				// 索引主键，仅仅显示不能搜索
				addDocumentField(document, table.getPK(), id, Store.YES,
						Index.NO, TermVector.NO);

				// 记录那些subfields，为了和子表相关字段合并后索引
				Map<String, Field> addedFields = new HashMap<String, Field>();
				
				// 根据配置字段
				for (Field field : table.getFields()) {
					processField(rs, document, field);
					// 如果是field，则直接建索引，否则先存起来
					if (field.getSubFields().isEmpty()) {
						addDocumentField(document, field);
					} else {
						addedFields.put(field.getColumn(), field);
					}
				}
				//-------------------处理子表-------------------//
				// 注意子表是根据各条子表记录字段简单相加
				for (SubTable subtable : table.getSubTables()) {
					processSubtable(conn, subtable, id, document, addedFields);
				}
				
				// 处理合并字段的值
				for (Field field : addedFields.values()) {
					addDocumentField(document, field);
				}
				// 加上document对象
				iwriter.addDocument(document);
			}
			// 避免oracle数据库报错：java.sql.SQLException: ORA-01000: 超出打开游标的最大数
			rs.close();
			ps.close();
		}
		
		// 处理directory
		Map<String, Directory> dirs = indexConfigInfo.getDirectorys();
		for (Directory dir : dirs.values()) {
			//logger.info(MessageUtil.getMessage("lucene.index.process",
			//		new Object[] { dir.getShowName(), id }));
			String path = dir.getPath();
			File file = new File(path);
			processFile(iwriter, dir, file);
		}
//		logger.info("开始测试是否清空数据");
//		for (Table table : indexConfigInfo.getTables().values()) {
//			for (Field field : table.getFields()) {
//				if (field.getSbContent().length() > 0) {
//					logger.info(field.getSbContent());
//				}
//			}
//			for (SubTable subtable : table.getSubTables()) {
//				for (Field field : subtable.getFields()) {
//					if (field.getSbContent().length() > 0) {
//						logger.info(field.getSbContent());
//					}
//				}
//			}
//		}
//		logger.info("测试数据完成");
		
	}
	
	private void processFile(IndexWriter iwriter, Directory dir, File baseFile) throws IOException {
		File[] files = baseFile.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				processFile(iwriter, dir, file);
			} else {
				logger.info(MessageUtil.getMessage("lucene.index.process",
						new Object[] { dir.getShowName(), file.getName() }));
				// 对于不能识别的文件类型，只搜索文件名
				Document document = new Document();
				addDocumentField(document, Constants.INDEX_NAME, dir.getName(),
						Store.YES, Index.NOT_ANALYZED, TermVector.NO);
				InputStream is = null;
				try {
					is = new BufferedInputStream(new FileInputStream(file));
				} catch (FileNotFoundException e) {
					//logger.error("★ERROR★：文档【" + name + "】解析出错，具体原因是：" + e.getMessage());
				}
				IParser parser = parserFactory.getParser(file.getName());
				String value = file.getName();
				if (parser != null) {
					value += "<br>" + parser.getContent(file.getName(), is);
				} 
				addDocumentField(document, Constants.INDEX_FILE_CONTENT, value, 
						Store.YES, Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS);
				String path = file.getAbsolutePath();// getPath();
				try {
					path = file.getCanonicalPath();
				} catch (IOException e) {
					path = file.getAbsolutePath();
				}
				addDocumentField(document, Constants.INDEX_FILE_PATH, 
						path, Store.YES, Index.NO, TermVector.NO);
				addDocumentField(document, Constants.INDEX_FILE_NAME, 
						file.getName(), Store.YES, Index.NO, TermVector.NO);
				// 加上document对象
				iwriter.addDocument(document);
			}
		}
	}

	/**
	 * @param rs
	 * @param document
	 * @param field
	 * @throws SQLException
	 */
	private void processField(ResultSet rs, Document document, Field field)
			throws SQLException {
		// 处理field
		if (field.getSubFields().isEmpty()) {
			Object obj = rs.getObject(field.getColumn());
			if (obj != null) {
				String value = extractColumnValue(rs, field, obj);
				// 将本条记录加进去
				field.getSbContent().append(value).append(FIELD_SPILT);
			}
		} else {
			// 处理subfield，先将值保存到Field
			for (SubField subField : field.getSubFields()) {
				Object obj = rs.getObject(subField.getColumn());
				if (obj != null) {
					String value = extractColumnValue(rs,
							subField, obj);
					field.getSbContent().append(value).append(FIELD_SPILT);
				}
			}
		}
	}

	/**
	 * 处理子表
	 * 
	 * @param con
	 * @param table
	 * @param id
	 * @param document
	 * @throws SQLException
	 */
	private void processSubtable(Connection con, SubTable subTable, String id,
			Document document, Map<String, Field> addedFields)
			throws SQLException {
		PreparedStatement psInner = con.prepareStatement(subTable.getSql());
		psInner.setString(1, id);
		ResultSet rsInner = psInner.executeQuery();

		while (rsInner.next()) {
			// 根据配置字段依次处理子表字段
			for (Field field : subTable.getFields()) {
				processField(rsInner, document, field);
			}
		}
		// 避免oracle数据库报错：java.sql.SQLException: ORA-01000: 超出打开游标的最大数
		rsInner.close();
		psInner.close();
		
		// 将子表相关字段索引，如果碰到和主表中同名的subfield，则延后到主表去索引
		for (Field field : subTable.getFields()) {
			if (addedFields.containsKey(field.getColumn())) {
				// 如果存在和主表同名的subfield，则合并到addFields，并且放弃主表配置
				StringBuilder sb = field.getSbContent();
				addedFields.get(field.getColumn()).getSbContent().append(
						sb.toString()).append(FIELD_SPILT);
				// 清空StringBuilder
				clearData(field);
			} else {
				// 处理普通的子表字段：直接建索引
				addDocumentField(document, field);
			}
		}
	}
	
	/**
	 * 清空缓存数据
	 * @param field
	 */
	private void clearData(Field field) {
		field.getSbContent().delete(0, field.getSbContent().length());
	}

	/**
	 * 增加lucene的field
	 * 
	 * @param document
	 * @param Field
	 * @param value
	 */
	private void addDocumentField(Document document, Field field) {
		addDocumentField(document, field.getColumn(), field.getSbContent()
				.toString(), parseStore(field.getStore()), parseIndex(field
				.getIndex()), parseTermVector(field.getTermvertor()));
		// FIXME 注意，这里要清空Stingbuilder
		clearData(field);
	}
	
	/**
	 * 增加lucene的field
	 * 
	 * @param document
	 * @param name
	 * @param value
	 * @param store
	 * @param index
	 * @param termVector
	 */
	private void addDocumentField(Document document, String name, String value,
			Store store, Index index, TermVector termVector) {
		if (StringUtils.isNotBlank(value)) {
			//logger.info("********************************************************** ");
			//logger.info(name + " -- " + store + " -- " + index + " -- " + termVector);
			//logger.info(value);
			// FIXME 目前所有的字段都是以Field建立索引，如果是数字字段可以
			// 考虑NumericField(尤其是在NumericRangeQuery中有用)
			if (value.endsWith(FIELD_SPILT)) {
				value = value.substring(0, value.length() - 1);
			}
			document.add(new org.apache.lucene.document.Field(name, value, store,
					index, termVector));
		}
	}

	/**
	 * 将对应字段值解析出来
	 * 
	 * @param rs
	 * @param fieldInfo
	 * @param obj
	 * @return
	 * @throws SQLException
	 */
	private String extractColumnValue(ResultSet rs, AbstractField field,
			Object obj) throws SQLException {
		String value = "";
		String column = field.getColumn();
		// 特殊处理类型 Date/Blob  Clob可以直接取String
		if (obj instanceof Date) {
			// 如果是Date类型，这转换为字符串 FIXME 只有建日期索引，没有处理日期查询
			// 并且需要考虑日期索引的粒度
			// 因为DateTools使用了GMT时区，所以直接使用会导致天数被减1，有两种解决方法
			// 1、使用Timestamp即可
			// Timestamp time = rs.getTimestamp(fieldInfo.getColumn());
			// value = DateTools.dateToString(time, Resolution.DAY);
			// 2、获取的时候转换为GMT时区
			Calendar ca = Calendar.getInstance(GMT);
			Date date = rs.getDate(column, ca);
			value = DateTools.dateToString(date, Resolution.DAY);
		} else if (obj instanceof Blob) {
			// 如果是Blob类型, 使用专门的转换器
			InputStream is = ((Blob) obj).getBinaryStream();
			String filename = rs.getString(field.getFilename());
			logger.info(MessageUtil.getMessage("lucene.index.process.attach",
					new Object[] { filename }));
			// 百强提出需求，把文件名也进行索引显示，但是和内容以前显示
			IParser parser = parserFactory.getParser(filename);
			if (parser != null) {
				value = filename + "<br>" + parser.getContent(filename, is);
			} else {
				value = filename;
			}
			IOUtils.closeQuietly(is);
		} else {
			// 其他字段全部按照String取值
			value = rs.getString(column);
		}
		// 保存数据库类型
		if (field.getTable().getType(column) == null) {
			int type =  rs.getMetaData().getColumnType(rs.findColumn(column));
			//field.setType(type);	
			field.getTable().addType(field.getColumn(), type);
		}
		return value;
	}

	/**
	 * 解析lucene Store参数
	 * 
	 * @param store
	 * @return
	 */
	private Store parseStore(String store) {
		if (StringUtils.isBlank(store)) {
			return Store.YES;
		} else {
			return Store.valueOf(store);
		}
		/*if (Store.YES.toString().equalsIgnoreCase(store)) {
			return Store.YES;
		} else if (Store.NO.toString().equalsIgnoreCase(store)) {
			return Store.NO;
		} 
		return Store.YES;*/
	}

	/**
	 * 解析Lucene Index参数
	 * 
	 * @param index
	 * @return
	 */
	private Index parseIndex(String index) {
		if (StringUtils.isBlank(index)) {
			return Index.ANALYZED;
		} else {
			return Index.valueOf(index);
		}
		/*if (Index.ANALYZED.toString().equalsIgnoreCase(index)) {
			return Index.ANALYZED;
		} else if (Index.ANALYZED_NO_NORMS.toString().equalsIgnoreCase(index)) {
			return Index.ANALYZED_NO_NORMS;
		} else if (Index.NO.toString().equalsIgnoreCase(index)) {
			return Index.NO;
		} else if (Index.NOT_ANALYZED.toString().equalsIgnoreCase(index)) {
			return Index.NOT_ANALYZED;
		} else if (Index.NOT_ANALYZED_NO_NORMS.toString().equalsIgnoreCase(
				index)) {
			return Index.NOT_ANALYZED_NO_NORMS;
		}
		return Index.ANALYZED;*/
	}

	/**
	 * 解析lucene TermVector参数
	 * 
	 * @param termvector
	 * @return
	 */
	private TermVector parseTermVector(String termvector) {
		if (StringUtils.isBlank(termvector)) {
			return TermVector.NO;
		} else {
			return TermVector.valueOf(termvector);
		}
		/*if (TermVector.NO.toString().equalsIgnoreCase(termvector)) {
			return TermVector.NO;
		} else if (TermVector.WITH_OFFSETS.toString().equalsIgnoreCase(
				termvector)) {
			return TermVector.WITH_OFFSETS;
		} else if (TermVector.WITH_POSITIONS.toString().equalsIgnoreCase(
				termvector)) {
			return TermVector.WITH_POSITIONS;
		} else if (TermVector.WITH_POSITIONS_OFFSETS.toString()
				.equalsIgnoreCase(termvector)) {
			return TermVector.WITH_POSITIONS_OFFSETS;
		} else if (TermVector.YES.toString().equalsIgnoreCase(termvector)) {
			return TermVector.YES;
		}
		return TermVector.NO;*/
	}

	public IndexConfigInfo getIndexConfigInfo() {
		return indexConfigInfo;
	}
	
	public void setIndexConfigInfo(IndexConfigInfo indexConfigInfo) {
		this.indexConfigInfo = indexConfigInfo;
	}

	public ParserFactory getParserFactory() {
		return parserFactory;
	}

	public void setParserFactory(ParserFactory parserFactory) {
		this.parserFactory = parserFactory;
	}

}
