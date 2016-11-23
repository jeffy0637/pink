package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import m.mcoupledate.R;
import m.mcoupledate.classes.funcs.Actioner;

/**
 * Created by user on 2016/10/27.
 */

public class StrokeDetailContainerAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private LayoutInflater mInflater;

    private View detailContainer;

    private TextView journal;
    private EditText journalEditMode;
    private ImageButton journalEditBtn;

    private Actioner journalSubmiter;

    public StrokeDetailContainerAdapter(Context context, final Actioner journalSubmiter)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);

        this.journalSubmiter = journalSubmiter;



        detailContainer = mInflater.inflate(R.layout.item_slidetoggableview_detail, null);

        journal = (TextView) detailContainer.findViewById(R.id.journal);
        journalEditMode = (EditText) detailContainer.findViewById(R.id.journalEditMode);

        journalEditBtn = (ImageButton) detailContainer.findViewById(R.id.journalEditBtn);
        journalEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (journalEditMode.getVisibility()==View.GONE)
                {
                    journalEditMode.setText(journal.getText());

                    journal.setVisibility(View.GONE);
                    journalEditMode.setVisibility(View.VISIBLE);

//                    journalEditMode.setSelection(journalEditMode.getText().length()-1);
                }
                else
                {
                    journal.setText(journalEditMode.getText());

                    journalEditMode.setVisibility(View.GONE);
                    journal.setVisibility(View.VISIBLE);

                    journalSubmiter.act(journalEditMode.getText().toString());
                }
            }
        });
    }


    @Override
    public int getGroupCount()
    {
        return 1;
    }


    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }


    @Override
    public Object getGroup(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        GroupHolder groupHolder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.item_slidetoggableview_togglericon, null);
            groupHolder = new GroupHolder();
            groupHolder.img = (ImageView)convertView.findViewById(R.id.togglerIcon);
            convertView.setTag(groupHolder);
        }
        else
        {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        if (!isExpanded)
        {
            groupHolder.img.setImageResource(R.drawable.arrow_seemore);
        }
        else
        {
            groupHolder.img.setImageResource(R.drawable.arrow_seeless);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        return detailContainer;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }


    public void setJournal(String journal)
    {
        this.journal.setText(journal);
    }


    class GroupHolder
    {
        public ImageView img;
    }
}
