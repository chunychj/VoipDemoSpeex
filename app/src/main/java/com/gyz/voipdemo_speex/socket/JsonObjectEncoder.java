package com.gyz.voipdemo_speex.socket;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JsonObjectEncoder extends MessageToByteEncoder<DemoPackage> {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected void encode(ChannelHandlerContext ctx, DemoPackage msg, ByteBuf out) throws Exception {
		String str = mapper.writeValueAsString(msg);
		byte[] buffer = str.getBytes("UTF-8");
		out.writeInt(buffer.length);
		out.writeBytes(buffer);
	}
	
}
