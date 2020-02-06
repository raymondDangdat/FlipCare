package comq.example.raymond.flipcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference patients;
    private String uId = "";
    private Button btn_chat_with_flp_caare;
    private Spinner spinnerAge, spinnerGender, spinnerHealth;
    private EditText editTextPatientName, editTextEmail;

    private String email, fullName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        patients = FirebaseDatabase.getInstance().getReference().child("flipcare").child("users").child("patients");


        spinnerHealth = findViewById(R.id.spinner_health);
        btn_chat_with_flp_caare = findViewById(R.id.btn_chat_with_flipcare);
        editTextPatientName = findViewById(R.id.edit_patient_name);
        editTextEmail = findViewById(R.id.edit_email);
        spinnerAge = findViewById(R.id.spinner_age);
        spinnerHealth = findViewById(R.id.spinner_health);
        spinnerGender = findViewById(R.id.spinner_gender);

        getUserInfo();

        spinnerHealth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String health = spinnerHealth.getSelectedItem().toString();
                btn_chat_with_flp_caare.setText("CHAT WITH FLIP CARE " + health+ " SPECIALIST");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_chat_with_flp_caare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserDetails();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getUserInfo() {
        patients.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue(String.class);
                fullName = dataSnapshot.child("fullName").getValue(String.class);

                editTextEmail.setText(email);
                editTextPatientName.setText(fullName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserDetails() {
        final String patientName = editTextPatientName.getText().toString();
        final String health = spinnerHealth.getSelectedItem().toString();

        if (!TextUtils.isEmpty(patientName)){
            //get crime id to new activity
            Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
            chatIntent.putExtra("patientName", patientName);
            chatIntent.putExtra("healthIssue", health);
            startActivity(chatIntent);

        }else {
            Toast.makeText(this, "Your name is required to chat with FlipCare", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_admin_sign_in) {
            startActivity(new Intent(MainActivity.this, DoctorSignIn.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_services) {
            startActivity(new Intent(MainActivity.this, ServicesActivity.class));

        } else if (id == R.id.nav_chat_history) {
            startActivity(new Intent(MainActivity.this, ChatHistoryActivity.class));

        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(MainActivity.this, ContactActivity.class));

        }else if (id == R.id.nav_about){
          startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_exit){
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(MainActivity.this, AuthenticationActivity.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
