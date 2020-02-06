package comq.example.raymond.flipcare;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import comq.example.raymond.Interface.ItemClickListener;
import comq.example.raymond.Model.ChatModel;
import comq.example.raymond.flipcare.Utils.FliCareUtils;

public class ChatActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar chatToolbar;
    private String patientName;
    private String healthIssue;

    private FirebaseAuth mAuth;
    private DatabaseReference chats, contacts, patients;
    private String uId = "";

    private FirebaseRecyclerAdapter<ChatModel, ViewHolder>adapter;

    private RecyclerView recycler_chats;
    RecyclerView.LayoutManager layoutManager;

    private TextView txt_greetings, txt_empathy, txt_help;
    private EditText editText_complaint;
    private Button btn_send;

    private FloatingActionButton fab;
    private EditText editTextChat;
    private Button btnSend;

    private TextView txt_about_desease;

    private ChatModel newChat;

    private String userName = "";

    String cancer, heart, nutrition, fever, drugs, diabetes, stress, sight, pain = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        chats = FirebaseDatabase.getInstance().getReference().child("flipcare").child("chats");
        contacts = FirebaseDatabase.getInstance().getReference().child("flipcare").child("contacts");
        patients = FirebaseDatabase.getInstance().getReference().child("flipcare").child("users").child("patients");


//        btn_send = findViewById(R.id.buttonSend);
//        editTextChat = findViewById(R.id.edit_chat);

        recycler_chats= findViewById(R.id.recycler_chats);
        recycler_chats.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_chats.setLayoutManager(layoutManager);


        fab = findViewById(R.id.fab);

        txt_about_desease = findViewById(R.id.txt_brief_about);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_about_desease.setVisibility(View.GONE);
                chat_dialog();

            }
        });



        cancer = "Cancer is a group of diseases involving abnormal cell growth with the potential to invade or spread to other parts of the body. These contrast with benign tumors, which do not spread. Possible signs and symptoms include a lump, abnormal bleeding, prolonged cough, unexplained weight loss, and a change in bowel movements. While these symptoms may indicate cancer, they can also have other causes. Over 100 types of cancers affect humans";
        heart = "The heart is a muscular organ about the size of a fist, located just behind and slightly left of the breastbone. The heart pumps blood through the network of arteries and veins called the cardiovascular system.";
        nutrition = "Nutrition is the science that interprets the interaction of nutrients and other substances in food in relation to maintenance, growth, reproduction, health and disease of an organism. It includes food intake, absorption, assimilation, biosynthesis, catabolism and excretion";
        fever = "Fever is when a human's body temperature goes above the normal range of 36–37° Centigrade (98–100° Fahrenheit). It is a common medical sign. Other terms for a fever include pyrexia and controlled hyperthermia. As the body temperature goes up, the person may feel cold until it levels off and stops rising.";
        drugs = "A drug is any substance that causes a change in an organism's physiology or psychology when consumed. Drugs are typically distinguished from food and substances that provide nutritional support. Consumption of drugs can be via inhalation, injection, smoking, ingestion, absorption via a patch on the skin, or dissolution under the tongue.";
        diabetes = "Diabetes is a disease that occurs when your blood glucose, also called blood sugar, is too high. Blood glucose is your main source of energy and comes from the food you eat. Insulin, a hormone made by the pancreas, helps glucose from food get into your cells to be used for energy.";
        stress = "Stress is a feeling of emotional or physical tension. It can come from any event or thought that makes you feel frustrated, angry, or nervous. Stress is your body's reaction to a challenge or demand. In short bursts, stress can be positive, such as when it helps you avoid danger or meet a deadline.";
        sight = "Sight (also called eyesight or vision) is one of the senses. Having sight means to be able to see. Seeing gives animals knowledge of the world. Some simple animals can only tell light from dark, but with vertebrates, the visual system is able to form images.";
        pain = "Pain is a distressing feeling often caused by intense or damaging stimuli. The International Association for the Study of Pain's widely used definition defines pain as an unpleasant sensory and emotional experience associated with actual or potential tissue damage, or described in terms of such damage.";
        //initialize toolBar
        chatToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat With FlipCare");


        //get crime id from Intent
        if (getIntent() != null){
            patientName = getIntent().getStringExtra("patientName");
            healthIssue = getIntent().getStringExtra("healthIssue");

            //Toast.makeText(this, ""+patientName + " " + healthIssue, Toast.LENGTH_SHORT).show();
        }

        if (healthIssue.equals("Cancer")){
            txt_about_desease.setText(cancer);

        }else if (healthIssue.equals("Heart")){
            txt_about_desease.setText(heart);

        }else if (healthIssue.equals("Nutrition")){
            txt_about_desease.setText(nutrition);

        }else if (healthIssue.equals("Fever")){
            txt_about_desease.setText(fever);

        }else if (healthIssue.equals("Drugs")){
            txt_about_desease.setText(drugs);

        }else if (healthIssue.equals("Diabetes")){
            txt_about_desease.setText(diabetes);

        }else if (healthIssue.equals("Stress")){
            txt_about_desease.setText(stress);

        }else if (healthIssue.equals("Sight")){
            txt_about_desease.setText(sight);

        }else if (healthIssue.equals("Pain")){
            txt_about_desease.setText(pain);

        }

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ChatActivity.this, PatientsChatActivity.class));
//                //chat_dialog();
//            }
//        });

        getUserDetail();

        loadChats();

//
//        btn_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                txt_about_desease.setVisibility(View.GONE);
//                sendChat();
//            }
//        });


    }

    private void getUserDetail() {
        patients.child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("fullName").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadChats() {
        FirebaseRecyclerOptions<ChatModel>options = new FirebaseRecyclerOptions.Builder<ChatModel>()
                .setQuery(chats.orderByChild("uId").equalTo(uId), ChatModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ChatModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatModel model) {
                holder.chat.setText(model.getMessage());


                String reUsername = model.getName();

                if (reUsername.equals(userName)){
                    holder.username.setText("");
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

    private void sendChat() {
        String chat = editTextChat.getText().toString().trim();
        if (TextUtils.isEmpty(chat)){
            Toast.makeText(this, "Input field is empty", Toast.LENGTH_SHORT).show();
        }else {
            final long dateSent = new Date().getTime();

            newChat = new ChatModel(patientName, chat, uId, dateSent);
            patients.push().setValue(newChat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    editTextChat.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }
    }

    private void chat_dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        View view = getLayoutInflater().inflate(R.layout.chat_dialog, null);

         editText_complaint = view.findViewById(R.id.edit_complaint);
         btn_send= view.findViewById(R.id.btn_send);

        //set onclick listener on login button
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText_complaint.getText().toString().isEmpty()){
                    String chat = editText_complaint.getText().toString().trim();
                    final long dateSent = new Date().getTime();

                    newChat = new ChatModel(patientName, chat, uId, dateSent);
                    chats.push().setValue(newChat).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            editText_complaint.setText("");

                            contacts.child(uId).child("userName").setValue(userName);
                            contacts.child(uId).child("uId").setValue(uId);
                            contacts.child(uId).child("timeSent").setValue(dateSent);

                            //startActivity(new Intent(ChatActivity.this, ChatActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }else {
                    Toast.makeText(ChatActivity.this, "Please type a valid complaint", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
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
