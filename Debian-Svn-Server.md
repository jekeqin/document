# Debian 服务器上安装 SVN 服务

**1. 安装 svn 服务**
```bash
apt-get update                                # 更新软件列表
apt-get install subversion subversion-tools   # 安装 svn 服务
svnserve –version                             # 安装完成后，查看 svn 服务版本
```

**2. 创建 svn 仓库**
```bash
mkdir /usr/svn          # SVN 仓库存放位置
cd /usr/svn             # SVN 进入仓库存放目录
svnadmin create mydir   # 创建 svn 资源库
```

**3. 编辑资源库配置文件**
```bash
cd /usr/svn/mydir/conf    # 进入资源库配置文件存放目录
vim svnserve.conf         # 修改全局配置文件

[general]
anon-access = none    # 拒绝匿名用户访问，none:无权限，read:仅可读，write:可读写
auth-access = write   # 有权限用户可读写
password-db = passwd  # 指定密码配置文件的路径
authz-db = authz      # 指定目录权限配置文件的路径
```

```bash
vim /usr/svn/mydir/passwd.conf    # 账号配置文件

myusername = mypassword     # 添加 svn 账号，格式为 账号=密码
username1 = password1
username2 = password2
...                         # 该行表示可配置 N 个账号

```

```bash
vim /usr/svn/mydir/authz.conf     # 分组权限配置文件

[groups]
g_manager = myusername,username1,username2  # 自定义分组，分组名 = 账号1,账号2,账号3...
my_group = username1,username2
java_group = username1
...                                         # 该行表示可配置 N 个分组

[/]                     # 资源库根目录
@g_manager = rw         # 指定该分组可读写
...                     # 该行表示可添加多个分组

[/java]                 # 资源库 java 代码目录
@java_group = rw        # 该分组拥有可读写权限
myusername = rw         # 表示该单个用户授权可读写
* = r                   # 表示除了以上分组外，其余人只能读，g_manager也拥有权限，因为它的目录授权级别高于 java 目录


```

### 启动、关闭 SVN 访问
```bash
killall svnserve                  # 关闭 SVN 服务
svnserve -d -r /usr/svn/mydir     # 启动 SVN 服务
```
