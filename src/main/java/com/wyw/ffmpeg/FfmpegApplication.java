package com.wyw.ffmpeg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FfmpegApplication {

    public static void main(String[] args) {
        SpringApplication.run(FfmpegApplication.class, args);
    }

}
