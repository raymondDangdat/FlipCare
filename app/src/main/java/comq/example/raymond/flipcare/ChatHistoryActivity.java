package comq.example.raymond.flipcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ChatHistoryActivity extends AppCompatActivity {
    private Toolbar chatHistoryToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        //initialize toolBar
        chatHistoryToolbar = findViewById(R.id.chat_history_toolbar);
        setSupportActionBar(chatHistoryToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat History Toolbar");
    }
}
