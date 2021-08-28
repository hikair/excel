package com.jdbc;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author 35147
 */
public class DruidUtils {
    private Properties PROPERTIES = new Properties();


    //声明一个连接池对象
    private DataSource ds = null;

    public DruidUtils(String dbType) {
        // 1.加载驱动
        InputStream is = DruidUtils.class.getResourceAsStream("/jdbc/"+dbType+".properties");
        try {
            PROPERTIES.load(is);
            //构造具体的连接池对象:ds
            ds = DruidDataSourceFactory.createDataSource(PROPERTIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2.建立连接
    public Connection getConnection(){
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 3.关闭资源
    public void closeAll(Connection conn, Statement stmt, ResultSet rs){
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}