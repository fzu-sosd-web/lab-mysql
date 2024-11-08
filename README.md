# Introduction

本仓库为SOSD-web组 [后端学习路线 - Lec3 数据库](https://xcnwn0k1tzrr.feishu.cn/wiki/YC2hw2zYSicJKtko8sKcEpPwn7g)
配套练习项目，你也可以理解这就是课程对应的lab课设。

没有实践的学习等于白学，因此我们非常建议你 ***make your hands dirty*** ，在思考和debug的过程中学习。

Lec3会有两个lab以供练习：
- Lab A: Sanity Test 要求完成基础的逻辑功能
- Lab B: Benchmark Test 对完成的功能有一定的性能要求


## Lab A: Sanity Test

你需要拥有的**前置知识**：
- maven的基本使用（让项目跑起来）
- MySQL（或其他dbms）环境搭建
- 数据库表设计能力
- Java编程能力

你最好遵守我们的[开发规范](https://xcnwn0k1tzrr.feishu.cn/wiki/WnQCwlFZniDhoWkYnLCcbwbRnAb)

### 任务：

一句话可以概括你要做的事情，就是实现`UserService`这个接口的所有方法。至于如何实现，如何创建数据库表，如何写SQL语句，就是你自己考虑的事情啦！

注意：不要修改UserService接口类的方法，也不要修改SanityTest类中的代码逻辑。

除了改变项目原本的框架代码，你想怎么写都是可以的，你想简单的使用JDBC，你想搞个ORM框架秀一下技术，都是可以的，
技术并不是堆越多越NB，有时候less is more，适合的就是最好的。

### 提示：

有待补充......

### 验证：

如果你使用的是IDEA等智能的IDE，那么你可以直接打开SanityTest这个类，直接点击绿色的小三角形运行。

测试代码已经写好了，你不需要去改动它。

当你看到命令行输出

```
>>> Pass all sanity tests, Congrats! >>>
```

那么恭喜你，你已经通过了Lab A 。你可以写一篇小小的心得体会来记录一下这个开心的结果。


## Lab B: Benchmark Test

待完成......