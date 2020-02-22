package com.gyz.voipdemo_speex.activity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.gyz.voipdemo_speex.R;
import com.gyz.voipdemo_speex.model.NetConfig;
import com.gyz.voipdemo_speex.socket.PojoClient;
import com.gyz.voipdemo_speex.util.AudioWrapper;
import com.gyz.voipdemo_speex.util.Speex;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
    private AudioWrapper audioWrapper = AudioWrapper.getInstance();
    private Button btnStart, btnStop;
    private Button btnExit,btSpeak;
    private EditText receive_ip,receive_port;
    private EditText connect_ip,connect_port;
    private int recordFlag = 1;
    private long recordTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Speex.getInstance().init();
    }
    
    private void initView() {
        btnStart = (Button) findViewById(R.id.start);
        btnStop = (Button) findViewById(R.id.stop);
        receive_ip = (EditText) findViewById(R.id.receive_ip);
        receive_port = (EditText) findViewById(R.id.receive_port);
        connect_ip = (EditText) findViewById(R.id.connect_ip);
        connect_port = (EditText) findViewById(R.id.connect_port);
        receive_ip.setText(getLocalHostIp());
        btnStop.setEnabled(false);
//        btSpeak.setEnabled(false);
        btnExit = (Button) findViewById(R.id.btnExit);
        btSpeak = (Button) findViewById(R.id.speak);
        init();
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            	init();
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btSpeak.setEnabled(true);
//                audioWrapper.startRecord();
                audioWrapper.startListen();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btSpeak.setEnabled(false);
//                audioWrapper.stopRecord();
                audioWrapper.stopListen();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                audioWrapper.stopListen();
                audioWrapper.stopRecord();
                System.exit(0);
//                PojoClient.start("192.168.1.102", 8080);
            }
        });
        btSpeak.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN && recordFlag == 1){
					recordFlag = 2;
					btSpeak.setText("正在说话");
					recordTime = System.currentTimeMillis();
					Toast.makeText(getApplicationContext(), "开始对讲",Toast.LENGTH_SHORT).show();
					audioWrapper.startRecord();
				}else if(event.getAction() == MotionEvent.ACTION_UP && recordFlag == 2){
					recordFlag = 1;
//					if(System.currentTimeMillis() - recordTime > 1000){
//						audioWrapper.stopRecord();
//					}else{
						audioWrapper.stopRecord();
						Toast.makeText(getApplicationContext(), "结束对讲",Toast.LENGTH_SHORT).show();
						btSpeak.setText("长按说话");
//					}
				} else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					recordFlag = 1;
					Toast.makeText(getApplicationContext(), "结束对讲",Toast.LENGTH_SHORT).show();
					audioWrapper.stopRecord();
					btSpeak.setText("长按说话");
				}
				return true;
			}
		});
    }
    
    private void init(){
    	NetConfig.SERVER_HOST = connect_ip.getText().toString();
    	NetConfig.SERVER_PORT = Integer.valueOf(connect_port.getText().toString());
    	NetConfig.LOCAL_PORT = Integer.valueOf(receive_port.getText().toString());
    }
    
    /**
     * 获取本机ip
     * @return
     */
    private String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                                    .getHostAddress())) {
                        return ipaddress = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
//             Log.e(TAG, "获取本地ip地址失败");
        }
        return ipaddress;
    }
}
