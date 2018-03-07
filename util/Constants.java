package com.cape.platform.module.search.util;

import org.apache.lucene.search.SortField;

/**
 * <p>Title:常量类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-8 上午12:13:50
 */
public interface Constants {
	// 索引名
	String INDEX_NAME = "NAME";                     // 类型
	String INDEX_FILE_NAME    = "FILE_NAME";
	String INDEX_FILE_CONTENT = "FILE_CONTENT";
	String INDEX_FILE_PATH    = "FILE_PATH";
	
	// 索引字段名称
	String RELATION_INDEX_KEYWORD   = "keyword";     // 关键字
	String RELATION_INDEX_FREQUENCE = "frequence";   // 查询次数
	
	String SEARCH_RESULT_URL = "SEARCH_RESULT_URL";    // 查询结果配置的默认URL
	String SEARCH_RESULT_TYPE = "SEARCH_RESULT_TYPE";  // 查询结果类型
	String SEARCH_RESULT_NAME = "SEARCH_RESULT_NAME";  // 查询结果显示类型
	
	//============================
	String QUERY_FALSE = "false";

	String TERMVECTOR_NO = "NO";

	String STORE_YES = "YES";

	String INDEX_ANALYZED = "ANALYZED";
	
	/**
	 * 空格的正则表达式（包括全角、半角空格，以及多个连续的空格）
	 */
	String PATTERN_BLANK = "[\\s\u3000]+";
	
	
	// 下面为搜索引擎排序字段类型
	public static final int SCORE = SortField.SCORE;

	public static final int DOC = SortField.DOC;

	public static final int STRING = SortField.STRING;

	public static final int INT = SortField.INT;

	public static final int FLOAT = SortField.FLOAT;

	public static final int LONG = SortField.LONG;

	public static final int DOUBLE = SortField.DOUBLE;

	public static final int SHORT = SortField.SHORT;

	public static final int CUSTOM = SortField.CUSTOM;

	public static final int BYTE = SortField.BYTE;
  
	public static final int STRING_VAL = SortField.STRING_VAL;
	
}
