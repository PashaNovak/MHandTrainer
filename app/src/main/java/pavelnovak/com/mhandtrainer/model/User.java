package pavelnovak.com.mhandtrainer.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by PavelNovak on 20.03.2018.
 */

@Table(name = "Users")
public class User extends Model {

    @Column(name = "Login")
    private String login;

    @Column(name = "Password")
    private String password;

    public String getLogin(){
        return login;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public User(String login, String password) {
        super();
        this.login = login;
        this.password = password;
    }

    public User(){
        super();
    }
}
