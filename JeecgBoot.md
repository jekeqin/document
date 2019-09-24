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
    + JeecgApplication.java
    + controller
      + TestController
    + service
      + TestService
    
StrpingBoot 启动时，会自动扫描主类所在包及所有子包的所有 Class，所以Main启动方法必须放在最上层

## 项目地址修改

### 前端
#### 1.src\utils\request.js[line:9]
```
#const service = axios.create({
#  baseURL: '/jeecg-boot', // api base_url
#  timeout: 6000 // 请求超时时间
#})
const service = axios.create({
  baseURL: '',
  timeout: 6000
})
```

#### 2.public\index.html[line:245]
```
#window._CONFIG['domianURL'] = 'http://127.0.0.1:8080/jeecg-boot';
window._CONFIG['domianURL'] = 'http://127.0.0.1:8080';
```

#### 3.vue.config.js
```
#'/jeecg-boot': {
#  target: 'http://localhost:8080', //请求本地 需要jeecg-boot后台项目
#  ws: false,
#  changeOrigin: true
#},
'/': {
  target: 'http://localhost:8080',
  ws: false,
  changeOrigin: true
},
```

