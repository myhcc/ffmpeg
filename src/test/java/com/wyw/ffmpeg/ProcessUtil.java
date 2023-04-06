package com.wyw.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtil {
    public static void main(String[] args) {
        String input = "D:\\temp\\[电影天堂www.dytt89.com]巨鳄2-2022_HD国语中字.mp4";
        String output = "D:\\temp\\a\\as\\test.m3u8";
        List<String> command = new ArrayList<>();
        command.add("D:\\ffmpeg-5.0.1-essentials_build\\ffmpeg-5.0.1-essentials_build\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add(input);
        command.add("-c:v");
        command.add("copy");
        command.add("-c:a");
        command.add("copy");
        command.add("-f");
        command.add("ssegment");
        command.add("-segment_format");
        command.add("mpegts");
        command.add("-segment_list");
        command.add(output);
        command.add("-segment_time");
        command.add("10");
        command.add("D:\\temp\\a\\as\\test%05d.ts");
        String result = ProcessUtil.execute(command);
        System.out.println(result);
        System.out.println("转换完成");

    }

    public static String execute(List<String> command) {
        StringBuffer inputStringBuffer = new StringBuffer();
        StringBuffer errorStringBuffer = new StringBuffer();
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            Process process = builder.start();
            System.out.println("============inputStream============");
            // 处理InputStream
            Thread t1 = new Thread(() -> {
                InputStream input = null;
                InputStreamReader reader = null;
                BufferedReader buffer = null;

                try {
                    input = process.getInputStream();
                    reader = new InputStreamReader(input);
                    buffer = new BufferedReader(reader);
                    String inputLine = "";
                    while ((inputLine = buffer.readLine()) != null) {
                        System.out.println(inputLine);
                        inputStringBuffer.append(inputLine);
                    }
                    //退出循环后表示结束流
                    System.out.println("===》》退出循环后表示结束流");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (buffer != null) {
                            buffer.close();
                        }
                        if (reader != null) {
                            reader.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t1.setName("deviceName");
            t1.start();

            System.out.println("============errorStream============");
            // 处理ErrorStream
            new Thread(() -> {
                InputStream input = null;
                InputStreamReader reader = null;
                BufferedReader buffer = null;
                try {
                    input = process.getErrorStream();
                    reader = new InputStreamReader(input);
                    buffer = new BufferedReader(reader);
                    String errorLine = "";
                    while ((errorLine = buffer.readLine()) != null) {
                        System.out.println(errorLine);
                        errorStringBuffer.append(errorLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (buffer != null) {
                            buffer.close();
                        }
                        if (reader != null) {
                            reader.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // 这里进程阻塞，将等待外部转换成功后，才往下执行
            //process.waitFor();
            t1.wait();

            /**
             * 只会存在一个输入流返回
             */
            if (inputStringBuffer != null) {
                return inputStringBuffer.toString();
            }
            if (errorStringBuffer != null) {
                return errorStringBuffer.toString();
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
