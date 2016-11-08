package m.mcoupledate.classes.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by user on 2016/11/6.
 */

public class DateInputEditText extends EditText
{
    private String tempText = null;
    private final String slashPattern = "[/]+";
    private final String otherMarkPattern = "[.-]+";
    private final String yPattern = "(19[0-9]{2})|(20[0-9]{2})";  // 1900~2099
    private String ym1Pattern = "(" + yPattern + ")/" + "([1-9])/";
    private String ym2Pattern = "(" + yPattern + ")/" + "((0[1-9])|(1[0-2]))";
    private final String ymdStdPattern = "((19[0-9]{2})|(20[0-9]{2}))" + "/" + "((0[1-9])|(1[0-2]))" + "/" + "((0[1-9])|([12][0-9])|(3[01]))";
    private final String ym1dPattern = "((19[0-9]{2})|(20[0-9]{2}))" + "/" + "([1-9])" + "/" + "((0[1-9])|([12][0-9])|(3[01]))";
    private final String ymd1Pattern = "((19[0-9]{2})|(20[0-9]{2}))" + "/" + "((0[1-9])|(1[0-2]))" + "/" + "([1-9])";
    private final String ym1d1Pattern = "((19[0-9]{2})|(20[0-9]{2}))" + "/" + "([1-9])" + "/" + "([1-9])";
//    private String ym1dPattern =  "("+ym1Pattern+")/" + "(([0-9])|([0-2][0-9])|3[0-1])";
//    private String ymd1Pattern = "(("+ym1Pattern+")|("+ym2Pattern+"))/" + "[0-9]";

    private Boolean ifValidating = false;
    private String prevValue = "";
    private DateInputEditText input = this;


    public DateInputEditText(Context context)
    {
        super(context);

        setDateFormatChecker();
    }

    public DateInputEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setDateFormatChecker();
    }

    public DateInputEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        setDateFormatChecker();
    }

    private void setDateFormatChecker()
    {


//        input.addTextChangedListener(new TextWatcher()
//        {
//            @Override
//            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2)
//            {
//
//
//                if (ifValidating)
//                    return ;
//
//                String dateValue = input.getText().toString();
//
//
//                if (dateValue.length()<prevValue.length())
//                {
//                    if (prevValue.endsWith("/"))
//                    {
//                        dateValue = dateValue.substring(0, (dateValue.length()-1));
//                    }
//                    else
//                    {
//                        prevValue = dateValue;
//                        return ;
//                    }
//                }
//                else if (dateValue.matches(yPattern))
//                {
//                    dateValue += "/";
//                }
//                else if (dateValue.matches(ym1Pattern))
//                {
//                    dateValue = dateValue.split("/")[0] + "/0" + dateValue.split("/")[1] + "/";
//                }
//                else if (dateValue.matches(ym2Pattern))
//                {
//                    dateValue += "/";
//                }
////                else if (dateValue.matches(ymd1Pattern))
////                {
////                    dateValue = dateValue.split("/")[0] + "/" + dateValue.split("/")[1] + "/0" + dateValue.split("/")[2];
////                }
//                else if (dateValue.length()>10)
//                {
//                    dateValue = dateValue.substring(0,10);
//                }
//
//
//                dateValue = dateValue.replaceAll(otherMarkPattern, "/");
//                dateValue = dateValue.replaceAll(slashPattern, "/");
//
//
//                ifValidating = true;
//
//                input.setText(dateValue);
//                input.setSelection(dateValue.length());
//
//                prevValue = dateValue;
//
//                ifValidating = false;
//
//            }
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });


        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused)
            {
                String text = input.getText().toString();

                if (focused)
                    tempText = text;
                else
                    input.setText(patternizeValue(text));

            }
        });
    }

    private String patternizeValue(String text)
    {
        text = text.replaceAll(otherMarkPattern, "/");
        text = text.replaceAll(slashPattern, "/");

        String[] splits;


        if (text.matches(ymdStdPattern) || text.compareTo("")==0)
        {   }
        else if (text.matches(ym1dPattern))
        {
            splits = text.split("/");
            text = splits[0] + "/0" + splits[1] + "/" + splits[2];
        }
        else if (text.matches(ymd1Pattern))
        {
            splits = text.split("/");
            text = splits[0] + "/" + splits[1] + "/0" + splits[2];
        }
        else if (text.matches(ym1d1Pattern))
        {
            splits = text.split("/");
            text = splits[0] + "/0" + splits[1] + "/0" + splits[2];
        }
        else
        {
            text = tempText;
        }

        return text;
    }


    public String getValue()
    {
        String value = patternizeValue(input.getText().toString());
        input.setText(value);

        if (value.matches(ymdStdPattern) || value.compareTo("")==0)
            return value.replace("/", "-");
        else
            return null;

    }

}
