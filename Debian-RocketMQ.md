# Debian 服务器上安装 RocketMQ

安装 RocketMQ 前，需先安装 jdk、Maven

**1. 下载 RocketMQ**
```bash
cd /usr/local                                # 进入目录
wget 'RocketMQ 下载地址'                      # https://rocketmq.apache.org，也可以Win下载后上传到服务器
unzip rocketmq-all-x.x.x-source-release.zip  # 解压
```

**2. 编译部署**

```bash
cd rocketmq-all-x.x.x/                              # 进入 rocketmq 安装文件目录
mvn -Prelease-all -DskipTests clean install -U      # 从 Maven 中央仓库下载项目依赖的 jar 包编译部署
# 编译完成后会输出如下信息
[INFO] -----------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] -----------------------------------------------------
[INFO] Total time:  07:50 min
[INFO] Finished at: 2019-03-18T1429:16+08:00
[INFO] -----------------------------------------------------

ls          # 查看当前目录文件及文件夹列表
acl       common           dustribution  LICENSE      NOTICE         remoting  target
broker    CONTRIBUTING.md  docs          logappender  openmessaging  srvutil   test
BUILDING  DEPENDENCIES     example       logging      pom.xml        store     tools
client    dev              filter        namesrv      README.md      style

cd ./distribution/target/apache-rocketmq        # 进入目录, RocketMQ 编译安装目录
```


**3. 配置 RocketMQ 内存**
```bash
# 修改 runserver.sh 内存配置
vim ./bin/runserver.sh
# 根据内存情况，修改内存配置
JAVA_OPT="${JAVA_OPT} -server -Xms4g -Xmx4g -Xmn2g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"

# 修改 runbroker.sh 内存配置
vim ./bin/runbroker.sh
# 根据内存情况，修改内存配置
JAVA_OPT="${JAVA_OPT} -server -Xms8g -Xmx8g -Xmn1g"
```


**4. 创建 RocketMQ 启动、关闭服务**
```bash
vim ./rocketmq.sh

#!/bin/bash
# ↑声明Shell命令类型， !/bin/bash = !/bin/sh，两种写法
# Shell文件名可不带扩展名，即 javacv.sh = javacv
# 级别
#chkconfig: 2345 80 90
# 描述
#description:RocketMQ service
# 进程名
#processname:rocketmq-service

# 定义变量，路径
ROCKETMQ_PATH="/usr/local/rocketmq-all-x.x.x/distribution/target/apache-rocketmq"

start(){


    # 定义变量，日期，用于日期文件切片
    current_date=`date "+%Y%m%d"`

    # 启动 rocketmq server mqnamesrv
    nohup sh ${ROCKETMQ_PATH}/bin/mqnamesrv > ${ROCKETMQ_PATH}/log/server.${current_date}.log 2>&1 &

    # 启动 broker mqbroker
    nohup sh ${ROCKETMQ_PATH}/bin/mqbroker -n localhost:9876 > ${ROCKETMQ_PATH}/log/broker.${current_date}.log 2>&1 &
}

stop(){
    # 先关闭 broker
    sh ${ROCKETMQ_PATH}/bin/mqshutdown broker

    # 再关闭 namesrv
    sh ${ROCKETMQ_PATH}/bin/mqshutdown namesrv
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    *)
    echo $"Usage: $0 {start|stop|restart}"      # 提示可输入以下参数

    RETVAL=1
esac
exit $RETVAL
```