package comq.example.raymond.flipcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthenticationActivity extends AppCompatActivity {
    private Toolbar authToolbar;
    private Button btnLogin, btnSignUp, btnDoctor;

    private FirebaseAuth mAuth;
    private DatabaseReference patients;
    private ProgressDialog progressDialog;

    private String uId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        patients = FirebaseDatabase.getInstance().getReference().child("flipcare").child("users").child("patients");


        //initialize toolBar
        authToolbar = findViewById(R.id.auth_toolbar);
        setSupportActionBar(authToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("FlipCare Authentication");

        btnLogin = findViewById(R.id.btn_login);
        btnDoctor = findViewById(R.id.btn_doctor_sign_in);
        btnSignUp = findViewById(R.id.btn_sign_up);


        btnDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthenticationActivity.this, DoctorSignIn.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUpDialog();
            }

            private void startSignUpDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
                View view = getLayoutInflater().inflate(R.layout.sign_up_dialog, null);


                final EditText editTextEmail = view.findViewById(R.id.edit_email);
                final EditText editTextPassword = view.findViewById(R.id.edit_password);
                final EditText editTextName = view.findViewById(R.id.edit_username);
                final EditText editTextC_password = view.findViewById(R.id.edit_c_password);
                Button btn_login = view.findViewById(R.id.btn_login);

                //set onclick listener on login button
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email = editTextEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        final String fullName = editTextName.getText().toString().trim();
                        String c_password = editTextC_password.getText().toString().trim();

                        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(c_password)){
                            Toast.makeText(AuthenticationActivity.this, "All fields must be filled please", Toast.LENGTH_SHORT).show();


                        }else if (!password.equals(c_password)){
                            Toast.makeText(AuthenticationActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.setTitle("Sign Up");
                            progressDialog.setMessage("Signing  Up...");
                            progressDialog.show();
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            progressDialog.dismiss();
                                            uId = mAuth.getCurrentUser().getUid();
                                            patients.child(uId).child("email").setValue(email);
                                            patients.child(uId).child("uId").setValue(uId);
                                            patients.child(uId).child("fullName").setValue(fullName)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Intent intent = new Intent(AuthenticationActivity.this, AuthenticationActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            progressDialog.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(AuthenticationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AuthenticationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginDialog();
            }
        });
    }

    private void startLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);


        final EditText editTextEmail = view.findViewById(R.id.edit_email);
        final EditText password = view.findViewById(R.id.edit_password);
        Button btn_login = view.findViewById(R.id.btn_login);

        //set onclick listener on login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().trim().isEmpty() || !password.getText().toString().trim().isEmpty()){
                    progressDialog.setTitle("Login");
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString().trim(), password.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                        progressDialog.dismiss();
                                        uId = mAuth.getCurrentUser().getUid();
                                        patients.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child(uId).exists()){
                                                    Intent profileIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                                                    profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(profileIntent);
                                                    finish();

                                                }else {
                                                    Toast.makeText(AuthenticationActivity.this, "Sorry, something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AuthenticationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Toast.makeText(AuthenticationActivity.this, "Sorry, you can't login with empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
