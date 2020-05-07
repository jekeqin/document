# Linux 系统已删除文件、文件夹恢复

### 恢复单个文件

```bash
# 以下[]、n 项为变量，依实际情况会有所变化

# 1、查看系统磁盘分区
Linux:~# df

# 2、进入debugfs修复模式，该工具为系统自带
Linux:~# debugfs

# 3、进入文件所在分区，阿里云服务器硬盘一般为/dev/vda1
debugfs: open /dev/sda1

# 4、查看制定目录被删除文件信息，被删文件前的inode号带有<>括号
debugfs: ls -d /目标目录
[inode] (n) filename <[inode]> (n) filename

# 5、查看被删文件inode日志
debugfs: logdump -i <[inode]>
Inode [inode] is at group [groupid], block **[1441809]**, offset **[128]**
Journal starts at block [3434], transaction [90549]
FS block [1441809] logged at sequence [90622], journal block [3950]

# 6、退出debugfs
debugfs: quit

# 7、执行命令找回文件，文件会保存至当前目录
Linux:~# dd if=/dev/sda1 of=/目标目录/被删文件名称 bs=**[128]** count=1 skip=**[1441809]**

```
