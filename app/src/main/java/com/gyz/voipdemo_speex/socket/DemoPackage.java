package com.gyz.voipdemo_speex.socket;

import java.util.Arrays;

public class DemoPackage {
	
	private int type;
	private long userId;
	private long groupId;
	private byte[] data;
	
	public DemoPackage() {
		super();
	}
	public DemoPackage(byte[] data) {
		super();
		this.data = data;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "DemoPackage [type=" + type + ", userId=" + userId + ", groupId=" + groupId + ", data="
				+ Arrays.toString(data) + "]";
	}
}
