package com.challenger.securitysteward.model;

import java.io.Serializable;


public class DeviceMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2395249537592809939L;
	private String title = null;
	private long date;
	private String body = null;
	private String extra = null;
	private String href = null;
	private boolean unread = true;
	private boolean systemMsg = false;

	/**
	 * Build a system message
	 * @param body
	 */
	public DeviceMessage(String body, long date) {
		systemMsg = true;
		this.title = body;
		this.date = date;
		this.body = body;
		unread = false;
	}
	
	public DeviceMessage(String title, long date, String body, String extra, String href, long unread) {
		this.title = title;
		this.date = date;
		this.body = body;
		this.extra = extra;
		this.href = href;
		this.unread = (unread != 0L);
		systemMsg = false;
	}
	
	public boolean isSystemMsg() {
		return systemMsg;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	public String getHref() {
		return href;
	}
	
	
	public void setHref(String href) {
		this.href = href;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

}
