package m.mcoupledate.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import m.mcoupledate.R;

/**
 * Created by user on 2016/9/26.
 */
public class GridAlbumAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater mInflater;
    public ArrayList<HashMap<String, Object>> album;


    public GridAlbumAdapter(Context context)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.album = new ArrayList<HashMap<String, Object>>();
    }

    public void add(Bitmap bitmap, String path)
    {
        HashMap<String, Object> pic = new HashMap<String, Object>();
        pic.put("bitmap", bitmap);
        pic.put("path", path);

        this.album.add(pic);

        this.notifyDataSetChanged();
    }

    public void remove(int position)
    {
        this.album.remove(position);
        this.notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return album.size();
    }

    @Override
    public HashMap<String, Object> getItem(int position) {
        return album.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = mInflater.inflate(R.layout.item_gridalbum, null);

        ((SquareImageView) convertView.findViewById(R.id.pic)).setImageBitmap((Bitmap) this.getItem(position).get("bitmap"));

        return convertView;
    }

}
