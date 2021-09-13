### 基础

#### JDK 与 JRE 的区别
```
JDK:
  Java 开发工具包，用于开发、编译源代码，其中包含 jre
JRE:
  Java 运行时环境
```

#### JVM 的内存模型
```
堆：
  所有线程共享的区域，在JVM启动的时候创建，用于存放对象的实例，配置参数为 -Xms -Xmx，堆内存不足时报 OutOfMemoryError 异常
栈：
  虚拟机栈：
    线程私有区域，每个方法在执行的时候会创建一个栈帧，其中存储 局部变量、操作数、动态链接、方法返回地址。
    每个方法从调用到执行完毕，对应一个栈帧在JVM栈中的入栈和出栈。
    通常所说的栈，一般是指JVM栈中的局部变量部分，局部变量所需内存在编译期间完成分配。
    若线程请求的栈深度大于JVM所允许的深度，会抛 StackOverflowError 异常。
    若虚拟机栈可动态扩展，扩展到无法申请足够内存时，抛 OutOfMemoryError 异常。 
  本地方法栈：
    与虚拟机栈类似，主要为JVM及使用到的 Native 方法服务。
    会抛 StackOverflowError 与 OutOfMemoryError 异常。
方法区：
  所有方法线程共享的区域，用于存储已经被虚拟机加载的Class信息、常量、静态变量等。
  该区域的内存回收主要针对 常量池 和 堆类型 的卸载。
程序计数器：
  一块较小的内存空间，当前线程所执行的字节码的行号指示器，如果是 Native 方法则为空。
  字节码解释器工作时通过改变计时器的值来选取下一条需要执行的字节码指令。
  分支、循环、跳转、异常处理、线程恢复等基础功能都需要依赖与计时器。
```

#### 内存回收算法
```
标记-清除算法
标记-整理算法
复制算法
分代收集算法(复合算法)
增量收集算法(复合算法)
分区算法(复合算法)
```

#### equals 与 == 的区别

#### 两个对象的 hashCode() 相同，则 equals 是否一定为 true

#### final 在 Java 中有什么作用

#### Math.round(-1.4) Math.round(-1.5) Math.round(-1.6) 结果是多少

#### 操作字符串的类有哪些，区别是什么

#### String str = "abc" 与 String str = new String("abc") 的区别

#### 如何将字符串反转

#### String 类的常用方法有哪些

#### 抽象类 接口类 普通类的区别

#### BIO NIO AIO 的区别

#### Files 的常用方法有哪些

#### 判断类 Class 相等的方式有几种

#### 强引用 弱引用 软引用

#### 原生注解有哪些

#### 线程池

#### 同步锁 synchronized 原理及使用

#### synchronized lock 的区别

#### synchronized volatile 的区别

#### synchronized ReentrantLock 的区别

#### 创建线程、线程池的方式

#### Runnable Callable 的区别

#### sleep() wait() 的区别

#### notify() notifyAll() 的区别

#### 线程 run() start() 的区别

#### 线程池中 submit() execute() 的区别

#### 如何保存多线程的运行安全

#### 线程的状态有哪些

#### 线程锁的升级原理

#### 线程死锁及怎么防止死锁

#### 线程池的资源消耗在哪

#### 如何实现对象克隆

#### 深克隆 浅克隆 的区别

#### ThreadLocal 的使用场景

#### 反射的原理

#### 动态代理的原理及实现步骤

#### throw throws 的区别

#### final finally finalize 的区别

#### Native(JNI)
---

### 容器

#### List Array LinkedList LinkedArrayList 的区别

#### ArrayList Vector 的区别

#### Collection Collections 的区别

#### Map HashMap TreeMap LinkedHashMap 的区别

#### Set Map 的区别

#### HashMap HashTable 的区别

#### HashMap 扩容原理

#### 哪些集合类是线程安全的

#### Iterator 的特征及使用方式

#### 如何确保集合不被修改

---

### Spring

#### SpringBoot 与 MVC 的区别

#### SpringBoot 的注解有哪些

#### AOP的原理

#### IOC是什么

#### Spring 常用的注入方式

#### Controller Service 默认是单例还是多例

#### Bean 的作用域及 自动装配方式

#### 事务的实现方式有哪些

#### Boot 的核心配置文件是什么

#### 配置文件有几种类型

#### Boot 哪些方式可实现热部署

---

### 数据库查询框架

#### Hibernate 的特点

#### Hibernate 的查询方式有几种

#### Hibernate Mybatis 的区别

#### Mybatis #{} ${} 的区别

#### Mybatis 分页方式有几种

#### Mybatis 逻辑分页与 物理分页的区别

#### 延迟加载的原理及如何开启

#### Mybatis 一级 二级缓存

#### 

---

### Mysql



