### Linux 服务器进程异常关闭日志：
```
# 日志
/var/log/messages
```

### Tomcat 进程守护
#### 方法1，利用系统定时器
1、编写 bash 脚本
```
#!/bin/sh

# 定义变量
task=`ps -ef|grep tomcat1|grep -v grep|awk '{print $2}'`

# 输出当前进程ID
echo 'tomcat task PID' $task

# 判断进程ID是否为空
if [ ! $task ]; then
    echo 'tomcat stopped'
    nohup /usr/local/tomcat/bin/startup.sh >> /usr/local/tomcat/tomcat.defend.log 2>&1 &
    echo 'tomcat restarted'
else
    echo 'tomcat running'
fi  # 结束if
exit  # 退出脚本
```
2、编辑系统定时器
```
vim /etc/crontab
###################
*/3 * * * * /usr/local/defend.sh >> /usr/local/tomcat.defend.sh 2>&1
```
3、重启系统定时器
```
# 重启
/etc/init.d/crond restart
# 重新加载配置
/etc/init.d/crond reload
```

#### 方法2，内部循环
1、编写 bash 脚本
```
#!/bin/sh

while true; do
    task=`ps -ef|grep tomcat1|grep -v grep|awk '{print $2}'`

    echo 'tomcat task PID' $task

    if [ ! $task ]; then
        echo 'tomcat stopped'
        nohup /usr/local/tomcat/bin/startup.sh >> /usr/local/tomcat/tomcat.defend.log 2>&1 &
        echo 'tomcat restarted'
    else
        echo 'tomcat running'
    fi
    sleep 60       # 睡眠，单位：秒
done
```
2、控制台启动脚本
```
nohup /usr/local/defend.sh >> /usr/local/tomcat/tomcat.defend.log 2>&1 &
```
