package com.challenger.securitysteward.controls;

import com.challenger.securitysteward.R;
import com.challenger.securitysteward.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

public class TabButton extends View {

	//The initial display of icon
	private Bitmap mBitmap;
	//After selecting the icon
	private Bitmap mClickBitmap;
	//Before selecting the color
	private int mColor = 0xFFAAAAAA;
	//After selecting the color
	private int mClickColor = 0xFF3F9FE0;
	//Color of message tip
	private int mColor_message = 0xFFFF0000;
	//Text size
	private float mTextSize;
	//Text under the icon displayed��Recommend shorter
	private String mText = "";
	//Transparency for selected icon��0f-Unselected��1f-Selected
	private float mAlpha = 0f;
	//Drawing position
	private Rect mBitmapRect;
	private Rect mTextRect;
	//Text brush
	private Paint mTextPaint;
	//Number of messages
	private int mMessageNumber = 0;
	
	public TabButton(Context context) {
        this(context, null);
    }

    public TabButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
	
	public TabButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.TabButton);

        int n = a.getIndexCount();

        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.TabButton_image:
                    BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                    mBitmap = drawable.getBitmap();
                    break;
                case R.styleable.TabButton_clickimage:
                    BitmapDrawable clickdrawable = (BitmapDrawable) a.getDrawable(attr);
                    mClickBitmap = clickdrawable.getBitmap();
                    break;
                case R.styleable.TabButton_clickcolor:
                    mClickColor = a.getColor(attr,0xFF3F9FE0);
                    break;
                case R.styleable.TabButton_text:
                    mText = a.getString(attr);
                    break;
                case R.styleable.TabButton_text_size:
                    mTextSize = a.getDimension(attr,12);
                    break;
            }

        }

        a.recycle();

        mTextRect = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        mTextPaint.setAntiAlias(true);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int padding = Utils.dp2px(getContext(), 4);
        int iconWidth = Math.min(getMeasuredWidth() - padding*2, getMeasuredHeight() - padding*2 - mTextRect.height());

        int left = getMeasuredWidth() / 2 - iconWidth / 2;
        int top = getMeasuredHeight() / 2 - (mTextRect.height() + iconWidth)
                / 2;
        mBitmapRect = new Rect(left, top, left + iconWidth, top + iconWidth);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		int alpha = (int) Math.ceil(255 * mAlpha);

        drawText(canvas,255,mColor);	// Draw original text 
        drawText(canvas,alpha,mClickColor);	// Draw text when color changed

        if(mBitmap != null) {
        	drawBitmap(canvas, 255, mColor, mBitmap);        	
        }
        if(mClickBitmap != null) {        	
        	drawBitmap(canvas,alpha,mClickColor,mClickBitmap);
        }

        if (mMessageNumber > 0){
            drawMessages(canvas);
        }
	}
	
	 /**
     * Drawing text
     * @param canvas
     * @param alpha
     * @param color
     */
    private void drawText(Canvas canvas, int alpha,int color)
    {
        mTextPaint.setColor(color);
        mTextPaint.setAlpha(alpha);
        int x = getMeasuredWidth() / 2 - mTextRect.width() / 2;
        int y = mBitmapRect.bottom + mTextRect.height();
        if(mBitmap == null) {
        	y = getMeasuredHeight() / 2 + mTextRect.height() / 2;
        }
        canvas.drawText(mText, x, y, mTextPaint);

    }

    /**
     * Drawing icon
     * @param canvas
     * @param alpha
     * @param color
     * @param bitmap
     */
    private void drawBitmap(Canvas canvas, int alpha,int color,Bitmap bitmap)
    {
        Bitmap bitmapTem = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvasTem = new Canvas(bitmapTem);
        Paint paint = new Paint();
        canvasTem.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        paint.setColor(color);
        paint.setAlpha(alpha);
        canvasTem.drawRect(mBitmapRect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvasTem.drawBitmap(bitmap, null, mBitmapRect, paint);
        canvas.drawBitmap(bitmapTem, 0, 0, null);
    }


    /**
     * Drawing message
     * @param canvas
     */
    private void drawMessages(Canvas canvas){
        Paint textPaint = new Paint();
        Rect textRect = new Rect();
        String text = mMessageNumber > 99 ? "99+" : mMessageNumber + "";
        int textSize = 0;
        if (text.length() == 1) {
            textSize = Utils.dp2px(getContext(), 12);
        }else if(text.length() == 2){
            textSize = Utils.dp2px(getContext(), 10);
        }else{
            textSize = Utils.dp2px(getContext(), 9);
        }

        textPaint.setColor(0xDDFFFFFF);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

        //Drawing a round
        int width = Utils.dp2px(getContext(), 18);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(mColor_message);

        RectF messageRectF = new RectF(mBitmapRect.right - width, 
        								mBitmapRect.top,
        								mBitmapRect.right, 
        								mBitmapRect.top + width);
        canvas.drawOval(messageRectF, paint);

        //Drawing number text
        float x = messageRectF.right - messageRectF.width() / 2f;
        float y = messageRectF.bottom - messageRectF.height() / 2f 
        		- fontMetrics.descent 
        		+ (fontMetrics.descent - fontMetrics.ascent) / 2;
        canvas.drawText(text, x, y,textPaint);

    }
    /**
     * Change the number of messages and refresh
     * @param number
     */
    public void setMessageNumber(int number){
        mMessageNumber = number;
        invalidateView();
    }

    /**
     * Change in transparency and refresh
     * @param alpha
     */
    public void setAlpha(float alpha)
    {
        this.mAlpha = alpha;
        invalidateView();
    }
    /**
     * Redraw
     */
    private void invalidateView()
    {
        if (Looper.getMainLooper() == Looper.myLooper())
        {
            invalidate();
        } else
        {
            postInvalidate();
        }
    }
    
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_ALPHA = "status_alpha";
    
    @Override
    protected Parcelable onSaveInstanceState() {
 
    	Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_ALPHA, mAlpha);
        return bundle;
    }

    
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
    	
    	if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATUS_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }
    	super.onRestoreInstanceState(state);
    }
    
    public String getText() {
    	
    	return mText;
    }
}
