# xmap
## 简介

xmap 是一个用 JavaFX 编写的用户友好的 FOFA、Hunter 客户端， 利用搜索引擎FoFa、Hunter，将众多常用的API封装到简洁的UI中，让网络安全专业人士更容易在目标网站上寻找资产，可以生成iconhash、导出excel、【重点】可以批量执行大量查询语句。 


![image](https://github.com/xvvvan/xmap/assets/62601362/6ea5ccc3-c73a-46ba-9dd2-4b26c409a84e)

## 功能介绍
1、右上角的iconhash处，可以输入url然后回车，会请求默认的url+favicon.ico路径，获取ico得到fofa的icon_hash 也可以将icon下载下来拖拽进这个textfield，也会自动计算

2、四个按钮F对应FOFA，点击会解析左边输入的目标，可以是domain和ip，domain会自动添加domain的语句，ip会转为c段

H为hunter平台的两个语句

M为另一个平台，此处不解释

IC会添加fofa的icon_hash

3、左边可以输入多行语句，以行为单位会自动批量执行！
