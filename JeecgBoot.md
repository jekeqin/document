## 前端

1.下载代码<br/>
`clone .git`

2.进入目录<br/>
`cd /ant-design-vue-jeecg`

3.初始化项目，自动安装对应工具包<br/>
`npm install`

4.更新 webpack 版本为最新版本<br/>
`npm install webpack@latest`

5.启动Debug<br/>
`npm run serve`

6.打包<br/>
`npm run build`


## 后台
启动<br/>
`org.jeecg.JeecgApplication.main`


若自己写相关代码，需要将代码放置于 org.jeecg 包之下<br/>
例：org.jeecg.test.controller

若要修改包名，则需要遵循以下规则

+ com
  + test
    + JeecgApplication
    + controller
      + TestController
    + service
      + TestService
    
