package com.wyw.ffmpeg.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

@Slf4j
@Service
public class FfmpegServiceImpl implements FfmpegService {

    @Async("ffmpegExecutor")
    @Override
    public void testAsync() {
        log.info("<<异步线程-业务开始>>");
        log.info(Thread.currentThread().getName());
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("<<异步线程-业务结束>>");
    }

    @Async("ffmpegExecutor")
    @Override
    public void execVideoTranscode(File ffmpegCmd, boolean isWindows, String sourceFile, String targetFile) {
        Runtime runtime = null;
        try {
            log.info("<<开始视频转码>> 文件名：{}", sourceFile);
            runtime = Runtime.getRuntime();
            long startTime = System.currentTimeMillis();
            String cmd = ffmpegCmd.getAbsolutePath() + " -y -i " + sourceFile + " -vcodec libx264 -vf scale=\"iw/1:ih/1\" " + targetFile;
            log.info("<<命令>> {}", cmd);
            Process process = null;
            if(isWindows){
                process = runtime.exec(cmd);
            }else{
                process = runtime.exec(new String[]{"sh", "-c", cmd});
            }
            // 通过读取进程的流信息，可以看到视频转码的相关执行信息，并且使得下面的视频转码时间贴近实际的情况
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null;
            while((line = br.readLine()) != null){
                log.debug("<<视频执行信息>> {}", line);
            }
            br.close();
            log.info("<<开始关闭进程相关流>>");
            process.getOutputStream().close();
            process.getInputStream().close();
            process.getErrorStream().close();
            long endTime = System.currentTimeMillis();
            log.info("<<视频转码完成>> 耗时 {}ms", (endTime - startTime));
        } catch (IOException e) {
            log.error("<<视频转码失败，原因：发生IO异常>>");
        } finally {
            if(Objects.nonNull(runtime)){
                runtime.freeMemory();
            }
        }
    }

}
