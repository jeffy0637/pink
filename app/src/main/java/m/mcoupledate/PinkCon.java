package m.mcoupledate;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/7/10.
 */


public class PinkCon {

//    private static String conAPI = "http://140.117.71.216/pinkCon/pinkCon.php";
//    private static String result;
//    private static StringRequest getRequest;
//    private static RequestQueue mQueue;


//    public static void get(RequestQueue mQueue)
//    {
//        StringRequest stringRequest = new StringRequest(conAPI,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        setResult(response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                setResult(error.toString());
//            }
//        });
//
//        mQueue.add(stringRequest);
//
//    }

    public static void exec(final String sql, RequestQueue mQueue, String conAPI)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, conAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("exec", sql);

                return map;
            }
        };

        mQueue.add(stringRequest);
    }

//    public static void setResult(String response)
//    {
//        result = response;
//    }



}
