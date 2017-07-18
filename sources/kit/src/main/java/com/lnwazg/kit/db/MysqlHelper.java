package com.lnwazg.kit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Mysql连接器
 * @author Administrator
 * @version 2016年2月19日
 */
public class MysqlHelper
{
    private String url;
    
    private String dbName;
    
    private String username;
    
    private String password;
    
    public MysqlHelper(String url, String dbName, String username, String password)
    {
        super();
        this.url = url;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
    }
    
    static
    {
        // 初始化驱动包
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (Exception e)
        {
            System.err.println("找不到Mysql驱动！");
            e.printStackTrace();
        }
    }
    
    public Connection getConnection()
        throws Exception
    {
        Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:3306/%s", url, dbName), username, password);
        if (conn == null)
        {
            throw new Exception("无法获取数据库连接！");
        }
        else
        {
            return conn;
        }
    }
    
    /**
     * 失败，则尝试操作回滚
     * 
     * @author a
     * @param conn
     */
    protected void rollback(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                conn.rollback();
            }
            catch (Exception e)
            {
            }
        }
    }
    
    protected void closeConnection(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                // 关闭数据库连接
                conn.close();
                conn = null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
