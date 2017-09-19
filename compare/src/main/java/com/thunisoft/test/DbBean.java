package com.thunisoft.test;
/**
 * 
 *<p>Description:数据库user实体类
 * @author zjf
 * @date 2017年9月14日 上午9:41:59
 */
public class DbBean {
	
	private String id;
	private String corp;
	private String dept;
	private String user;
	private String province;
	private String sql;
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCorp() {
		return corp;
	}
	public void setCorp(String corp) {
		this.corp = corp;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	

}
