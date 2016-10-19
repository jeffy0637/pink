package m.mcoupledate.classes.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by user on 2016/10/16.
 */
public class TimeInputEditText extends EditText
{
    private String tempText = null;
    private String stdTimePattern = "(([0-1][0-9]|2[0-3])):[0-5][0-9]";
    private String noPreZeroTimePattern = "[0-9]:[0-5][0-9]";
    private String noColonTimePattern = "(([0-1][0-9]|2[0-3]))[0-5][0-9]";
    private String noColonPreZeroTimePattern = "[0-9][0-5][0-9]";
    private TimeInputEditText input = this;
//    private double max = 0.0, min = 23.59;
//    private TimeInputEditText partnerInput;
//    private Boolean ifStartTime;


    public TimeInputEditText(Context context) {
        super(context);
//        setPartnerInput(context);
        setTimeFormatChecker();
    }

    public TimeInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setPartnerInput(context);
        setTimeFormatChecker();
    }

    public TimeInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setPartnerInput(context);
        setTimeFormatChecker();
    }

    private void setTimeFormatChecker()
    {
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused)
            {
                String text = input.getText().toString();
                if (focused)
                {
                    tempText = text;
                }
                else
                {
                    if (text.matches(stdTimePattern))
                    {   }
                    else if (text.matches(noPreZeroTimePattern))
                    {
                       text = "0"+text;
                    }
                    else if (text.matches(noColonTimePattern))
                    {
                        text = text.substring(0,2)+":"+text.substring(2,4);
                    }
                    else if (text.matches(noColonPreZeroTimePattern))
                    {
                        text = "0"+text.charAt(0)+":"+text.substring(1,3);
                    }
                    else
                    {
                        text = tempText;
                    }


//                    if (text!=null && !checkMaxMin(text))
//                    {
//                        text = tempText;
//                    }

                    input.setText(text);


//                    if (text!=null)
//                    {
//                        if (ifStartTime)
//                            partnerInput.setMin(text);
//                        else
//                            partnerInput.setMax(text);
//                    }
                }
            }
        });
    }

//    private Boolean checkMaxMin(String text)
//    {
//        Double textDouble = Double.valueOf(text.replace(":", "."));
//        if (textDouble>=min && textDouble<=max)
//            return true;
//        else
//            return false;
//    }

//    public void setMax(String text)
//    {
//        max = Double.valueOf(text.replace(":", "."));
//    }
//
//    public void setMin(String text)
//    {
//        min = Double.valueOf(text.replace(":", "."));
//    }


//    private void setPartnerInput(Context context)
//    {
//        if (input.getHint().toString().compareTo(context.getResources().getString(R.string.startTimeHint))==0)
//        {
//            partnerInput = (TimeInputEditText) ((LinearLayout) input.getParent()).getChildAt(3);
//            ifStartTime = true;
//        }
//        else
//        {
//            partnerInput = (TimeInputEditText) ((LinearLayout) input.getParent()).getChildAt(1);
//            ifStartTime = false;
//        }
//
//    }

}
