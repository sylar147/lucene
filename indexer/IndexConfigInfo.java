package com.cape.platform.module.search.indexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;

import com.cape.platform.module.search.bean.BaseModule;
import com.cape.platform.module.search.bean.directory.Directory;
import com.cape.platform.module.search.bean.table.AbstractTable;
import com.cape.platform.module.search.bean.table.Field;
import com.cape.platform.module.search.bean.table.SubField;
import com.cape.platform.module.search.bean.table.SubTable;
import com.cape.platform.module.search.bean.table.Table;
import com.cape.platform.module.search.util.Constants;

/**
 * <p>
 * Title:xml配置文件解析类
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
 * @date 2009-12-6 下午08:28:53
 */
public class IndexConfigInfo {

	private static final String NODE_SQL = "sql";

	private static final String ATTR_URL = "url";
	
	private static final String ATTR_ISWORKFLOW = "isWorkflow";
	
	private static final String ATTR_NAME = "name";

	private static final String ATTR_SHOWNAME = "showName";

	private static final String ATTR_PK = "pk";

	private static final String ATTR_QUERY = "query";

	private static final String ATTR_TERMVECTOR = "termvector";

	private static final String ATTR_STORE = "store";

	private static final String ATTR_INDEX = "index";

	private static final String ATTR_FILENAME = "filename";

	private static final String ATTR_COLUMN = "column";

	private static final String XPATH_FIELD = "./fields/field";

	private static final String XPATH_SUBTABLE = "./subtables/subtable";

	// 默认路径
	public static final String DEFAULT_SCHEMA_LOCATION = "com/cape/platform/module/search/configuration/luceneIndexer.xsd";

	// 日志记录器
	private static final Log logger = LogFactory.getLog(IndexConfigInfo.class);

	// 索引配置xml路径
	private String path;
	
	// xsd文件路径
	private String schemaLocation;

	// 配置文件table列表
	private Map<String, Table> tables = new HashMap<String, Table>();
	
	private Map<String, Set<String>> queryFields = new HashMap<String, Set<String>>();
	
	// TODO 应该去掉tables和directorys这样的对象，转而用单一对象或者单一Map<String, BaseModule extends>
	private Map<String, Directory> directorys = new HashMap<String, Directory>();
	
	// 全局ServletContext引用
	//private ServletContext servletContext;
	
	/**
	 * 构造函数
	 */
	public IndexConfigInfo() {
		schemaLocation = DEFAULT_SCHEMA_LOCATION;
	}
	
	/**
	 * 初始化，加载配置文件
	 */
	public synchronized void init() {
		logger.info("开始加载【" + path + "】 文件........");
		tables.clear();
		Document document = parseDom4jDocument();
		// 开始解析配置文件，依次处理主表
		// for (Object obj : document.selectNodes("/index/table")) {
		for (Object obj : document.getRootElement().elements()) {
			Element element = (Element) obj;
			if (element.getName().equals("table")) {
				Table table = new Table();
				addModule(element, table);
				// 处理table特定属性
				boolean isWorkflow = Boolean.valueOf(element.attributeValue(ATTR_ISWORKFLOW));
				String url = element.attributeValue(ATTR_URL);
				table.setUrl(url);
				table.setWorkflow(isWorkflow);
				
				// 处理公用
				processAbstractTable(element, table);
				
				// 处理子表
				for (Object objSub : element.selectNodes(XPATH_SUBTABLE)) {
					Element subElement = (Element) objSub;
					SubTable subtable = new SubTable();
					table.addSubTable(subtable);
					processSubTable(subElement, subtable);
				}
				tables.put(table.getName(), table);
			} else {
				Directory dir = new Directory();
				addModule(element, dir);
				dir.setPath(element.attributeValue("path"));
				directorys.put(dir.getName(), dir);
			}

		}
		logger.info("加载【" + path + "】 文件完成！！！");
	}
	
	private void addModule(Element element, BaseModule module) {
		String name = element.attributeValue(ATTR_NAME);
		String showName = element.attributeValue(ATTR_SHOWNAME);
		module.setName(name);
		module.setShowName(showName);
	}
	
	/**
	 * 重新加载配置文件
	 */
	public void reload() {
		init();
	}

	/**
	 * 处理子表
	 * 
	 * @param Element
	 * @param table
	 */
	private void processSubTable(Element element, SubTable table) {
		// 子表本身没有特殊属性，所以只是处理公用
		processAbstractTable(element, table);
	}

