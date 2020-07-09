### Bash命令
```
# 查看系统版本（可列出所有版本信息）：
lsb_release -a

# 查看系统版本（仅列出系统版本）：
cat /etc/redhat-release
cat /etc/issue

# 查看内核版本：
cat /proc/version
uname -a

# 查看端口监听占用
netstat -tnlp|grep 8080
```

### 防火墙
#### iptables 防火墙
```bash
service iptables status     # 查看防火墙状态
service iptables stop       # 停止防火墙
service iptables start      # 启动防火墙
service iptables restart    # 重启防火墙
chkconfig iptables off      # 永久关闭防火墙
chkconfig iptables on       # 永久关闭后重启
```
开启80端口
```
vim /etc/sysconfig/iptables
# 加入如下代码
-A INPUT -m state --state NEW -m tcp -p tcp --dport 80 -j ACCEPT

service iptables restart    # 保存退出后重启防火墙
```
#### firewalld 防火墙
```
systemctl status firewalld      # 查看firewall服务状态
    # 出现Active: active (running)切高亮显示则表示是启动状态。
    # 出现 Active: inactive (dead)灰色表示停止，看单词也行。

firewall-cmd --state            # 查看firewall的状态

service firewalld start         # 开启服务
service firewalld restart       # 重启服务
service firewalld stop          # 关闭服务

firewall-cmd --list-all         # 查看防火墙规则

firewall-cmd --query-port=8080/tcp              # 查询端口是否开放
firewall-cmd --permanent --add-port=80/tcp      # 开放80端口
firewall-cmd --permanent --remove-port=8080/tcp # 移除端口

firewall-cmd --reload                           #重启防火墙(修改配置后要重启防火墙)

# 参数解释
#   1、firwall-cmd：是Linux提供的操作firewall的一个工具；
#   2、--permanent：表示设置为持久；
#   3、--add-port：标识添加的端口；



### CentOS系统更新，漏洞修复
```
# 升级所有包，不改变软件设置和系统设置，系统版本升级，内核不改变
yum -y upgrade

# 升级所有包，改变软件设置和系统设置,系统版本内核都升级(不推荐)
yum -y update
```

### Bash版本漏洞检测
```
env x='() { :;}; echo vulnerable'  bash -c "echo this is a test" 
# 输出以下内容则不需要修复
this is a test 
# 输出以下内容则需要修复
vulnerable  
this is a test  
# 修复命令
yum -y update bash 
```


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
