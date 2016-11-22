package com.challenger.securitysteward.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MessageManagement {
	
	private static MessageManagement single;
	private Context context;
	private Map<String, List<DeviceMessage>> mListMessage;
	private SQLiteDatabase db;

	public static synchronized MessageManagement getInstance() {
		if(single == null) {
			single = new MessageManagement();
		}
		return single;
	}
	
	public void initialize(Context context) {
		this.context = context;
		mListMessage = new HashMap<String, List<DeviceMessage>>();
		
		db = this.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
		String sql = "select * from messages";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			String did = c.getString(c.getColumnIndex("did"));
			String title = c.getString(c.getColumnIndex("title"));
			long date = c.getLong(c.getColumnIndex("date"));
			String body = c.getString(c.getColumnIndex("body"));
			String extra = c.getString(c.getColumnIndex("extra"));
			String href = c.getString(c.getColumnIndex("href"));
			long unread = c.getLong(c.getColumnIndex("unread"));
			long sys = c.getLong(c.getColumnIndex("sys"));
			DeviceMessage msg;
			if(sys == 1L) {
				msg = new DeviceMessage(body, date);
			} else {
				msg = new DeviceMessage(title, date, body, extra, href, unread);
			}
			if(!mListMessage.containsKey(did)) {
				mListMessage.put(did, new ArrayList<DeviceMessage>());
			}
			List<DeviceMessage> messages = mListMessage.get(did);
		    messages.add(msg);
		    
		    c.moveToNext();
		}
		db.close();
	}
	
	public List<DeviceMessage> getDeviceMessages(String did) {
		if(!mListMessage.containsKey(did)) {
			mListMessage.put(did, new ArrayList<DeviceMessage>());
		}
		return mListMessage.get(did);
	}
	
	public void addDeviceMessage(String did, DeviceMessage message) {
		if(!mListMessage.containsKey(did)) {
			mListMessage.put(did, new ArrayList<DeviceMessage>());
		}
		List<DeviceMessage> ls = mListMessage.get(did);
	    ls.add(message);
	    db = this.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
	    ContentValues values = new ContentValues();
	    values.put("did", did);
	    values.put("title", message.getTitle());
	    values.put("date", message.getDate());
	    values.put("body", message.getBody());
	    values.put("extra", message.getExtra());
	    values.put("href", message.getHref());
	    values.put("unread", message.isUnread() ? 1 : 0);
	    values.put("sys", message.isSystemMsg() ? 1 : 0);
	    db.insert("messages", "did", values);
	    db.close();
	}
	
	public void setDeviceMessageReaded(String did) {
		List<DeviceMessage> ls = mListMessage.get(did);
		if(ls != null) {
			for(DeviceMessage i : ls) {
				i.setUnread(false);
			}
			db = this.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
			String sql = "update messages set unread=0 where did='" + did + "'";
			try {
				db.execSQL(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			db.close();
		}
	}
	
	public int getMessageUnreadCount() {
		int ret = 0;
		for(List<DeviceMessage> li : mListMessage.values()) {
			for(DeviceMessage m : li) {
				ret += m.isUnread() ? 1 : 0;
			}
		}
		return ret;
	}
	
	public boolean clearMessage(String did) {
		if(!mListMessage.containsKey(did)) {
			mListMessage.put(did, new ArrayList<DeviceMessage>());
		}
		List<DeviceMessage> ls = mListMessage.get(did);
	    try {
			db = this.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
			String sql = "delete from messages where did='" + did +"'";
			db.execSQL(sql);
			ls.clear();
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean clearAll() {
		try {
			db = this.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
			String sql = "delete from messages";
			db.execSQL(sql);
			mListMessage.clear();
			db.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
