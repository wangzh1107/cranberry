
> 一个Java程序猿的自我修养：系统设计、常用技术栈、Java 基础、算法。

## 系统设计
- 数据库锁
    - [悲观锁](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/dblock/PessimisticLock.java)
    - [乐观锁](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/dblock/OptimismLock.java)
- 限流     
    - [单机限流【计数器算法实现】](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/ratelimiter/StandAloneRateLimiter.java)
    - [分布式限流【基于 redis 实现】](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/ratelimiter/RedisRateLimiter.java)
- 分布式锁
    - [基于数据库实现](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/distributedlock/DbDistributedLock.java)
    - [基于 Redis 实现](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/distributedlock/RedisDistributedLock.java)
    - [Redisson 分布式锁的简单使用](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/distributedlock/RedissonDistributedLock.java)
    - 【TODO】基于 zk 实现 
    - [Curator 分布式锁的简单使用](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/distributedlock/CuratorDistributedLock.java)
- example
    - [zkClient 简单使用](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/example/ZkExample.java)
- redis 常见问题
    - [数据库缓存双写一致性](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/redisproblem/DbCacheDoubleWriteConsistency.java)
- 布隆过滤器
    - [简单手撸实现](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/systemdesign/bloomfilter/SimpleBloomFilter.java)
    - [guava 布隆过滤器实现通过去重工具](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/deduplication)
- 设计模式
    - [适配器模式](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/adapter)
    - [命令模式](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/command)
    - [装饰器模式](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/decorator)
    - 工厂模式
        - [简单工厂](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/factory/simple)
        - [工厂方法](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/factory/factory)
        - [抽象工厂](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/factory/abstractfactory)
    - [观察者模式](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/observer)
    - [单例模式](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/singleton)
    - [状态模式](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/state)
    - [策略模式](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/systemdesign/designpattern/strategy)

## 常用技术栈
- Spring 事务
    - [编程式事务【基于 PlatformTransactionManager】](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/thirdparty/transaction/ProgrammingTx.java)
    - [编程式事务【基于 TransactionTemplate】](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/thirdparty/transaction/TemplateTx.java)
    - [声明式事务【基于 @Transactional 注解】](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/thirdparty/transaction/AnnotationTx.java)


## Java 基础
- 线程池
    - [线程中断与线程池关闭问题](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/javabasis/threadpoolproblem/ThreadPoolShutdown.java)
- Java 8 新特性
    - [lambda 表达式](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/javabasis/newfeature/lambdaexpression)
    - [Stream API](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/javabasis/newfeature/streamapi/StreamApiTest.java)
    - [Collection forEach](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/javabasis/newfeature/collectionforeach/CollectionForEachTest.java)
- [Java SPI 机制](https://github.com/dearKundy/cranberry/tree/master/src/main/java/com/kundy/cranberry/javabasis/spi)
- 多线程
    - 工具
        - [CountDownLatch、CyclicBarrier、Semaphore](https://github.com/dearKundy/cranberry/blob/master/docs/javabasis/multithread/tools/CountDownLatch、CyclicBarrier、Semaphore.md)
- [Java 集合](https://github.com/dearKundy/cranberry/blob/master/docs/javabasis/Java集合.md)

## 算法
- 排序
    - [选择排序](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/algorithm/sort/SelectSort.java)
    - [冒泡排序](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/algorithm/sort/BubbleSort.java)
    - [插入排序](https://github.com/dearKundy/cranberry/blob/master/src/main/java/com/kundy/cranberry/algorithm/sort/InsertSort.java)