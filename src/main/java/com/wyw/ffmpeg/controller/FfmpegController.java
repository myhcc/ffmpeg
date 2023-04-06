package com.wyw.ffmpeg.controller;

import com.wyw.ffmpeg.service.FfmpegService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FilenameFilter;

@Slf4j
@RestController
public class FfmpegController {

    @Autowired
    private FfmpegService ffmpegService;

    @Value("${wyw.ffmpeg.win.path}")
    private String ffmpegWinPath;
    @Value("${wyw.ffmpeg.linux.path}")
    private String ffmpegLinuxPath;
    @Value("${wyw.file.format}")
    private String fileFormat;

    @GetMapping("testAsync")
    public void testAsync() throws InterruptedException {
        log.info("<<开始异步调用方法>>");
        ffmpegService.testAsync();
        log.info("<<结束异步调用方法>>");
    }


    @GetMapping("/exec")
    public void exec(String sourcePath, String targetPath) throws InterruptedException {
        sourcePath="D:\\temp\\a";
        targetPath="D:\\temp\\a\\as";
        log.info("CPU --> {}", Runtime.getRuntime().availableProcessors());
        // 检查ffmpeg命令是否存在
        String osName = System.getProperties().getProperty("os.name");
        File ffmpegCmd;
        boolean isWindows = false;
        if(osName.toLowerCase().startsWith("win")){
            log.info("<<Windows版本>>");
            isWindows = true;
            ffmpegCmd = new File(ffmpegWinPath + File.separatorChar + "ffmpeg.exe");
        }else{
            log.info("<<非Windows版本>>");
            ffmpegCmd = new File(ffmpegLinuxPath + File.separatorChar + "ffmpeg");
        }
        if(!ffmpegCmd.exists()){
            log.error("找不到ffmpeg命令，找寻路径：{}", ffmpegCmd.getAbsolutePath());
            return;
        }
        // 检查源目录和目标目录是否存在
        if(invalidPath(sourcePath, targetPath)){
            return;
        }
        // 获取符合条件的文件
        File[] files = new File(sourcePath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {

                if(name.endsWith(fileFormat)){
                    log.info("<<符合的文件>> {}", name);
                    return true;
                }else{
                    log.warn("<<不符合的文件或目录>> {}", name);
                    return false;
                }

            }
        });
        log.info("<<开始调用异步转码方法>>");
        for(File file : files){
            ffmpegService.execVideoTranscode(ffmpegCmd, isWindows, file.getAbsolutePath(), getTargetFile(file, targetPath));
        }
        log.info("<<结束调用异步转码方法>>");
    }

    private String getTargetFile(File file, String targetPath){
        String absolutePath = file.getAbsolutePath();
        return targetPath + absolutePath.substring(absolutePath.lastIndexOf(File.separatorChar));
    }

    /**
     * 检查源目录和目标目录是否存在
     * @param sourcePath
     * @param targetPath
     */
    private boolean invalidPath(String sourcePath, String targetPath){
        File srcPath = new File(sourcePath);
        if(!srcPath.exists()){
            log.error("<<源目录不存在>>");
            return true;
        }
        File tgtPath = new File(targetPath);
        if(!tgtPath.exists()){
            tgtPath.mkdirs();
        }
        return false;
    }


}
