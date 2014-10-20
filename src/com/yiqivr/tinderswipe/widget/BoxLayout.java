package com.yiqivr.tinderswipe.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.yiqivr.tinderswipe.R;

/**
 * @author lvning
 * @version create time:2014-10-17_下午4:17:03
 * @Description 收藏盒子
 */
public class BoxLayout extends LinearLayout {

	// 收藏单个动画
	public static final int STACK_INDEX_BACK = 0;
	public static final int STACK_INDEX_CENTER = 1;
	public static final int STACK_INDEX_FORE = 2;
	// 查看收藏动画
	public static final int ONE_COLLECT_ANIM = 3;
	public static final int TWO_COLLECT_ANIM = 4;
	public static final int MORE_COLLECT_ANIM = 5;
	// 收回收藏动画
	public static final int ONE_PAGER_ANIM = 6;

	public static final int TWO_PAGER_ANIM_LEFT = 7;// 两个，左边在中间
	public static final int TWO_PAGER_ANIM_RIGHT = 8;// 两个，右边在中间
	public static final int MORE_PAGER_ANIM_LEFT = 9;// 多个，左边在中间
	public static final int MORE_PAGER_ANIM_RIGHT = 10;// 多个，右边在中间

	public static final int MORE_PAGER_ANIM_OTHER = 11;// 其他

	private View foreView, centerView, backView;
	private boolean foreVisible, centerVisible, backVisible;

	private static final long ANIM_TIME = 1200l;

