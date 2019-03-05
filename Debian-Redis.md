# Debian Linux 安装 Redis

**1. 下载 Redis 包**
```bash
wget http://download.redis.io/releases/redis-x.x.x.tar.gz       # 下载安装文件
tar xzf redis-x.x.x.tar.gz                                      # 解压缩
cd redis-x.x.x                                                  # 进入 Redis 安装文件目录
```


**2. 执行编译**
```bash
make            # 编译
make test       # 编译测试
make install    # 安装
```


**3. 建立 Redis 服务文件存放目录**
```bash
mkdir /usr/local/redis      # 创建文件夹，目录可以根据习惯自行选择位置
cd /usr/local/redis         # 进入文件夹
```


**4. 将 /usr/local/bin 目录下的 redis 相关文件，复制或移动到 /usr/local/redis**
```bash
cp /usr/local/bin/redis-benchmark   /usr/local/redis        # 复制文件，cp 命令换成 mv为移动
cp /usr/local/bin/redis-check-aof   /usr/local/redis        # 该文件可能不存在，可以忽略
cp /usr/local/bin/redis-check-dump  /usr/local/redis        # 该文件可能不存在，可以忽略
cp /usr/local/bin/redis-cli         /usr/local/redis        # 复制文件
cp /usr/local/bin/redis-sentinel    /usr/local/redis        # 复制文件
cp /usr/local/bin/redis-server      /usr/local/redis        # 复制文件
```

**5. 将 redis-x.x.x 目录下的 redis.conf 复制到 /usr/local/redis**
```bash
cd redis-x.x.x                      # 进入 Redis 安装文件目录
cp redis.conf /usr/local/redis      # 复制文件
```

**6. 打开 /usr/local/redis/redis.conf 文件进行编辑**
```bash
vim /usr/local/redis/redis.conf

bind 0.0.0.0            # 搜索此行 bind 127.0.0.1，将其修改为 bind 0.0.0.0 或注释掉，否则将无法外网访问
requirepass mypass      # 搜索此行，设置 Redis 访问密码
daemonize yes           # 搜索此行，将其修改为 yes，设置 Redis 可以后台运行
```

**7. 打开 /etc/sysctl.conf 文件进行编辑**

```bash
vm.overcommit_memory=1            # 在文件尾部新增此行，设置数据缓存到磁盘，否则重启服务器时 Redis 缓存数据会丢失
```
```bash
sysctl vm.overcommit_memory=1     # 执行命令手动加载该配置，也可以不执行，直接重新服务器
```


**8. 将 redis-x.x.x/utils 目录下的 redis_init_script 文件复制到 /etc/init.d/**
```bash
cp redis-x.x.x/utils/redis_init_script /etc/init.d/redis    # 复制文件并重命名为 redis
```

**9. 打开 /etc/init.d/redis 文件并进行编辑**
```bash
vim /etc/init.d/redis

# 找到 CONF="/etc/redis/{REDISPORT}.conf" ，将其修改为 CONF="/usr/local/redis/redis.conf"
CONF="/usr/local/redis/redis.conf"      
```


### Redis 服务启动、关闭
```
/etc/init.d/redis start     # 启动 redis
/etc/init.d/redis stop      # 关闭 redis
```


