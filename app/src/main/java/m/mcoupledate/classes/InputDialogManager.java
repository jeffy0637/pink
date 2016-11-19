package m.mcoupledate.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 2016/10/16.
 */
public class InputDialogManager
{
    private Context context;

    protected View dialogContent;
    public AlertDialog dialog;
    public HashMap<String, Object> vars = new HashMap<String, Object>();


    public InputDialogManager(Context context, @LayoutRes int layoutId, String dialogTitle)
    {
        this.context = context;

        this.dialogContent = LayoutInflater.from(context).inflate(layoutId, null);

        initContent();
        initDialog(dialogTitle);
    }

    public InputDialogManager(Context context, @LayoutRes int layoutId, String dialogTitle, String positiveBtnText, String neutralBtnText)
    {
        this.context = context;

        this.dialogContent = LayoutInflater.from(context).inflate(layoutId, null);

        initContent();
        initDialog(dialogTitle, positiveBtnText, neutralBtnText);
    }

    protected void initContent() {}

    public String getInputsData() throws JSONException
    {   return "";      }

    public JSONArray getInputsJSONArr() throws JSONException
    {   return new JSONArray();      }

    public JSONObject getInputsJSONObj() throws JSONException
    {   return new JSONObject();      }

    //return true if the dialog is ready to be dismissed
    protected Boolean onConfirm()
    {   return true;    }

    //return true if the dialog is ready to be dismissed
    protected Boolean onCancel()
    {   return true;    }

    public void printResult(){}

    public void updateContentData(Object... args) {}

    public View dialogFindViewById(@IdRes int id)
    {
        return dialogContent.findViewById(id);
    }


    private void initDialog(String dialogTitle)
    {
        dialog = new AlertDialog.Builder(context)
                .setTitle(dialogTitle)
                .setView(this.dialogContent)
                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        onConfirm();
                    }
                })
                .setNeutralButton("清除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        onCancel();
                    }
                }).create();
    }

    private void initDialog(String dialogTitle, String positiveBtnText, String neutralBtnText)
    {
        dialog = new AlertDialog.Builder(context)
                .setTitle(dialogTitle)
                .setView(this.dialogContent)
                .setPositiveButton(positiveBtnText, null)
                .setNeutralButton(neutralBtnText, null).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog)
            {
                Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if (onConfirm())
                            dialog.dismiss();
                    }
                });

                Button neutralBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        if (onCancel())
                            dialog.dismiss();
                    }
                });
            }
        });
    }

}
