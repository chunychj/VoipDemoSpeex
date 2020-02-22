package com.gyz.voipdemo_speex.socket;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class JsonObjectDecoder extends ByteToMessageDecoder{
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;
		}
		int beginIndex = in.readerIndex();
		int len = in.readInt();
		if (in.readableBytes() < len) { //长度不够
			in.readerIndex(beginIndex);
			return;
		}
		byte[] data = new byte[len];
		in.readBytes(data);
		String str = new String(data, "UTF-8");
		DemoPackage demoPackage = mapper.readValue(str, DemoPackage.class);
		out.add(demoPackage);
	}

}
