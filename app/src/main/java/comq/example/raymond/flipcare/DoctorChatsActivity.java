package comq.example.raymond.flipcare;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import comq.example.raymond.Interface.ItemClickListener;
import comq.example.raymond.Model.ChatModel;
import comq.example.raymond.flipcare.Utils.FliCareUtils;

public class DoctorChatsActivity extends AppCompatActivity {
    private Toolbar chatsToolBar;

    private String uId = "";
    private FloatingActionButton fab;

    private EditText editText_reply;
    private Button btn_send;

    private ChatModel newChat;


    private DatabaseReference chats, contacts, patients;

    private TextView txt_p_name;

    String p_name = "";


    private RecyclerView recycler_chats;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<ChatModel, ViewHolder >adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_chats);

        fab = findViewById(R.id.fab);


        recycler_chats= findViewById(R.id.recycler_chats);
        recycler_chats.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_chats.setLayoutManager(layoutManager);

        txt_p_name = findViewById(R.id.txt_patient_name);

        chats = FirebaseDatabase.getInstance().getReference().child("flipcare").child("chats");
        contacts = FirebaseDatabase.getInstance().getReference().child("flipcare").child("contacts");
        patients = FirebaseDatabase.getInstance().getReference().child("flipcare").child("users").child("patients");

        //initialize toolBar
        chatsToolBar = findViewById(R.id.chats_toolbar);
        setSupportActionBar(chatsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chats");

        //get crime id from Intent
        if (getIntent() != null){
            uId = getIntent().getStringExtra("uId");
            //Toast.makeText(this, ""+uId, Toast.LENGTH_SHORT).show();

            if (!uId.isEmpty()){
                getUserChats(uId);

                getUserName();

            }

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_dialog();

            }
        });



    }

    private void getUserName() {
        contacts.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                p_name = dataSnapshot.child("userName").getValue(String.class);
                txt_p_name.setText(p_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void chat_dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorChatsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.chat_dialog, null);

        editText_reply = view.findViewById(R.id.edit_complaint);
        btn_send= view.findViewById(R.id.btn_send);

        //set onclick listener on login button
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText_reply.getText().toString().isEmpty()){
                    String chat = editText_reply.getText().toString().trim();
                    final long dateSent = new Date().getTime();
                    String patientName = "FlipCare";

                    newChat = new ChatModel(patientName, chat, uId, dateSent);
                    chats.push().setValue(newChat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            editText_reply.setText("");

//                            contacts.child(uId).child("userName").setValue(userName);
//                            contacts.child(uId).child("uId").setValue(uId);
//                            contacts.child(uId).child("timeSent").setValue(dateSent);

                            //startActivity(new Intent(ChatActivity.this, ChatActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DoctorChatsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }else {
                    Toast.makeText(DoctorChatsActivity.this, "Please type a valid complaint", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getUserChats(String uId) {
        FirebaseRecyclerOptions<ChatModel>options = new FirebaseRecyclerOptions.Builder<ChatModel>()
                .setQuery(chats.orderByChild("uId").equalTo(uId), ChatModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ChatModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatModel model) {
                //holder.username.setText(model.getName());
                holder.chat.setText(model.getMessage());

                String reUsername = model.getName();

                if (reUsername.equals("FlipCare")){
                    holder.username.setText("You");
                }else {
                    holder.username.setText(model.getName());
                }


                holder.dateSent.setText(FliCareUtils.dateFromLong(model.getDateSent()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_layout, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_chats.setAdapter(adapter);
        adapter.startListening();

    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView username, dateSent, chat;
        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.txt_name);
            dateSent = itemView.findViewById(R.id.txt_date);
            chat = itemView.findViewById(R.id.txt_chat);

            itemView.setOnClickListener(this);
        }


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
