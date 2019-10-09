# Debian 服务器上安装 JDK


**1. 下载 JDK**

前往官网下载 JDK 安装包，jdk-x.x.x_linux-x64_bin.tar.gz，然后上传至服务器

或使用 wget 命令下载

**2. 配置 JDK 环境变量**
配置文件尾部追加 Maven 配置
```bash
vim /etc/profile

export JAVA_HOME=/usr/local/jdk-x.x.x                   # JDK 安装根路径
#export JRE_HOME=/usr/local/jdk/jre                     # 公用 JRE 路径，注意，JDK10 版本后 jre 需单独下载
#export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:${CLASSPATH}
export CLASSPATH=.:${JAVA_HOME}/lib:${CLASSPATH}
export PATH=$JAVA_HOME/bin:$PATH                        # 注意，Debian 系统一般自带 JDK，将 JAVA_HOME 放在前面，才能识别到自己安装的版本，放在后面以先识别的是系统自带版本

# 修改完成后，按‘Esc’键退出编辑模式，再输入‘:wq’ 退出并保存

source /etc/profile                                     # 手动刷新配置

java -version                                           # JDK 版本验证
```


### sudo 命令下 SDK 版本与 root 下版本不一致问题
##### centos
1、查看系统 openjdk
```bash
#rpm -qa|grep java
~:rpm -qa|grep jdk
jdk-1.7.0_80-fcs.x86_64
```
2、删除系统 openjdk
```bash
~:rpm -e —nodeps jdk-1.7.0_80-fcs.x86_64
```

3、修改系统默认 jdk 路径
```bash
update-alternatives --install "/usr/bin/java" "java" "/usr/local/jdk1.8.0_131/jre/bin/java" 1
```
```
usage: alternatives --install <link> <name> <path> <priority>
[link]      快捷方式路径
[name]      表示命令链接符号名称
[path]      引入可替代方案的主要链接
[priority]  优先级
```
