package com.challenger.securitysteward.receivers;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.model.DeviceMessage;
import com.challenger.securitysteward.utils.SystemUtils;
import com.igexin.sdk.PushConsts;

public class PushReceiver extends BroadcastReceiver{

	private final String TAG = "Receiver";

	@SuppressWarnings("unused")
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		
		case PushConsts.GET_CLIENTID:
			String cid = bundle.getString("clientid");
			// TODO:处理cid返回
			break;
		case PushConsts.GET_MSG_DATA:
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");
			byte[] payload = bundle.getByteArray("payload");
			if (payload != null) {
				String data = new String(payload);
				// TODO:接收处理透传（payload）数据
				Log.d(TAG, data);
				try {
					JSONObject json = new JSONObject(data);
					if(json.getString("type").equals("notification")) {
						pushNotification(context, json);
					} else if(json.getString("type").equals("message")) {
						Log.d(TAG, "type is message");
						if(!SystemUtils.isApplicationBroughtToBackground(context)) {
							Log.d("Receiver","activity has alived");
							Intent intentSend = new Intent();
							intentSend.setAction("com.challenger.securitysteward.SEND_DEVICE_MESSAGE");
							intentSend.putExtra("data", data);
							context.sendBroadcast(intentSend);
						} else {
							// Push notification
							Log.d("Receiver","activity gone to background.");
							pushNotification(context, json);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}
	
	private void pushNotification(Context context, JSONObject data) {
		Intent intent = new Intent(context, NotificationReceiver.class);
		String did;
		try {
			did = data.getString("did");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		String title;
		try {
			title = data.getString("title");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		long date;
		try {
			date = data.getLong("timestamp");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		String body;
		try {
			body = data.getString("body");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		String extra;
		try {
			extra = data.getString("extra");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		String href;
		try {
			href = data.getString("href");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		DeviceMessage msg = new DeviceMessage(title, date, body, extra, href, 1L);
		intent.putExtra("message", msg);
		intent.putExtra("did", did);
		PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
			new Random().nextInt(),
			new Notification.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(body)
				.setDefaults(Notification.DEFAULT_ALL)
				.setTicker(title)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.build()
		);
	}

}
