### Linux 服务器进程异常关闭日志：
```
# 日志
/var/log/messages
```

### 查看进程关联的文件
```bash
ls -lh /proc/进程PID/fd
# 例：ls -lh /proc/243/fd
```

### Tomcat 进程守护
#### 方法1，利用系统定时器
1、编写 bash 脚本
```bash
#!/bin/sh

# 定义变量，存储当前 tomcat 进程ID
task=`ps -ef|grep tomcat1|grep -v grep|awk '{print $2}'`

# 当前时间
#time=`date "+%Y-%m-%d %H:%m:%S"`
#time=$(date '+%Y-%m-%d %H:%m:%S')
time=$(date)

# 输出当前进程ID
echo [$time]' tomcat task PID' $task

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
2、增加 bash 脚本执行权限
```bash
# chmod 755 /usr/local/defend.sh
chmod +x /usr/local/defend.sh
```

3、编辑系统定时器,https://www.cnblogs.com/ftl1012/p/crontab.html
>>①
```bash
# 系统定时器配置文件，可以查看配置说明
vim /etc/crontab
------------------------
# 每3分钟执行一次，注意root，使用 root 权限执行
*/3 * * * * root /usr/local/defend.sh >> /usr/local/tomcat.defend.sh 2>&1
```
>>②
```
# 用户定时器配置文件，可以使用[crontab -e]命令打开编辑，[crontab -l]查看当前用户定时器
vim /var/spool/cron/root
------------------------
# 每3分钟执行一次
*/3 * * * * /usr/local/defend.sh >> /usr/local/tomcat.defend.sh 2>&1
```

4、重启系统定时器
```bash
# 重启
/etc/init.d/crond restart
# 重新加载配置
/etc/init.d/crond reload
```
crontab日志：`/var/log/cron`

#### 方法2，内部循环
1、编写 bash 脚本
```bash
#!/bin/sh

while true; do
    task=`ps -ef|grep tomcat1|grep -v grep|awk '{print $2}'`

    time=`date "+%Y-%m-%d %H:%m:%S"`

    echo [$time]' tomcat task PID' $task

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
2、增加 bash 脚本执行权限
```bash
# chmod 755 /usr/local/defend.sh
chmod +x /usr/local/defend.sh
```
3、控制台启动脚本
```bash
nohup /usr/local/defend.sh >> /usr/local/tomcat/tomcat.defend.log 2>&1 &
```
