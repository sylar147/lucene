package com.cape.platform.module.search.parser;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.NodeList;

/**
 * <p>Title:html读取</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-4 下午03:48:39
 */
public class HtmlParser implements IParser {
	
	private final static Log logger = LogFactory.getLog(HtmlParser.class);

	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.parser.IParser#getContent(java.lang.String, java.io.InputStream)
	 */
	public String getContent(String name, InputStream is) {
		try {
			Parser parser = new Parser(new Lexer(new Page(is, "UTF-8")));
			NodeList nodes = parser.parse(null);
			StringBean sb = new StringBean();
			nodes.visitAllNodesWith(sb);
			return sb.getStrings();
		} catch (Throwable e) {
			logger.error("★ERROR★：文档【" + name + "】解析出错，具体原因是：" + e.getMessage());
		}
		return "";
	}
}
