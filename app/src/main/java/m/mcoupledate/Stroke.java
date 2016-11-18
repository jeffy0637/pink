package m.mcoupledate;

/**
 * Created by user on 2016/11/9.
 */

public class Stroke {

    /*
    定义学生的构造器，创建学生对象时定义学生的信息。
     */
    public Stroke(String title, String starttime, String endtime){
        this.title = title;
        this.starttime = starttime;
        this.endtime = endtime;
    }
    private String title;//行程名稱
    private String starttime;//開始日期
    private String endtime;//結束日期

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getStarttime() {
        return starttime;
    }
    public void setStarttime(String age) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }
    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

}
