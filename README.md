NutzWk 基于Nutz的开源企业级开发框架
======

[![Build Status](https://travis-ci.org/Wizzercn/NutzWk.png?branch=bootstrap)](https://travis-ci.org/Wizzercn/NutzWk)
[![GitHub release](https://img.shields.io/github/release/Wizzercn/NutzWk.svg)](https://github.com/Wizzercn/NutzWk/releases)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![PowerByNutz](https://img.shields.io/badge/PowerBy-Nutz-green.svg)](https://github.com/nutzam/nutz)

在线演示地址
======
https://nutzwk.wizzer.cn/                 NutzWk v3.x、v4.x

https://vue.wizzer.cn                     NutzWk v3.x-vue


# 前言

项目源于2010年，那时老东家还在使用Jsp和Struts1，需要手动创建并释放连接池、需要配置XML请求路径和类映射关系、要支持刚刚兴起的JSON非常痛苦等等的原因，开始选择适用“快速开发、功能丰富、扩展性强、性能优越”等技术要求的框架产品，讨厌Spring的繁杂配置、Hibernate及Mybatis的繁琐，讨厌一切让开发变得低效和繁杂的技术，这和Nutz的设计理念不谋而合。

使用本框架开发商用项目始于2012年，先是基于NutzWk v1.0开发了CMS网站群管理系统、网络问政系统，而后分别用于交通厅网站群项目、12345市长热线项目、财政厅数据上报、羽毛球场地管理、新媒体指数等项目，3.x应用于Police大数据分析、Police视频监控平台及中科大财务处、天乐e生活等各类微信公众号项目中，4.x应用于B2C商城、B2B2C产品、物联网、xx医院药物闭环APP等项目，经过几年的积累，使用NutzWk开发并商用的项目少则几十多则上百。因为她是开源的，不光老东家和现所在公司在用，广大网友也在用哦。

我们有强大的后援 —— Nutz 社区支持  https://nutz.cn  及 Nutz 使用手册 https://nutzam.com/core/nutz_preface.html

### QQ交流群: 68428921

ps：这几年明显感受到国产开源项目越来越多，各种五花八门的框架，让使用者很难选择；不鼓吹自己的框架多么多么好，个人觉得适用于项目需要并且能在满足技术需求的基础上最大限度的提高开发效率的框架，就是好框架。

# 版本说明

*   NutzWk v5.x 微服务版本(分支名:nutzboot-dubbo,微服务dubbo分布式版本)
*   NutzWk v4.x 模块化版本(分支名:modular,统一提供代码生成器及IDEA可视化插件,,可拆模块用dubbo等分布式技术)
*   NutzWk v3.x 单应用版本(分支名:bootstrap-3.3.x,CMS+微信+系统+权限+常用功能封装 beetl/velocity)
*   NutzWk v2.0 试验版(不建议使用)
*   NutzWk v1.0 传统版(分支名:master,velocity 支持IE6)

# 本版说明(v5.x)

## NutzWk 5.x 运行环境：

*   JDK 8 162+
*   Tomcat 8.5.28 + / Jetty 9.4 +
*   Maven 3.5.x +

## NutzWk 5.x 技术选型：

*   核心框架：Nutz、Nutz MVC、Nutz Dao、Nutzboot
*   分布式框架：Dubbo、Zookeeper
*   安全框架：Apache Shiro
*   任务调度：Quartz
*   数据库连接池：Druid 
*   支持数据库：MySql、Oracle、SqlServer、达梦 
*   缓存框架：Redis、Ehcache
*   消息队列：Rabbitmq
*   扩展功能：WebSocket-Nutz、方法缓存-Wkcache、搜索引擎-Elasticsearch、工作流-Activiti等
*   前端框架：Bootstrap+JQuery

## NutzWk 5.x 使用说明(等待更新)：



## NutzWk 5x 代码生成器安装使用(等待更新):




# 鸣谢
*   [@wendal](https://github.com/wendal) (代码贡献者,技术大牛,Nutz主要作者,无所不知且乐于助人)
*   [@rekoe](https://github.com/Rekoe) (代码贡献者)
*   [@enilu](https://github.com/enilu) (代码生成器及IDEA插件贡献者)
*   [@loyalove](https://github.com/loyalove) (Vue代码贡献者)
*   [@threefish](https://github.com/threefish) (控制类快速定位模板页面IDEA插件贡献值)
*   以及交流群里热心的小伙伴们~

# 关于

*   本项目完全开源，商用完全免费
*   欢迎打赏，以资鼓励

![后台截图](demo.png)

![打赏](pay.jpg)

