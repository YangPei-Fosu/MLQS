# MQLS Dev 0.0.12

## 更新内容

| 版本    | 更新内容                      | 更新时间       |
|-------|---------------------------|------------|
| 0.0.1 | 初始化系统框架                   | 2025-11-04 |
| 0.0.2 | 完成记忆持久化部分                 | 2025-11-05 |
| 0.0.3 | 完成代理部分、构建ES部分的框架          | 2025-11-05 |
| 0.0.4 | 完善了代理与缓存部分的问题             | 2025-11-07 |
| 0.0.5 | 添加了基础的用户模块、添加了会话模块        | 2025-11-10 |
| 0.0.5 | 完善了用户模块与会话模块的逻辑           | 2025-11-10 |
| 0.0.6 | 完善了接口交互逻辑                 | 2025-11-11 |
| 0.0.7 | 构建基本的知识库框架，优化了缓存在进程关闭时的行为 | 2025-11-11 |
| 0.0.8 | 完善了注册和登录的mapper           | 2025-11-11 |
| 0.0.9 | 完善了知识库框架的逻辑 | 2025-11-12 |
| 0.0.10 | 构建案例库框架，引入婚姻法到知识库 | 2025-11-20 |
| 0.0.11 | 引入了bge-reranker-large重排模型完善处理逻辑 | 2025-11-21 |

注：测试时先在用户表中添加一个用户

##安装重排模型步骤：
1、创建虚拟环境
python -m xinference_env .
2、激活虚拟环境
.\xinference_env\Scripts\activate
3、安装xinference
pip install "xinference[all]"
4、安装flask
pip install flask
5、启动xinference：IP本地9997端口（注意：若更改则同时更改yml内的rerank.url、rerank_main.py中的url以确保一致）
xinference-local --host 127.0.0.1 --port 9997
6、启动bge-reranker-large模型
xinference launch --model-name bge-reranker-large --model-type rerank --replica 1
7、等模型下完之后启动服务（src/main/resources/python/rerank_main.py）注意在虚拟环境内启动以免其他问题
8、运行ExtraServiceTest中的rerank_test()即可测试是否成功

## UML类图

https://www.processon.com/v/6909b12d9586bb564a4990ab
![image-20251106162048407](MQLS.jpg)