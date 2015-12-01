package org.xzc.labelviewlayout_lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by xzchaoo on 2015/12/1 0001.
 */
public class LabelViewLayout extends FrameLayout {
	public LabelViewLayout(Context context) {
		this(context, null);
	}

	public LabelViewLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	//4个位置
	public enum Position {
		LEFT_TOP(0), RIGHT_TOP(1), LEFT_BOTTOM(2), RIGHT_BOTTOM(3);
		private int value;

		Position(int value) {
			this.value = value;
		}

		public static Position fromInteger(int value) {
			switch (value) {
				case 0:
					return LEFT_TOP;
				case 1:
					return RIGHT_TOP;
				case 2:
					return RIGHT_BOTTOM;
				case 3:
					return LEFT_BOTTOM;
			}
			throw new IllegalArgumentException("value必须为0,1,2,3");
		}
	}

	private static void setPositionInner(final View label, final Position p, final int distance) {
		//该Layout不要有padding

		View parent = (View) label.getParent();
		int pw = parent.getMeasuredWidth();
		int ph = parent.getMeasuredHeight();
		int w = label.getMeasuredWidth();
		int h = label.getMeasuredHeight();
		switch (p) {
			case LEFT_TOP:
				label.setTranslationX(-w / 2);
				label.setTranslationY(distance);
				label.setPivotX(w / 2);
				label.setPivotY(-distance);
				label.setRotation(-45);
				break;
			case LEFT_BOTTOM:
				label.setTranslationX(-w / 2);
				label.setTranslationY(ph /*- parent.getPaddingTop() - parent.getPaddingBottom()*/ - h - distance);
				label.setPivotX(w / 2);
				label.setPivotY(h + distance);
				label.setRotation(45);
				break;
			case RIGHT_TOP:
				label.setTranslationX(w / 2);
				label.setTranslationY(distance);
				label.setPivotX(w / 2);
				label.setPivotY(-distance);
				label.setRotation(45);
				break;
			case RIGHT_BOTTOM:
				label.setTranslationX(w / 2);
				label.setTranslationY(ph /*-parent.getPaddingTop() - parent.getPaddingBottom()*/ - h - distance);
				label.setPivotX(w / 2);
				label.setPivotY(h + distance);
				label.setRotation(-45);
				break;
		}

	}

	private static void setPosition(final View label, final Position p, final int distance) {
		label.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				label.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				setPositionInner(label, p, distance);
			}
		});
	}

	private Position position = Position.LEFT_TOP;
	private int distance = 50;

	public LabelViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelViewLayout);
		position = Position.fromInteger(ta.getInteger(R.styleable.LabelViewLayout_position, 0));
		distance = ta.getDimensionPixelSize(R.styleable.LabelViewLayout_distance, 50);
		ta.recycle();
		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				updatePosition();
			}
		});
	}

	private void updatePosition() {
		//TODO 现在这里有个问题 如果onGlobalLayout已经调用过了 那么再次使用addOnGlobalLayoutListener是不会被回调的
		setPositionInner(label, position, distance);
		setPosition(label, position, distance);
	}

	private View content;
	private View label;

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() != 2) {
			throw new IllegalStateException("LabelViewLayout下必须有且只有2个元素.");
		}
		content = getChildAt(0);
		label = getChildAt(1);
	}

	public void setPosition(Position pos) {
		if (!position.equals(pos)) {
			position = pos;
			updatePosition();
		}
	}

	public void setDistance(int distancePx) {
		if (distance != distancePx) {
			distance = distancePx;
			updatePosition();
		}
	}

	public View getContent() {
		return content;
	}

	public int getDistance() {
		return distance;
	}

	public View getLabel() {
		return label;
	}

	public Position getPosition() {
		return position;
	}
}
