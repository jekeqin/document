# WIN10自定义右键菜单

### 自定义文件右键菜单

```
HKEY_CLASSES_ROOT\*\Background\shell
```
新建 项
```
HKEY_CLASSES_ROOT
    *
        Background
            shell
                Terminal            # 项
                    默认            # 字符串值，默认：空
                    icon            # 字符串值，填入：icon图标路径
                    command         # 项
                        默认        # 字符串值，填入：cmd /c set CURRENT_PATH="%V" & start wt
                        
```


### 自定义文件夹右键菜单

> HKEY_CLASSES_ROOT\Directory\Background\shell
新建 项
```
HKEY_CLASSES_ROOT
    Directory
        Background
            shell
                Terminal            # 项
                    默认            # 字符串值，默认：空
                    icon            # 字符串值，填入：icon图标路径
                    command         # 项
                        默认        # 字符串值，填入：cmd /c set CURRENT_PATH="%V" & start wt
                        
```

### 设置Terminal接收路径

commandline: cmd.exe /s /k pushd %CURRENT_PATH%
```
{
    "guid": "{0caa0dad-35be-5f56-a8ff-afceeeaa6101}",
    "name": "命令提示符",
    "commandline": "cmd.exe /s /k pushd %CURRENT_PATH%",
    "hidden": false,
    "acrylicOpacity": 0.6,
    "useAcrylic": true
}
```
