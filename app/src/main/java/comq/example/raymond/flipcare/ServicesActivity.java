package comq.example.raymond.flipcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ServicesActivity extends AppCompatActivity {
    private Toolbar servicesToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        //initialize toolBar
        servicesToolbar = findViewById(R.id.services_toolbar);
        setSupportActionBar(servicesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Flip Care Services");
    }
}
