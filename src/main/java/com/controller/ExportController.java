package com.controller;

import com.common.utils.MyUtils;
import com.model.DataBase;
import com.model.Result;
import com.service.ExportSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 35147
 */
@RestController
public class ExportController {

    @Autowired
    ExportSerivce exportSerivce;

    /**
     * 导入或修改数据 默认第一行为数据库字段，第二行为字段中文名，第三行才是数据
     * 默认第一个为id用系统自动生成id sys_guid()   id要留空
     * 输出sql到d盘的test.text
     * @param file
     * @return
     */
    @RequestMapping("/importData")
    public Result importData(MultipartFile file, DataBase dataBase){

        if(dataBase == null || MyUtils.checkFieldHasNull(dataBase)) {
            return Result.ofFail("1001","存在请求参数为空");
        }
        //上传文件 获取字节输入流
        try {
            switch (dataBase.getOperate()) {
                case "1":return exportSerivce.importData(file,dataBase);
                case "2":return exportSerivce.updateData(file,dataBase);
                default: return Result.ofFail("1004","非法操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ofFail("1003","服务器异常");
        }
    }
}
