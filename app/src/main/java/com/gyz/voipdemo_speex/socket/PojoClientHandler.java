package com.gyz.voipdemo_speex.socket;

import com.gyz.voipdemo_speex.runnable.AudioDecoder;

import android.util.Log;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class PojoClientHandler extends ChannelInboundHandlerAdapter{
	private AudioDecoder decoder = AudioDecoder.getInstance();
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
        decoder.startDecoding();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
        	if (msg instanceof DemoPackage) {
    			DemoPackage demoPackage = (DemoPackage) msg;
//    			System.out.println("心跳包 = " + demoPackage);
    			if(demoPackage.getType() == 2){
                    Log.i("HUA", "收到语音数据包:"+demoPackage.toString());
                    decoder.addData(demoPackage.getData(), demoPackage.getData().length);
                }
        	}
        } finally {
        	ReferenceCountUtil.release(msg);
        }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		System.out.println("userEventTriggered = " + evt);
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			IdleState state = event.state();
			if (state == IdleState.ALL_IDLE) { //读写空闲时发送心跳
				DemoPackage demoPackage = new DemoPackage();
				demoPackage.setType(0);
				ctx.writeAndFlush(demoPackage);
			} 
		}
		super.userEventTriggered(ctx, evt);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelInactive = " + ctx);
		PojoClient.doConnect();
	}
	
	
}
