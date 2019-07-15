

# Debian 安装 Nginx
Nginx 安装有两种方式

## 1. apt命令安装

`apt-get install nginx      # Debian 系统安装`

安装完成后的目录在 /etc/nginx

默认监听端口：80

服务器配置
+ /etc/nginx
    + Nginx配置目录。所有Nginx配置文件都驻留在此处。
+ /etc/nginx/nginx.conf
    + 主要的Nginx配置文件。可以对此进行修改以更改Nginx全局配置。
+ /etc/nginx/sites-available/
    + 可以存储每站点服务器块的目录。除非链接到目录，否则Nginx不会使用sites-enabled目录中的配置文件。通常，所有服务器块配置都在此目录中完成，然后通过链接到其他目录来启用。
    + 此目录下的站点要激活，需要将其快捷方式放到sites-enabled目录
        + `ln -s /etc/nginx/sites-available/a.b.com /etc/nginx/sites-enabled/a.b.com`
+ /etc/nginx/sites-enabled/
    + 存储已启用的每站点服务器块的目录。通常，这些是通过链接到sites-available目录中的配置文件来创建的。
+ /etc/nginx/snippets
    + 此目录包含可以包含在Nginx配置中其他位置的配置片段。可能可重复的配置段是重构为片段的良好候选者。

服务器日志
+ /var/log/nginx/access.log
    + 除非Nginx配置为执行其他操作，否则对Web服务器的每个请求都将记录在此日志文件中。
+ /var/log/nginx/error.log
    + 任何Nginx错误都将记录在此日志中。

网站目录
+ /var/www/html
    + 实际的Web内容（默认情况下仅包含您之前看到的默认Nginx页面）是从/var/www/html目录中提供的。这可以通过更改Nginx配置文件来更改。

启动和停止
> 通过apt安装的nginx，会自动创建服务 /lib/systemd/system/nginx.service


## 2. 下载安装包，命令安装

下载 nginx

`wget -c http://nginx.org/download/nginx-1.16.0.tar.gz`

解压到指定目录

`tar xzvf nginx-1.16.0.tar.gz -C /usr/local`

安装依赖包
```
apt-get update
apt-get install libpcre3 libpcre3-dev openssl libssl-dev libperl-dev
```
安装 nginx
```
cd /usr/local/nginx-1.16.0
./configure --with-http_ssl_module
make
make install
```

### 注册 service

/etc/systemd/system/nginx.service
```
[Unit]
Description=Nginx web server
After=network.target

[Service]
Type=forking
PIDFile=/usr/local/nginx/conf/nginx.pid
ExecStartPre=/usr/local/nginx -t -q -g 'daemon on; master_process on;'
ExecStart=/usr/local/nginx -g 'daemon on; master_process on;'
ExecReload=/usr/local/nginx -g 'daemon on; master_process on;' -s reload
ExecStop=/usr/local/nginx -s quit

[Install]
WantedBy=multi-user.target
```


## 启动和停止
```
systemctl start nginx       # 启动
systemctl stop nginx        # 停止
systemctl reload nginx      # 重新加载配置
systemctl restart nginx     # 重启
```


## 日志按天分割
```
http{
    ...
    
    map_hash_bucket_size 64;
    map $time_iso8601 $logdate {
        '~^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})' '$1-$2-$3';     # $1第一个参数，$2第二个参数,...
        default 'nodate'; 
    }
    access_log logs/access.${logdate}.log main;     # access.2019-07-15.log
    
    server{
        ...
    }
}
```


## 安全配置
### 禁止访问指定后缀文件
```
server {
    listen       *:80;
    server_name  localhost;
    charset utf-8;
    location / {
            proxy_pass http://127.0.0.1:8080/;
    }
    location ~* \.(ini|asp|php|class|war|java)$ {
        deny all;
    }
}
```
### 禁止指定IP访问
```
http{
    ...
    deny 192.168.10.4;
    deny ...;
    
    server{
        ...
    }
}
```



## 参考文章
信姜缘[ https://cloud.tencent.com/developer/article/1359785](https://cloud.tencent.com/developer/article/1359785)

[ https://www.howtoing.com/how-to-install-nginx-on-debian-9/](https://www.howtoing.com/how-to-install-nginx-on-debian-9/)

不会飞的蝴蝶[ https://cloud.tencent.com/developer/article/1353636](https://cloud.tencent.com/developer/article/1353636)

神农民[ https://blog.csdn.net/shennongminblog/article/details/76158397](https://blog.csdn.net/shennongminblog/article/details/76158397)