	/**
	 * 处理table元素
	 * 
	 * @param tableElement
	 * @param table
	 */
	private void processAbstractTable(Element element, AbstractTable table) {
		// 设置主键
		table.setPK(element.attributeValue(ATTR_PK));

		// 设置sql
		table.setSql(element.element(NODE_SQL).getText());

		// 设置Field
		for (Object obj : element.selectNodes(XPATH_FIELD)) {
			Element fieldElement = (Element) obj;
			// 设置field属性
			Field field = new Field();
			field.setTable(table);
			field.setColumn(fieldElement.attributeValue(ATTR_COLUMN));
			field.setFilename(fieldElement.attributeValue(ATTR_FILENAME));

			field.setIndex(fieldElement.attributeValue(ATTR_INDEX,
					Constants.INDEX_ANALYZED));
			field.setStore(fieldElement.attributeValue(ATTR_STORE,
					Constants.STORE_YES));
			field.setTermvertor(fieldElement.attributeValue(ATTR_TERMVECTOR,
					Constants.TERMVECTOR_NO));

			Boolean query = (Boolean) ConvertUtils.convert(fieldElement
					.attributeValue(ATTR_QUERY, Constants.QUERY_FALSE),
					Boolean.class);
			field.setQuery(query);
			if (query) {
				Set<String> set = queryFields.get(table.getName());
				if (set == null) {
					set = new HashSet<String>();
					queryFields.put(table.getName(), set);
				}
				set.add(field.getColumn());
			}
			
			// 设置subfield
			List<?> lstSubElement = fieldElement.elements();
			if (!lstSubElement.isEmpty()) {
				List<SubField> subFields = new ArrayList<SubField>();
				for (Object objSubField : lstSubElement) {
					Element subElement = (Element) objSubField;
					SubField subField = new SubField();
					subField.setTable(table);
					subField.setColumn(subElement.attributeValue(ATTR_COLUMN));
					subField.setFilename(subElement
							.attributeValue(ATTR_FILENAME));
					subFields.add(subField);
				}
				field.setSubFields(subFields);
			}
			table.addField(field);
		}
	}

	/**
	 * 根据查询类型，获取所有query='true'的配置字段以及unionField字段
	 * 注意：如果names为空或者size=0，则返回所有的query=true字段，
	 * 另外，如果在不同name直接存在同名字段，则只返回一个名称(去掉了冗余名称)
	 * 
	 * @param names
	 * @return
	 */
	public Set<String> getAllQueryField(Collection<String> names) {
		Set<String> set = new HashSet<String>();
		for (String name : names) {
			Set<String> sets = queryFields.get(name);
			if (sets != null) {
				set.addAll(sets);
			}
		}
		return set;
//		if (names == null || names.size() == 0) {
//			return getAllQueryField();
//		}
//		Set<String> set = new HashSet<String>();
//		for (String name : names) {
//			Table table = tables.get(name);
//			if (table != null) {
//				// 处理主表字段
//				addAllQueryFields(set, table);
//				// 处理子表字段
//				for (SubTable subTable : table.getSubTables()) {
//					addAllQueryFields(set, subTable);
//				}
//			}
//		}
//		return set;
	}

	/**
	 * 将所有的query=true的字段取出
	 * 
	 * @param set
	 * @param subTable
	 */
	protected void addAllQueryFields(Set<String> set, AbstractTable subTable) {
		for (Field field : subTable.getFields()) {
			if (field.isQuery()) {
				set.add(field.getColumn());
			}
		}
	}

	/**
	 * 获取所有类型的query=true的字段
	 * 
	 * @return
	 */
//	public Set<String> getAllQueryField() {
//		return getAllQueryField(tables.keySet());
//	}

	/**
	 * 获取配置url
	 * 
	 * @param name
	 * @return
	 */
	public String getDefaultURL(String name) {
		Table table = tables.get(name);
		if (table != null) {
			return table.getUrl();
		}
		return null;
	}
	
	public String getShowName(String name) {
		if (isDirectory(name)) {
			return directorys.get(name).getShowName();
		} else {
			return tables.get(name).getShowName();
		}
	}
	
	public boolean isDirectory(String name) {
		return directorys.containsKey(name);
	}
	
	public boolean isQueryField(String name, String field) {
		return queryFields.get(name).contains(field);
	}
	
