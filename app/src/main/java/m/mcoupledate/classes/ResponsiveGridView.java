package m.mcoupledate.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by user on 2016/9/23.
 */
public class ResponsiveGridView extends GridView {

        public ResponsiveGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ResponsiveGridView(Context context) {
            super(context);
        }

        public ResponsiveGridView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }

}
