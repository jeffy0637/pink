package m.mcoupledate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {

    private List<Stroke> mData;//定义数据。
    private LayoutInflater mInflater;//定义Inflater,加载我们自定义的布局。

    /*
    定义构造器，在Activity创建对象Adapter的时候将数据data和Inflater传入自定义的Adapter中进行处理。
    */
    public MyAdapter(LayoutInflater inflater,List<Stroke> data){
        mInflater = inflater;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Stroke getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
        //获得ListView中的view
        View viewStudent = mInflater.inflate(R.layout.trip_item,null);
        //获得学生对象
        Stroke stroke = mData.get(position);
        //获得自定义布局中每一个控件的对象。

        TextView title = (TextView) viewStudent.findViewById(R.id.travel_name);
        TextView starttime = (TextView) viewStudent.findViewById(R.id.travel_date);
        //TextView endtime = (TextView) viewStudent.findViewById(R.id.textview_sex);

        //将数据一一添加到自定义的布局中。

        title.setText(stroke.getTitle());
        starttime.setText(stroke.getStarttime());
        //endtime.setText(stroke.getEndtime());

        return viewStudent ;
    }

    public void addItem(Stroke i){
        mData.add(i);
    }
    public void removeItem(int index){
        //remove
    }


}