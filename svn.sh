#!/bin/bash
# ↑声明Shell命令类型， !/bin/bash = !/bin/sh，两种写法
# Shell文件名可不带扩展名，即 javacv.sh = javacv
# 级别
#chkconfig: 2345 80 90
# 描述
#description:javacv service
# 进程名
#processname:javacv-service

# 创建快捷方式
# ln -s /xxx/xxx/xxx.sh /etc/init.d/xxx

# 定义变量，路径
BASE_PATH="/usr/svn"

start(){
    # 先调用执行 stop() 方法
    stop

    # 定义变量，日期，用于日期文件切片
    current_date=`date "+%Y%m%d"`

    # 控制台输出提示内容
    echo $"Starting $prog: "

    # 运行 jar，方式1，不带 main.args 参数
    #java -jar /home/vrcut.jar

    # 运行jar，方式2，附带 main.args 参数，多个参数用空格分隔，第一个[>]指定日志路径及文件名称格式，尾部[&]表示后台运行
    #nohup java -jar /home/vrcut.jar 'jdbc:mysql://127.0.0.1:3306/mydata?characterEncoding=UTF-8' dbuser dbpass args_n > /home/JavaOpenCV.${current_date}.log 2>&1 &

    # 运行jar，方式3，参数路径方式，可设置 Jvm 内存，[>>]重定向日志路径，[2>&1]将标准错误重定向到标准输出，[ &]后台运行
    #     [     脚本路径         ]   [          日志输出路径              ]
    nohup svnserve -d -r /usr/svn >> ${BASE_PATH}/out.${current_date}.log 2>&1 &

    pid     # 调用 pid() 方法

    # 控制台输出提示内容
    echo "svn-server service started"
}

stop(){
#    echo -n $"Stopping $prog: "
#    javac -version
    
    # 按进程名称搜索进程并控制台输出
    pid

    # 按进程名称搜索进程并kill，再输出
    echo `kill -9 $(ps -ef|grep svnserve|grep -v grep|awk '{print $2}')`

    # 控制台输出
    echo $"Stopped svn-server service"
}


pid(){
    # 按进程名搜索进程，第二个grep为排除ps命令自身
    echo `ps -ef|grep svnserve|grep -v grep`
}

case "$1" in    # 判断参数1
    start)      # 参数等于start
    start       # 执行 start() 方法
    ;;          # 执行结束，后续 case 项不再执行
    stop)
    stop
    ;;
    restart)
    stop
    start
    ;;
    pid)
    pid
    ;;
    *)
    echo $"Usage: $0 {start|stop|restart|pid}"    # 提示输入参数为以下3个
    RETVAL=1
esac
exit $RETVAL
