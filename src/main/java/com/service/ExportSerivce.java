package com.service;

import com.jdbc.DbUtils;
import com.model.DataBase;
import com.model.Result;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
public class ExportSerivce {

    /**
     * 导入数据
     * @param file
     * @param dataBase
     * @return
     * @throws Exception
     */
    public Result importData(MultipartFile file, DataBase dataBase) throws Exception{
        String tableName = dataBase.getTableName();
        String dbType = dataBase.getDbType();
        InputStream inputStream = file.getInputStream();
        Workbook excel = WorkbookFactory.create(inputStream);
        Sheet sheet = excel.getSheetAt(0);
        StringBuffer sql = new StringBuffer("insert into " + tableName + " (");
        // 拿到数据库字段名
        Row head = sheet.getRow(0);
        Iterator<Cell> headIt = head.cellIterator();
        int colNum = 0;
        // 判断第一个字段是否是id
        boolean isId = false;
        String str = getCellValue(head.getCell(0));
        if(str.contains("id") || str.contains("ID")) {
            isId = true;
        }
        while(headIt.hasNext()) {
            String field = getCellValue(headIt.next()).replace("'","");
            if("".equals(field)) {
                return Result.ofFail("1002","存在字段名为空");
            }
            sql.append(field + ",");
            colNum++;
        }
        // 删除多余的逗号
        if(sql.toString().endsWith(",")) {
            sql.delete(sql.lastIndexOf(","),sql.length());
        }
        int index = 0;
        if(isId) {
            String id = "mysql".equals(dbType)?"replace(uuid(),'-','')":"sys_guid()";
            sql.append(") values("+id+",");
            index = 1;
        }else {
            sql.append(") values(");
        }

        // 插入的前半段语句，重复使用
        String prefix = sql.toString();

        // 遍历行 从第三行（数据行）开始
        for(int i=2;i<=sheet.getLastRowNum();i++) {
            Row row = sheet.getRow(i);
            for(int j=index;j<colNum;j++) {
                Cell cell = row.getCell(j);
                sql.append(getCellValue(cell)+",");
            }
            // 删除多余的逗号
            if(sql.toString().endsWith(",")) {
                sql.delete(sql.lastIndexOf(","),sql.length());
            }
            sql.append(")\n");
            if(i != sheet.getLastRowNum()) {
                sql.append(prefix);
            }
        }
        // 执行sql
        return Result.ofSuccess(DbUtils.execute(sql.toString(),dataBase));
    }

    /**
     * 更新数据，根据第一个字段直接覆盖其他不是空的字段
     * @param file
     * @param dataBase
     * @return
     * @throws Exception
     */
    public Result updateData(MultipartFile file, DataBase dataBase) throws Exception{
        String tableName = dataBase.getTableName();
        String dbType = dataBase.getDbType();
        InputStream inputStream = file.getInputStream();
        Workbook excel = WorkbookFactory.create(inputStream);
        Sheet sheet = excel.getSheetAt(0);
        StringBuffer sql = new StringBuffer("update " + tableName + " set ");
        // 拿到数据库字段名
        Row head = sheet.getRow(0);
        Iterator<Cell> headIt = head.cellIterator();
        // 用于统计表的字段数
        int colNum = 0;
        // 表的所有字段
        List<String> headList = new ArrayList<>();
        while(headIt.hasNext()) {
            String field = getCellValue(headIt.next()).replace("'","");
            if("".equals(field)) {
                return Result.ofFail("1002","存在字段名为空");
            }
            headList.add(field);
            colNum++;
        }

        // 更新的前半段语句，重复使用
        String prefix = sql.toString();

        // 遍历行 从第三行（数据行）开始
        for(int i=2;i<=sheet.getLastRowNum();i++) {
            Row row = sheet.getRow(i);
            for(int j=1;j<colNum;j++) {
                Cell cell = row.getCell(j);
                String value = getCellValue(cell).replace("'","");
                if(!"".equals(value)) {
                    // 字段名 = 值 ,
                    sql.append(headList.get(j) + " = " + getCellValue(cell) + ",");
                }
            }
            // 删除多余的逗号
            if(sql.toString().endsWith(",")) {
                sql.delete(sql.lastIndexOf(","),sql.length());
            }
            sql.append(" where " + headList.get(0) + " = " +getCellValue(row.getCell(0)) + "\n");
            if(i != sheet.getLastRowNum()) {
                sql.append(prefix);
            }
        }
        return Result.ofSuccess(DbUtils.execute(sql.toString(),dataBase));
    }

    /**
     * 拿到不同类型单元格中的值
     * 1. 字符串: 字符串
     * 2. 布尔: toString
     * 3. 数值(double): 格式化后的字符串
     * @param cell 获取的单元格
     * @return 单元格中的值
     */
    private static String getCellValue(Cell cell) {
        String resultValue = "''";
        // 判空
        if (Objects.isNull(cell)) {
            return resultValue;
        }

        // 拿到单元格类型
        int cellType = cell.getCellType();
        switch (cellType) {
            // 字符串类型
            case Cell.CELL_TYPE_STRING:
                // 加上单引号
                resultValue = StringUtils.isEmpty(cell.getStringCellValue()) ? "''" : "'"+cell.getStringCellValue().trim()+"'";
                break;
            // 布尔类型
            case Cell.CELL_TYPE_BOOLEAN:
                resultValue = String.valueOf(cell.getBooleanCellValue());
                break;
            // 数值类型
            case Cell.CELL_TYPE_NUMERIC:
                resultValue = new DecimalFormat("#.######").format(cell.getNumericCellValue());
                break;
            // 取空串
            default:
                break;
        }
        return resultValue;
    }
}
