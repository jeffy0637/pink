package m.mcoupledate.classes.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import m.mcoupledate.R;

/**
 * Created by user on 2016/9/28.
 */
public class RecyclerAlbumAdapter extends RecyclerView.Adapter<RecyclerAlbumAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    public ArrayList<HashMap<String, Object>> album;

    private int albumWidth, albumHeight;

    public RecyclerAlbumAdapter(Context context, int albumHeight)
    {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.album = new ArrayList<HashMap<String, Object>>();

        this.albumWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        this.albumWidth = albumHeight;
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


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.item_gridalbum,viewGroup,false);

//        view.setLayoutParams(new RecyclerView.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels, RecyclerView.LayoutParams.MATCH_PARENT));
//        Log.d("HFVIEWHEIGHT", String.valueOf(Resources.getSystem().getDisplayMetrics().widthPixels) + String.valueOf(this.albumHeight));
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

//        viewHolder.pic.setImageMatrix();
        viewHolder.pic.setImageBitmap((Bitmap) this.album.get(position).get("bitmap"));
    }

    @Override
    public int getItemCount() {
        return album.size();
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView pic;

        public ViewHolder(View view){
            super(view);
            pic = (ImageView) view.findViewById(R.id.pic);
        }

    }
}
