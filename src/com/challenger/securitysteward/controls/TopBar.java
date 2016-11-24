package com.challenger.securitysteward.controls;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.utils.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBar extends RelativeLayout {
	
	//interface for click
	public interface TopBarClickListener {
		public void onLeftClick(View v);
		public void onRightClick(View v);
	}
	
	//Listener
	private TopBarClickListener mTopBarClickListener = null;
	
	//Controls
	private Button mLeftButton;
	private ImageButton mRightButton;
	private TextView mTextViewTitle;
	
	//Left properties
	private String mLeftText = "";
	
	//Right properties
	private Drawable mRightImage;
	
	//Common button properties
	private Drawable mButtonBackground;
	private int mButtonTextColor;
	private float mButtonTouchAlpha = 0.6f;
	private float mButtonNormalAlpha = 1.0f;
	
	//Title properties
	private int mTitleTextColor;
	private String mTitleText = "";
	private float mTitleTextSize;
	private Drawable mTopBarBackground;
	
	private ImageButton mImgViewBtn;
	private Context mCurContext;
	private boolean mLeftBtnExist = false;

	//Layout parameters
	private LayoutParams mLeftParams, mRightParams, mTitleParams;
	
	public TopBar(Context context) {
		
		super(context);
	}

	public TopBar(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		
		mCurContext = context;
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
		
        mRightImage = ta.getDrawable(R.styleable.TopBar_rightImage);
        
        mButtonTextColor = ta.getColor(R.styleable.TopBar_btnTextColor, 0);
        mButtonBackground = ta.getDrawable(R.styleable.TopBar_btnBackground);

        mTitleText = ta.getString(R.styleable.TopBar_title);
        mTitleTextColor = ta.getColor(R.styleable.TopBar_titleTextColor, 0);
        mTitleTextSize = ta.getDimension(R.styleable.TopBar_titleTextSize, 0);
        mTopBarBackground = ta.getDrawable(R.styleable.TopBar_topbar_background);
        
        ta.recycle();

        // Instantiation a control
        mLeftButton = new Button(context);
        mRightButton = new ImageButton(context);
        mTextViewTitle = new TextView(context);
        mImgViewBtn = new ImageButton(context);

        // Assigned values to controls
        mLeftButton.setTextColor(mButtonTextColor);
        mLeftButton.setBackground(mButtonBackground);
        mLeftButton.setText(mLeftText);
        mLeftButton.setTag("LEFT");

        mRightButton.setBackground(mRightImage);
        mRightButton.setTag("RIGHT");

        mTextViewTitle.setTextColor(mTitleTextColor);
        mTextViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP,mTitleTextSize);
        mTextViewTitle.setText(mTitleText);
        mTextViewTitle.setEllipsize(TruncateAt.END);
        mTextViewTitle.setSingleLine();
        mTextViewTitle.setGravity(Gravity.CENTER);
        mTextViewTitle.setFitsSystemWindows(true);

        //!!!need modify
        setBackground(mTopBarBackground);

        // Instantiation left/right LayoutParams
        mLeftParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        mRightParams = new LayoutParams(Utils.dp2px(context, 40), Utils.dp2px(context, 40));
        int x = (int) (mRightParams.width * 2);
        
        //mTitleParams = new LayoutParams(5 * mRightParams.width, ViewGroup.LayoutParams.MATCH_PARENT);
        mTitleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTitleParams.setMargins(x, 0, x, 0);
        
        // Add rule : How to display
        mLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mLeftParams.addRule(RelativeLayout.CENTER_VERTICAL);
        
        mTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        
        mRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mRightParams.addRule(RelativeLayout.CENTER_VERTICAL);
		mRightParams.rightMargin = Utils.dp2px(context, 12);
        
		mLeftButton.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
		
        // Add to view
        addView(mLeftButton, mLeftParams);
        addView(mTextViewTitle, mTitleParams);
        addView(mRightButton, mRightParams);
        
        //Set click listener
        mLeftButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mTopBarClickListener != null) {
					mTopBarClickListener.onLeftClick(v);
					
				}
			}
		});
        
        mRightButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mTopBarClickListener != null)
					mTopBarClickListener.onRightClick(v);
			}
		});
        ButtonListener mButtonListener = new ButtonListener();
        mLeftButton.setOnTouchListener(mButtonListener);
        mRightButton.setOnTouchListener(mButtonListener);
	}
	
	public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
		
		super(context, attrs, defStyleAttr);
	}

	public void setOnClickListener(TopBarClickListener listener) {
		
		mTopBarClickListener = listener;
	}
	
	public void setLeftText(String text) {
		
		removeView(mImgViewBtn);
		mLeftText = text;
		mLeftButton.setPadding(Utils.dp2px(mCurContext, 28), 0, 0, 0);
		mLeftButton.setText(mLeftText);
		mLeftButton.setTextSize(mTitleTextSize * 0.8f);
		int mTmpSzie = Utils.dp2px(mCurContext, 38);
		LayoutParams imgBtnPara = new LayoutParams(mTmpSzie, mTmpSzie);
		imgBtnPara.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		imgBtnPara.addRule(RelativeLayout.CENTER_VERTICAL);
		mImgViewBtn.setBackgroundResource(R.drawable.btn_back_normal);
		addView(mImgViewBtn, 0, imgBtnPara);
		mLeftBtnExist = true;
	}
	
	public void setRightButtonDrawable(Drawable drawable) {
		
		mRightImage = drawable;
		mRightButton.setBackground(mRightImage);
	}
	
	public void setTitleText(String text) {
		
		mTitleText = text;
		mTextViewTitle.setText(mTitleText);
	}
	
	public String getTitleText() {
		
		return mTitleText;
	}
	
	public boolean hasLeftButton() {
		return mLeftBtnExist;
	}
	
	public void performLeftClick() {
		mLeftButton.performClick();
	}
	
	public void hideRightButton() {
		
		mRightButton.setVisibility(View.INVISIBLE);
	}
	
	public void showRightButton() {
		
		mRightButton.setVisibility(View.VISIBLE);
	}
	
	public void hideLeftButton() {
		
		mLeftButton.setVisibility(View.INVISIBLE);
	}
	
	public void showLeftButton() {
		
		mLeftButton.setVisibility(View.VISIBLE);
	}
	
	class ButtonListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			int x = (int)event.getRawX();
			int y = (int)event.getRawY();
			
			int mEps = 100;
			int vWidth = v.getWidth() + 2 * mEps;
			int vHeight = v.getHeight() + 2 * mEps;
			
			int[] locat = new int[2];
			v.getLocationOnScreen(locat);
			locat[0] -= mEps;
			locat[1] -= mEps;
			
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				
				v.setAlpha(mButtonTouchAlpha);
				if(v.getTag().equals("LEFT")) {
					mImgViewBtn.setBackgroundResource(R.drawable.btn_back_press);
				}
			} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				
				if(!(x > locat[0] &&x - locat[0] < vWidth 
					&& y > locat[1] && y - locat[1] < vHeight)) {
					
					//Move out
					v.setAlpha(mButtonNormalAlpha);
					if(v.getTag().equals("LEFT")) {
						mImgViewBtn.setBackgroundResource(R.drawable.btn_back_normal);
					}
				}
				else {
					
					//Move in
					v.setAlpha(mButtonTouchAlpha);
					if(v.getTag().equals("LEFT")) {
						mImgViewBtn.setBackgroundResource(R.drawable.btn_back_press);
					}
				}
			} else if(event.getAction() == MotionEvent.ACTION_UP) {
				
				v.setAlpha(mButtonNormalAlpha);
				if(v.getTag().equals("LEFT")) {
					mImgViewBtn.setBackgroundResource(R.drawable.btn_back_normal);
				}
				
				if((x > locat[0] &&x - locat[0] < vWidth 
					&& y > locat[1] && y - locat[1] < vHeight)) {
					
					v.performClick();
				}
			}
			return true;
		}
		
	}
}
