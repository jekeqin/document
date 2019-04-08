# JMeter 并发测试/压力测试/服务器运行监控

+ JMeter 下载
    + 下载地址：[官网 http://jmeter.apache.org](http://jmeter.apache.org/)
    + 汉化
        + /bin/jmeter.properties  取消 language=en 注释
        + 设置 language=zh_CN
+ JMeter 软件启动
    + /bin/jmeter.bat
+ JMeter 测试启动
    + 不要使用GUI运行压力测试，GUI仅用于压力测试的创建和调试；执行压力测试请不要使用GUI。使用下面的命令来执行测试\
    `jmeter -n -t [jmx file] -l [results file] -e -o [Path to web report folder]`
    + JMeter批处理文件的环境变量：\
    `HEAP="-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m"`

+ JMeter 服务器监控插件下载及安装，监控端
    + 下载地址：[https://jmeter-plugins.org/install/Install/](https://jmeter-plugins.org/install/Install/)
    + https://jmeter-plugins.org/downloads/old/
    + https://jmeter-plugins.org/wiki/PluginsManager/
    + jmeter-plugins-manager-1.3.jar
        + 下载后放到 /lib/ext/ 目录下
    + 启动 JMeter 
        + 选项菜单
            + Plugins Manager
                + Abaliable Plugins 选项卡
                    + 勾选 PerfMon (Servers Performance Monitoring)
                    + 点击 Apply Changes and Restart JMeter
                    >支持Base64加解密等多个函数的插件 Custom JMeter Functions\
                    >用于服务器性能监视的 PerfMon Metrics Collector\
                    >用于建立压力变化模型的 Stepping Thread Group\
                    >用于Json解析的 JSON Path Extractor\
                    >用于展示响应时间曲线的 Response Times Over Time\
                    >用于展示TPS曲线的 Transactions per Second
+ JMeter 服务器端监控插件，被监控端
    + 下载地址：[https://github.com/undera/perfmon-agent](https://github.com/undera/perfmon-agent)
    + ServerAgent-2.2.3.zip
        + Windows: ServerAgent.bat
        + Linux: ServerAgent.sh
        + `ServerAgent-2.2.3/startAgent.sh--udp-port 4445 --tcp-port 4445`
---
## 简单使用流程

+ 添加线程组，右击测试计划 (Test Plan)，
    + 添加 (Add)
        + 线程(用户)  (Thread(Users))
            + 线程组 (Thread Group)

+ 线程组设置， 点击 线程组
    + 线程属性 (Thread Properties)
        + 线程数(Number of Threads (users))
            + *一个用户占一个线程，  10个线程就是模拟10个用户*
        + Ramp-Up时间(秒)
            + *设置线程需要多长时间全部启动。如果线程数为200 ，准备时长为10 ，那么需要1秒钟启动20个线程。也就是每秒钟启动20个线程。*
        + 循环次数 (Loop Count)
            + *每个线程发送请求的次数。如果线程数为200 ，循环次数为10 ，那么每个线程发送10次请求。总请求数为200x10=2000 。如果勾选了“永远”，那么所有线程会一直发送请求，直到选择停止运行脚本。*

+ 添加CSV数据文件，右击线程组 (Thread Group)
    + 添加 (Add)
        + 配置元件 (Config Element)
            + CSV 数据文件设置  (CSV Data Set Config)
    + 数据文件内容格式如下 ( txt文件 )
        >name1,25\
        >name2,30\
        >name3,19\
        >...

+ CSV 数据设置， 点击 CSV 数据文件设置
    + 设置 CVS 数据文件 (Configure the CSV Data Source)
        + 文件名 (Filename)
            + *选择数据文件路径*
        + 文件编码 (File encoding)
            + *设置文件编码，建议 utf-8*
        + 变量名称(西文逗号间隔) (Variable Names(comma-delimited))
            + *填写变量名称，半角逗号间隔，例：name,age*

+ 文件读取网址
    + Tools
        + 函数助手对话框
            + 选择一个功能
                + __StringFromFile
            + 函数参数
                + 输入文件的全路径
                    + *值，粘贴 txt文件路径*
            + 点击 生成
                + *例：${__StringFromFile(D:\api.txt,,,)}*
    + 网址文件格式如下 ( txt文件 )
        >/user/login.json\
        >/user/friends.json\
        >/shop/list.do\
        >/shop/map.html\
        >...


+ 添加 HTTP请求，右击 线程组(Thread Group)
    + 添加 (Add)
        + 取样器 (Sampler)
            + HTTP请求 (HTTP Request)

+ HTTP请求设置，点击 HTTP请求
    + Web服务器 (Web Server)
        + 协议 (Procotol)
            + *请求协议，http*
        + 服务器名称或IP (Server Name or IP)
            + *填写服务器地址，不含协议头及端口，端口填在端口参数输入框，例：api.mysite.com*
        + 端口号 (Port Number)
            + *填端口号，例：80、8080*
    + HTTP请求
        + 方法，选择接口对应开放的方法
        + 路径，两种填写方案
            + 直接填写接口地址
                + *例：/user/login.json*
            + 填写网址文件
                + *例：${__StringFromFile(D:\api.txt,,,)}*
                + *网址文件通过前面的操作步骤生成*

+ 测试报告，右击线程组
    + 添加
        + 监听器 (Listener)
            + 汇总报告 (Summary Report)\
                *也可以查看其他报告*
            
+ 启动测试，文首命令启动


---
## 服务器状态实时监听

+ CPU，内存，磁盘，网络，TPS，响应时间图等等，才是压力测试的目的所在，服务器状态随着请求增加的变化曲线才是我们更加需要看到的。
+ 想看到服务器变化的曲线图，我们需要下载 JMeterPlugins-Extras.jar 和 JMeterPlugins-Standard.jar ，将这两个jar包放入Jmeter的安装路径下的lib/ext/ 下面。
+ 重启Jmeter后，右击“线程组”——“添加”——“监听器”，我们可以看到多了好多监听器，大多数以“jp@gc”开头的监听器，我们选择“PerfMon Metrics Collector”，里面将会显示服务器的状态信息
+ 现在还看不到，为什么，因为要测试的服务器需要安装一个包，叫 ServerAgent.zip
    + 比如我的服务器是linux，我们将 ServerAgent 在服务器中解压，运行里面的 startAgent.sh 就可以啦
    + 默认端口是4444，Telnet 有可能 ping 不到 4444 端口导致显示不了
        + 将端口改成 4445 或其他端口启动就可以了
        + 启动命令如下\
        `ServerAgent-2.2.3/startAgent.sh--udp-port 4445 --tcp-port 4445`
    + 我们回到JMeter软件，按下图填入服务器的地址和端口号，以及需要监控的硬件，如CPU，内存，磁盘，网络等等，内容如下
        >HOST/IP|Port|Metric to collect|Metric parameter(see help)
        >:---|:---|:---|:---|
        >127.0.0.1|4444|CPU
        >127.0.0.1|4444|Memory
        >127.0.0.1|4444|Disks I/O
        >127.0.0.1|4444|Network I/O
        + 添加
            + 监听器
                + jp@gc - PerfMon Metrics Collector
                + 启动运行之后，可以在Chart下看到服务器状态


---
参考文章：[冷枫孤雪 https://www.cnblogs.com/fanjc/p/10309579.html](https://www.cnblogs.com/fanjc/p/10309579.html)\
参考文章：[KK_Yolanda https://www.cnblogs.com/zhaoxd07/p/5197669.html](https://www.cnblogs.com/zhaoxd07/p/5197669.html)\
参考文章：[老_张 https://www.cnblogs.com/imyalost/p/7751981.html](https://www.cnblogs.com/imyalost/p/7751981.html)\
参考文章：[lsoqvle https://blog.csdn.net/cbzcbzcbzcbz/article/details/78023327](https://blog.csdn.net/cbzcbzcbzcbz/article/details/78023327)
