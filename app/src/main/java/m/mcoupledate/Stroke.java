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
    public String title;//行程名稱
    public String startDate;//開始日期
    public String endDate;//結束日期
    public String tId;

    public ArrayList<String> siteList = new ArrayList<>();
    public HashMap<String, Integer> cityarea = new HashMap<>();
    private double score;

    private String searchQuery = "";


    public Stroke(String title, String startDate, String endtime, String tId)
    {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endtime;
        this.tId = tId;
    }

    public Stroke(JSONObject aStrokeObj)
    {
        this.title = aStrokeObj.optString("tName");
        this.startDate = aStrokeObj.optString("startDate");
        this.endDate = aStrokeObj.optString("endDate");
        this.tId = aStrokeObj.optString("tId");

        JSONArray sitesJSONArray = aStrokeObj.optJSONArray("sites");
        for (int a=0; a<sitesJSONArray.length(); ++a)
            this.siteList.add(sitesJSONArray.optString(a));

        JSONArray cityareaJSONArray = aStrokeObj.optJSONArray("cityarea");
        for (int a=0; a<cityareaJSONArray.length(); ++a)
            cityarea.put(cityareaJSONArray.optJSONObject(a).optString("caName"), cityareaJSONArray.optJSONObject(a).optInt("count"));


        this.score = aStrokeObj.optDouble("score");
    }


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String age) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
        if (this.title.matches(titlePattern) && !anotherStroke.title.matches(titlePattern))
        {
            return 1;
        }
        else if (!this.title.matches(titlePattern) && anotherStroke.title.matches(titlePattern))
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
