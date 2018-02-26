package ofeksprojects.ofek.com.nightout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int AUTHENTICATION_REQUEST_CODE = 1112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        authenticateUser();
    }

    private void authenticateUser() {
        FirebaseApp.initializeApp(getApplicationContext());
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startAuthenticationActivity();
        }
    }

    private void startAuthenticationActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        Intent intent=AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        startActivityForResult(intent,AUTHENTICATION_REQUEST_CODE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTHENTICATION_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this,MainNavActivity.class);
            } else {
                // Sign in failed, check response for error code
                assert response != null;
                Log.e("authentication failed", "error code : "+response.getErrorCode());
            }
        }
    }
}
