package pavelnovak.com.mhandtrainer.model;

/**
 * Created by PavelNovak on 20.03.2018.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

@Table(name = "Training")
public class Training extends Model{

    @Column(name = "loginUser")
    private String loginUser;

    @Column(name = "Type")
    private String type;

    @Column(name = "StartDate")
    private Date startDate;

    @Column(name = "Duration")
    private String duration;

    @Column(name = "Count")
    private int count;

    @Column(name = "Comment")
    private String comment;

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
