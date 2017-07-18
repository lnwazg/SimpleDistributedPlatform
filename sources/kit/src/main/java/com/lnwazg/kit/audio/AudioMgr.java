package com.lnwazg.kit.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

/**
 * 音频播放器
 * @author nan.li
 * @version 2016年5月10日
 */
public class AudioMgr
{
    
    /**
     * 播放音频文件<br>
     * 可以播放MP3
     * @author nan.li
     * @param audioFile
     */
    public static void playAudio(File audioFile)
    {
        try
        {
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(audioFile));
            Player player = new Player(buffer);
            player.play();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