	/**
	 * 获取字段类型，此方法比较慢
	 * @param name
	 * @param field
	 * @return
	 * 
	 * @deprecated 遍历影响速度
	 */
//	public int getFieldType(String name, String field) {
//		Table table = tables.get(name);
//		List<Field> fields = table.getFields();
//		for (Field f: fields) {
//			if (f.getColumn().equals(field)) {
//				return f.getType();
//			}
//		}
//		// 找子表
//		List<SubTable> subtables = table.getSubTables();
//		for (SubTable subtable : subtables) {
//			List<Field> fs = subtable.getFields();
//			for (Field f: fs) {
//				if (f.getColumn().equals(field)) {
//					return f.getType();
//				}
//			}
//		}
//		return Types.NULL;
//	}

	/**
	 * Sax方式解析xml
	 * 
	 * @return
	 * @throws DocumentException
	 */
	protected Document parseDom4jDocument() {
		// 因为dom4j本身不提供验证，所以需要通过其他方式完成，这个在dom4j的FAQ中有说明
		// （第一种：Apaches Xerces 1.4.x + dom4j，第二种：MSV + dom4j）
		// 所以改成由JAXP1.3的方式对schema进行验证

		// SAXReader reader = new SAXReader();
		// reader.setValidation(true);
		// reader.setFeature("http://xml.org/sax/features/validation", true);
		// reader.setFeature("http://apache.org/xml/features/validation/schema",true);
		// reader.setProperty(
		// "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
		// "BizModel.xsd");
		// Document document = reader.read(is);

		DOMReader reader = new DOMReader();
		return reader.read(parseW3cDocument());
	}

	/**
	 * 解析org.w3c.dom.Document
	 * 
	 * @return
	 */
	protected org.w3c.dom.Document parseW3cDocument() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		// 使用schema验证xml文件
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Source[] sources = new Source[] { new StreamSource(
					getClasspathInputStream(schemaLocation)) };
			Schema schema = schemaFactory.newSchema(sources);
			factory.setSchema(schema);
			return factory.newDocumentBuilder().parse(getClasspathInputStream(path));
		} catch (SAXException e) {
			throw new ConfigParserException(e);
		} catch (IOException e) {
			throw new ConfigParserException(e);
		} catch (ParserConfigurationException e) {
			throw new ConfigParserException(e);
		}
	}

	/**
	 * 得到xml文件的inputStream
	 * 
	 * @return
	 */
	private InputStream getClasspathInputStream(String name) throws IOException{
		// 在类路径范围内搜索
		String absolute = name;
		if (!absolute.startsWith("/")) {
			absolute = "/" + absolute;
		}
		// 不能避免缓存
		//InputStream is = IndexConfigInfo.class.getResourceAsStream(absolute);
		//Thread.currentThread().getContextClassLoader().getResourceAsStream("");
		// 不能避免缓存
		//ClassLoader.getSystemClassLoader().getResourceAsStream("");
		// 可以通过openStream的方式重新打开文件，避免缓存
		return IndexConfigInfo.class.getResource(absolute).openStream();
		// 获取实际文件路径
		//IndexConfigInfo.class.getResource("").getFile();
		
		/**
		 *  当平台中的Java文件打包为jar文件时，原来的直接classes读取配置文件会失败，
		 * 	借用Spring的ClassPathResource类，从类路径中读取
		 *  马义 2010年6月30日
		 */
//		ClassPathResource classPathResource = new ClassPathResource(absolute);
//		return classPathResource.getInputStream();
		// 如果是web项目，可以通过ServletContext.getResourceAsStream()方法，可避免缓存
		//InputStream is = servletContext.getResourceAsStream(
		//		"/WEB-INF/classes" + absolute);
		//return is;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public Map<String, Table> getTables() {
		//return new HashMap<String, Table>(tables);
		return tables;
	}
	
	public Set<String> getTableNames() {
		//return new HashSet<String>(tables.keySet());
		return tables.keySet();
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}
	
	public Map<String, Directory> getDirectorys() {
		//return new HashMap<String, Directory>(directorys);
		return directorys;
	}
	
	public Set<String> getDirectoryNames() {
		//return new HashSet<String>(directorys.keySet());
		return directorys.keySet();
	}

	//public void setServletContext(ServletContext servletContext) {
		//this.servletContext = servletContext;
	//}
	
	// public static void main(String[] args) {
	// IndexConfigInfo config = new IndexConfigInfo();
	// config.setPath("");
	// config.init();
	// System.out.println();
	//	}
}
