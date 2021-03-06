package com.github.chenglei1986.clock;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class ClockView extends View {

    private static final float MIN_WIDTH_DP = 50;
    private static final float MIN_HEIGHT_DP = 50;

    private static final int DEFAULT_CLOCK_FACE_COLOR = Color.parseColor("#A9ADB0");
    private static final int DEFAULT_OUTER_RIM_COLOR = Color.BLACK;
    private static final int DEFAULT_INNER_RIM_COLOR = Color.BLACK;
    private static final int DEFAULT_THICK_MARKER_COLOR = Color.BLACK;
    private static final int DEFAULT_THIN_MARKER_COLOR = Color.BLACK;
    private static final int DEFAULT_NUMBER_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_HOUR_HAND_COLOR = Color.BLACK;
    private static final int DEFAULT_MINUTE_HAND_COLOR = Color.BLACK;
    private static final int DEFAULT_SWEEP_HAND_COLOR = Color.BLACK;
    private static final int DEFAULT_CENTER_CIRCLE_COLOR = Color.BLACK;
    private static final int DEFAULT_ANIMATION_DURATION_MILLI = 1200;
    private static final int DEFAULT_ANIM_START_DELAY_MILLI = 500;

    private static final float DEFAULT_OUTER_RIM_WIDTH = dipToPx(1);
    private static final float DEFAULT_INNER_RIM_WIDTH = dipToPx(1);
    private static final float DEFAULT_THICK_MARKER_WIDTH = dipToPx(3);
    private static final float DEFAULT_THICK_MARKER_LENGTH = dipToPx(20);
    private static final float DEFAULT_THIN_MARKER_WIDTH = dipToPx(1);
    private static final float DEFAULT_THIN_MARKER_LENGTH = dipToPx(10);
    private static final float DEFAULT_NUMBER_TEXT_SIZE = dipToPx(18);
    private static final float DEFAULT_HOUR_HAND_WIDTH = dipToPx(5);
    private static final float DEFAULT_MINUTE_HAND_WIDTH = dipToPx(3);
    private static final float DEFAULT_SWEEP_HAND_WIDTH = dipToPx(1);
    private static final float DEFAULT_CENTER_CIRCLE_RADIUS = dipToPx(5);

    private int mClockFaceColor;
    private int mOuterRimColor;
    private int mInnerRimColor;
    private int mThickMarkerColor;
    private int mThinMarkerColor;
    private int mNumberTextColor;
    private int mHourHandColor;
    private int mMinuteHandColor;
    private int mSweepHandColor;
    private int mCenterCircleColor;

    private float mOuterRimWidth;
    private float mInnerRimWidth;
    private float mThickMarkerWidth;
    private float mThinMarkerWidth;
    private float mNumberTextSize;
    private float mHourHandWidth;
    private float mMinuteHandWidth;
    private float mSweepHandWidth;
    private float mCenterCircleRadius;

    private boolean mShowThickMarkers = true;
    private boolean mShowThinMarkers = true;
    private boolean mShowNumbers = true;
    private boolean mShowSweepHand = true;
    private boolean mAnimationPlayed = true;
    private boolean mAnimation = true;

    private NumberType mNumberType;

    private int mMinWidth = (int) dipToPx(MIN_WIDTH_DP);
    private int mMinHeight = (int) dipToPx(MIN_HEIGHT_DP);

    private Rect mPaintRect = new Rect();
    private int mRefreshRectLeft, mRefreshRectTop, mRefreshRectRight, mRefreshRectBottom;
    private float mHour, mMinute, mSecond;
    private int mMilliSecond;

    private Paint mColckFacePaint = new Paint();
    private Paint mOuterRimPaint = new Paint();
    private Paint mInnerRimPaint = new Paint();
    private Paint mThickMarkerPaint = new Paint();
    private Paint mThinMarkerPaint = new Paint();
    private TextPaint mNumberPaint = new TextPaint();
    private Paint mHourHandPaint = new Paint();
    private Paint mMinuteHandPaint = new Paint();
    private Paint mSweepHandPaint = new Paint();
    private Paint mCenterCirclePaint = new Paint();

    private TimeZone mTimeZone;
    private String mTimeZoneId;

    private Calendar mCalendar;

    private enum NumberType {
        ARABIC, ROMAN
    }

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ClockView);
        mClockFaceColor = attr.getColor(R.styleable.ClockView_clockFaceColor, DEFAULT_CLOCK_FACE_COLOR);
        mOuterRimColor = attr.getColor(R.styleable.ClockView_outerRimColor, DEFAULT_OUTER_RIM_COLOR);
        mInnerRimColor = attr.getColor(R.styleable.ClockView_innerRimColor, DEFAULT_INNER_RIM_COLOR);
        mThickMarkerColor = attr.getColor(R.styleable.ClockView_thickMarkerColor, DEFAULT_THICK_MARKER_COLOR);
        mThinMarkerColor = attr.getColor(R.styleable.ClockView_thinMarkerColor, DEFAULT_THIN_MARKER_COLOR);
        mNumberTextColor = attr.getColor(R.styleable.ClockView_numberTextColor, DEFAULT_NUMBER_TEXT_COLOR);
        mHourHandColor = attr.getColor(R.styleable.ClockView_hourHandColor, DEFAULT_HOUR_HAND_COLOR);
        mMinuteHandColor = attr.getColor(R.styleable.ClockView_minuteHandColor, DEFAULT_MINUTE_HAND_COLOR);
        mSweepHandColor = attr.getColor(R.styleable.ClockView_sweepHandColor, DEFAULT_SWEEP_HAND_COLOR);
        mCenterCircleColor = attr.getColor(R.styleable.ClockView_centerCircleColor, DEFAULT_CENTER_CIRCLE_COLOR);

        mOuterRimWidth = attr.getDimension(R.styleable.ClockView_outerRimWidth, DEFAULT_OUTER_RIM_WIDTH);
        mInnerRimWidth = attr.getDimension(R.styleable.ClockView_innerRimWidth, DEFAULT_INNER_RIM_WIDTH);
        mThickMarkerWidth = attr.getDimension(R.styleable.ClockView_thickMarkerWidth, DEFAULT_THICK_MARKER_WIDTH);
        mThinMarkerWidth = attr.getDimension(R.styleable.ClockView_thinMarkerWidth, DEFAULT_THIN_MARKER_WIDTH);
        mNumberTextSize = attr.getDimension(R.styleable.ClockView_numberTextSize, DEFAULT_NUMBER_TEXT_SIZE);
        mHourHandWidth = attr.getDimension(R.styleable.ClockView_hourHandWidth, DEFAULT_HOUR_HAND_WIDTH);
        mMinuteHandWidth = attr.getDimension(R.styleable.ClockView_minuteHandWidth, DEFAULT_MINUTE_HAND_WIDTH);
        mSweepHandWidth = attr.getDimension(R.styleable.ClockView_sweepHandWidth, DEFAULT_SWEEP_HAND_WIDTH);
        mCenterCircleRadius = attr.getDimension(R.styleable.ClockView_centerCircleRadius, DEFAULT_CENTER_CIRCLE_RADIUS);

        mShowThickMarkers = attr.getBoolean(R.styleable.ClockView_showThickMarkers, mShowThickMarkers);
        mShowThinMarkers = attr.getBoolean(R.styleable.ClockView_showThinMarkers, mShowThinMarkers);
        mShowNumbers = attr.getBoolean(R.styleable.ClockView_showNumbers, mShowNumbers);
        mShowSweepHand = attr.getBoolean(R.styleable.ClockView_showSweepHand, mShowSweepHand);
        mAnimation = attr.getBoolean(R.styleable.ClockView_animation, mAnimation);

        mTimeZoneId = attr.getString(R.styleable.ClockView_timeZoneId);
        mNumberType = NumberType.values()[attr.getInt(R.styleable.ClockView_numberType, 0)];

        if (!TextUtils.isEmpty(mTimeZoneId)) {
            mTimeZone = TimeZone.getTimeZone(mTimeZoneId);
        } else {
            mTimeZone = TimeZone.getDefault();
        }
        attr.recycle();
    }

    private void init(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);
        initPaint();
        if (mAnimation) {
            initAnimation();
        }
    }

    private void initPaint() {
        mColckFacePaint.setAntiAlias(true);
        mColckFacePaint.setColor(mClockFaceColor);
        mColckFacePaint.setStyle(Paint.Style.FILL);

        mOuterRimPaint.setAntiAlias(true);
        mOuterRimPaint.setColor(mOuterRimColor);
        mOuterRimPaint.setStyle(Paint.Style.STROKE);
        mOuterRimPaint.setStrokeWidth(mOuterRimWidth);

        mInnerRimPaint.setAntiAlias(true);
        mInnerRimPaint.setColor(mInnerRimColor);
        mInnerRimPaint.setStyle(Paint.Style.STROKE);
        mInnerRimPaint.setStrokeWidth(mInnerRimWidth);

        mThickMarkerPaint.setAntiAlias(true);
        mThickMarkerPaint.setColor(mThickMarkerColor);
        mThickMarkerPaint.setStyle(Paint.Style.STROKE);
        mThickMarkerPaint.setStrokeWidth(mThickMarkerWidth);

        mThinMarkerPaint.setAntiAlias(true);
        mThinMarkerPaint.setColor(mThinMarkerColor);
        mThinMarkerPaint.setStyle(Paint.Style.STROKE);
        mThinMarkerPaint.setStrokeWidth(mThinMarkerWidth);

        mNumberPaint.setAntiAlias(true);
        mNumberPaint.setColor(mNumberTextColor);
        mNumberPaint.setTextSize(mNumberTextSize);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);

        mHourHandPaint.setAntiAlias(true);
        mHourHandPaint.setColor(mHourHandColor);
        mHourHandPaint.setStyle(Paint.Style.STROKE);
        mHourHandPaint.setStrokeWidth(mHourHandWidth);

        mMinuteHandPaint.setAntiAlias(true);
        mMinuteHandPaint.setColor(mMinuteHandColor);
        mMinuteHandPaint.setStyle(Paint.Style.STROKE);
        mMinuteHandPaint.setStrokeWidth(mMinuteHandWidth);

        mSweepHandPaint.setAntiAlias(true);
        mSweepHandPaint.setColor(mSweepHandColor);
        mSweepHandPaint.setStyle(Paint.Style.STROKE);
        mSweepHandPaint.setStrokeWidth(mSweepHandWidth);

        mCenterCirclePaint.setAntiAlias(true);
        mCenterCirclePaint.setColor(mCenterCircleColor);
        mCenterCirclePaint.setStyle(Paint.Style.FILL);
    }

    private void initAnimation() {
        mCalendar = Calendar.getInstance(mTimeZone);
        float hour = mCalendar.get(Calendar.HOUR);
        float minute = mCalendar.get(Calendar.MINUTE);
        float second = mCalendar.get(Calendar.SECOND) + (DEFAULT_ANIMATION_DURATION_MILLI + DEFAULT_ANIM_START_DELAY_MILLI) / 1000.0f;
        int milliSecond = (int) (1000 * second + mCalendar.get(Calendar.MILLISECOND));
        ValueAnimator hourAnimator = ValueAnimator.ofFloat(0, hour);
        ValueAnimator minuteAnimator = ValueAnimator.ofFloat(0, minute);
        ValueAnimator secondAnimator = ValueAnimator.ofFloat(0, second);
        ValueAnimator milliSecondAnimator = ValueAnimator.ofInt(0, milliSecond);

        hourAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mHour = (float) animation.getAnimatedValue();
            }
        });
        minuteAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMinute = (float) animation.getAnimatedValue();
            }
        });
        secondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSecond = (float) animation.getAnimatedValue();
            }
        });
        milliSecondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mMilliSecond = (int) animation.getAnimatedValue();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(hourAnimator, minuteAnimator, secondAnimator, milliSecondAnimator);
        set.setDuration(DEFAULT_ANIMATION_DURATION_MILLI);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorSet.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationPlayed = false;
                invalidate();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationPlayed = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimationPlayed = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.setStartDelay(DEFAULT_ANIM_START_DELAY_MILLI);
        set.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = mMinWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = mMinHeight;
        }

        int centerX = width / 2;
        int centerY = height / 2;
        int rectSize = Math.min(width, height);
        mPaintRect.set(centerX - rectSize / 2, centerY - rectSize / 2, centerX + rectSize / 2, centerY + rectSize / 2);

        setMeasuredDimension(width, height);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mPaintRect.centerX(), mPaintRect.centerY());
        drawClockFace(canvas);
        drawOuterRim(canvas);
        if (mShowThickMarkers) {
            drawThickMarkers(canvas);
        }
        if (mShowThinMarkers) {
            drawThinMarkers(canvas);
        }
        if (mShowNumbers) {
            drawNumbers(canvas);
        }
        drawInnerRim(canvas);

        if (mAnimationPlayed || !mAnimation) {
            mCalendar = Calendar.getInstance(mTimeZone);
            mHour = mCalendar.get(Calendar.HOUR);
            mMinute = mCalendar.get(Calendar.MINUTE);
            mSecond = mCalendar.get(Calendar.SECOND);
            mMilliSecond = (int) (1000 * mSecond + mCalendar.get(Calendar.MILLISECOND));
        }

        drawHourHand(canvas, mHour, mMinute, mSecond);
        drawMinuteHand(canvas, mMinute, mSecond);
        if (mShowSweepHand) {
            drawSweepHand(canvas, mMilliSecond);
        }
        canvas.drawCircle(0, 0, mCenterCircleRadius, mCenterCirclePaint);

        if (mAnimation && !mAnimationPlayed) {
            postInvalidate();
        } else {
            postInvalidate(
                    mRefreshRectLeft + mPaintRect.centerX() - (int) mThickMarkerWidth,
                    mRefreshRectTop + mPaintRect.centerY() - (int) mThickMarkerWidth,
                    mRefreshRectRight + mPaintRect.centerX() + (int) mThickMarkerWidth,
                    mRefreshRectBottom + mPaintRect.centerY() + (int) mThickMarkerWidth);
            mRefreshRectLeft = mRefreshRectRight = mRefreshRectTop = mRefreshRectBottom = 0;
        }
    }

    private void drawClockFace(Canvas canvas) {
        canvas.drawCircle(0, 0, mPaintRect.width() / 2, mColckFacePaint);
    }

    private void drawThickMarkers(Canvas canvas) {
        int radius = mPaintRect.width() / 2;
        for (int degree = 0; degree < 360; degree += 30) {
            double radian = degree * Math.PI / 180;
            canvas.drawLine(
                    radius * (float)Math.cos(radian),
                    radius * (float)Math.sin(radian),
                    (radius - DEFAULT_THICK_MARKER_LENGTH) * (float)Math.cos(radian),
                    (radius - DEFAULT_THICK_MARKER_LENGTH) * (float)Math.sin(radian),
                    mThickMarkerPaint);
        }
    }

    private void drawThinMarkers(Canvas canvas) {
        int radius = mPaintRect.width() / 2;
        for (int degree = 0; degree < 360; degree += 6) {
            if (degree % 30 == 0) {
                continue;
            }
            double radian = degree * Math.PI / 180;
            canvas.drawLine(
                    radius * (float)Math.cos(radian),
                    radius * (float)Math.sin(radian),
                    (radius - DEFAULT_THIN_MARKER_LENGTH) * (float)Math.cos(radian),
                    (radius - DEFAULT_THIN_MARKER_LENGTH) * (float)Math.sin(radian),
                    mThinMarkerPaint);
        }
    }

    private void drawOuterRim(Canvas canvas) {
        int radius = mPaintRect.width() / 2;
        canvas.drawCircle(0, 0, radius - DEFAULT_THIN_MARKER_LENGTH, mOuterRimPaint);
    }

    private void drawNumbers(Canvas canvas) {
        int radius = mPaintRect.width() / 2;
        int number = 0;
        Paint.FontMetrics fm = mNumberPaint.getFontMetrics();
        float numberHeight = -fm.ascent + fm.descent;
        for (int degree = -60; degree < 300; degree += 30) {
            double radian = degree * Math.PI / 180;
            String numberText;
            if (mNumberType == NumberType.ROMAN) {
                numberText = convertToRomanNumber(number++);
            } else {
                numberText = String.valueOf(++number);
            }
            canvas.drawText(numberText,
                    (radius - DEFAULT_THICK_MARKER_LENGTH - numberHeight / 2) * (float)Math.cos(radian),
                    (radius - DEFAULT_THICK_MARKER_LENGTH - numberHeight / 2) * (float)Math.sin(radian) - (fm.ascent + fm.descent) / 2,
                    mNumberPaint);
        }
    }

    private void drawInnerRim(Canvas canvas) {
        int radius = mPaintRect.width() / 2;
        Paint.FontMetrics fm = mNumberPaint.getFontMetrics();
        float numberHeight = -fm.ascent + fm.descent;
        canvas.drawCircle(0, 0, radius - DEFAULT_THICK_MARKER_LENGTH - numberHeight - fm.bottom, mInnerRimPaint);
    }

    private void drawHourHand(Canvas canvas, float hour, float minute, float second) {
        Paint.FontMetrics fm = mNumberPaint.getFontMetrics();
        float numberHeight = -fm.ascent + fm.descent;
        int radius = (int) (mPaintRect.width() / 2 - DEFAULT_THICK_MARKER_LENGTH - numberHeight - fm.bottom - dipToPx(5));
        double radian = (hour - 3) * Math.PI / 6 + minute * Math.PI / 360 + second * Math.PI / 21600;
        float stopX = radius * (float)Math.cos(radian);
        float stopY = radius * (float)Math.sin(radian);
        canvas.drawLine(0, 0, stopX, stopY, mHourHandPaint);
        setRefreshRectCoordinates((int)stopX, (int)stopY);
    }

    private void drawMinuteHand(Canvas canvas, float minute, float second) {
        int radius = (int) (mPaintRect.width() / 2 - DEFAULT_THIN_MARKER_LENGTH - dipToPx(5));
        double radian = (minute - 15) * Math.PI / 30 + second * Math.PI / 1800;
        float stopX = radius * (float)Math.cos(radian);
        float stopY = radius * (float)Math.sin(radian);
        canvas.drawLine(0, 0, stopX, stopY, mMinuteHandPaint);
        setRefreshRectCoordinates((int)stopX, (int)stopY);
    }

    private void drawSweepHand(Canvas canvas, int milliSecond) {
        int radius = mPaintRect.width() / 2;
        double radian = (milliSecond - 15000) * Math.PI / 30000;
        float stopX = radius * (float)Math.cos(radian);
        float stopY = radius * (float)Math.sin(radian);
        canvas.drawLine(0, 0, stopX, stopY, mSweepHandPaint);
        setRefreshRectCoordinates((int)stopX, (int)stopY);
    }

    private static float dipToPx(float dipValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
                Resources.getSystem().getDisplayMetrics());
    }

    private void setRefreshRectCoordinates(int x, int y) {
        mRefreshRectLeft = Math.min(mRefreshRectLeft, x);
        mRefreshRectTop = Math.min(mRefreshRectTop, y);
        mRefreshRectRight = Math.max(mRefreshRectRight, x);
        mRefreshRectBottom = Math.max(mRefreshRectBottom, y);
    }

    private String convertToRomanNumber(int number) {
        return ROMAN_NUMBER_LIST[number];
    }

    private static final String[] ROMAN_NUMBER_LIST = {
            "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ",
            "Ⅶ", "Ⅷ", "Ⅸ", "Ⅹ", "Ⅺ", "Ⅻ"};
}
