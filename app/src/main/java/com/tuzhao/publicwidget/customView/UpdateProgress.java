package com.tuzhao.publicwidget.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tuzhao.utils.ConstansUtil;

/**
 * Created by juncoder on 2018/8/28.
 * <p>
 * 更新对话框的进度条
 * </p>
 */
public class UpdateProgress extends View {

    private Paint mBackgroundPaint;

    private Paint mProgressPaint;

    private Paint mTextPaint;

    private String mProgressText = "0%";

    private int mProgress;

    private LinearGradient mLinearGradient;

    private RectF mBackgroundRectF;

    private RectF mProgressRectF;

    private float mRadius;

    public UpdateProgress(Context context) {
        this(context, null);
    }

    public UpdateProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpdateProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(Color.parseColor("#ededed"));

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * 12);
        mTextPaint.setColor(ConstansUtil.B1_COLOR);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLinearGradient == null) {
            mLinearGradient = new LinearGradient(0, getMeasuredHeight() / 2f, getMeasuredWidth(), getMeasuredHeight() / 2f, Color.parseColor("#ffe527"), Color.parseColor("#ffc931"), Shader.TileMode.CLAMP);
            mProgressPaint.setShader(mLinearGradient);

            mBackgroundRectF = new RectF();
            mBackgroundRectF.right = getMeasuredWidth();
            mBackgroundRectF.bottom = getMeasuredHeight();

            mProgressRectF = new RectF();
            mProgressRectF.bottom = getMeasuredHeight();

            mRadius = getResources().getDisplayMetrics().density * 10 + 0.5f;

        }

        mProgressRectF.right = getMeasuredWidth() * mProgress / 100.f;

        //画背景
        canvas.drawRoundRect(mBackgroundRectF, mRadius, mRadius, mBackgroundPaint);

        //画进度，采取裁剪画布，只画当前进度所在区域的方法
        canvas.save();
        canvas.clipRect(mProgressRectF);
        canvas.drawRoundRect(mBackgroundRectF, mRadius, mRadius, mProgressPaint);
        canvas.restore();

        //画显示当前进度的文字
        float textWidth = mTextPaint.measureText(mProgressText);
        float baseX = getMeasuredWidth() / 2 - textWidth / 2;
        float baseY = getMeasuredHeight() / 2 - ((mTextPaint.descent() / 2 + mTextPaint.ascent()) / 2);
        canvas.drawText(mProgressText, baseX, baseY, mTextPaint);
    }

    public void setProgress(int progress) {
        if (progress >= 0 && progress <= 100 && mProgress != progress) {
            mProgress = progress;
            mProgressText = progress + "%";
            invalidate();
        }
    }

}
