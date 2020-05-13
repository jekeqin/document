# Git 相关命令

```bash
# 检出分支，通过远程分支创建本地仓库
git clone xxx.git

# 获取远程更新，主要用于更新分支列表
git fetch

# 查看远程分支
git branch -a

# 创建分支
git branch [分支名]

# 切换至指定分支
git checkout [分支名]

# 查看当前所属分支状态
git status

# 保存至本地仓库，[.]代表所有存在新增、删除、修改的文件
git add .
# 对保存至本地出库的文件添加注释
git commit -m '注释内容'

# 拉取远程代码，从服务器拉取代码到本地
git pull

# 推送本地代码，推送本地代码到服务器
git push

# 合并分支，将指定分支合并至当前分支
git merge [分支名]
```
