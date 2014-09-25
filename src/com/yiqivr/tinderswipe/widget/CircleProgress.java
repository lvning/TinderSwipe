package com.yiqivr.tinderswipe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.yiqivr.tinderswipe.R;

/**
 * @author lvning
 * @version create time:2014-9-15_下午4:13:50
 * @Description 圆形进度
 */
public class CircleProgress extends View {

	private CircleAttribute mCircleAttribute;
	private int mMaxProgress = 100;
	private int mSubCurProgress;
	private int backColor, foreColor;
	private Bitmap centerImg;

	public CircleProgress(Context paramContext) {
		this(paramContext, null);
	}

	public CircleProgress(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext, paramAttributeSet);
		defaultParam(paramContext);
	}

	private void init(Context paramContext, AttributeSet paramAttributeSet) {
		final TypedArray ta = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CircleProgress);
		backColor = ta.getColor(R.styleable.CircleProgress_backColor, Color.parseColor("#898989"));
		foreColor = ta.getColor(R.styleable.CircleProgress_foreColor, Color.parseColor("#13DEFF"));
		int centerImgId = ta.getResourceId(R.styleable.CircleProgress_centerPic, android.R.drawable.star_on);
		centerImg = BitmapFactory.decodeResource(getResources(), centerImgId);
		ta.recycle();
	}

	private void defaultParam(Context paramContext) {
		this.mCircleAttribute = new CircleAttribute();
		this.mMaxProgress = 100;
		this.mSubCurProgress = 0;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float f1 = 360.0F * this.mSubCurProgress / this.mMaxProgress;
		canvas.drawArc(this.mCircleAttribute.inRoundOval, 0.0F, 360.0F, this.mCircleAttribute.mBRoundPaintsFill,
				this.mCircleAttribute.circlePaint);
		canvas.drawArc(this.mCircleAttribute.mRoundOval, 0.0F, 360.0F, this.mCircleAttribute.mBRoundPaintsFill,
				this.mCircleAttribute.mBottomPaint);
		canvas.drawArc(this.mCircleAttribute.mRoundOval, this.mCircleAttribute.mDrawPos, f1,
				this.mCircleAttribute.mBRoundPaintsFill, this.mCircleAttribute.mSubPaint);
		canvas.drawBitmap(centerImg, this.mCircleAttribute.inRoundOval.centerX() - centerImg.getWidth() / 2,
				this.mCircleAttribute.inRoundOval.top + centerImg.getHeight() / 2, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int i = View.MeasureSpec.getSize(widthMeasureSpec);
		View.MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(resolveSize(i, widthMeasureSpec), resolveSize(i, heightMeasureSpec));
	}

	@Override
	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
		this.mCircleAttribute.autoFix(paramInt1, paramInt2);
	}

	/**
	 * 设置进度
	 * 
	 * @param progress
	 *            取值范围0-100
	 */
	public void setCurProgress(int progress) {
		this.mSubCurProgress = progress;
		invalidate();
	}

	public int getCurProgress() {
		return mSubCurProgress;
	}

	private class CircleAttribute {
		public boolean mBRoundPaintsFill = true;
		public Paint mBottomPaint;
		public Paint circlePaint;
		public int mDrawPos = -90;
		public int mSubPaintColor = foreColor;
		public int mBottomPaintColor = backColor;
		public int mPaintWidth = 0;
		public RectF mRoundOval = new RectF();
		public RectF inRoundOval = new RectF();
		public int mSidePaintInterval = 4;
		public Paint mSubPaint;

		public CircleAttribute() {
			this.mSubPaint = new Paint();
			this.mSubPaint.setAntiAlias(true);
			this.mSubPaint.setStyle(Paint.Style.FILL);
			this.mSubPaint.setStrokeWidth(this.mPaintWidth);
			this.mSubPaint.setColor(this.mSubPaintColor);

			this.mBottomPaint = new Paint();
			this.mBottomPaint.setAntiAlias(true);
			this.mBottomPaint.setStyle(Paint.Style.FILL);
			this.mBottomPaint.setStrokeWidth(this.mPaintWidth);
			this.mBottomPaint.setColor(this.mBottomPaintColor);

			this.circlePaint = new Paint();
			this.circlePaint.setAntiAlias(true);
			this.circlePaint.setStyle(Paint.Style.FILL);
			this.circlePaint.setStrokeWidth(this.mPaintWidth);
			this.circlePaint.setColor(this.mBottomPaintColor);
		}

		public void autoFix(int width, int height) {
			int left = CircleProgress.this.getPaddingLeft();
			int right = CircleProgress.this.getPaddingRight();
			int top = CircleProgress.this.getPaddingTop();
			int bottom = CircleProgress.this.getPaddingBottom();
			this.mRoundOval.set(left + this.mPaintWidth / 2 + mSidePaintInterval, top + this.mPaintWidth / 2
					+ mSidePaintInterval, width - right - this.mPaintWidth / 2 - mSidePaintInterval, height - bottom
					- this.mPaintWidth / 2 - mSidePaintInterval);
			this.inRoundOval.set(left + this.mPaintWidth / 2, top + this.mPaintWidth / 2, width - right
					- this.mPaintWidth / 2, height - bottom - this.mPaintWidth / 2);
		}
	}
}
