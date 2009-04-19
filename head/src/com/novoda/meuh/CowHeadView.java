package com.novoda.meuh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class CowHeadView extends View{
	private Bitmap		cowHead;
	private int			cowHeadXOffset;
	private int			cowHeadYOffset;

	private Animation	anim;
	private Canvas		canvas;

	public CowHeadView(Context context) {
		super(context);
		cowHead = BitmapFactory.decodeResource(getResources(), R.drawable.me);
		cowHeadXOffset = cowHead.getWidth() / 2;
		cowHeadYOffset = cowHead.getHeight() / 2;
	}
	
	public CowHeadView(Context context, AttributeSet set) {
		super(context);
		cowHead = BitmapFactory.decodeResource(getResources(), R.drawable.me);
		cowHeadXOffset = cowHead.getWidth() / 2;
		cowHeadYOffset = cowHead.getHeight() / 2;
	}

	public void spinCowHead(long totalMooPower) {
		anim.setDuration(Math.abs(totalMooPower));
		synchronized (this) {
			startAnimation(anim);
		}
	}

	private void createAnim(Canvas canvas) {
		anim = new RotateAnimation(0, 360, canvas.getWidth() / 2, canvas.getHeight() / 2);
		anim.setRepeatMode(Animation.RESTART);
		anim.setRepeatCount(0);
		anim.setDuration(1800L);
		startAnimation(anim);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.canvas = canvas;

		if (anim == null) {
			createAnim(canvas);
		}

		int centerX = canvas.getWidth() / 2;
		int centerY = canvas.getHeight() / 2;

		canvas.drawBitmap(cowHead, centerX - cowHeadXOffset, centerY - cowHeadYOffset, null);
	}
}

