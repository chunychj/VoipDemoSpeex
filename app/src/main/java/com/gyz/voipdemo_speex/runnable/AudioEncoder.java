package com.gyz.voipdemo_speex.runnable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.gyz.voipdemo_speex.model.AudioData;
import com.gyz.voipdemo_speex.util.Speex;

public class AudioEncoder implements Runnable {
    String LOG = "AudioEncoder";

    private static AudioEncoder encoder;
    private boolean isEncoding = false;

    private List<AudioData> dataList = null;

    public static AudioEncoder getInstance() {
        if (encoder == null) {
            encoder = new AudioEncoder();
        }
        return encoder;
    }

    private AudioEncoder() {
        dataList = Collections.synchronizedList(new LinkedList<AudioData>());
    }

    public void addData(short[] data, int size) {
        AudioData rawData = new AudioData();
        rawData.setSize(size);
        short[] tempData = new short[size];
        System.arraycopy(data, 0, tempData, 0, size);
        rawData.setRealData(tempData);
        dataList.add(rawData);
    }

    /*
     * start encoding
     */
    public void startEncoding() {
        System.out.println(LOG + "start encode thread");
        if (isEncoding) {
            Log.e(LOG, "encoder has been started  !!!");
            return;
        }
        new Thread(this).start();
    }

    /*
     * end encoding
     */
    public void stopEncoding() {
        this.isEncoding = false;
    }

    public void run() {
        // start sender before encoder
        AudioSender sender = new AudioSender();
        sender.startSending();

        int encodeSize = 0;
        byte[] encodedData;

        isEncoding = true;
        while (isEncoding) {
            if (dataList.size() == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (isEncoding) {
                AudioData rawData = dataList.remove(0);
                encodedData = new byte[Speex.getInstance().getFrameSize()];
                
                encodeSize = Speex.getInstance().encode(rawData.getRealData(),
                    0, encodedData, rawData.getSize());
                System.out.println();
                if (encodeSize > 0) {
                    sender.addData(encodedData, encodeSize);
                }
            }
        }
        System.out.println(LOG + "end encoding");
        sender.stopSending();
    }

}