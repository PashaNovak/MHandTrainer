package pavelnovak.com.mhandtrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pavelnovak.com.mhandtrainer.model.User;

public class SignUpActivity extends Activity {
    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;

    private EditText loginSignUp;
    private EditText passwordSignUp;
    private EditText repeatPassworsSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        loginSignUp = (EditText) findViewById(R.id.login_sign_up);
        passwordSignUp = (EditText) findViewById(R.id.password_sign_up);
        repeatPassworsSignUp = (EditText) findViewById(R.id.repeat_password_sign_up);

        Button signUp = (Button) findViewById(R.id.sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation(loginSignUp.getText().toString()) && validation(passwordSignUp.getText().toString())){
                    if (passwordSignUp.getText().toString().equals(repeatPassworsSignUp.getText().toString())){
                        if (getUsers().isEmpty()){
                            addUser(loginSignUp.getText().toString(), passwordSignUp.getText().toString());
                            goToBase();
                        } else {
                            boolean isLoginUnic = true;
                            for (int i = 0; i < getUsers().size(); i++){
                               if (getUsers().get(i).getLogin().equals(loginSignUp.getText().toString())){
                                   Toast.makeText(getApplicationContext(), "This login already exist, please, enter another!", Toast.LENGTH_LONG).show();
                                   loginSignUp.setText("");
                                   isLoginUnic = false;
                               }
                            }
                            if (isLoginUnic){
                                addUser(loginSignUp.getText().toString(), passwordSignUp.getText().toString());
                                goToBase();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
                        passwordSignUp.setText("");
                        repeatPassworsSignUp.setText("");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect data (should be only letters and/or numbers)", Toast.LENGTH_LONG).show();
                    loginSignUp.setText("");
                    passwordSignUp.setText("");
                    repeatPassworsSignUp.setText("");
                }
            }
        });
    }

    public void goToBase(){
        Intent intent = new Intent(getApplicationContext(), BaseActivity.class);
        intent.putExtra(BaseActivity.EXTRA_FROM, "sign_up");
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(StartActivity.APP_PREFERENCE_LOGIN, loginSignUp.getText().toString());
        editor.apply();
        startActivity(intent);
        loginSignUp.setText("");
        passwordSignUp.setText("");
        repeatPassworsSignUp.setText("");
    }

    public void addUser(String login, String password){
        User thisUser = new User();
        thisUser.setLogin(login);
        thisUser.setPassword(password);
        thisUser.save();
    }

    public boolean validation(String str){
        if (str.equals("")){
            return false;
        } else {
            String pattern = "\\d*\\w*";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(str);
            return m.matches();
        }
    }

    public static List<User> getUsers(){
        return new Select()
                .from(User.class)
                .execute();
    }
}
