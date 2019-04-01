# Debian 服务器上安装 Maven


**1. 下载 JDK**
前往官网下载 JDK 安装包，jdk-x.x.x_linux-x64_bin.tar.gz，然后上传至服务器

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
