package stark.a.is.zhang.draganddraw.data;

import android.graphics.PointF;

public class Box {
    private PointF mOrigin;
    private PointF mCurrent;
    private float mTempDegree;
    private float mDegree;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public Box(PointF origin, PointF current) {
        mOrigin = origin;
        mCurrent = current;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public float getDegree() {
        return mDegree + mTempDegree;
    }

    public void setTempDegree(float degree) {
        mTempDegree = degree;
    }

    public void setDegree(float degree) {
        mDegree = degree;
    }

    public void saveDegree() {
        mDegree = mDegree + mTempDegree;
        mTempDegree = 0;
    }
}
