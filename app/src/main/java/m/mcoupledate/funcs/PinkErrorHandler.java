package m.mcoupledate.funcs;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by user on 2016/10/5.
 */
public class PinkErrorHandler {

    public static void retryConnect(View view, View.OnClickListener onClickListener)
    {
        Snackbar.make(view, "抱歉，暫時無法連線", Snackbar.LENGTH_INDEFINITE).setAction("重新整理", onClickListener).show();
    }
}
