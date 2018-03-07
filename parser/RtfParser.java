package com.cape.platform.module.search.parser;

import java.io.InputStream;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title:rft文件读取类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-4 下午03:32:50
 */
public class RtfParser implements IParser{
	private static final Log logger = LogFactory.getLog(RtfParser.class);

	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.parser.IParser#getContent(java.lang.String, java.io.InputStream)
	 */
	public String getContent(String name, InputStream is) {
		String content = "";
		try {
			DefaultStyledDocument styledDoc = new DefaultStyledDocument();
			new RTFEditorKit().read(is, styledDoc, 0);
			content = new String(styledDoc.getText(0, styledDoc.getLength())
					.getBytes("ISO8859_1")); // 提取文本
		} catch (Throwable e) {
			logger.error("★ERROR★：文件【" + name + "】解析出错，具体原因是：" + e.getMessage());
		}
		return content;
	}
	
	public static void main(String[] args) throws Exception {
		/*String content = getContent(new FileInputStream(
				"c:\\Documents and Settings\\IBM\\桌面\\test.rtf"));
		System.out.println(content);*/
	}
}
