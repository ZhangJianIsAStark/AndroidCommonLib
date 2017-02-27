package stark.a.is.zhang.draganddraw.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import stark.a.is.zhang.draganddraw.data.Box;

public class BoxDrawingView extends View {
    private Box mCurrentBox;
    private List<Box> mBoxes = new ArrayList<>();

    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(Color.GREEN);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.WHITE);
    }

    private static int NORMAL = 0;
    private int mNormalPointerId = -1;

    private static int ROTATE = 1;
    private int mRotatePointerId = -1;

    private int mMode = NORMAL;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mCurrentBox = new Box(current);
                mBoxes.add(mCurrentBox);
                mNormalPointerId = event.getPointerId(event.getActionIndex());
                break;

            case MotionEvent.ACTION_MOVE:
                if (mMode == NORMAL) {
                    if (mCurrentBox != null) {
                        mCurrentBox.setCurrent(current);
                    }
                } else if (mMode == ROTATE){
                    if (mRotatePointerId != -1 && mNormalPointerId != -1) {
                        float normalX = event.getX(mNormalPointerId);
                        float normalY = event.getY(mNormalPointerId);

                        float rotateX = event.getX(mRotatePointerId);
                        float rotateY = event.getY(mRotatePointerId);

                        float k = Math.abs((normalX-rotateX)/(normalY-rotateY));
                        float degree = (float) Math.toDegrees(Math.atan(k));

                        if ((normalX < rotateX && normalY < rotateY)
                            || (normalX > rotateX && normalY > rotateY)){
                            degree = -degree;
                        }

                        for (Box box : mBoxes) {
                            box.setTempDegree(degree);
                        }
                    }
                }

                invalidate();

                break;

            case MotionEvent.ACTION_UP:
                mCurrentBox = null;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                changeMode(event, false);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                changeMode(event, true);
                break;
        }

        return true;
    }

    private void changeMode(MotionEvent event, boolean up) {
        int pointerCount = event.getPointerCount();

        if (up) {
            --pointerCount;
        }

        if (pointerCount == 2 && !up) {
            mMode = ROTATE;
            mRotatePointerId = event.getPointerId(event.getActionIndex());
        } else if (pointerCount == 1){
            mMode = NORMAL;
            mRotatePointerId = -1;

            for (Box box : mBoxes) {
                box.saveDegree();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxes) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.save();
            canvas.rotate(box.getDegree());
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.restore();
        }
    }

    private static final String PARENT_KEY = "parentData";
    private static final String BOX_INFO_KEY = "boxInfo";
    private static final String ORIGIN_KEY = "originPointF";
    private static final String CURRENT_KEY = "currentPointF";
    private static final String DEGREE_KEY = "degree";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(PARENT_KEY, super.onSaveInstanceState());

        ArrayList<Bundle> boxInfo = new ArrayList<>();
        for (Box box : mBoxes) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ORIGIN_KEY, box.getOrigin());
            bundle.putParcelable(CURRENT_KEY, box.getCurrent());
            bundle.putFloat(DEGREE_KEY, box.getDegree());

            boxInfo.add(bundle);
        }

        state.putParcelableArrayList(BOX_INFO_KEY, boxInfo);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;

        super.onRestoreInstanceState((Parcelable) bundle.get(PARENT_KEY));

        ArrayList<Bundle> boxInfo = bundle.getParcelableArrayList(BOX_INFO_KEY);

        if (boxInfo != null && boxInfo.size() > 0) {
            for (Bundle info : boxInfo) {
                Box box = new Box((PointF) info.getParcelable(ORIGIN_KEY),
                        (PointF) info.getParcelable(CURRENT_KEY));
                box.setDegree(info.getFloat(DEGREE_KEY, 0));

                mBoxes.add(box);
            }
        }
    }
}
