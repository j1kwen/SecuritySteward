package com.challenger.securitysteward;

import java.io.IOException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.challenger.securitysteward.model.DeviceManagement;
import com.challenger.securitysteward.model.MediaManagement;
import com.challenger.securitysteward.model.MessageManagement;
import com.challenger.securitysteward.model.UserManagement;
import com.challenger.securitysteward.utils.DataBaseHelper;
import com.challenger.securitysteward.utils.SDKUtils;
import com.challenger.securitysteward.utils.Utils;
import com.igexin.sdk.PushManager;

public class SplashActivity extends BaseActivity {

	protected static final int FAILURE = 0;
	protected static final int SUCCESS = 1;
	protected static final int OFFLINE = 2;
	
	private static final int SHOW_TIME_MIN = 1500;
	private static final int SHOW_TIME_MAX = 6000;
	
	private long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_splash); 

        new AsyncTask<Void, Void, Integer>() { 

            @Override
            protected Integer doInBackground(Void... params) { 
            	
            	int result; 
        		mStartTime = System.currentTimeMillis(); 
        		result = loadingCache(); 
        		long loadingTime = System.currentTimeMillis() - mStartTime; 
        		if (loadingTime < SHOW_TIME_MIN) { 
        			try { 
        				Thread.sleep(SHOW_TIME_MIN - loadingTime); 
        			} catch (InterruptedException e) { 
        				e.printStackTrace(); 
        			}
        		} 
         		return result;  
            } 

            @Override
            protected void onPostExecute(Integer result) { 
            	
            	if(result == OFFLINE) {
    				Utils.setToastBottom(getApplicationContext(), R.string.no_network);
            	}
         	
            	Intent intent = new Intent(SplashActivity.this, MainActivity.class); 
                
            	if(getIntent().getBundleExtra(Utils.EXTRA_BUNDLE) != null){
                    intent.putExtra(Utils.EXTRA_BUNDLE,
                            getIntent().getBundleExtra(Utils.EXTRA_BUNDLE));
                    intent.putExtra("message", getIntent().getSerializableExtra("message"));
                    intent.putExtra("did", getIntent().getStringExtra("did"));
                }
            	
            	startActivityForResult(intent, 0); 
                
                overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
            }; 
            
        }.execute(new Void[]{}); 
    } 

    private int loadingCache() { 
        
//    	if(!SystemUtils.isServiceWork(getApplicationContext(), "com.challenger.securitysteward.services.MainService")) {
//    		Intent service = new Intent(getApplicationContext(), MainService.class);
//    		startService(service);
//    	}
    	PushManager.getInstance().initialize(getApplicationContext());
    	DataBaseHelper dbHelper = new DataBaseHelper(getApplicationContext());
    	try {
    		dbHelper.createDataBase();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	UserManagement mUser = (UserManagement)readObject(Utils.KEY_SHARED_USER_INFO, Utils.KEY_SHARED_USER_BOJECT);
    	MessageManagement.getInstance().initialize(getApplicationContext());
    	DeviceManagement.getInstance().initialize(getApplicationContext());
    	MediaManagement.getInstance().initialize(getApplicationContext());
    	SDKUtils.getInstance().initialize(getApplicationContext());
    	Utils.setUserManagement(mUser);
    	
    	int cnt = 0;
    	while(Utils.clientId == null || Utils.clientId.equals("")) {
    		try {
    			if(System.currentTimeMillis() - mStartTime > SHOW_TIME_MAX) {
    				return OFFLINE;
    			}
    			Utils.clientId = PushManager.getInstance().getClientid(getApplicationContext());
    			Log.d("Get Did", "get did " + Utils.clientId + " ... => " + cnt);
    			Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		cnt++;
    	}
    	//dbHelper.exportDatabase();
        return SUCCESS; 
    }

	@Override
	protected void initView() {
		
	}

	@Override
	protected void initEventListener() {
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}
}
