package com.jdbc;

import com.model.DataBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbUtils {

    public static String execute(String sqlStr, DataBase dataBase) {
        DruidUtils druidUtils = new DruidUtils(dataBase.getDbType());
        Connection conn = druidUtils.getConnection();
        Statement stat = null;
        // 多条
        String[] sql = sqlStr.split("\\n");
        List<String> errSql = new ArrayList();
        List<String> succSql = new ArrayList();
        List<String> noNeedSql = new ArrayList();
        // 失败条数
        int errCount = 0;
        int noNeedCount = 0;
        try {
            stat =  conn.createStatement();
            for (String s : sql) {
                try {
                    // true执行成功，false无需操作
                    boolean flag = stat.execute(s);
                    if(flag) {
                        succSql.add(s);
                    }else {
                        noNeedSql.add(s);
                        noNeedCount++;
                    }
                } catch (SQLException throwables) {
                    errCount++;
                    errSql.add(s);
                }
            }
            writeLog(errSql,"errSql");
            writeLog(succSql,"succSql");
            writeLog(noNeedSql,"noNeedSql");
            StringBuffer msg = new StringBuffer();
            switch (dataBase.getOperate()) {
                case "1":msg.append("新增");break;
                case "2":msg.append("修改");break;
                default:break;
            }
            msg.append("成功" + (sql.length - errCount - noNeedCount) + "，失败" + errCount+"条，无需操作" + noNeedCount + "条");
            return msg.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }finally {
            druidUtils.closeAll(conn,stat,null);
        }
    }

    public static void writeLog(List<String> log, String name) throws Exception {
        if(log.size()<1) {
            return;
        }
        String date = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        String fPath = "D:/sqlLog/"+name+"_"+date+".text";
        File file = new File(fPath).getParentFile();
        if(!file.exists()){
            file.mkdirs();
        }
        // 写
        BufferedWriter bw = new BufferedWriter(new FileWriter(fPath));
        for (String s : log) {
            bw.write(s+";\n");
        }
        bw.flush();
        bw.close();// 使用后记得关闭
    }

}
