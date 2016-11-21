package m.mcoupledate;

/**
 * Created by bbkk5 on 2016/10/15.
 * 自己寫一個class之後可以用來新增firebase的東西
 */
public class Site {
    private long order;
    private String journal;
    public long sId;
    private long time;//逗留時間
    public String sName;

    public Site(long sId, String sName)
    {
        this.sId = sId;
        this.sName = sName;
    }

    public Site(long order, String journal, long sId, long time){
        this.order = order;
        this.journal = journal;
        this.sId = sId;
        this.time = time;
    }

    public void setOrder(long order){
        this.order = order;
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

    public long getOrder(){
        return this.order;
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
