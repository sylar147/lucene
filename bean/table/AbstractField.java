package com.cape.platform.module.search.bean.table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>Title:抽象field基类</p>
 * <p>Description:</p>
 * <p>Company:北京金航数码科技有限责任公司</p>
 * @author liyg
 * @version 1.0
 * @date 2010-1-7 下午05:40:42
 */
public abstract class AbstractField {
	// 字段对应的table引用
	private AbstractTable table;
	
	// 字段名称
	private String column;
	
	// 字段类型 FIXME 这里偷了一下懒，在进行索引的过程中对该字段赋值，即和数据库类型保持一致
	//private int type = Types.NULL;
	
	// 如果column为blob，则filename为文件名称字段
	private String filename;
	
	// 增加hashCode属性，避免每次调用hashCode方法都生成一次
	private int hashCode = Integer.MIN_VALUE;

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AbstractField) {
			AbstractField field = (AbstractField) obj;
			if (this.getColumn() == null || field.getColumn() == null) {
				return false;
			}
			return this.getColumn().equals(field.getColumn());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// 增加hashCode属性，避免每次调用hashCode方法都生成一次
		// FIXME 目前这个生产办法不一定高效
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getColumn())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":"
						+ this.getColumn().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("column", column).append(
				"filename", filename).toString();
	}

//	public int getType() {
//		return type;
//	}
//
//	public void setType(int type) {
//		this.type = type;
//	}

	public AbstractTable getTable() {
		return table;
	}

	public void setTable(AbstractTable table) {
		this.table = table;
	}
	
}
