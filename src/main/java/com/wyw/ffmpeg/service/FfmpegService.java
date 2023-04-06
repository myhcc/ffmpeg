package com.wyw.ffmpeg.service;

import java.io.File;

public interface FfmpegService {

    void testAsync() throws InterruptedException;

    void execVideoTranscode(File ffmpegCmd, boolean isWindows, String sourceFile, String targetFile);

}
