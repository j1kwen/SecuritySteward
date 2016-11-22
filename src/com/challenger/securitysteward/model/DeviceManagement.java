package com.challenger.securitysteward.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DeviceManagement {
	
	@SuppressWarnings("unused")
	private static final String TAG = "DeviceManagement";
	private static DeviceManagement single = null;
	private Context context;
	private SQLiteDatabase db;
	
	private List<DeviceItemModel> mListDevices = null;
	
	public static synchronized DeviceManagement getInstance() {
		if(single == null) {
			single = new DeviceManagement();
		}
		return single;
	}
	
	public void initialize(Context context) {
		this.context = context;
		mListDevices = new ArrayList<DeviceItemModel>();
		loadDataBase();
	}
	
	private void loadDataBase() {
		mListDevices.clear();
		db = this.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
		String sql = "select * from devices";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		while(!c.isAfterLast()){

			String did = c.getString(c.getColumnIndex("did"));
			String name = c.getString(c.getColumnIndex("name"));
			String remark = c.getString(c.getColumnIndex("remark"));
			long last_modify = c.getLong(c.getColumnIndex("last_modify"));
			long level = c.getLong(c.getColumnIndex("level"));

		    mListDevices.add(new DeviceItemModel(did, name, remark, level, last_modify));
		    c.moveToNext();
		}
		db.close();
	}
	
	public void updateDataBase() {
		db = this.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
		String sql = "delete from devices";
		db.execSQL(sql);
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		for(DeviceItemModel device : mListDevices) {
			
			ContentValues values = new ContentValues();
			values.put("did", device.getDid());
			values.put("name", device.getName());
			values.put("remark", device.getRemark());
			values.put("last_modify", device.getLastmodify());
			values.put("level", device.getLevel());
			db.insert("devices", "did", values);
		}
	    db.close();
	}
	
	public void resetOrder() {
		Collections.sort(mListDevices, new Comparator<DeviceItemModel>() {

			@Override
			public int compare(DeviceItemModel lhs, DeviceItemModel rhs) {
				if(lhs.getLastmodify() != rhs.getLastmodify()) {
					return lhs.getLastmodify() > rhs.getLastmodify() ? -1 : 1;
				}
				return 0;
			}
		});
	}
	
	public void addDevice(DeviceItemModel device) {
		db = this.context.openOrCreateDatabase("data.db", Context.MODE_PRIVATE, null);
		String sql = "select * from devices where did='" + device.getDid() + "'";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		if(c.isAfterLast()) {

			ContentValues values = new ContentValues();
			values.put("did", device.getDid());
			values.put("name", device.getName());
			values.put("remark", device.getRemark());
			values.put("last_modify", device.getLastmodify());
			values.put("level", device.getLevel());
			db.insert("devices", "did", values);
		} else {
			ContentValues values = new ContentValues();
			values.put("name", device.getName());
			values.put("remark", device.getRemark());
			values.put("last_modify", device.getLastmodify());
			values.put("level", device.getLevel());
			db.update("devices", values, "did='" + device.getDid() + "'", null);
		}
	    db.close();
	    loadDataBase();
	}

	public DeviceItemModel get(int position) {
		return mListDevices.get(position);
	}
	
	public DeviceItemModel get(String did) {
		for(DeviceItemModel i : mListDevices) {
			Log.d("debug","find " +i.getDid());
			if(i.getDid().equals(did)) {
				return i;
			}
		}
		return new DeviceItemModel();
	}
	
	public void clear() {
		mListDevices.clear();
	}
	
	public void setAllDevices(List<DeviceItemModel> list) {
		mListDevices.clear();
		mListDevices.addAll(list);
		updateDataBase();
	}
	
	public List<DeviceItemModel> getDevicesList() {
		return mListDevices;
	}
	
}
