package m.mcoupledate.classes.customView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import m.mcoupledate.R;

/**
 * Created by user on 2016/11/22.
 */
@Deprecated
public class SlideTogglableView extends ExpandableListView
{
    private Context context;

    private LinearLayout toggler;

    public SlideTogglableView(Context context)
    {
        super(context, null);
        construct();
    }

    public SlideTogglableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        construct();
    }

    public SlideTogglableView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        construct();
    }

    private void construct()
    {
        this.setAdapter(new SlideTogglableViewAdapter(context));

        this.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

//        LayoutParams mwLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//
//        toggler = new LinearLayout(context);
//        toggler.setLayoutParams(mwLayoutParams);
//        toggler.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
//        toggler.requestLayout();
//
//        ImageView togglerIcon = new ImageView(context);
//        togglerIcon.setImageResource(R.drawable.arrow_seemore);
//        togglerIcon.setLayoutParams(new ViewGroup.LayoutParams(50, 50));
//        togglerIcon.requestLayout();
//
//        toggler.addView(togglerIcon, 0);
//
////        toggler.setOnClickListener(this);
//
//
//        this.addView(toggler, 0);
//
//        requestLayout();

    }




    public class SlideTogglableViewAdapter extends BaseExpandableListAdapter
    {
        private Context context;
        private LayoutInflater mInflater;

        public SlideTogglableViewAdapter(Context context)
        {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
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
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            return null;
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
                convertView = mInflater.inflate(R.layout.item_select_siteclass_title, null);
                groupHolder = new GroupHolder();
                groupHolder.img = (ImageView)convertView.findViewById(R.id.classStatus);
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
            if (convertView==null)
            {
                convertView = mInflater.inflate(R.layout.item_slidetoggableview_detail, null);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return true;
        }





        class GroupHolder
        {
            public TextView txt;

            public ImageView img;
        }
    }


}


