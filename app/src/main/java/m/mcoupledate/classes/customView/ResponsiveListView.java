package m.mcoupledate.classes.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by user on 2016/10/21.
 */

public class ResponsiveListView extends ListView {

    public ResponsiveListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResponsiveListView(Context context) {
        super(context);
    }

    public ResponsiveListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}

