# 使用指南

### 如何调用？

1. 安装FFmpeg工具

默认情况下，Windows环境下，FFmpeg命名的所在目录为：`D:\soft\ffmpeg-4.4\bin`,非Windows环境下，FFmpeg命令的所在目录为：`/usr/local/ffmpeg`

> 目前只判断系统是否为Windows

2. 运行程序

> 示例：`java -jar ffmpeg-0.0.1-SNAPSHOT.jar`

3. 发送请求

请求类型：GET

请求参数

sourcePath：视频文件所在的目录

targetPath：保存转码视频的目录

> 视频转码时，只处理sourcePath目录下的`mp4`格式的视频文件，可以通过配置参数`wyw.file.format`指定需要被转码的视频文件，但不会递归的处理sourcePath下的子目录里的视频文件

**演示**

> note：IP和Port使用自己的

Windows环境

将`D:\soft\ffmpeg-4.4\ss`下的mp4格式的视频文件经转码保存到`D:\soft\ffmpeg-4.4\pp`下

> 符号"\"需要进行URL编码

```bash
http://localhost:8080/exec?sourcePath=D:%5Csoft%5Cffmpeg-4.4%5Css&targetPath=D:%5Csoft%5Cffmpeg-4.4%5Cpp
```

Linux环境

将`/root/tmp/ss`下的mp4格式的视频文件经转码保存到`/root/tmp/pp`下

```bash
http://192.168.10.55:8080/exec?sourcePath=/root/tmp/ss&targetPath=/root/tmp/pp
```

### 如何配置并发数？

项目中使用异步线程池，可通过配置线程池核心数`wyw.executor.core-pool-size`实现，默认为10

### 性能消耗情况如何？

**Windows环境**

> 版本：Windows 10 \
> CPU物理个数：1\
> CPU内核：4 \
> CPU逻辑处理器：4 

现象：

- 视频转码时，无论视频有多少个，CPU的使用率占满（100%）

- 多线程调用时，待转码的视频个数与视频转码总耗时近乎成正比

**CentOS环境**

> 版本：ContOS 7.9 \
> CPU物理个数：4 \
> CPU内核：2 \
> CPU逻辑处理器：8  

现象：

- 视频转码时，无论视频有多少个，每个CPU的使用率基本占满（近100%），总CPU使用率基本占满（近800%）

- 多线程调用时，待转码的视频个数与视频转码总耗时近乎成正比

**结论**

可以使用多线程调用ffmpeg命令执行转码，但不推荐。因为使用多线程并不能提高视频转码效率，相反，它会导致单个的视频转码效率成比例的降低，虽然总体耗时差不多。 综述，推荐使用有且只有一个线程用于调用ffmpeg命令执行视频转码。

# 提高转码效率
- 添加参数`-preset ultrafast`
> 开启ffmpeg本身的多线程功能，测试时发现速度反而变慢，可能文件太小，体现不出多线程的作用
- 使用GPU进行转码。ffmpeg需要集成相关组件，并添加相关硬件编码参数
