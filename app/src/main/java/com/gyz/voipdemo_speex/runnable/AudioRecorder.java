package com.gyz.voipdemo_speex.runnable;

import com.gyz.voipdemo_speex.model.AudioConfig;
import com.gyz.voipdemo_speex.util.Speex;

import android.media.AudioRecord;
import android.util.Log;

public class AudioRecorder implements Runnable {

    String LOG = "Recorder ";

    private boolean isRecording = false;
    private AudioRecord audioRecord;

    private int audioBufSize = 0;

    /*
     * start recording
     */
    public void startRecording() {
        audioBufSize = AudioRecord.getMinBufferSize(AudioConfig.SAMPLERATE,
            AudioConfig.RECORDER_CHANNEL_CONFIG, AudioConfig.AUDIO_FORMAT);
        if (null == audioRecord) {
            audioRecord = new AudioRecord(AudioConfig.AUDIO_RESOURCE,
                    AudioConfig.SAMPLERATE,
                    AudioConfig.RECORDER_CHANNEL_CONFIG,
                    AudioConfig.AUDIO_FORMAT, audioBufSize);
        }
        new Thread(this).start();
    }

    /*
     * stop
     */
    public void stopRecording() {
        this.isRecording = false;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void run() {
        // start encoder before recording
        AudioEncoder encoder = AudioEncoder.getInstance();
        encoder.startEncoding();
        System.out.println(LOG + "audioRecord startRecording()");
        audioRecord.startRecording();
        System.out.println(LOG + "start recording");

        this.isRecording = true;
        int size = Speex.getInstance().getFrameSize();
        short[] samples = new short[size];
        while (isRecording) {
            int bufferRead = audioRecord.read(samples, 0, size);
            if (bufferRead > 0) {
                // add data to encoder
                encoder.addData(samples, bufferRead);
//              System.out.println("samples:"+Arrays.toString(samples));
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(LOG + "end recording");
        audioRecord.stop();
        encoder.stopEncoding();
    }
}
