package com.common.utils;

import java.lang.reflect.Field;

public class MyUtils {

    /**
     * 判断对象中是否存在任意一个属性为空或空字符串， 只有都有值才返回true
     * @param object
     * @return
     */
    public static boolean checkFieldHasNull(Object object){
        // 得到类对象
        Class clazz = (Class)object.getClass();
        // 得到所有属性
        Field fields[] = clazz.getDeclaredFields();
        //定义返回结果
        boolean flag = false;
        for(Field field : fields){
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                // 得到属性值
                fieldValue = field.get(object);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            //只要有一个属性值为null 就返回true 表示对象中有属性为空
            if(fieldValue == null || "".equals(fieldValue)){
                flag = true;
                break;
            }
        }
        return flag;
    }
}
