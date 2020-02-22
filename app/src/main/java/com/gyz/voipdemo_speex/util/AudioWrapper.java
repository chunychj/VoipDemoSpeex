package com.gyz.voipdemo_speex.util;

import android.R.integer;

import com.gyz.voipdemo_speex.runnable.AudioReceiver;
import com.gyz.voipdemo_speex.runnable.AudioRecorder;


public class AudioWrapper {

	private AudioRecorder audioRecorder;
	private AudioReceiver audioReceiver;

	private static AudioWrapper instanceAudioWrapper;

	private AudioWrapper() {
	}

	public static AudioWrapper getInstance() {
		if (null == instanceAudioWrapper) {
			instanceAudioWrapper = new AudioWrapper();
		}
		return instanceAudioWrapper;
	}

	public void startRecord() {
		if (null == audioRecorder) {
			audioRecorder = new AudioRecorder();
		}
		audioRecorder.startRecording();
	}

	public void stopRecord() {
		if (audioRecorder != null)
			audioRecorder.stopRecording();
	}

	public void startListen() {
		if (null == audioReceiver) {
			audioReceiver = new AudioReceiver();
		}
		audioReceiver.startRecieving();
	}

	public void stopListen() {
		if (audioRecorder != null)
			audioRecorder.stopRecording();
	}
}
