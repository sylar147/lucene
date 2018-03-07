package com.cape.platform.module.search.parser;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;

/**
 * <p>Title:微软office产品读取类，包括ppt/pptx/doc/docx/xls/xlsx/vsd等文档</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-8 上午12:12:53
 */
public class OoxmlParser implements IParser{
	private final static Log logger = LogFactory.getLog(OoxmlParser.class);
	
	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.parser.IParser#getContent(java.lang.String, java.io.InputStream)
	 */
	public String getContent(String name, InputStream is) {
		try {
			POITextExtractor tractor = ExtractorFactory.createExtractor(is);
			return tractor.getText();
		} catch (Throwable e) {
			logger.error("★ERROR★：文档【" + name + "】解析出错，具体原因是：" + e.getMessage());
		}
		return "";
	}
}
