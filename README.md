# xmap
资产测绘 支持fofa和hunter，导出excel，快速生成常用语句
![image](https://github.com/xvvvan/xmap/assets/62601362/6ea5ccc3-c73a-46ba-9dd2-4b26c409a84e)
1、右上角的iconhash处，可以输入url然后回车，会请求默认的url+favicon.ico路径，获取ico得到fofa的icon_hash
2、四个按钮F对应FOFA，点击会解析左边输入的目标，可以是domain和ip，domain会自动添加domain的语句，ip会转为c段
H为hunter平台的两个语句
M为另一个平台，此处不解释
IC会添加fofa的icon_hash
3、左边可以输入多行语句，以行为单位会自动批量执行
