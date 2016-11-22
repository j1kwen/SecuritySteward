package com.challenger.securitysteward.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.challenger.securitysteward.utils.Utils;

public class DeviceItemModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1835376474122100558L;

	private String name = null, did = null, remark = null;
	private long lastmodify, level;
	private List<DeviceMessage> messages;
	
	public DeviceItemModel() {
		level = 0;
	}
	public DeviceItemModel(String did, String name, String remark, long level, long last_modify) {

		this.did = did;
		this.name = name;
		this.lastmodify = last_modify;
		this.remark = remark;
		this.level = level;
		messages = Utils.getMessageManagement().getDeviceMessages(did);
	}
	public boolean isHasUnread() {
		for(DeviceMessage i : messages) {
			if(i.isUnread()) {
				return true;
			}
		}
		return false;
	}
	
	public void setLevel(long level) {
		this.level = level;
	}
	
	public long getLevel() {
		return this.level;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public long getLastmodify() {
		long x = lastmodify;
		if(messages != null && messages.size() > 0) {
			x = messages.get(messages.size() - 1).getDate();
		}
		return x;
	}
	public String getLastmodifyString() {
		long x = lastmodify;
		if(messages != null && messages.size() > 0) {
			x = messages.get(messages.size() - 1).getDate();
		}
		Timestamp time = new Timestamp(x);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		if(isSameDay(time, now)) {
			return time.toString().substring(11, 16);
		} else if(isYestoday(time, now)) {
			return "yestoday";
		} else if(isSameYear(time, now)) {
			return time.toString().substring(5, 10);
		} else {	
			return time.toString().substring(0, 10);
		}		
	}
	
	private boolean isSameDay(Timestamp a, Timestamp b) {
		long ad = a.getTime() / 1000 / 60 / 60 / 24;
		long bd = b.getTime() / 1000 / 60 / 60 / 24;
		return ad == bd;
	}
	private boolean isYestoday(Timestamp a, Timestamp b) {
		long ad = a.getTime() / 1000 / 60 / 60 / 24;
		long bd = b.getTime() / 1000 / 60 / 60 / 24;
		return (ad - bd == 1 || ad - bd == -1);
	}
	@SuppressWarnings("deprecation")
	private boolean isSameYear(Timestamp a, Timestamp b) {
		return a.getYear() == b.getYear();
	}
}