	public BoxLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BoxLayout(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.box_card_layout, this);
		foreView = findViewById(R.id.boxpic_out);
		centerView = findViewById(R.id.boxpic_out1);
		backView = findViewById(R.id.boxpic_out2);
		setGone();
	}

	protected void setGone() {
		foreView.setAlpha(0);
		centerView.setAlpha(0);
		backView.setAlpha(0);
	}

	protected void setVisible() {
		if (foreVisible)
			foreView.setAlpha(1);
		if (centerVisible)
			centerView.setAlpha(1);
		if (backVisible)
			backView.setAlpha(1);
	}

	public void setForeImage(int resId) {
		foreView.setBackgroundResource(resId);
		if (foreView.getAlpha() == 0) {
			foreView.setAlpha(1);
		}
		startAnim(STACK_INDEX_FORE);
	}

	public void setCenterImage(int resId) {
		centerView.setBackgroundResource(resId);
		centerView.setAlpha(1);
		startAnim(STACK_INDEX_CENTER);
	}

	public void setBackImage(int resId) {
		backView.setBackgroundResource(resId);
		backView.setAlpha(1);
		startAnim(STACK_INDEX_BACK);
	}

	public float getChildTop() {
		return backView.getTop();
	}

	public float getChildLeft() {
		return backView.getLeft();
	}

	private void startAnim(int index) {
		View v = null;
		switch (index) {
		case STACK_INDEX_BACK:
			v = backView;
			break;
		case STACK_INDEX_CENTER:
			v = centerView;
			break;
		case STACK_INDEX_FORE:
			v = foreView;
			break;
		default:
			break;
		}
		ObjectAnimator transAnim = ObjectAnimator.ofFloat(v, "translationY", 150f, 0f);
		transAnim.setInterpolator(new OvershootInterpolator());
		transAnim.setDuration(250);
		transAnim.start();
	}

	public void setReadyPullIn(int animType, float transByY, final float backTransByX, final Runnable endAction) {
		setVisible();
		ObjectAnimator transAnim = ObjectAnimator.ofFloat(this, "translationY", transByY, 0);
		transAnim.setDuration(ANIM_TIME).setInterpolator(new DecelerateInterpolator());

		ObjectAnimator centerRotateAnim = ObjectAnimator.ofFloat(centerView, "rotation", 0f, -5);
		centerRotateAnim.setDuration(ANIM_TIME);

		ObjectAnimator foreRotateAnim = ObjectAnimator.ofFloat(foreView, "rotation", 0f, -5);
		foreRotateAnim.setDuration(ANIM_TIME);

		ObjectAnimator centerTransAnim = ObjectAnimator.ofFloat(centerView, "translationX", backTransByX, 0);
		centerTransAnim.setDuration(ANIM_TIME).setInterpolator(new OvershootInterpolator());

		ObjectAnimator backTransAnim = ObjectAnimator.ofFloat(backView, "translationX", backTransByX, 0);
		backTransAnim.setDuration(ANIM_TIME).setInterpolator(new OvershootInterpolator());

		ObjectAnimator backTransAnim2 = ObjectAnimator.ofFloat(backView, "translationX", 2 * backTransByX, 0);
		backTransAnim2.setDuration(ANIM_TIME).setInterpolator(new OvershootInterpolator());

		ObjectAnimator centerTransAnimN1 = ObjectAnimator.ofFloat(centerView, "translationX", -backTransByX, 0);
		centerTransAnimN1.setDuration(ANIM_TIME).setInterpolator(new OvershootInterpolator());

		ObjectAnimator foreTransAnimN2 = ObjectAnimator.ofFloat(foreView, "translationX", -2 * backTransByX, 0);
		foreTransAnimN2.setDuration(ANIM_TIME).setInterpolator(new OvershootInterpolator());

		ObjectAnimator foreTransAnimN1 = ObjectAnimator.ofFloat(foreView, "translationX", -backTransByX, 0);
		foreTransAnimN1.setDuration(ANIM_TIME).setInterpolator(new OvershootInterpolator());

		AnimatorSet set = new AnimatorSet();
		switch (animType) {
		case ONE_PAGER_ANIM:
			set.play(transAnim);
			break;
		case TWO_PAGER_ANIM_LEFT:
			set.play(transAnim).before(centerRotateAnim).before(backTransAnim);
			break;
		case TWO_PAGER_ANIM_RIGHT:
			centerView.setTranslationX(-backTransByX);
			backView.setTranslationX(0);
			set.play(centerTransAnimN1).with(centerRotateAnim).after(transAnim);
			break;
		case MORE_PAGER_ANIM_LEFT:
			set.play(transAnim).before(foreRotateAnim).before(centerTransAnim).before(centerRotateAnim)
					.before(backTransAnim2);
			break;
		case MORE_PAGER_ANIM_RIGHT:
			foreView.setTranslationX(-2 * backTransByX);
			centerView.setTranslationX(-backTransByX);
			backView.setTranslationX(0);
			set.play(foreTransAnimN2).with(foreRotateAnim).with(centerTransAnimN1).with(centerRotateAnim)
					.after(transAnim);
			break;
		case MORE_PAGER_ANIM_OTHER:
			foreView.setTranslationX(-backTransByX);
			centerView.setTranslationX(0);
			backView.setTranslationX(backTransByX);
			set.play(transAnim).before(foreTransAnimN1).before(foreRotateAnim).before(backTransAnim)
					.before(centerRotateAnim);
			break;
		default:
			break;
		}
		set.start();
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				endAction.run();

			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
	}

	public void popOut(int animType, float transByY, final float backTransByX, final Runnable endAction) {
		ObjectAnimator transAnim = ObjectAnimator.ofFloat(this, "translationY", 0, transByY);
		transAnim.setDuration(ANIM_TIME).setInterpolator(new OvershootInterpolator());

		ObjectAnimator centerRotateAnim = ObjectAnimator.ofFloat(centerView, "rotation", -5, 0f);
		centerRotateAnim.setDuration(ANIM_TIME);

		ObjectAnimator foreRotateAnim = ObjectAnimator.ofFloat(foreView, "rotation", -5, 0f);
		foreRotateAnim.setDuration(ANIM_TIME);

		ObjectAnimator centerTransAnim = ObjectAnimator.ofFloat(centerView, "translationX", 0, backTransByX);
		centerTransAnim.setDuration(ANIM_TIME).setInterpolator(new AccelerateInterpolator());

		ObjectAnimator backTransAnim = ObjectAnimator.ofFloat(backView, "translationX", 0, backTransByX);
		backTransAnim.setDuration(ANIM_TIME).setInterpolator(new AccelerateInterpolator());

		ObjectAnimator backTransAnim2 = ObjectAnimator.ofFloat(backView, "translationX", 0, 2 * backTransByX);
		backTransAnim2.setDuration(ANIM_TIME).setInterpolator(new AccelerateInterpolator());

		AnimatorSet set = new AnimatorSet();
		switch (animType) {
		case ONE_COLLECT_ANIM:
			set.play(transAnim);
			backVisible = true;
			centerVisible = false;
			foreVisible = false;
			break;
		case TWO_COLLECT_ANIM:
			set.play(transAnim).with(centerRotateAnim).before(backTransAnim);
			backVisible = true;
			centerVisible = true;
			foreVisible = false;
			break;
		case MORE_COLLECT_ANIM:
			set.play(transAnim).with(centerRotateAnim).with(foreRotateAnim).before(backTransAnim2)
					.before(centerTransAnim);
			backVisible = true;
			centerVisible = true;
			foreVisible = true;
			break;
		default:
			break;
		}
		set.start();
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				setGone();
				endAction.run();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
	}

}
