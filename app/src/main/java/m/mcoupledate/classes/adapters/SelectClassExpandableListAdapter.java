package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import m.mcoupledate.R;
import m.mcoupledate.classes.DropDownMenu.ConstellationAdapter;
import m.mcoupledate.classes.customView.ResponsiveGridView;

/**
 * Created by user on 2016/10/27.
 */

public class SelectClassExpandableListAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<HashMap<String, Object>> classes;
    private ArrayList<View> childViews = new ArrayList<View>();

    private SparseArray<ConstellationAdapter> rGridViewAdapters = new SparseArray<>();


    public static final int SELECTCLASS_CITY = 0, SELECTCLASS_AREA = 1, SELECTCLASS_TIME = 2, SELECTCLASS_FOODKIND = 3, SELECTCLASS_COUNTRY = 4;
    private String[] classNames = {"大行政區", "小行政區", "時段", "種類", "口味"};



    public SelectClassExpandableListAdapter(Context context, ArrayList<HashMap<String, Object>> classes)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.classes = classes;

        for (final HashMap aClass : classes)
        {
            View view = mInflater.inflate(R.layout.item_select_siteclass_content, null);

            final ResponsiveGridView rGirdView = (ResponsiveGridView) view.findViewById(R.id.classOptions);

            final ConstellationAdapter rGridViewAdapter = new ConstellationAdapter(context, ((ArrayList<String>)aClass.get("classOptions")), ((int)aClass.get("optionSelectType")));

            rGridViewAdapters.put(((int) aClass.get("classType")), rGridViewAdapter);


            rGirdView.setAdapter(rGridViewAdapter);
            rGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    rGridViewAdapter.setCheckItem(position);
                    refreshOptions(((int) aClass.get("classType")), rGridViewAdapters.get(SELECTCLASS_AREA), rGridViewAdapter.getItem(position));

                }
            });


            childViews.add(view);
        }
    }


    @Override
    public int getGroupCount()
    {
        return classes.size();
    }


    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }


    @Override
    public Object getGroup(int groupPosition)
    {
        return classes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return classes.get(groupPosition);
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
            groupHolder.txt = (TextView) convertView.findViewById(R.id.className);
            groupHolder.img = (ImageView)convertView.findViewById(R.id.classStatus);
            convertView.setTag(groupHolder);
        }
        else
        {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        if (!isExpanded)
        {
            groupHolder.img.setImageResource(R.drawable.selected);
        }
        else
        {
            groupHolder.img.setImageResource(R.drawable.unselected);
        }

        groupHolder.txt.setText(classNames[(int) classes.get(groupPosition).get("classType")]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        return childViews.get(groupPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }


    protected void refreshOptions(int classType, ConstellationAdapter rGridViewAdapter, String param) {}

    public JSONObject getClassesJSONObj() throws JSONException {
        JSONObject classesValues = new JSONObject();

        for (int a=0; a<rGridViewAdapters.size(); ++a)
        {
            classesValues.put(String.valueOf(rGridViewAdapters.keyAt(a)), rGridViewAdapters.valueAt(a).getCheckedListJSONArray());
        }

//        for (rGridViewAdapters.)
        return classesValues;
    }




    class GroupHolder
    {
        public TextView txt;

        public ImageView img;
    }
}
