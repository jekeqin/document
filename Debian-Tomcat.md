# Debian 服务器安装 Tomcat

使用tomcat前，需先安装 jdk

## 1. 下载 tomcat

apache-tomcat-9.0.19.tar.gz ，然后上传至服务器

或命令下载
`wget -c http://mirrors.tuna.tsinghua.edu.cn/apache/tomcat/tomcat-9/v9.0.19/bin/apache-tomcat-9.0.19.tar.gz`

## 2. 解压到指定目录 /usr/local/tomcat

tar xzvf apache-tomcat-9.0.19.tar.gz

3.编写服务类 /etc/systemd/system/tomcat.service
```bash
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target
​
[Service]
Type=forking
​
Environment=JAVA_HOME=/usr/local/jdk-1.8.0
Environment=CATALINA_PID=/usr/local/tomcat/conf/tomcat.pid
Environment=CATALINA_HOME=/usr/local/tomcat
Environment=CATALINA_BASE=/usr/local/tomcat
Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'
Environment='CATALINA_OPTS=-Xmx1024M -Xms512M -server -XX:+UseParallelGC'
​
ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh
​
[Install]
WantedBy=multi-user.target
```

## 3. 加载 service

`systemctl daemon-reload        # 重新加载systemd守护程序`

## 4. 开机启动

`systemctl enable tomcat        # 开启随系统启动`

`systemctl disabled tomcat      # 关闭随系统启动`

## 5. Tomcat 启动/停止

```
systemctl start tomcat          # 启动
systemctl stop tomcat           # 停止
systemctl restart tomcat        # 重启
systemctl status tomcat         # 运行状态
```




## 参考资料
ma_fighting[ https://www.cnblogs.com/mafeng/p/10316351.html](https://www.cnblogs.com/mafeng/p/10316351.html)

GeekZ[ https://cloud.tencent.com/developer/article/1360761](https://cloud.tencent.com/developer/article/1360761)