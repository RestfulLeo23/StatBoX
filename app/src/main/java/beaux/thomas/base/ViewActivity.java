package beaux.thomas.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

public class ViewActivity extends AppCompatActivity {
    public static final int GET_NEW_STAT = 2;
    public static final int OK = 1;
    final Context context = this;
    private Button button;

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

        updateStatScreen();

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
    public void updateStatScreen() {

        TextView textView = findViewById(R.id.StatName);

        List<String> Pull = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(textView.getText().toString());
        int n = Pull.size();
        String arr[] = new String[n];
        arr = Pull.toArray(arr);
        int size = arr.length;

        TextView[] Stats = new TextView[5];
        TextView stat1 = findViewById(R.id.Stat1);
        TextView stat2 = findViewById(R.id.Stat2);
        TextView stat3 = findViewById(R.id.Stat3);
        TextView stat4 = findViewById(R.id.Stat4);
        TextView stat5 = findViewById(R.id.Stat5);
        Stats[0] = stat1;
        Stats[1] = stat2;
        Stats[2] = stat3;
        Stats[3] = stat4;
        Stats[4] = stat5;

        if (size == 0) {
            for (int i = 0; i<5 ; i++){
                Stats[i].setVisibility(View.GONE);
            }
        }
        else{
            for (int i = 0; i<size ; i++){
                Stats[i].setVisibility(View.VISIBLE);
                Stats[i].setText(arr[i]);
            }
            for (int j =size; j < 5 ; j++){
                Stats[j].setVisibility(View.GONE);
            }
        }


    }

}
