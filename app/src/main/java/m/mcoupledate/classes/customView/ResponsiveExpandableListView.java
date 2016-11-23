package m.mcoupledate.classes.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by user on 2016/11/22.
 */

public class ResponsiveExpandableListView extends ExpandableListView {
    public ResponsiveExpandableListView(Context context) {
        super(context);
    }

    public ResponsiveExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResponsiveExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
