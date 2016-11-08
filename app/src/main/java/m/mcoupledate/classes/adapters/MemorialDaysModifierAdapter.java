package m.mcoupledate.classes.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import m.mcoupledate.R;
import m.mcoupledate.classes.InputDialogManager;
import m.mcoupledate.classes.customView.DateInputEditText;
import m.mcoupledate.funcs.Actioner;


/**
 * Created by user on 2016/11/8.
 */

public class MemorialDaysModifierAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private ArrayList<MemorialDay> memorialDaysList;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private InputDialogManager modifierDialogManager;
    private DatePickerDialog datePickerDialog;

    private AlertDialog ifDeleteDialog;
    private int nowPosition;

    public final static int UPDATEMEMORIALDAYTYPE_NEW = 8341, UPDATEMEMORIALDAYTYPE_UPDATE = 8342, UPDATEMEMORIALDAYTYPE_DELETE = 8343;



    public MemorialDaysModifierAdapter(final Context context, final ArrayList<MemorialDay> memorialDaysList, final Actioner memorialDayUpdater)
    {
        this.mInflater = LayoutInflater.from(context);
        this.memorialDaysList = memorialDaysList;

        final Calendar calendar = Calendar.getInstance();;
        this.modifierDialogManager = new InputDialogManager(context, R.layout.dialog_memorialday_modifier, "編輯紀念日", "完成", "取消")
        {
            @Override
            protected void initContent()
            {
                EditText name = (EditText) dialogFindViewById(R.id.name);
                final DateInputEditText date = (DateInputEditText) dialogFindViewById(R.id.date);

                vars.put("name", name);
                vars.put("date", date);

                vars.put("position", null);



                datePickerDialog= new DatePickerDialog(context,
                    new DatePickerDialog.OnDateSetListener()
                    {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                        {
                            String monthOfYearStr = String.valueOf(monthOfYear+1);
                            String dayOfMonthStr = String.valueOf(dayOfMonth);

                            if ((monthOfYear+1)<10)
                                monthOfYearStr = "0" + monthOfYearStr;

                            if (dayOfMonth<10)
                                dayOfMonthStr = "0" + dayOfMonthStr;

                            date.setText(year + "/" + monthOfYearStr + "/" + dayOfMonthStr);
                        }
                    }
                    , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                );

                ImageButton dateEditBtn = (ImageButton) dialogFindViewById(R.id.dateEditBtn);
                dateEditBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {   datePickerDialog.show();    }
                });

            }
            @Override
            protected Boolean onConfirm()
            {
                String eventName = ((EditText) vars.get("name")).getText().toString();
                String eventDate = ((DateInputEditText) vars.get("date")).getValue();

                if (((int) vars.get("position"))!=-1)
                {
                    MemorialDay theDay = getItem((int) vars.get("position"));

                    if (eventDate!=null && eventDate.compareTo("")!=0 && eventName.compareTo("")!=0)
                    {
                        memorialDayUpdater.act(UPDATEMEMORIALDAYTYPE_UPDATE, eventName, eventDate, theDay.name, theDay.date);

                        theDay.name = eventName;
                        theDay.date = eventDate;

                        notifyDataSetChanged();

                        return true;
                    }
                    else
                    {
                        Toast.makeText(context, "請正確填寫名稱與日期", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else
                {
                    MemorialDay theDay = new MemorialDay();

                    if (eventDate!=null && eventDate.compareTo("")!=0 && eventName.compareTo("")!=0)
                    {
                        theDay.name = eventName;
                        theDay.date = eventDate;

                        add(theDay);

                        memorialDayUpdater.act(UPDATEMEMORIALDAYTYPE_NEW, theDay.name, theDay.date);
                        return true;
                    }
                    else
                    {
                        Toast.makeText(context, "請正確填寫名稱與日期", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

            }

            @Override
            public void updateContentData(Object... args)
            {
                vars.put("position", args[0]);

                ((EditText) vars.get("name")).setText((String) args[1]);
                ((DateInputEditText) vars.get("date")).setText(((String) args[2]).replace("-", "/"));


                if (((int)args[0])!=-1)
                {
                    String[] dateArr = ((String) args[2]).split("-");
                    datePickerDialog.updateDate(Integer.valueOf(dateArr[0]), (Integer.valueOf(dateArr[1])-1), Integer.valueOf(dateArr[2]));
                }
                else
                {
                    datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }

            }
        };

        ifDeleteDialog = new AlertDialog.Builder(context)
            .setTitle("確定刪除?")
            .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    MemorialDay theDay = getItem(nowPosition);
                    memorialDayUpdater.act(UPDATEMEMORIALDAYTYPE_DELETE, theDay.name, theDay.date);

                    memorialDaysList.remove(nowPosition);
                    notifyDataSetChanged();
                }
            })
            .setNeutralButton("取消", null)
            .create();
    }

    @Override
    public int getCount()
    {
        return memorialDaysList.size();
    }

    @Override
    public MemorialDay getItem(int arg0)
    {
        return memorialDaysList.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView==null)
            convertView = mInflater.inflate(R.layout.item_memorialday_modifier, null);

        final MemorialDay theDay = getItem(position);


        ((TextView) convertView.findViewById(R.id.mDateName)).setText(theDay.name);
        ((TextView) convertView.findViewById(R.id.mDate)).setText(theDay.date.replace("-", "/"));
        ((TextView) convertView.findViewById(R.id.diffTime)).setText(calDiffDays(theDay.date));

        ImageButton editBtn = (ImageButton) convertView.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                modifierDialogManager.updateContentData(position, theDay.name, theDay.date);
                modifierDialogManager.dialog.show();

            }
        });

        ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                nowPosition = position;
                ifDeleteDialog.show();
            }
        });


        return convertView;
    }

    private String calDiffDays(String date)
    {
        Date d1 = null;
        try
        {   d1 = formatter.parse(date);  }
        catch (ParseException e)
        {   e.printStackTrace();    }

        Date d2 = new Date();

        return  String.valueOf((d2.getTime() - d1.getTime()) / (1000*60*60*24));
    }

    private void add(MemorialDay theDay)
    {
        int a = 0;
        for (MemorialDay aDay : memorialDaysList)
        {
            if (aDay.date.compareTo(theDay.date)>0)
            {
                memorialDaysList.add(a, theDay);
                notifyDataSetChanged();
                return ;
            }
            ++a;
        }

        memorialDaysList.add(theDay);
        notifyDataSetChanged();
    }

    public void addByUser()
    {
        modifierDialogManager.updateContentData(-1, "", "");
        modifierDialogManager.dialog.show();
    }


    public void changeData(ArrayList<MemorialDay> memorialDaysList)
    {
        this.memorialDaysList = memorialDaysList;
        notifyDataSetChanged();
    }


    public static class MemorialDay
    {
        public String name, date;

        public MemorialDay()
        {}

        public MemorialDay(String name, String date)
        {
            this.name = name;
            this.date = date;
        }
    }

}
