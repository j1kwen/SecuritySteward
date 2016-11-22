package com.challenger.securitysteward.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		Intent service = new Intent(context, MainService.class);
//	    context.startService(service);
	    Log.d("TAG1","Auto booting services");
	}

}
