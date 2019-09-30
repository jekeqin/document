# Windows 搭建 Vue 环境

以下环境基于 Win10 系统

## 1. 安装 Node.js

官网下载 https://nodejs.org/en/
>12.0.0 Current <br/>
>https://nodejs.org/dist/v12.0.0/node-v12.0.0-x64.msi

运行安装程序，安装到指定目录
>+---Node.js runtime<br/>
>|---npm package manager<br/>
>|---Online documentation shortcuts<br/>
>+---Add to PATH        // 环境变量

安装完成后，nodejs 目录文件如下
```bash
node_modules                # 文件夹
install_tools.bat           # Windows 批处理文件
node.exe                    # 应用程序
node_etw_provider.man       # MAN 文件
nodevars.bat                # Windows 批处理文件
npm                         # 文件
npm.cmd                     # Windows 命令脚本
npx                         # 文件
npx.cmd                     # Windows 命令脚本
```

查看 npm 版本： `npm -v`

在 nodejs 目录下两个新建文件夹
```bash
node_cache      # 文件夹，用于存放缓存文件
node_global     # 文件夹，用于存放全局模块
```

CMD 命令设置缓存文件夹<br/>
`npm config set cache "D:\Program Files\nodejs\node_cache"`

CMD 命令设置全局模块存放路径<br/>
`npm config set prefix "D:\Program Files\nodejs\node_global"`

## 2. 安装 cnpm

此步骤可选，因 npm 服务器在国外，访问受网络影响较大，因此更换为 taobao 镜像 cnpm；<br/>
cnpm 与 npm 完全一致，只是使用时将 npm 换成 cnpm，[-g]参数表示全局安装<br/>
`npm install -g cnpm --registry=https://registry.npm.taobao.org`


## 3. 设置 Node.js 环境变量

右击桌面“这台电脑”->属性->高级系统设置->环境变量

修改 Path 变量，新增一行<br/>
`D:\Program Files\nodejs\node_global`

新增变量 NODE_PATH ，值为<br/>
`D:\Program Files\nodejs\node_modules`

配置好之后，需要重启 CMD 或Windows PowerShell 窗口

```
#查看配置
npm config ls

```

## 4. 安装 Vue

安装 Vue<br/>
`cnpm install vue -g`

安装 Vue命令行工具，即 Vue-cli2 脚手架, 2和3只能同时安装一个<br/>
`cnpm install vue-cli -g`

安装 Vue命令行工具，即 Vue-cli3 脚手架<br/>
`cnpm install @vue/cli -g`

## 5. 创建项目

进入 Vue 工作区根本目：D:\VueWork

```bash
PS C:\Users\admin>cd D:\VueWork     # 切换工作区目录
```
创建项目命令：
```bash
vue init webpack-simple [项目名]    # 项目名称为英文数字组合
```
创建项目时，需根据命令行提示输入相关信息，括号内为默认值，若不修改则直接回车，需修改则先按 Backspace 删除
```bash
? Project name (myweb)
? Project description (A Vue.js project)
? Author 
? License (MIT)
? Use sass? N
```

例：`vue init webpack-simple myweb`

创建好项目之后，进入项目目录，继续运行命令
```bash
cd myweb            # 进入项目目录
cnpm install         # 安装工程依赖模块
```

vue-cli2 项目运行命令：`cnpm run dev`

vue-cli2 项目打包命令：`cnpm run bulid`


vue-cli3 项目运行命令：`cnpm run serve`

vue-cli3 项目打包命令：`cnpm run bulid`


打包后的项目文件位于 .\dist 目录下


## 6. 路由 vue-router

1. npm 安装 vue-router，进入项目根目录，执行命令<br/>
    `cnpm install vue-router`

2. 修改 main.js 引入 VueRouter
```js
import VueRouter from 'vue-router'
Vue.use(VueRouter)
```

3. 设置路由
main.js 新增以下代码，与 new Vue() 同级
```js
import Login from '../pages/Login.vue';
const router = new VueRouter({
    routes: [
        //动态路径参数以冒号开头，path为菜单路由，name为页面动态加载显示区域， component .vue页面
        { path: '/user/:id', name:'body', component: User } 
    ]
})

const app = new Vue({
    el: '#app',
    render: h => h(App),
    router:router           // 启用 路由
});
```

在 App.vue 中增加 路由跳转页面显示区域
```html
<template>
    <div>
        <router-view/>
        <router-view name='menu'/>  <!-- name与routes.item中的name对应 -->
        <router-view name='body'/>
    </div>
</template>
```

https://router.vuejs.org/zh/installation.html



---
## Element

1. npm 安装 ElementUI，进入项目根目录，执行命令<br/>
    `cnpm i element-ui -S -g`

2. 修改 main.js 引入 ElementUI

```js
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI);
```

注：在引入 ElementUI 时，可能会报错
```js
./node_modules/element-ui/lib/theme-chalk/fonts/element-icons.ttf
Module parse failed: Unexpected character '' (1:0)
```
解决方式，在项目 webpack.config.js -> module.rules 中加入以下配置
```js
{
    test: /\.(eot|svg|ttf|woff|woff2)$/,
    loader: 'file-loader'
}
```


https://element.eleme.cn/2.0/#/zh-CN/component/quickstart
