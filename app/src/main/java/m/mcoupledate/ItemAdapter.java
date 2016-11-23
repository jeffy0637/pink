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

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import m.mcoupledate.classes.adapters.StrokeDetailContainerAdapter;
import m.mcoupledate.classes.funcs.Actioner;
import m.mcoupledate.draglib.DragItemAdapter;

import static m.mcoupledate.StrokeActivity.getTripType;

public class ItemAdapter extends DragItemAdapter<Site, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private View headerr;
    String tripType;

    private Context context;

    //Firebase用
    final String url = "https://couple-project.firebaseio.com/travel";
    final String tId = BoardFragment.getTypeFormBoardFragment();

    public ItemAdapter(ArrayList<Site> list, int layoutId, int grabHandleId, boolean dragOnLongPress)
    {
        super(dragOnLongPress);
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);
    }

    public ItemAdapter(View header, ArrayList<Site> list, int layoutId, int grabHandleId, boolean dragOnLongPress)
    {
        super(dragOnLongPress);
        headerr = header;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);
    }

    public ItemAdapter(View header, ArrayList<Site> list, int layoutId, int grabHandleId, boolean dragOnLongPress, Context context)
    {
        super(dragOnLongPress);
        headerr = header;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        setItemList(list);

        this.context = context;
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

        String name = mItemList.get(position).sName;
        holder.mText.setText(name);
        holder.itemView.setTag(name);

        String address = mItemList.get(position).getAddress();
        holder.maddress.setText(address);

        //holder.mStartTime.setText();
        //holder.mEndTime.setText();

        //holder.menu_button.setText("Menu");

    }

    /**
     * 取得list的ID
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getId();
    }

    /**
     * 抓到list的各個元素 TextView會在其他地方操作 button直接在這裡觸發事件
     */
    public class ViewHolder extends DragItemAdapter<Site, ViewHolder>.ViewHolder
    {
        public ImageButton carIcon;
        public TextView mText;
        public Button menu_button;
        public TextView maddress;
        public TextView mStartTime;
        public TextView mEndTime;
        public ExpandableListView detailContainer;

        public ViewHolder(final View itemView)
        {
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

            Actioner journalSubmiter = new Actioner(){
                @Override
                public void act(Object... args)
                {
                    String journalText = (String) args[0];

                }
            };
            StrokeDetailContainerAdapter strokeDetailContainerAdapter = new StrokeDetailContainerAdapter(context, journalSubmiter);
            detailContainer = (ExpandableListView) itemView.findViewById(R.id.detailContainer);
            detailContainer.setAdapter(strokeDetailContainerAdapter);

            strokeDetailContainerAdapter.setJournal("245678yuj89ughbuikyhvbnmiok;lhnjmpuo;lj;dcxs");



            tripType = getTripType();


            /*
            偵測前面有沒有座標
            每次拖拉都要重新計算
             */
            //每個景點的顯示資訊
            mText = (TextView) itemView.findViewById(R.id.text);
            maddress = (TextView) itemView.findViewById(R.id.site_address);
            mStartTime = (TextView) itemView.findViewById(R.id.site_address);
            mEndTime = (TextView) itemView.findViewById(R.id.startTime);
            switch (tripType){
                case "my":
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
                                            //這裡連接firebase
                                            final Firebase myFirebaseRef = new Firebase(url);
                                            ChildEventListener ref = myFirebaseRef.addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                    if((""+dataSnapshot.child("tId").getValue()).equals(tId)){
                                                        if(dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).getChildrenCount() >1){
                                                            //刪view用的 剩一個時getTripType不刪
                                                            removeItem(getPositionForItemId(getItemId()));
                                                            ((TextView) headerr.findViewById(R.id.item_count)).setText("景點數 : " + getItemCount());
                                                            //刪view用的
                                                            //Toast.makeText(v.getContext(), ""+(BoardFragment.getCurrentColumn()+1)+"  "+(getPosition() + 1), Toast.LENGTH_SHORT).show();
                                                            //getPosition() + 1 => 0 1 2 3...
                                                            //BoardFragment.getCurrentColumn() + 1 => 1 2 3...
                                                            Firebase removeSiteRef = dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).child("" + (getPosition() + 1)).getRef();
                                                            removeSiteRef.removeValue();
                                                            if(dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).getChildrenCount() - 1 == 0){//一筆以下的資料 刪除後不用再排序
                                                                //Toast.makeText(v.getContext(), "一筆以下的資料 刪除後不用再排序" + (getPosition() + 1) + (dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).getChildrenCount() - 1), Toast.LENGTH_SHORT).show();
                                                            }
                                                            else if(getPosition() + 1 == dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).getChildrenCount() -1 ){//2筆以上的資料 刪除最後一個不用排序
                                                                //Toast.makeText(v.getContext(), "2筆以上的資料 刪除最後一個不用排序"+ (getPosition() + 1) + (dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).getChildrenCount() - 1), Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{// 其他情況都要新排序
                                                                //Toast.makeText(v.getContext(), "其他情況都要新排序"+ (getPosition() + 1) + (dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).getChildrenCount() - 1), Toast.LENGTH_SHORT).show();
                                                                long childCount = dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).getChildrenCount();

                                                                for(int i = getPosition() + 2 ; i < childCount ; i++) {//從下一個位置開始往下數 都要排序

                                                                    Firebase moveSiteRef = dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).child("" + (i - 1)).getRef();//路徑是前一個
                                                                    String journal = "" + dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).child("" + i).child("journal").getValue();
                                                                    String sId = "" + dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).child("" + i).child("sId").getValue();
                                                                    String time = "" + dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).child("" + i).child("times").getValue();
                                                                    //Log.v("debug3", sId + " "+time);
                                                                    Site site = new Site(i - 1, journal, Long.parseLong(sId), Long.parseLong(time));//存到前一個位置
                                                                    moveSiteRef.setValue(site);
                                                                }
                                                                //刪除最後一個
                                                                Firebase moveLastSiteRef = dataSnapshot.child("site").child("day" + (BoardFragment.getCurrentColumn() + 1)).child("" + (childCount - 1)).getRef();
                                                                moveLastSiteRef.removeValue();
                                                            }
                                                        }
                                                        else//剩一個時不給刪 不然那天會不見 之後應該改天數的刪除插入對firebase的讀取
                                                            Toast.makeText(v.getContext(), "只剩一個景點囉 請更換其他景點", Toast.LENGTH_SHORT).show();
                                                    }

                                                }

                                                @Override
                                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {//沒反應
                                                     }

                                                @Override
                                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                    //沒反應

//                                            removeItem(getPositionForItemId(getItemId()));
//                                            ((TextView) headerr.findViewById(R.id.item_count)).setText("景點數 : " + getItemCount());
                                                }

                                                @Override
                                                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                                                @Override
                                                public void onCancelled(FirebaseError firebaseError) { }
                                            });
                                            //停止監控 下次刪除才會重頭算
                                            myFirebaseRef.removeEventListener(ref);
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
                    break;
                case "collection":
                case "search":
                    menu_button = (Button) itemView.findViewById(R.id.menu_button);
                    menu_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            PopupMenu popup = new PopupMenu(v.getContext(), v);//第二个参数是绑定的那个view
                            MenuInflater inflater = popup.getMenuInflater();
                            inflater.inflate(R.menu.menu_button_not_my, popup.getMenu());
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.journal:
                                            Toast.makeText(v.getContext(), "遊記···", Toast.LENGTH_SHORT).show();
                                            break;
                                        case R.id.picture:
                                            Toast.makeText(v.getContext(), "相簿···", Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popup.show();
                        }
                    });
                    break;
            }

        }

        /**
         * list的觸發事件(短按)
         * @param view
         */
        @Override
        public void onItemClicked(View view) {
            //傳判定值
            //intent.putExtra("from","search");
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
