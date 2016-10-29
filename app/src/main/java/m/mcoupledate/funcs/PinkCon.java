package m.mcoupledate.funcs;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

/**
 * Created by user on 2016/10/5.
 */
public class PinkCon
{
    public static final String INIT_FAIL = "抱歉，暫時無法載入";
    public static final String SUBMIT_FAIL = "抱歉，暫時無法送出";
    public static final String CONNECT_FAIL = "抱歉，暫時無法連線";
    public static final String SEARCH_FAIL = "抱歉，暫時無法載入搜尋結果";
    public static final String LOAD_PINKSITES_FAIL = "抱歉，暫時無法載入地圖景點";

    public static final String URL = "http://140.117.71.216/pinkCon/";

    public static void retryConnect(View rootView, String msg, InitErrorBar initErrorBar, View.OnClickListener onClickListener)
    {
        if ((initErrorBar!=null && !initErrorBar.isShown()) || initErrorBar==null)
            Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE).setAction("再試一次", onClickListener).show();
    }

    public static void retryConnect(View rootView, String msg, InitErrorBar initErrorBar, String errTitle, String errMsg, View.OnClickListener onClickListener)
    {
        Log.d(errTitle, errMsg);

        if ((initErrorBar!=null && !initErrorBar.isShown()) || initErrorBar==null)
            Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE).setAction("再試一次", onClickListener).show();
    }

    public static InitErrorBar getInitErrorSnackBar(View rootView, String msg, final Activity activity)
    {
        return new InitErrorBar(
                Snackbar.make(rootView, msg, Snackbar.LENGTH_INDEFINITE).setAction("重新整理", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        Intent reloadIntent = activity.getIntent();
                        if (reloadIntent!=null)
                            reloadIntent.setClass(activity, activity.getClass());
                        else
                            reloadIntent = new Intent(activity, activity.getClass());

                        reloadIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        activity.overridePendingTransition(0, 0);
                        activity.startActivity(reloadIntent);
                        activity.finish();
                    }
                })
        );
    }

    public static class InitErrorBar
    {
        private Snackbar snackbar;

        public InitErrorBar(Snackbar snackbar)
        {   this.snackbar = snackbar;   }

        public void show(String errorTitle, String errorMsg)
        {
            if (!snackbar.isShown())
                snackbar.show();

            try
            {
                if (errorMsg!=null && errorMsg.compareTo("")!=0)
                    Log.d(errorTitle, errorMsg);
                else
                    Log.d(errorTitle, "nullMessage");
            }
            catch (Exception e)
            {
                Log.d("HFcannotPrintError", errorTitle);
            }
        }

        public void show()
        {
            if (!snackbar.isShown())
                snackbar.show();
        }

        protected Boolean isShown()
        {
            return snackbar.isShown();
        }
    }


    /*
                PinkCon.retryConnect(getRootView(), PinkCon.INIT_FAIL, initErrorBar,
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {   initSiteInfoFromMariDB();  }
                    });

     */
}
