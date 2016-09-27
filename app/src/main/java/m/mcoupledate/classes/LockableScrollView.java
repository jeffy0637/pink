package m.mcoupledate.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by user on 2016/9/25.
 */
public class LockableScrollView extends ScrollView {

    private Boolean scrollable = true;

    public LockableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public LockableScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LockableScrollView(Context context)
    {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (scrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return scrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (scrollable) return super.onInterceptTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return scrollable; // mScrollable is always false at this point
            default:
                return super.onInterceptTouchEvent(ev);
        }
    }


    public void lock()
    {
        scrollable = false;
    }

    public void unlock()
    {
        scrollable = true;
    }


}
