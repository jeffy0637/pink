/**
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package m.mcoupledate;
import android.support.v4.util.Pair;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import java.util.ArrayList;

public class ItemAdapter extends m.mcoupledate.DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private View headerr;

    public ItemAdapter(ArrayList<Pair<Long, String>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);
    }
    public ItemAdapter(View header, ArrayList<Pair<Long, String>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        super(dragOnLongPress);
        headerr = header;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 取得list的String並存入TextView
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String text = mItemList.get(position).second;
        holder.mText.setText(text);
        holder.itemView.setTag(text);

        holder.menu_button.setText("Menu");

    }

    /**
     * 取得list的ID
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return mItemList.get(position).first;
    }

    /**
     * 抓到list的各個元素 TextView會在其他地方操作 button直接在這裡觸發事件
     */
    public class ViewHolder extends m.mcoupledate.DragItemAdapter<Pair<Long, String>, ViewHolder>.ViewHolder {
        public ImageButton carIcon;
        public TextView mText;
        public Button menu_button;

        public ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId);
            //lil linearlayout的
            carIcon = (ImageButton) itemView.findViewById(R.id.carIcon);
            carIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //carIcon.setVisibility(v.INVISIBLE);
                    //把點擊carIcon後的動作寫在這裡
                }
            });
            /*
            偵測前面有沒有座標
            每次拖拉都要重新計算
             */
            //ll linearlayout的
            mText = (TextView) itemView.findViewById(R.id.text);

            menu_button = (Button) itemView.findViewById(R.id.menu_button);
            menu_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu popup = new PopupMenu(v.getContext(), v);//第二个参数是绑定的那个view
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_button, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.journal:
                                    Toast.makeText(v.getContext(), "遊記···", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.delete:
                                    removeItem(getPositionForItemId(getItemId()));
                                    ((TextView) headerr.findViewById(R.id.item_count)).setText("景點數 : " + getItemCount());
                                    //Toast.makeText(v.getContext(), "删除···", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(v.getContext(), "目前"+getItemCount() + "個點", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.picture:
                                    Toast.makeText(v.getContext(), "相簿···", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.change_site:
                                    Toast.makeText(v.getContext(), "換景點···", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }

        /**
         * list的觸發事件(短按)
         * @param view
         */
        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "進入景點", Toast.LENGTH_SHORT).show();
        }

        /**
         * list的觸發事件(長按)
         * @param view
         * @return
         */
        @Override
        public boolean onItemLongClicked(View view) {

            Toast.makeText(view.getContext(), "拖拉排序", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
