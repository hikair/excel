package com.model;

/**
 * 数据库参数接收类
 * @author Administrator
 */
public class DataBase {

    /**
     * 操作的表名
     */
    private String tableName;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 操作类型
     */
    private String operate;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    @Override
    public String toString() {
        return "DataBase{" +
                "tableName='" + tableName + '\'' +
                ", dbType='" + dbType + '\'' +
                ", operate='" + operate + '\'' +
                '}';
    }
}
