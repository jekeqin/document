# Debian 服务器上安装 Maven
安装 Maven 前，需先安装 jdk
**1. 下载 Maven**
```bash
cd /usr/local                               # 进入目录
wget 'maven下载地址'                         # http://maven.apache.org/download.cgi，也可以Win下载后上传到服务器
tar -xzvf apache-maven-x.x.x-bin.tar.gz     # 解压
```

**2. 配置 Maven 环境变量**
配置文件尾部追加 Maven 配置
```bash
vim /etc/profile

export MAVEN_HOME=/usr/local/apache-maven-x.x.x         # Maven 文件夹路径
export PATH=$PATH:$MAVEN_HOME/bin                       # 配置附加
# 修改完成后，按‘Esc’键退出编辑模式，再输入‘:wq’ 退出并保存

source /etc/profile                                     # 手动刷新配置

mvn -version                                            # Maven 版本验证
```

**3. 本地仓库配置**
修改 MAVEN_HOME/conf 目录下 settings.xml 文件
```xml
vim settings.xml

<settings ...>
    
    <localRepository>/usr/local/mavenRepository</localRepository>   # 取消注释，将本地仓库路径修改为自己的目录

    <mirrors>                                                       # 该配置为设置镜像地址，如果下载较慢，可更换为国内地址
        <mirror>
                <id>nexus-aliyun</id>                               # 阿里云镜像
                <mirrorOf>central</mirrorOf>
                <name>Nexus aliyun</name>
                <url>http://maven.aliyun.com/nexus/content/groups/public</url>
        </mirror>
    </mirrors>

    ...

</settings>

# 修改完成后，按‘Esc’键退出编辑模式，再输入‘:wq’ 退出并保存
```



### 常用命令
```bash
mvn clean           # 清理项目打包文件，即项目下的target目录
mvn compile         # 编译项目下的src/main/Java目录源代码
mvn package         # 项目打包，在项目target目录下生成编译后的jar或war等文件
mvn install         # 项目打包并发布到本地仓库
mvn deploy          # 项目打包并发布到远程仓库
mvn test            # 单元测试命令，执行src/test/java/下的junit的单元测试用例
mvn site            # 生成项目相关信息的网站
mvn eclipse:eclipse # 将项目转化eclipse项目
mvn dependency:tree # 打印出项目的整个依赖关系树
mvn archetype:generate  # 创建一个maven普通java项目
mvn tomcat:run      # 在tomcat容器中运行web应用，需要在pom文件中配置tomcat插件
mvn jetty:run       # 在jetty容器中运行web应用，需要在pom文件中配置jetty插件

mvn [package] [-Dmaven.test.skip=true]  # 即打包的时候跳过单元测试，install、deplay命令都可以使用

mvn -h              # 获取更多命令的帮助
```
