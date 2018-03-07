package com.cape.platform.module.search.lucene.boost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title:激励词服务，单实例</p>
 * <p>Description:TODO 事实上应该为每个需要的对得分进行特殊控制的词设置单独的boost，
 * 目前这里只是为部分无意义的关键字设置了较低的boost</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-12-24 上午11:59:04
 */
public class BoostWords {
	
	/**
	 * 低激励词典存放路径
	 */
	public static final String PATH_DIC_LOWER_CONSTANT = "/com/cape/platform/module/search/lucene/boost/lowerConstantBoost.dic";
	
	/**
	 * 默认低激励
	 */
	public static final float BOOST = 0.1f;
	
	/**
	 * 自身实例
	 */
	private static final BoostWords instance;
	
	/**
	 * 日志记录器
	 */
	private Log logger = LogFactory.getLog(BoostWords.class);
	
	static {
		instance = new BoostWords();
	}
	
	/**
	 * 存储激励词和其boost
	 */
	private Set<String> words = new HashSet<String>();
	
	/**
	 * 激励
	 */
	private float boost = BOOST;
	
	/**
	 * 私有构造函数
	 */
	private BoostWords() {
		loadBoostWords();
	}
	
	/**
	 * 加载激励词
	 */
	private void loadBoostWords() {
		//读取主词典文件
        InputStream is = BoostWords.class.getResourceAsStream(PATH_DIC_LOWER_CONSTANT);
        if(is != null){
        	try {
        		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        		String theWord = null;
        		do {
        			theWord = br.readLine();
        			if (theWord != null && !"".equals(theWord.trim())) {
        				words.add(theWord);
        			}
        		} while (theWord != null);
        		
        	} catch (IOException ioe) {
        		logger.warn("加载词典【" + PATH_DIC_LOWER_CONSTANT +"】出错", ioe);
        	}finally{
        		try {
        			if(is != null){
        				is.close();
        				is = null;
        			}
        		} catch (IOException e) {
        			
        		}
        	}
        }
	}
	
	/**
	 * 内部获取boost
	 * @param text
	 * @return
	 */
	public float getBoost(String text) {
		if (words.contains(text)) {
			return boost;
		}
		return 1f;
	} 
	
	/**
	 * 判断一个词是否激励词
	 * @param text
	 * @return
	 */
	public boolean isBoost(String text) {
		return words.contains(text);
	}
	
	/**
	 * 获取实例，可以当做初始化使用
	 * @return
	 */
	public static BoostWords getInstance(){
		return instance;
	}

	/**
	 * 设置boost
	 * 
	 * @param boost
	 */
	public void setBoost(float boost) {
		this.boost = boost;
	}
}
