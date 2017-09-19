package com.thunisoft.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Shujuku {
	 public static List<DbBean> getInfo() {
	        Connection connection=null;
	        Statement statement =null;
	        try{
	            String url="jdbc:postgresql://172.16.32.235:5432/qg_uim?ApplicationName=uimserver&Charset=utf8";
	            String user="fynwmh";
	            String password = "tusc@6789#JKL";
	            Class.forName("org.postgresql.Driver");
	            connection= DriverManager.getConnection(url, user, password);
	            System.out.println("是否成功连接pg数据库"+connection);
	            String sql="SELECT u1.c_id as id ,c1.c_name as corp ,d1.c_name as dept ,u1.c_name as user "
	            		+ "from  db_uim.t_aty_user u1 "
	            		+ "LEFT JOIN db_uim.t_aty_corp  c1 ON u1.c_corp=c1.c_id  "
	            		+ "LEFT JOIN  db_uim.t_aty_dept d1  on u1.c_dept=d1.c_id";

	            statement=connection.createStatement();
	            ResultSet resultSet=statement.executeQuery(sql);
	            ArrayList<DbBean> dbList=new ArrayList<DbBean>();
	            System.out.println("8888888888");
 	            while(resultSet.next()){
	            	DbBean db=new DbBean();
	                String corp=resultSet.getString("corp");
	                String dept=resultSet.getString("dept");
	                String user1=resultSet.getString("user");
	                String id=resultSet.getString("id");
	                db.setCorp(corp);
	                db.setDept(dept);
	                db.setUser(user1);
	                db.setId(id);
	                dbList.add(db);
	            }
 	            return  dbList;
	        }catch(Exception e){
	        	e.printStackTrace();
	        	throw new RuntimeException(e);
	        }finally{
	            try{
	                statement.close();
	            }
	            catch(SQLException e){
	                e.printStackTrace();
	                throw new RuntimeException(e);
	            }finally{
	                try{
	                    connection.close();
	                }
	                catch(SQLException e){
	                    e.printStackTrace();
	                    throw new RuntimeException(e);
	                }
	            }
	        }
	    }
	}