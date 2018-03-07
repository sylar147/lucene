package com.cape.platform.module.search.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hwpf.extractor.WordExtractor;

import com.cape.platform.module.search.parser.PdfParser;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-3 下午10:09:49
 * @deprecated 请参考<code>com.cape.platform.module.search.parser.OoxmlParser</code>
 */
@Deprecated
public class WordReader {
	private final static Log logger = LogFactory.getLog(PdfParser.class);

	public static String getDocContent(InputStream is) {
		try {
			WordExtractor w = new WordExtractor(is);
			return w.getText();
		} catch (Exception e) {
			logger.error("解析word文件出错", e);
		}
		return null;
	}

	public static void main(String[] args) throws FileNotFoundException {
		/*FileInputStream fis = new FileInputStream(
				"d:\\工作备份\\PlatformDorado\\other\\建议和想法.doc");
		String content = getDocContent(fis);
		System.out.println(content);*/
	}

}
