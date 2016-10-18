package m.mcoupledate;

/**
 * Created by bbkk5 on 2016/10/15.
 * 自己寫一個class之後可以用來新增firebase的東西
 */
public class Site {
    private long day;
    private String journal;
    private long sId;
    private long time;//逗留時間

    public Site(){}

    public Site(long day, String journal, long sId, long time){
        this.day = day;
        this.journal = journal;
        this.sId = sId;
        this.time = time;
    }

    public void setDay(long day){
        this.day = day;
    }

    public void setJournal(String journal){
        this.journal = journal;
    }

    public void setsId(long sId){
        this.sId = sId;
    }

    public void setTimes(long time){
        this.time = time;
    }

    public long getDay(){
        return this.day;
    }

    public String getJournal(){
        return this.journal;
    }

    public long getsId(){
        return this.sId;
    }

    public long getTimes(){
        return this.time;
    }
}
