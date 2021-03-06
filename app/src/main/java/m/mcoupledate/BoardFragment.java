/**
 * Copyright 2014 Magnus Woxblom
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package m.mcoupledate;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.classes.funcs.PinkCon;
import m.mcoupledate.draglib.BoardView;
import m.mcoupledate.draglib.DragItem;

public class BoardFragment extends Fragment {

    private Context context;

    private RequestQueue mQueue;

    private static int sCreatedItems = 0;
    private static BoardView mBoardView;
    private int mColumns;

    Intent intent;
    private String tripType;
    private String tripTId;
    private static String startHour,startMin;

    private static String tripTIdForSend;

    //Firebase用
    final String url = "https://couple-project.firebaseio.com/travel";


    private int hourOfDay, minute;

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    public static String getTypeFormBoardFragment(){
        return tripTIdForSend;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getActivity();

        mQueue = Volley.newRequestQueue(context);

        tripType = (String)getArguments().get("TT");
        tripTId = (String)getArguments().get("TI");
        tripTIdForSend = tripTId;

        Calendar calendar = Calendar.getInstance();
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        //測試
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_layout, container, false);

        mBoardView = (BoardView) view.findViewById(R.id.board_view);
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setCustomDragItem(new MyDragItem(getActivity(), R.layout.column_item));
        mBoardView.setBoardListener(new BoardView.BoardListener() {
            @Override
            public void onItemDragStarted(int column, int row) {
                //Toast.makeText(mBoardView.getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
                //對firebase的這筆資料做鎖定
            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {
                TextView itemCount1 = (TextView) mBoardView.getHeaderView(oldColumn).findViewById(R.id.item_count);
                itemCount1.setText(Integer.toString(mBoardView.getAdapter(oldColumn).getItemCount()));
                TextView itemCount2 = (TextView) mBoardView.getHeaderView(newColumn).findViewById(R.id.item_count);
                itemCount2.setText(Integer.toString(mBoardView.getAdapter(newColumn).getItemCount()));
            }

            @Override
            public void onItemDragEnded(final int fromColumn, final int fromRow, final int toColumn, final int toRow) {
                if (fromColumn != toColumn || fromRow != toRow) {
                    //Toast.makeText(mBoardView.getContext(), "Start - column: " + fromColumn + " row: " + fromRow, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(mBoardView.getContext(), "End - column: " + toColumn + " row: " + toRow, Toast.LENGTH_SHORT).show();
                    //被插的重新排序(往下一格)
                    Firebase.setAndroidContext(mBoardView.getContext());//this用mBoardView.getContext()取代
                    new Firebase(url).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if((""+dataSnapshot.child("tId").getValue()).equals(tripTId)){
                                if(fromColumn == toColumn){//同一天插入景點
                                    //要先暫存插人的
                                    String tempjournal = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + fromRow).child("journal").getValue();
                                    String tempsId = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + fromRow).child("sId").getValue();
                                    String temptime = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + fromRow).child("times").getValue();
                                    if(fromRow > toRow){
                                        for(int i = fromRow - 1 ; i >= toRow ; i--){
                                            Firebase dragSiteRef = dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + (i + 1)).getRef();//路徑是後一個
                                            String journal = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("journal").getValue();
                                            String sId = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("sId").getValue();
                                            String time = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("times").getValue();
                                            //Log.v("debug3", i + " " + journal + " " + sId + " "+time);
                                            Site site = new Site(i + 1, journal, Long.parseLong(sId), Long.parseLong(time));//存到後一個位置
                                            dragSiteRef.setValue(site);
                                        }
                                    }
                                    else{
                                        for(int i = fromRow + 1 ; i <= toRow ; i ++){
                                            Firebase dragSiteRef = dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + (i - 1)).getRef();//路徑是後一個
                                            String journal = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("journal").getValue();
                                            String sId = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("sId").getValue();
                                            String time = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("times").getValue();
                                            //Log.v("debug3", i + " " + journal + " " + sId + " "+time);
                                            Site site = new Site(i - 1, journal, Long.parseLong(sId), Long.parseLong(time));//存到後一個位置
                                            dragSiteRef.setValue(site);
                                        }
                                    }
                                    //把插人的存入
                                    Firebase dragSiteRef = dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + toRow).getRef();//路徑是插入點
                                    Site site = new Site(toRow, tempjournal, Long.parseLong(tempsId), Long.parseLong(temptime));//存到後一個位置
                                    dragSiteRef.setValue(site);
                                    //不用刪除插人的 因為已經被覆蓋
                                }
                                else{//不同天插入景點
                                    //把被插的那天重新排序
                                    for(int i = (int)dataSnapshot.child("site").child("day" + (toColumn + 1)).getChildrenCount() - 1 ; i >= toRow ; i--){
                                        Firebase dragSiteRef = dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + (i + 1)).getRef();//路徑是後一個
                                        String journal = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("journal").getValue();
                                        String sId = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("sId").getValue();
                                        String time = "" + dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + i).child("times").getValue();
                                        //Log.v("debug3", i + " " + journal + " " + sId + " "+time);
                                        Site site = new Site(i + 1, journal, Long.parseLong(sId), Long.parseLong(time));//存到後一個位置
                                        dragSiteRef.setValue(site);
                                    }
                                    //把插人的存入
                                    Firebase dragSiteRef = dataSnapshot.child("site").child("day" + (toColumn + 1)).child("" + toRow).getRef();//路徑是插入點
                                    String journal = "" + dataSnapshot.child("site").child("day" + (fromColumn + 1)).child("" + fromRow).child("journal").getValue();
                                    String sId = "" + dataSnapshot.child("site").child("day" + (fromColumn + 1)).child("" + fromRow).child("sId").getValue();
                                    String time = "" + dataSnapshot.child("site").child("day" + (fromColumn + 1)).child("" + fromRow).child("times").getValue();
                                    Site site = new Site(toRow, journal, Long.parseLong(sId), Long.parseLong(time));//存到後一個位置
                                    dragSiteRef.setValue(site);
                                    //插人的那天重新排序
                                    for(int i = fromRow + 1 ; i < (int)dataSnapshot.child("site").child("day" + (fromColumn + 1)).getChildrenCount() ; i++){
                                        Firebase dragSiteRef2 = dataSnapshot.child("site").child("day" + (fromColumn + 1)).child("" + (i - 1)).getRef();//路徑是前一格
                                        String journal2 = "" + dataSnapshot.child("site").child("day" + (fromColumn + 1)).child("" + i).child("journal").getValue();
                                        String sId2 = "" + dataSnapshot.child("site").child("day" + (fromColumn + 1)).child("" + i).child("sId").getValue();
                                        String time2 = "" + dataSnapshot.child("site").child("day" + (fromColumn + 1)).child("" + i).child("times").getValue();
                                        Site site2 = new Site(i - 1, journal2, Long.parseLong(sId2), Long.parseLong(time2));//存到後一個位置
                                        dragSiteRef2.setValue(site2);
                                    }
                                    //要刪除插人的(或是最後一個 => 情況不同 結果一樣) 並且重新排序插人那排的
                                    Firebase removeDragSiteRef = dataSnapshot.child("site").child("day" + (fromColumn + 1)).child("" + (dataSnapshot.child("site").child("day" + (fromColumn + 1)).getChildrenCount() - 1)).getRef();//路徑是最後一個人  插人的人會被覆蓋 所以不用理他
                                    removeDragSiteRef.removeValue();

                                }
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                            Toast.makeText(mBoardView.getContext(), "87", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//                            Toast.makeText(mBoardView.getContext(), "8787", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                            Toast.makeText(mBoardView.getContext(), "878787", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
//                            Toast.makeText(mBoardView.getContext(), "87878787", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Board");

        //在特定行程加入天數與其景點
        Firebase.setAndroidContext(mBoardView.getContext());//this用mBoardView.getContext()取代
        new Firebase(url).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if((""+dataSnapshot.child("tId").getValue()).equals(tripTId)){
                    addFirebaseColumnList();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //menu.findItem(R.id.action_disable_drag).setVisible(mBoardView.isDragEnabled());
        //menu.findItem(R.id.action_enable_drag).setVisible(!mBoardView.isDragEnabled());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_disable_drag:
                mBoardView.setDragEnabled(false);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_enable_drag:
                mBoardView.setDragEnabled(true);
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_add_column:
                addColumnList();
                return true;
            case R.id.action_remove_column:
                //在這裡接firebase
                Firebase.setAndroidContext(mBoardView.getContext());//this用mBoardView.getContext()取代
                new Firebase(url).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if((""+dataSnapshot.child("tId").getValue()).equals(tripTId)){
                            mBoardView.removeColumn(mColumns - 1);
                            Firebase removeDayRef = (dataSnapshot.child("site").child("day" + mColumns)).getRef();
                            removeDayRef.removeValue();
                            mColumns--;
                        }
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        //這個是準的
//                        Toast.makeText(mBoardView.getContext(), "87 3", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//                        Toast.makeText(mBoardView.getContext(), "8787 3", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                        Toast.makeText(mBoardView.getContext(), "878787 3", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
//                        Toast.makeText(mBoardView.getContext(), "87878787 3", Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            case R.id.action_clear_board:
                mBoardView.clearBoard();
                mColumns = 0;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 新增一天時(與addFirebaseColumn區分開來)
     */
    private void addColumnList() {
        Firebase.setAndroidContext(mBoardView.getContext());//this用mBoardView.getContext()取代
        new Firebase(url).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if((""+dataSnapshot.child("tId").getValue()).equals(tripTId)){
                    //這段用來動態新增list
                    final ArrayList<Site> mItemArray = new ArrayList<>();
                    final int addItems = 0;
                    for (int i = 0; i < addItems; i++) {
                        long id = sCreatedItems++;
                        mItemArray.add(new Site(id, "景點名稱"));
                    }
                    //這段用來header的景點數計算 天數計算還有問題
                    final int[] count = {0};
                    final int column = mColumns;
                    final View header = View.inflate(getActivity(), R.layout.column_header, null);
                    final ItemAdapter listAdapter = new ItemAdapter(header, mItemArray, R.layout.column_item, R.id.item_layout, true, BoardFragment.this.getActivity());
                    ((TextView) header.findViewById(R.id.text)).setText("第" + (mColumns + 1) + "天");
                    ((TextView) header.findViewById(R.id.item_count)).setText("景點數 : " + mItemArray.size());
                    switch (tripType) {
                        case "my":
                        header.setOnClickListener(new View.OnClickListener() {
                            //這段是新增新的景點(點擊header)
                            @Override
                            public void onClick(View v) {
                                final long id = sCreatedItems++;
                                String[] provinces = new String[] {"景點的搜尋", "收藏的景點"};

                                new AlertDialog.Builder(mBoardView.getContext())
                                        .setTitle("從哪裡加入景點")
                                        //.setView(choose)
                                        .setItems(provinces, new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(mBoardView.getContext(), "" + which, Toast.LENGTH_SHORT).show();
                                                if(which == 0){
                                                    Intent intent = new Intent(mBoardView.getContext(), SiteSearchActivity.class);
                                                    intent .putExtra("tId",tripTId);
                                                    intent.putExtra("column", "" + (column + 1));
                                                    intent.putExtra("count", "" + mItemArray.size());
                                                    intent.putExtra("transId", "" + id);
                                                    intent.putExtra("fromTravel",true);
                                                    intent.putExtra("searchType",SearchSites.SEARCHTYPE_BROWSE);
                                                    startActivity(intent);

                                                    Site item = new Site(id, "景點名稱1");
                                                    mBoardView.addItem(column, mItemArray.size(), item, true);
                                                    ((TextView) header.findViewById(R.id.item_count)).setText("景點數 : " + mItemArray.size());
                                                }
                                                else if(which == 1){
                                                    Intent intent = new Intent(mBoardView.getContext(), MyLike.class);
                                                    intent.putExtra("tId",tripTId);
                                                    intent.putExtra("column", "" + (column + 1));
                                                    intent.putExtra("count", "" + mItemArray.size());
                                                    intent.putExtra("fromTravel",true);
                                                    intent.putExtra("searchType",SearchSites.SEARCHTYPE_MYLIKES);
                                                    intent.putExtra("transId", "" + id);
                                                    startActivity(intent);

                                                    Site item = new Site(id, "景點名稱1");
                                                    mBoardView.addItem(column, mItemArray.size(), item, true);
                                                    ((TextView) header.findViewById(R.id.item_count)).setText("景點數 : " + mItemArray.size());
                                                }
                                            }
                                        })
                                        .show();


                                //mBoardView.moveItem(4, 0, 0, true);
                            //mBoardView.removeItem(column, 0);
                            //mBoardView.moveItem(0, 0, 1, 3, false);
                            //mBoardView.replaceItem(0, 0, item1, true);
                            //((TextView) header.findViewById(R.id.item_count)).setText("" + mItemArray.size());
                            }
                        });
                        //長按header修改出發時間

                            header.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.BUTTON_POSITIVE, new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            ((TextView) header.findViewById(R.id.departureTime)).setText("開始時間: " + hourOfDay + "時" + minute + "分");
                                            startHour = Integer.toString(hourOfDay);
                                            startMin = Integer.toString(minute);
                                        }
                                    }, hourOfDay, minute, false);

                                    timePickerDialog.show();
                                    return true;//true為結束長按動作後不再執行短按
                                }
                            });
                            break;
                        case "collection":
                            break;
                        case "search":
                            break;
                    }
                    mBoardView.addColumnList(listAdapter, header, false);
                    mColumns++;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * 把firebase的資料顯示出來(與addColumnList區分開來)
     */
    private void addFirebaseColumnList() {
        //加入景點
        Firebase.setAndroidContext(mBoardView.getContext());//this用mBoardView.getContext()取代
        new Firebase(url).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if((""+dataSnapshot.child("tId").getValue()).equals(tripTId)){
                    long numberOfDay = dataSnapshot.child("site").getChildrenCount();

                    ArrayList<ArrayList<JSONObject>> mAllDaySiteArray = new ArrayList<>();

                    for(int i = 0 ; i < numberOfDay ; i++){
                        long eachNumberOfDay = dataSnapshot.child("site").child("day" + (i + 1)).getChildrenCount();
                        //這段用來動態新增list
                        final ArrayList<JSONObject> mJSONItemArray = new ArrayList<>();
                        final int addItems = (int)eachNumberOfDay;
                        for (int j = 0; j < addItems; j++)
                        {
                            DataSnapshot aSiteSnapshot = dataSnapshot.child("site").child("day" + (i + 1)).child(""+j);

                            JSONObject aSiteJSONObj = new JSONObject();
                            try
                            {
                                aSiteJSONObj.put("sId", aSiteSnapshot.child("sId").getValue());
                                aSiteJSONObj.put("times", aSiteSnapshot.child("times").getValue());
                                aSiteJSONObj.put("journal", aSiteSnapshot.child("journal").getValue());
                                aSiteJSONObj.put("order", aSiteSnapshot.child("order").getValue());
                                long id = sCreatedItems++;
                                aSiteJSONObj.put("id", id);
                            }
                            catch (JSONException e)
                            {   e.printStackTrace();    }

                            mJSONItemArray.add(aSiteJSONObj);
                        }

                        mAllDaySiteArray.add(mJSONItemArray);
                    }
                    Log.d("HFallSites", mAllDaySiteArray.toString());
                    addAllDaySites(mAllDaySiteArray, dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private static class MyDragItem extends DragItem {

        public MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            CardView dragCard = ((CardView) dragView.findViewById(R.id.card));
            CardView clickedCard = ((CardView) clickedView.findViewById(R.id.card));

            dragCard.setMaxCardElevation(40);
            dragCard.setCardElevation(clickedCard.getCardElevation());
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
            dragCard.setForeground(clickedView.getResources().getDrawable(R.drawable.card_view_drag_foreground));
        }

        @Override
        public void onMeasureDragView(View clickedView, View dragView) {
            CardView dragCard = ((CardView) dragView.findViewById(R.id.card));
            CardView clickedCard = ((CardView) clickedView.findViewById(R.id.card));
            int widthDiff = dragCard.getPaddingLeft() - clickedCard.getPaddingLeft() + dragCard.getPaddingRight() -
                    clickedCard.getPaddingRight();
            int heightDiff = dragCard.getPaddingTop() - clickedCard.getPaddingTop() + dragCard.getPaddingBottom() -
                    clickedCard.getPaddingBottom();
            int width = clickedView.getMeasuredWidth() + widthDiff;
            int height = clickedView.getMeasuredHeight() + heightDiff;
            dragView.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            dragView.measure(widthSpec, heightSpec);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            CardView dragCard = ((CardView) dragView.findViewById(R.id.card));
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 40);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            CardView dragCard = ((CardView) dragView.findViewById(R.id.card));
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 6);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }
    }
    /**
     * 回傳現在所在天數
     * @return
     */
    public static int getCurrentColumn(){
        return mBoardView.getClosestColumn();
    }


    private void addAllDaySites(final ArrayList<ArrayList<JSONObject>> allDaySiteArray, final DataSnapshot dataSnapshot)
    {
        final ArrayList<ArrayList<String>> allDaySIdArray = new ArrayList<>();
        final SparseArray<HashMap<String, JSONObject>> allDaySiteHashMap = new SparseArray<>();

        for (int a=0; a<allDaySiteArray.size(); ++a)
        {
            ArrayList<String> aDaySIdArray = new ArrayList<>();
            HashMap<String, JSONObject> aDaySiteHashMap = new HashMap<>();

            ArrayList<JSONObject> aDaySiteArray = allDaySiteArray.get(a);
            for (int b=0; b<aDaySiteArray.size(); ++b)
            {
                aDaySIdArray.add(aDaySiteArray.get(b).optString("sId"));
                aDaySiteHashMap.put(aDaySiteArray.get(b).optString("sId"), aDaySiteArray.get(b));
            }

            allDaySIdArray.add(aDaySIdArray);
            allDaySiteHashMap.put(a, aDaySiteHashMap);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL + "getDetailDictByDaySIdArray.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("HFresponse", response);
                        try {
                            JSONObject sIdDetailDict = new JSONObject(response);

                            for (int a = 0; a < allDaySiteArray.size(); ++a) {
                                final ArrayList<Site> mItemArray = new ArrayList<>();

                                ArrayList<JSONObject> aDaySiteArray = allDaySiteArray.get(a);

                                for (int b = 0; b < aDaySiteArray.size(); ++b) {
                                    JSONObject aSiteJSONObject = aDaySiteArray.get(b);
                                    Log.d("HFaSite", a + " - " + b + " - " + aSiteJSONObject.toString());
                                    JSONObject aSiteDetail = sIdDetailDict.getJSONObject(aSiteJSONObject.optString("sId"));

                                    mItemArray.add(new Site(aSiteJSONObject.optLong("order"), aSiteJSONObject.optString("journal"), aSiteJSONObject.optLong("sId"), Long.parseLong(aSiteJSONObject.optString("times")), aSiteDetail.optString("sName"), aSiteDetail.optString("address"), aSiteJSONObject.optLong("id")));
                                }


                                //這段用來header的景點數計算 天數計算還有問題
                                final int[] count = {0};
                                final int column = mColumns;
                                final View header = View.inflate(context, R.layout.column_header, null);
                                final ItemAdapter listAdapter = new ItemAdapter(header, mItemArray, R.layout.column_item, R.id.item_layout, true, context);
                                ((TextView) header.findViewById(R.id.text)).setText("第" + (mColumns + 1) + "天");
                                ((TextView) header.findViewById(R.id.item_count)).setText("景點數 : " + mItemArray.size());
                                switch (tripType) {
                                    case "my":
                                        header.setOnClickListener(new View.OnClickListener() {
                                            //這段是新增新的景點(點擊header)
                                            @Override
                                            public void onClick(View v) {
                                                final long id = sCreatedItems++;
                                                String[] provinces = new String[]{"景點的搜尋", "收藏的景點"};

                                                new AlertDialog.Builder(mBoardView.getContext())
                                                        .setTitle("從哪裡加入景點")
                                                        //.setView(choose)
                                                        .setItems(provinces, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //Toast.makeText(mBoardView.getContext(), "" + which, Toast.LENGTH_SHORT).show();
                                                                if (which == 0) {
                                                                    Intent intent = new Intent(mBoardView.getContext(), SiteSearchActivity.class);
                                                                    intent.putExtra("tId", tripTId);
                                                                    intent.putExtra("column", "" + (column + 1));
                                                                    intent.putExtra("count", "" + mItemArray.size());
                                                                    intent.putExtra("fromTravel", true);
                                                                    intent.putExtra("searchType", SearchSites.SEARCHTYPE_BROWSE);
                                                                    intent.putExtra("transId", "" + id);
                                                                    startActivity(intent);

                                                                    Site item = new Site(id, "景點名稱1");
                                                                    mBoardView.addItem(column, mItemArray.size(), item, true);
                                                                    ((TextView) header.findViewById(R.id.item_count)).setText("景點數 : " + mItemArray.size());
                                                                } else if (which == 1) {
                                                                    Intent intent = new Intent(mBoardView.getContext(), MyLike.class);
                                                                    intent.putExtra("tId", tripTId);
                                                                    intent.putExtra("column", "" + (column + 1));
                                                                    intent.putExtra("count", "" + mItemArray.size());
                                                                    intent.putExtra("fromTravel", true);
                                                                    intent.putExtra("searchType", SearchSites.SEARCHTYPE_MYLIKES);
                                                                    intent.putExtra("transId", "" + id);
                                                                    startActivity(intent);

                                                                    Site item = new Site(id, "景點名稱1");
                                                                    mBoardView.addItem(column, mItemArray.size(), item, true);
                                                                    ((TextView) header.findViewById(R.id.item_count)).setText("景點數 : " + mItemArray.size());
                                                                }
                                                            }
                                                        })
                                                        .show();

                                            }
                                        });
                                        //長按header輸入要刪除的景點(應該改成整列清除)

                                        header.setOnLongClickListener(new View.OnLongClickListener() {
                                            @Override
                                            public boolean onLongClick(View v) {
                                                TimePickerDialog timePickerDialog = new TimePickerDialog(context, AlertDialog.BUTTON_POSITIVE, new TimePickerDialog.OnTimeSetListener() {
                                                    @Override
                                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                        ((TextView) header.findViewById(R.id.departureTime)).setText("開始時間: " + hourOfDay + "時" + minute + "分");
                                                        startHour = Integer.toString(hourOfDay);
                                                        startMin = Integer.toString(minute);
                                                    }
                                                }, hourOfDay, minute, false);

                                                timePickerDialog.show();
                                                return true;//true為結束長按動作後不再執行短按

                                            /*
                                            new AlertDialog.Builder(mBoardView.getContext())
                                                    .setTitle("刪除整天行程")
                                                    //.setView(choose)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            while (mItemArray.size() != 0){
                                                                mBoardView.removeItem(column, 0);
                                                            }
                                                            ((TextView) header.findViewById(R.id.item_count)).setText("景點數 : " + mItemArray.size());
                                                        }
                                                    }).show();*/
                                            }
                                        });
                                        break;
                                    case "collection":
                                        break;
                                    case "search":
                                        break;
                                }
                                mBoardView.addColumnList(listAdapter, header, false);
                                mColumns++;
                            }
                        }
                        catch (JSONException e)
                        {   e.printStackTrace();    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("allDaySites", allDaySIdArray.toString());
                Log.d("HFinputSites", allDaySIdArray.toString());

                return map;
            }
        };

        mQueue.add(stringRequest);

    }
}
