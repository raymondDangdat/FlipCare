package comq.example.raymond.flipcare;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {
    //timeout for the splash screen
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginItent = new Intent(SplashScreenActivity.this, AuthenticationActivity.class);
                startActivity(loginItent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
