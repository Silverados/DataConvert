# DataConvert

DataConvert初步的开发计划为：支持Excel读取后转换为Java数据类, 并在未来添加对Json格式、CSharp格式、Lua格式的支持。

- 输入部分目前先限定为只有Excel，那么需要支持两种格式: xls, xlsx.
- 计划的实现中应该要有良好的接口设计，以支持各种输入和输出的继承。
- 输出部分应该支持某些区域的定制化，这就意味着输出前需要检验输出目录下是否以及存在该文件，如果存在则需要保留自定义部分的代码。
- 输出部分应该指明输入的源头, 以便快速定位来源。
- 解析输入文件时允许只导出sheet级别的。
- 在实践中有出现过一个类中的方法过大的情况。

- 支持名称管理器导出
- 

# 使用方法
- `java DataConvert.jar -t all`: 导全表
- `java DataConvert.jar -cs -t skill`: 导技能表