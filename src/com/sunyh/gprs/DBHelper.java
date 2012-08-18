package com.sunyh.gprs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库操作工具类
 * 
 */
public class DBHelper {

	private static final String DATABASE_NAME = "com.sunyh.gprs.db";// 数据库名
	private static final String TAG = DATABASE_NAME;// 调试标签
	SQLiteDatabase db;
	Context context;//应用环境上下文   Activity 是其子类

	DBHelper(Context _context) {
		context = _context;
		//开启数据库
		 
		db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null);
		CreateTable();
		Log.v(TAG, "db path=" + db.getPath());
	}

	/**
	 * t_gprsData 记录数据变动
	 * savetime 保存的时间,时间戳
	 * gprsValue gprs流量的数据
	 * totalValue 总流量的数据
	 * action 执行的类型 1-正常保存，2-开机后第一次保存
	 * notes 预留
	 * 建表
	 * 列名 区分大小写？
	 * 都有什么数据类型？
	 * SQLite 3 
	 * 	TEXT    文本
		NUMERIC 数值
		INTEGER 整型
		REAL    小数
		NONE    无类型
	 * 查询可否发送select ?
	 */
	public void CreateTable() {
		try {
			db.execSQL("CREATE TABLE t_gprsData (" +
					"savetime TEXT PRIMARY KEY"
					+ "gprsValue NUMERIC" 
					+ "totalValue NUMERIC" 
					+ "action INTEGER" 
					+ "notes TEXT" 
					+ ");");
			Log.v(TAG, "Create Table t_gprsData ok");
		} catch (Exception e) {
			Log.v(TAG, "Create Table t_user err,table exists.");
		}
	}

	public boolean save(DayM day,int action){
    	String sql="";
    	try{
    		sql="insert into t_gprsData values(%s,%s,%s,%s,%s)";
    		String.format(sql, day.getDay(),day.getGPRS(),day.getTotal(),action,"");
	        db.execSQL(sql);
	        Log.v(TAG,"insert Table t_gprsData ok");
	        return true;
	        
        }catch(Exception e){
        	Log.v(TAG,"insert Table t_gprsData err ,sql: "+sql);
        	return false;
        }
    }
	
	/**
	 * 查询所有记录
	 * 
	 * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet
	 */
	public Cursor loadAll(){
		
		Cursor cur=db.query("t_user", new String[]{"_ID","NAME"}, null,null, null, null, null);
		
		return cur;
	}
      public void close(){
		db.close();
	}
}