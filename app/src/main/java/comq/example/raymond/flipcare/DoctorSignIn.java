package comq.example.raymond.flipcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class DoctorSignIn extends AppCompatActivity {
    private Toolbar docToolbar;
    private Button btn_sign_in;
    private MaterialEditText editTextEmail, editTextPassword;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference doctors;

    private String uId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_sign_in);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        doctors = FirebaseDatabase.getInstance().getReference().child("flipcare").child("users").child("doctors");

        //initialize toolBar
        docToolbar = findViewById(R.id.doc_toolbar);
        setSupportActionBar(docToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Doctor's Sign In");

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btn_sign_in = findViewById(R.id.buttonSignIn);

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });




    }

    private void logInUser() {
        progressDialog.setMessage("Sign in pleaase wait...");
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email field is empty", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        if (email.equals("flipcare@gmail.com")){
                            Intent intent = new Intent(DoctorSignIn.this, DoctorsHome.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            progressDialog.dismiss();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(DoctorSignIn.this, "Something went wrong, please make sure you are a flipcare specialist", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DoctorSignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
