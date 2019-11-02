package com.example.recognition;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by victor on 23.12.2017.
 */

public class ViewfinderView extends View {
    private static final int MIN_FOCUS_BOX_WIDTH = 40;
    private static final int MIN_FOCUS_BOX_HEIGHT = 10;
    private static final int RADIUS_CIRCLE = 32;

    private final Paint paintOut, paintIn;
    private final int maskColor;
//    private final int frameColor;
    private final int cornerColor;

    private SurfaceView viewParent;
    private final Point ScrRes = new Point();

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintOut = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintIn = new Paint(Paint.ANTI_ALIAS_FLAG);

        Resources resources = context.getResources();

        maskColor = resources.getColor(R.color.viewfinder_mask);
        int frameColor = resources.getColor(R.color.viewfinder_frame);
        cornerColor = resources.getColor(R.color.viewfinder_corners);

        paintIn.setColor(frameColor);
        paintIn.setStyle(Paint.Style.STROKE);
        paintIn.setStrokeWidth(5);
        box = new Rect();
        boxF = new RectF();
        this.setOnTouchListener(getTouchListener());
    }

    void setViewParent(SurfaceView viewParent)
    {
        this.viewParent = viewParent;
    }

    public SurfaceView getViewParent(){
        return this.viewParent;
    }

    private Rect box /*= new Rect()*/ ;

    private final RectF boxF /*= new RectF()*/;

    private  Rect getBoxRect() {

      /*  if (box == null) {*/
        if(box.isEmpty()){

        //    ScrRes = FocusBoxUtils.getScreenResolution(getContext());

            ScrRes.x = viewParent.getWidth();
            ScrRes.y = viewParent.getHeight();

            int width = ScrRes.x * 4 / 5 ;
            int height = ScrRes.y / 9;

            width = width < MIN_FOCUS_BOX_WIDTH ? MIN_FOCUS_BOX_WIDTH : width;

            height = height < MIN_FOCUS_BOX_HEIGHT ? MIN_FOCUS_BOX_HEIGHT : height;

            int left = (ScrRes.x - width) / 2;
            int top = (ScrRes.y - height) / 2;

      //      box = new Rect(left, top, left + width, top + height);
            box.set(left, top, left + width, top + height);
        }

        return box;
    }

    public Rect getBox() {
        return box;
    }
    public RectF getBoxF() {
        return boxF;
    }

    public RectF getNormBox(){
        RectF rct = new RectF(boxF);
        rct.top /= (float)viewParent.getHeight();
        rct.bottom /= (float)viewParent.getHeight();
        rct.left /= (float)viewParent.getWidth();
        rct.right /= (float)viewParent.getWidth();
        return rct;
    }

    private void updateBoxRect(int dW, int dH) {

        int newWidth = (box.width() + dW > ScrRes.x - 4 || box.width() + dW < MIN_FOCUS_BOX_WIDTH)
                ? 0
                : box.width() + dW;

        int newHeight = (box.height() + dH > ScrRes.y - 4 || box.height() + dH < MIN_FOCUS_BOX_HEIGHT)
                ? 0
                : box.height() + dH;

        int leftOffset = (ScrRes.x - newWidth) / 2;

        int topOffset = (ScrRes.y - newHeight) / 2;

        if (newWidth < MIN_FOCUS_BOX_WIDTH || newHeight < MIN_FOCUS_BOX_HEIGHT)
            return;

        box = new Rect(leftOffset, topOffset, leftOffset + newWidth, topOffset + newHeight);
      //  box.set(leftOffset, topOffset, leftOffset + newWidth, topOffset + newHeight);
    }

    private OnTouchListener touchListener;

    private OnTouchListener getTouchListener() {

        if (touchListener == null)
            touchListener = new OnTouchListener() {

                int lastX = -1;
                int lastY = -1;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = -1;
                            lastY = -1;
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            int currentX = (int) event.getX();
                            int currentY = (int) event.getY();
                            try {
                                Rect rect = getBoxRect();
                                final int BUFFER = 50;
                                final int BIG_BUFFER = 60;
                                if (lastX >= 0) {
                                    if (((currentX >= rect.left - BIG_BUFFER
                                            && currentX <= rect.left + BIG_BUFFER)
                                            || (lastX >= rect.left - BIG_BUFFER
                                            && lastX <= rect.left + BIG_BUFFER))
                                            && ((currentY <= rect.top + BIG_BUFFER
                                            && currentY >= rect.top - BIG_BUFFER)
                                            || (lastY <= rect.top + BIG_BUFFER
                                            && lastY >= rect.top - BIG_BUFFER))) {
                                        updateBoxRect(2 * (lastX - currentX),
                                                2 * (lastY - currentY));
                                    } else if (((currentX >= rect.right - BIG_BUFFER
                                            && currentX <= rect.right + BIG_BUFFER)
                                            || (lastX >= rect.right - BIG_BUFFER
                                            && lastX <= rect.right + BIG_BUFFER))
                                            && ((currentY <= rect.top + BIG_BUFFER
                                            && currentY >= rect.top - BIG_BUFFER)
                                            || (lastY <= rect.top + BIG_BUFFER
                                            && lastY >= rect.top - BIG_BUFFER))) {
                                        // Top right corner: adjust both top and right sides
                                        updateBoxRect(2 * (currentX - lastX),
                                                2 * (lastY - currentY));
                                    } else if (((currentX >= rect.left - BIG_BUFFER
                                            && currentX <= rect.left + BIG_BUFFER)
                                            || (lastX >= rect.left - BIG_BUFFER
                                            && lastX <= rect.left + BIG_BUFFER))
                                            && ((currentY <= rect.bottom + BIG_BUFFER
                                            && currentY >= rect.bottom - BIG_BUFFER)
                                            || (lastY <= rect.bottom + BIG_BUFFER
                                            && lastY >= rect.bottom - BIG_BUFFER))) {
                                        // Bottom left corner: adjust both bottom and left sides
                                        updateBoxRect(2 * (lastX - currentX),
                                                2 * (currentY - lastY));
                                    } else if (((currentX >= rect.right - BIG_BUFFER
                                            && currentX <= rect.right + BIG_BUFFER)
                                            || (lastX >= rect.right - BIG_BUFFER
                                            && lastX <= rect.right + BIG_BUFFER))
                                            && ((currentY <= rect.bottom + BIG_BUFFER
                                            && currentY >= rect.bottom - BIG_BUFFER)
                                            || (lastY <= rect.bottom + BIG_BUFFER
                                            && lastY >= rect.bottom - BIG_BUFFER))) {
                                        // Bottom right corner: adjust both bottom and right sides
                                        updateBoxRect(2 * (currentX - lastX),
                                                2 * (currentY - lastY));
                                    } else if (((currentX >= rect.left - BUFFER
                                            && currentX <= rect.left + BUFFER)
                                            || (lastX >= rect.left - BUFFER
                                            && lastX <= rect.left + BUFFER))
                                            && ((currentY <= rect.bottom
                                            && currentY >= rect.top)
                                            || (lastY <= rect.bottom
                                            && lastY >= rect.top))) {
                                        // Adjusting left side: event falls within BUFFER pixels of
                                        // left side, and between top and bottom side limits
                                        updateBoxRect(2 * (lastX - currentX), 0);
                                    } else if (((currentX >= rect.right - BUFFER
                                            && currentX <= rect.right + BUFFER)
                                            || (lastX >= rect.right - BUFFER
                                            && lastX <= rect.right + BUFFER))
                                            && ((currentY <= rect.bottom
                                            && currentY >= rect.top)
                                            || (lastY <= rect.bottom
                                            && lastY >= rect.top))) {
                                        // Adjusting right side: event falls within BUFFER pixels of
                                        // right side, and between top and bottom side limits
                                        updateBoxRect(2 * (currentX - lastX), 0);
                                    } else if (((currentY <= rect.top + BUFFER
                                            && currentY >= rect.top - BUFFER)
                                            || (lastY <= rect.top + BUFFER
                                            && lastY >= rect.top - BUFFER))
                                            && ((currentX <= rect.right
                                            && currentX >= rect.left)
                                            || (lastX <= rect.right
                                            && lastX >= rect.left))) {
                                        // Adjusting top side: event falls within BUFFER pixels of
                                        // top side, and between left and right side limits
                                        updateBoxRect(0, 2 * (lastY - currentY));
                                    } else if (((currentY <= rect.bottom + BUFFER
                                            && currentY >= rect.bottom - BUFFER)
                                            || (lastY <= rect.bottom + BUFFER
                                            && lastY >= rect.bottom - BUFFER))
                                            && ((currentX <= rect.right
                                            && currentX >= rect.left)
                                            || (lastX <= rect.right
                                            && lastX >= rect.left))) {
                                        updateBoxRect(0, 2 * (currentY - lastY));
                                    }
                                }
                            } catch (NullPointerException e) {
                            }
                            v.invalidate();
                            lastX = currentX;
                            lastY = currentY;
                            return true;
                        case MotionEvent.ACTION_UP:
                            lastX = -1;
                            lastY = -1;
                            return true;
                    }
                    return false;
                }
            };

        return touchListener;
    }


    @Override
    public void onDraw(Canvas canvas) {

        Rect frame = getBoxRect();

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        paintOut.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paintOut);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paintOut);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paintOut);
        canvas.drawRect(0, frame.bottom + 1, width, height, paintOut);


        canvas.drawRect(frame.left, frame.top, frame.right , frame.bottom , paintIn);

        paintOut.setColor(cornerColor);
        canvas.drawCircle(frame.left - RADIUS_CIRCLE, frame.top - RADIUS_CIRCLE, RADIUS_CIRCLE, paintOut);
        canvas.drawCircle(frame.right + RADIUS_CIRCLE, frame.top - RADIUS_CIRCLE, RADIUS_CIRCLE, paintOut);
        canvas.drawCircle(frame.left - RADIUS_CIRCLE, frame.bottom + RADIUS_CIRCLE, RADIUS_CIRCLE, paintOut);
        canvas.drawCircle(frame.right + RADIUS_CIRCLE, frame.bottom + RADIUS_CIRCLE, RADIUS_CIRCLE, paintOut);
    //    boxF = new RectF(frame);
        boxF.set(frame);

    }
}