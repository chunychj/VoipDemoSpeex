package com.gyz.voipdemo_speex.runnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

import com.gyz.voipdemo_speex.model.AudioConfig;
import com.gyz.voipdemo_speex.model.AudioData;

public class AudioPlayer implements Runnable {
    String LOG = "AudioPlayer ";
    private static AudioPlayer player;

    private List<AudioData> dataList = null;
    private AudioData playData;
    private boolean isPlaying = false;

    private AudioTrack audioTrack;

    //
    private File file;
    private FileOutputStream fos;

    private AudioPlayer() {
        dataList = Collections.synchronizedList(new LinkedList<AudioData>());

        file = new File("/sdcard/audio/decode.amr");
        try {
            if (!file.exists())
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static AudioPlayer getInstance() {
        if (player == null) {
            player = new AudioPlayer();
        }
        return player;
    }

    public void addData(short[] rawData, int size) {
        AudioData decodedData = new AudioData();
        decodedData.setSize(size);
        short[] tempData = new short[size];
        System.arraycopy(rawData, 0, tempData, 0, size);
        decodedData.setRealData(tempData);
        dataList.add(decodedData);
        Log.e(LOG, "Player添加数据 " + dataList.size());
    }

    /*
     * init Player parameters
     */
    private boolean initAudioTrack() {
        int bufferSize = AudioRecord.getMinBufferSize(AudioConfig.SAMPLERATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioConfig.AUDIO_FORMAT);
        if (bufferSize < 0) {
            Log.e(LOG, LOG + "initialize error!");
            return false;
        }
        Log.i(LOG, "Player初始化的 buffersize大小" + bufferSize);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                AudioConfig.SAMPLERATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioConfig.AUDIO_FORMAT, bufferSize, AudioTrack.MODE_STREAM);
        // set volume:设置播放音量
        audioTrack.setStereoVolume(1.0f, 1.0f);
        audioTrack.play();
        return true;
    }

    private void playFromList() throws IOException {
        while (isPlaying) {
            while (dataList.size() > 0) {
                playData = dataList.remove(0);
                Log.e(LOG, "播放后list中剩余数据数量： " + dataList.size());
                audioTrack.write(playData.getRealData(), 0, playData.getSize());
                // fos.write(playData.getRealData(), 0, playData.getSize());
                // fos.flush();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    public void startPlaying() {
        if (isPlaying) {
            Log.e(LOG, "验证播放器是否打开"  + isPlaying);
            return;
        }
        new Thread(this).start();
    }

    public void run() {
        this.isPlaying = true;
        if (!initAudioTrack()) {
            Log.i(LOG, "播放器初始化失败");
            return;
        }
        Log.e(LOG, "开始播放");
        try {
            playFromList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.audioTrack != null) {
            if (this.audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                this.audioTrack.stop();
                this.audioTrack.release();
            }
        }
        Log.d(LOG, LOG + "end playing");
    }

    public void stopPlaying() {
        this.isPlaying = false;
    }
}
