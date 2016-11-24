package m.mcoupledate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016/11/9.
 */

public class Stroke implements Comparable
{
    public String tripName;//行程名稱
    public String start_date;//開始日期
    public String end_date;//結束日期
    public String tId;
    public String editor;

    public ArrayList<String> siteList = new ArrayList<>();
    public HashMap<String, Integer> cityarea = new HashMap<>();
    private double score;

    private String searchQuery = "";


    public Stroke(String title, String startDate, String endtime, String tId) {
        this.tripName = title;
        this.start_date = startDate;
        this.end_date = endtime;
        this.tId = tId;
    }

    public Stroke(JSONObject aStrokeObj)
    {
        this.tripName = aStrokeObj.optString("tName");
        this.start_date = aStrokeObj.optString("startDate");
        this.end_date = aStrokeObj.optString("endDate");
        this.tId = aStrokeObj.optString("tId");

        JSONArray sitesJSONArray = aStrokeObj.optJSONArray("sites");
        if (sitesJSONArray!=null) {
            for (int a = 0; a < sitesJSONArray.length(); ++a)
                this.siteList.add(sitesJSONArray.optString(a));
        }

        JSONArray cityareaJSONArray = aStrokeObj.optJSONArray("cityarea");
        if (cityareaJSONArray!=null) {
            for (int a = 0; a < cityareaJSONArray.length(); ++a)
                cityarea.put(cityareaJSONArray.optJSONObject(a).optString("caName"), cityareaJSONArray.optJSONObject(a).optInt("count"));
        }

        this.score = aStrokeObj.optDouble("score");
    }


    public String getTitle() {
        return tripName;
    }
    public void setTitle(String title) {
        this.tripName = title;
    }

    public String getStartDate() {
        return start_date;
    }
    public void setStartDate(String start_date) {
        this.start_date = start_date;
    }

    public String getEndDate() {
        return end_date;
    }
    public void setEndDate(String end_date) {
        this.end_date = end_date;
    }


    public Boolean containsFeature(String feature)
    {
        String pattern = ".*" + feature + ".*";

        for (String sName : this.siteList)
        {
            if (sName.matches(pattern))
                return true;
        }

        for (String caName : this.cityarea.keySet())
        {
            if (caName.matches(pattern))
                return true;
        }


        return false;
    }



    public void setSearchQuery(String query)
    {   searchQuery = query;    }

    @Override
    public int compareTo(Object o)
    {
        //  -1: less than another
        //  0:  equals to another
        //  1:  more than another

        Stroke anotherStroke = (Stroke) o;

        String titlePattern = ".*" + searchQuery + ".*";

        //  1.比對title相似
        if (this.tripName.matches(titlePattern) && !anotherStroke.tripName.matches(titlePattern))
        {
            return 1;
        }
        else if (!this.tripName.matches(titlePattern) && anotherStroke.tripName.matches(titlePattern))
        {
            return -1;
        }
        else
        {
            //  2.比對包含sites的match數
            int thisMatchSiteCount = this.getMatchQuerySiteCount(searchQuery);
            int anotherMatchSiteCount = anotherStroke.getMatchQuerySiteCount(searchQuery);

            if (thisMatchSiteCount > anotherMatchSiteCount)
            {
                return 1;
            }
            else if (thisMatchSiteCount < anotherMatchSiteCount)
            {
                return -1;
            }
            else
            {
                //  3.比對feature的match數
                int thisMatchCityareaCount = this.getMatchQueryCityareaCount(searchQuery);
                int anotherMatchCityareaCount = anotherStroke.getMatchQueryCityareaCount(searchQuery);

                if (thisMatchCityareaCount > anotherMatchCityareaCount)
                {
                    return 1;
                }
                else if (thisMatchCityareaCount < anotherMatchCityareaCount)
                {
                    return -1;
                }
                else
                {
                    //  依照php之前算的分數排
                    double diffScore = this.getScore() - anotherStroke.getScore();

                    if (diffScore<0.01 && diffScore>=0)
                        return 0;
                    else if (diffScore>0)
                        return 1;
                    else
                        return -1;
                }
            }
        }
    }

    private int getMatchQuerySiteCount(String query)
    {
        int count = 0;
        String pattern = ".*" + query + ".*";

        for (String sName : this.siteList)
        {
            if (sName.matches(pattern))
                ++count;
        }

        return count;
    }

    private int getMatchQueryCityareaCount(String query)
    {
        int count = 0;
        String pattern = ".*" + query + ".*";

        for (String caName : this.cityarea.keySet())
        {
            if (caName.matches(pattern))
                count += this.cityarea.get(caName);
        }

        return count;
    }

    private double getScore()
    {   return score;   }
}
