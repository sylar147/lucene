package com.cape.platform.module.search.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.cape.platform.framework.view.base.ActionBase;
import com.cape.platform.module.attachment.util.ContentTypeUtil;
import com.cape.platform.module.search.indexer.IndexRefresher;
import com.cape.platform.module.search.indexer.IndexerFailureException;

/**
 * <p>Title:搜索引擎默认的action类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-5-25 下午09:35:48
 */
public class FulltextSearchAction extends ActionBase{
	private static final long serialVersionUID = 1L;
	
	private String path;
	private File file;
	
	public void refreshIndex() throws IndexerFailureException {
		try {
			IndexRefresher.getInstance().indexAll();
		} catch (IndexerFailureException e) {
			throw e;
		}
	}
	
	public String getFileContent() {
		file = new File(path);
		return SUCCESS;
	}
	
	public InputStream getInputStream() throws Exception {
		return new FileInputStream(file);
	}

	public String getPath() {
		return path;
	}

	public Long getContentLength() {
		return file.length();
	}
	
	public String getFilename() throws Exception {
		return new String(file.getName().getBytes(), "iso8859-1");
	}
	
	public String getContentType() {
		return ContentTypeUtil.getContentType(file.getName());
	}

	public void setPath(String path) {
		this.path = path;
	}

}
