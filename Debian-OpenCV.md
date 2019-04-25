# Debian 服务器安装 OpenCV

## 1. 安装依赖组件
```
:~$ apt-get install build-essential
:~$ apt-get install cmake git libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev
:~$ apt-get install python-dev python-numpy libtbb2 libtbb-dev libjpeg-dev libpng-dev libtiff-dev libjasper-dev libdc1394-22-dev
:~$ apt-get install ant				// opencv.Jar生成必须组件
```

## 2. 下载 OpenCV-4.1.0 Sources Zip 压缩包

下载：`wget -c https://github.com/opencv/opencv/archive/4.1.0.zip`

解压到 /usr/local/opencv-4.1.0

创建编译所需目录：
```
:~$ cd ~/opencv-4.1.0/			//进入解压后的opencv根目录
:~$ mkdir build					//创建编译目录
:~$ cd build/					//进入build目录
```

编译安装 OpenCV：
```
:~$ cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local ..	    //编译前进行编译设置
:~$ make					//编译
:~$ sudo make install				//编译完成后进行安装
```

编译并生成.jar、.so
```
:~$ cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local -DBUILD_TESTS=OFF ..		//编译前进行编译设置
:~$ make -j8					//编译java代码
:~$ sudo make install				//编译完成后安装jar相关文件
```

## 3. Java 所需 JAR 包文件的位置

编译成功后，以下两处会出现相关文件
```
~/OpenCV-3.4.1/build/bin/opencv-341.jar
/usr/local/share/OpenCV/java/opencv-341.jar		//项目需要的jar包
/usr/local/share/OpenCV/java/libopencv_java341.so	//项目需要的类库，相当于Windows下的.dll
```

## 4. 如何使用

在使用 OpenCV 任何类前，在启动程序时，需先加载 .so 文件，相当于 Windows 下先加载 .dll

+ 方式一
    ```
    String opencvpath = System.getProperty("user.dir") + "/lib/";
    System.load(opencvpath + "libopencv_java341.so");
    ```
+ 方式二
    1. 需要将 .so 文件改名为 Core.NATIVE_LIBRARY_NAME 一致
    2. `System.loadLibrary(Core.NATIVE_LIBRARY_NAME);`

## 参考网址

[https://opencv.org/releases.html](https://opencv.org/releases.html)

[https://docs.opencv.org/trunk/d7/d9f/tutorial_linux_install.html](https://docs.opencv.org/trunk/d7/d9f/tutorial_linux_install.html)

[http://blog.csdn.net/u010946556/article/details/49703527](http://blog.csdn.net/u010946556/article/details/49703527)

[https://www.linuxidc.com/Linux/2016-05/131605.htm](https://www.linuxidc.com/Linux/2016-05/131605.htm)