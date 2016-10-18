package m.mcoupledate;

/**
 * Created by bbkk5 on 2016/10/15.
 * 新創一個行程
 * 也許編輯收藏的行程也會用到 之後再修改
 */
public class Travel {
    private long editor;
    private String start_date;
    private String end_date;
    private long tId;
    //private Site site;

    public Travel(){}

    public Travel(long editor, String start_date, String end_date, long tId){
        this.editor = editor;
        this.start_date = start_date;
        this.end_date = end_date;
        this.tId = tId;
    }

    public void setEditor(long editor){
        this.editor = editor;
    }

    public void setStart_date(String start_date){
        this.start_date = start_date;
    }

    public void setEnd_date(String end_date){
        this.end_date = end_date;
    }

    public void settId(long tId){
        this.tId = tId;
    }

    public long getEditor(){
        return  this.editor;
    }

    public String getStart_date(){
        return this.start_date;
    }

    public String getEnd_date(){
        return this.end_date;
    }

    public long gettId(){
        return this.tId;
    }
}
