package ofeksprojects.ofek.com.nightout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

public class AuthActivity extends AppCompatActivity {
    LoginButton facebookLoginBtn;
    SignInButton googleLoginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setViews();
    }

    private void setViews() {

    }
}
