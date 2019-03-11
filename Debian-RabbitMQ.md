# Debian 服务器上安装 RabbitMQ 服务

**1. 查看系统 Codename**
```
root@debian:# lsb_release -a
No LSB modules are available.
Distributor ID: Debian
Description:    Debian GNU/Linux 9.5 (stretch)
Release:        9.5
Codename:       stretch
```

**2. 修改 /etc/apt/sources.list 文件配置，新增 RabbitMQ 源地址**
```
vim /etc/apt/sources.list

# Erlang 依赖包源地址，将 $Codename 修改为上一步查到的 stretch
#deb https://dl.bintray.com/rabbitmq-erlang/debian $Codename erlang
deb https://dl.bintray.com/rabbitmq-erlang/debian stretch erlang

# RabbitMQ 安装包源地址
#deb https://dl.bintray.com/rabbitmq/debian $Codename main
deb https://dl.bintray.com/rabbitmq/debian stretch main
```

**3. 安装 HTTPS 支持**
```
apt-get install apt-transport-https
```

**4. 导入 RabbitMQ Signing Key 到本地**
```
# 从 Github 下载 Key，并导入本地
wget -O - "https://github.com/rabbitmq/signing-keys/releases/download/2.0/rabbitmq-release-signing-key.asc" | sudo apt-key add -
```

**5. 安装 RabbitMQ**
```
# 先更新
apt-get update
# 再安装
apt-get install rabbitmq-server
```

**6. 为 RabbitMQ 安装可视化界面**
```
#可视化界面访问地址 http://127.0.0.1:15672
/usr/sbin/rabbitmq-plugins enable rabbitmq_management
```

## 用户管理
```
# 新增用户
rabbitmqctl  add_user  Username  Password
# 删除用户
rabbitmqctl  delete_user  Username
# 修改密码
rabbitmqctl  change_password  Username  Newpassword
# 查看当前用户列表
rabbitmqctl  list_users
```

### 角色权限管理
+ administrator，超级管理员，可登陆管理控制台(启用management plugin的情况下)，可查看所有的信息，并且可以对用户，策略(policy)进行操作。
+ monitoring，监控者，可登陆管理控制台(启用management plugin的情况下)，同时可以查看rabbitmq节点的相关信息(进程数，内存使用情况，磁盘使用情况等)
+ policymaker，策略制定者，可登陆管理控制台(启用management plugin的情况下), 同时可以对policy进行管理。但无法查看节点的相关信息(上图红框标识的部分)。
+ management，普通管理者，仅可登陆管理控制台(启用management plugin的情况下)，无法看到节点信息，也无法对策略进行管理。
+ 其他，无法登陆管理控制台，通常就是普通的生产者和消费者。
```
# 设置权限，User为用户名，
# Tag为角色名(对应于上面的administrator，monitoring，policymaker，management，或其他自定义名称)。
rabbitmqctl  set_user_tags  User  Tag1  Tag2
```

### 用户权限管理
用户权限指的是用户对exchange，queue的操作权限，包括配置权限，读写权限。配置权限会影响到exchange，queue的声明和删除。读写权限影响到从queue里取消息，向exchange发送消息以及queue和exchange的绑定(bind)操作。

例如： 将queue绑定到某exchange上，需要具有queue的可写权限，以及exchange的可读权限；向exchange发送消息需要具有exchange的可写权限；从queue里取数据需要具有queue的可读权限。详细请参考官方文档中"How permissions work"部分。
```
# 设置用户权限
#rabbitmqctl  set_permissions  [-p  VHostPath]  User  ConfP  WriteP  ReadP
rabbitmqctl  set_permissions -p / user_admin '.*' '.*' '.*'
# 查看(指定hostpath)所有用户的权限信息
rabbitmqctl  list_permissions  [-p  VHostPath]
# 查看指定用户的权限信息
rabbitmqctl  list_user_permissions  User
# 清除用户的权限信息
rabbitmqctl  clear_permissions  [-p VHostPath]  User
```


## 启动和停止
```
# 启动
systemctl start  rabbitmq-server
# 停止
systemctl stop   rabbitmq-server
# 查看状态
systemctl status rabbitmq-server
```

### 默认端口
```
4369 -- erlang发现口
5672 --client端通信口
15672 -- 管理界面ui端口
25672 -- server间内部通信口
```
