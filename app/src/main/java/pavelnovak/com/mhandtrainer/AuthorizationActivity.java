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

public class AuthorizationActivity extends Activity {
    public static final String APP_PREFERENCES = "mysettings";
    SharedPreferences mSettings;

    private EditText loginEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        loginEditText = findViewById(R.id.login_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

        Button buttonSignIn = findViewById(R.id.sign_in);
        Button buttonSignUp = findViewById(R.id.sign_up_auth);

        final Intent intentToBase = new Intent(this, BaseActivity.class);
        intentToBase.putExtra(BaseActivity.EXTRA_FROM, "authorization");

        final Intent intentToSignUp = new Intent(this, SignUpActivity.class);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation(loginEditText.getText().toString()) && validation(passwordEditText.getText().toString())){
                    if (!getUsers().isEmpty()){
                        boolean noThisUser = true;
                        for (int i = 0; i < getUsers().size(); i++){
                            if (getUsers().get(i).getLogin().equals(loginEditText.getText().toString())
                                    && getUsers().get(i).getPassword().equals(passwordEditText.getText().toString())){
                                startActivity(intentToBase);
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putString(StartActivity.APP_PREFERENCE_LOGIN, loginEditText.getText().toString());
                                editor.apply();
                                loginEditText.setText("");
                                passwordEditText.setText("");
                                noThisUser = false;
                            }
                        }
                        if (noThisUser){
                            Toast.makeText(getApplicationContext(), "There is no this user, please, sign up or put correct data!", Toast.LENGTH_LONG).show();
                            loginEditText.setText("");
                            passwordEditText.setText("");
                        }
                    } else if (getUsers().isEmpty()){
                        Toast.makeText(getApplicationContext(), "There is no users, please, sign up)", Toast.LENGTH_LONG).show();
                        loginEditText.setText("");
                        passwordEditText.setText("");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect data (should be only letters and/or numbers)", Toast.LENGTH_LONG).show();
                    loginEditText.setText("");
                    passwordEditText.setText("");
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intentToSignUp);
            }
        });
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
