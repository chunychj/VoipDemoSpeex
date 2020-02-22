package com.gyz.voipdemo_speex.util;

public class Speex
{
    private static final int DEFAULT_COMPRESSION = 8;
    private static final Speex speex = new Speex();
    public native int open(int compression);
    public native int getFrameSize();
    public native int decode(byte encoded[], short lin[], int size);
    public native int encode(short lin[], int offset, byte encoded[], int size);
    public native void close();

    private Speex() {
        
    }
    
    public static Speex getInstance() {
        return speex;
    }
    
    public void init() {
        load();
        open(DEFAULT_COMPRESSION);
    }

    private void load() {
        try {
            System.loadLibrary("speex");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
