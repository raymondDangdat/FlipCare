package comq.example.raymond.flipcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class PatientsChatActivity extends AppCompatActivity {
    private Toolbar patientsChatsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_chat);
    }
}
