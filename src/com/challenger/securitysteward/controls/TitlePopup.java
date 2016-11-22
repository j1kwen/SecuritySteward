package com.challenger.securitysteward.controls;

import java.util.ArrayList;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author 
 *
 */
@SuppressLint("InflateParams")
public class TitlePopup extends PopupWindow {
	private Context mContext;

	protected final int LIST_PADDING = 10;
	
	private Rect mRect = new Rect();
	
	private final int[] mLocation = new int[2];
	
	@SuppressWarnings("unused")
	private int mScreenWidth,mScreenHeight;

	private boolean mIsDirty;
	
	private int popupGravity = Gravity.NO_GRAVITY;	
	
	private OnItemOnClickListener mItemOnClickListener;
	
	private ListView mListView;
	
	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();	
	
	
	public TitlePopup(Context context){
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	public TitlePopup(Context context, int width, int height){
		this.mContext = context;
		setFocusable(true);
		setTouchable(true);	
		setOutsideTouchable(true);
		
		mScreenWidth = Utils.getScreenWidth(mContext);
		mScreenHeight = Utils.getScreenHeight(mContext);
		
		setHeight(height);
		
		setBackgroundDrawable(new ColorDrawable(0));
		
		setAnimationStyle(R.style.AnimTools);
		
		setContentView(LayoutInflater.from(mContext).inflate(R.layout.title_popup, null));
		
		initUI();
	}
	private void initUI() {
		mListView = (ListView) getContentView().findViewById(R.id.title_list);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,long arg3) {
				dismiss();
				
				if(mItemOnClickListener != null)
					mItemOnClickListener.onItemClick(mActionItems.get(index), index);
			}
		}); 
	}
	
	public void show(View view){
		view.getLocationOnScreen(mLocation);
		
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),mLocation[1] + view.getHeight());
		
		if(mIsDirty){
			populateActions();
		}
		
		int curWidth = getWidth();
		for(int i = 0 ; i < mActionItems.size() ; i++) {
			int tp = mActionItems.get(i).mTitle.toString().length() * 10 + 100;
			setWidth(Math.max(curWidth,Utils.dp2px(mContext, tp)));
		}
		
		showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING - (getWidth()/2), mRect.bottom);
	}
	
	private void populateActions(){
		mIsDirty = false;
		
		mListView.setAdapter(new BaseAdapter() {			
			@SuppressWarnings("deprecation")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = null;
				
				if(convertView == null){
					textView = new TextView(mContext);
					textView.setTextColor(mContext.getResources().getColor(android.R.color.white));
					textView.setTextSize(15);
					textView.setGravity(Gravity.CENTER_VERTICAL);
					textView.setPadding(0, 10, 0, 10);
					textView.setSingleLine(true);
					
				}else{
					textView = (TextView) convertView;
				}
				
				ActionItem item = mActionItems.get(position);
				
				textView.setText(item.mTitle);
				textView.setCompoundDrawablePadding(10);
                textView.setCompoundDrawablesWithIntrinsicBounds(item.mDrawable, null , null, null);
                
                return textView;
			}
			
			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				return mActionItems.get(position);
			}
			
			@Override
			public int getCount() {
				return mActionItems.size();
			}
		}) ;
	}
	
	public void addAction(ActionItem action){
		if(action != null){
			mActionItems.add(action);
			mIsDirty = true;
		}
	}
	
	public void cleanAction(){
		if(mActionItems.isEmpty()){
			mActionItems.clear();
			mIsDirty = true;
		}
	}
	
	public ActionItem getAction(int position){
		if(position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}			
	
	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.mItemOnClickListener = onItemOnClickListener;
	}
	
	public static interface OnItemOnClickListener{
		public void onItemClick(ActionItem item , int position);
	}
}
