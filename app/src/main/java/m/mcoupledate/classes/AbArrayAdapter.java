package m.mcoupledate.classes;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by user on 2016/9/25.
 */
@Deprecated
public class AbArrayAdapter<S> extends ArrayAdapter{

    ArrayList<String> list;

//    public AbArrayAdapter(Context context, int resource) {
//        super(context, resource);
//
////        this.album = objects;
//    }

//    public AbArrayAdapter(Context context, int resource, int textViewResourceId) {
//        super(context, resource, textViewResourceId);
//
////        this.album = objects;
//    }

    public AbArrayAdapter(Context context, int resource, ArrayList objects) {
        super(context, resource, objects);

        this.list = objects;
    }
//
//    public AbArrayAdapter(Context context, int resource, int textViewResourceId, List objects) {
//        super(context, resource, textViewResourceId, objects);
//
//        this.album = objects;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d("HFCOUNT --- "+String.valueOf(position), this.list.get(position));
        return super.getView(position, convertView, parent);
    }

    public void changeData(ArrayList objects)
    {
        Log.d("HFABARRAYAdapterObject", objects.toString());
        this.list.clear();
        this.list.addAll(objects);
        this.notifyDataSetChanged();
        Log.d("HFABARRAYAdapter", this.list.toString());
    }
}
