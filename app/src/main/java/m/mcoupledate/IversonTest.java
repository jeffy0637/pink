package m.mcoupledate;

/*
用來測試firebase的
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class IversonTest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iverson_test);



        ListView list = (ListView) findViewById(R.id.listView);
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1);
        list.setAdapter(adapter);

        Firebase.setAndroidContext(this);
        final String url = "https://couple-project.firebaseio.com/travel";
        final String tId = "12345";
        new Firebase(url).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if((""+dataSnapshot.child("tId").getValue()).equals(tId)) {
                    //找出特定行程的編輯者
//                    for (int i = 0; i < dataSnapshot.child("editor").getChildrenCount(); i++)
//                        adapter.add("" + dataSnapshot.child("editor").child("" + i).getValue());

//                    //在特定行程加入景點
//                    Firebase siteRef = (dataSnapshot.child("site").child("" + dataSnapshot.child("site").getChildrenCount()).getRef());//上面寫法是為了從要找的節點加入
//                    //Firebase myFirebaseRef = new Firebase(url);//下面寫法是為了從給定節點加入
//                    //Firebase siteRef = myFirebaseRef.child("0/site").child("" + dataSnapshot.child("site").getChildrenCount());
//                    Site site = new Site(2, "這是一個屌地方", 8, 5);//資料更改後不準了 day拿掉變成order 所以路徑要指到哪一天才對
//                    siteRef.setValue(site);

//                    //在特定行程算出幾天
//                    String start, end;
//                    Date sDay = null, eDay = null;
//                    start = (String)dataSnapshot.child("start_date").getValue();
//                    end = (String)dataSnapshot.child("end_date").getValue();
//                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");//要轉成Date的
//                    try {
//                        sDay = dateFormatter.parse(start);
//                        eDay = dateFormatter.parse(end);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    long diff = eDay.getTime() - sDay.getTime();
//                    adapter.add("" + (diff/(1000*60*60*24) + 1));

//                    //在特定行程加一天或減一天
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(eDay);
//                    //cal.add(Calendar.DATE, 1);//加一天
//                    //cal.add(Calendar.DATE, -1);//減一天
//                    end = dateFormatter.format(cal.getTime());//轉成字串
//                    Firebase siteRef = (dataSnapshot.child("end_date").getRef());
//                    siteRef.setValue(end);
//                }
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

        final String tId2 = "12345";
        Firebase.setAndroidContext(this);
        final String url2 = "https://couple-project.firebaseio.com";
        new Firebase(url2).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                //新增一個travel
//                Firebase strokeRef = (dataSnapshot.child("" + dataSnapshot.getChildrenCount()).getRef());//上面寫法是為了從要找的節點加入
//                Travel travel = new Travel(623077527870321L, "2016-12-31", "2017-01-03", 9453);
//                strokeRef.setValue(travel);
//                //刪除一個指定的travel
//               for (int i = 0 ; i < dataSnapshot.getChildrenCount() ; i++)
//                    if(("" + dataSnapshot.child("" + i).child("tId").getValue()).equals(tId2)){
//                        Firebase strokeRemoveRef = (dataSnapshot.child("" + i).getRef());
//                        strokeRemoveRef.removeValue();
//                    }
                for(int i = 0 ; i < dataSnapshot.getChildrenCount() ; i++){
                    if((""+dataSnapshot.child("" + i).child("tId").getValue()).equals(tId2)){
                        Firebase siteMoveRef = dataSnapshot.child("" + i).child("site").child("" + dataSnapshot.child("" + i).child("site").getChildrenCount()).getRef();
                        Site site = new Site(1, "這是一個鳥地方", 2, 3);
                        siteMoveRef.setValue(site);
                    }
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

}
