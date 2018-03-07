package com.cape.platform.module.search.parser;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.searchengine.lucene.LucenePDFDocument;
import org.pdfbox.util.PDFTextStripper;

/**
 * <p>Title:pdf文件读取类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2009-12-3 下午11:45:21
 */
public class PdfParser implements IParser {

	private final static Log logger = LogFactory.getLog(PdfParser.class);

	/**
	 * 直接从pdf文件生成lucene的Document对象
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static Document indexPDF(InputStream is) throws IOException {
		return LucenePDFDocument.getDocument(is);
	}

	/* (non-Javadoc)
	 * @see com.cape.platform.module.search.parser.IParser#getContent(java.lang.String, java.io.InputStream)
	 */
	public String getContent(String name, InputStream is) {
		PDDocument document = null;
		try {
			document = PDDocument.load(is);
			if (!document.isEncrypted()) {
				// FIXME 只有那些没有加密的pdf文件才可被建立索引，需要测试一下？
				//document.decrypt("");
				return new PDFTextStripper().getText(document);
			}
		} catch (Throwable e) {
			logger.error("★ERROR★：文件" + name + " 】解析出错，具体原因是：" + e.getMessage());
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
				}
			}
		}
		return "";
	}

	public static void main(String[] args) throws Exception {
		/*
		 * FileInputStream fis = new FileInputStream( "E:\\books\\Refactoring to
		 * Patterns.pdf"); String content = getPDFContent(fis);
		 * System.out.println(content);
		 */
	}
}
