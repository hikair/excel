# excel
This is a project that can batch import the data in Excel files into MySQL or Oracle database.
技术栈: springboot + vue

功能: 数据批量导入或更新到数据库，并记录下sql保存到本地

注意事项：
表格默认第一行为数据库对应字段名。数据行从第三行开始
新增时，表格第一列字段名若包含id，默认自动生成
修改时，默认根据第一列修改其他列，确保第一列为主键

错误码对照表
1001  存在请求参数为空
1002  存在字段名为空（数据库字段名）
1003  服务器异常
1004  非法操作