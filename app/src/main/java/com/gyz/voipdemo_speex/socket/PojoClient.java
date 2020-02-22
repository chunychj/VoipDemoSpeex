package com.gyz.voipdemo_speex.socket;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class PojoClient {

	public static void main(String[] args) {
		PojoClient.start("192.168.1.89", 8080);
	}
	
	private static Bootstrap bootstrap;
	private static String host;
	private static int port;
	public static Channel channel;

	public static void start(String host, int port) {
		PojoClient.host = host;
		PojoClient.port = port;
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				socketChannel.pipeline().addLast(new IdleStateHandler(60, 0, 15, TimeUnit.SECONDS));
				socketChannel.pipeline().addLast(new JsonObjectDecoder());
				socketChannel.pipeline().addLast(new JsonObjectEncoder());
				socketChannel.pipeline().addLast(new PojoClientHandler());
			}
		});
		doConnect();
	}
	
	public static void doConnect() {
			System.out.println("连接服务器[" + host + ":" + port + "]");
			ChannelFuture future = bootstrap.connect(host, port);
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						channel = future.channel();
					} else {
						future.channel().eventLoop().schedule(new Runnable() {
							@Override
							public void run() {
								doConnect();
							}
						}, 10, TimeUnit.SECONDS);
					}
				}
			});
	}
}
