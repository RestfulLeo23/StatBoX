package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ViewActivity extends AppCompatActivity {
    public static final int GET_NEW_STAT = 2;
    public static final int OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(HomeScreen.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.StatName);
        textView.setText(message);

    }

    /** Called when the user taps the Send button */
    public void InputStatMode(View view) {
        Intent intent = new Intent(this, InputStat.class);
        startActivityForResult(intent,GET_NEW_STAT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (GET_NEW_STAT): {
                if (resultCode == OK) {
                    // TODO Extract the data returned from the child Activity.
                    String[] returnValue = data.getStringArrayExtra("STAT");
                    String sV = returnValue[1];
                    String sT = returnValue[2];
                    // Ideally here is where ill add the stat to the activity and then store it in
                    // the DB.
                    break;
                }
            }
        }
    }


}
