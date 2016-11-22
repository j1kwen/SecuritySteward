package com.challenger.securitysteward.fragment;

import android.view.View;

public class BaseFragment extends android.support.v4.app.Fragment {

	protected View mCurrentView;
	protected String mTitle = "";

	public BaseFragment(String title) {
		super();
		mTitle = title;
	}
	
}
