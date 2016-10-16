package m.mcoupledate.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by user on 2016/10/16.
 */
public class InputDialogManager
{
        private Context context;

        protected View dialogContent;
        public AlertDialog dialog;

        protected HashMap<String, Object> vars = new HashMap<String, Object>();


        public InputDialogManager(Context context, @LayoutRes int layoutId, String dialogTitle)
        {
            this.context = context;

            this.dialogContent = LayoutInflater.from(context).inflate(layoutId, null);

            initContent();
            initDialog(dialogTitle);
        }

        protected void initContent() {}

        public String getInputsData() throws JSONException
        {   return "";      }

        protected void onCancel() {}


        public void initDialog(String dialogTitle)
        {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(dialogTitle)
                    .setView(this.dialogContent)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            onCancel();
                        }
                    }).create();
        }
}
