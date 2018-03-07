package com.cape.platform.module.search.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-3 下午10:09:16
 * @deprecated 请参考<code>com.cape.platform.module.search.parser.OoxmlParser</code>
 */
@Deprecated
public class PptReader {

	private final static Log logger = LogFactory.getLog(PptReader.class);

	public static String getPptContent(InputStream is) {
		StringBuffer content = new StringBuffer("");
		// HSLFSlideShow contains the main functionality for the Powerpoint file
		// "reader". It is only a very basic class for now
		// SlideShow is a friendly wrapper on top of the more scary
		// HSLFSlideShow
		try {
			SlideShow ss = new SlideShow(new HSLFSlideShow(is));
			Slide[] slides = ss.getSlides();
			for (int i = 0; i < slides.length; i++) {
				// This class represents a run of text in a powerpoint document.
				// That run could be text on a sheet, or text in a note.
				// It is only a very basic class for now
				TextRun[] t = slides[i].getTextRuns();
				for (int j = 0; j < t.length; j++) {
					content.append(t[j].getText());
				}
				content.append(slides[i].getTitle());
			}
		} catch (IOException e) {
			logger.error("解析ppt出错", e);
		}
		return content.toString();
	}
	
	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream(
				"e:\\books\\database\\关系数据库标准语言SQL.PPT");
		String content = getPptContent(fis);
		System.out.println(content);
	}

}
