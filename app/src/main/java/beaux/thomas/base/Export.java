package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.List;


public class Export extends AppCompatActivity {
    public String actNAME;
    public String[] Entry= new String[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        Intent intent = getIntent();
        actNAME = intent.getStringExtra("Activity");
        TextView textView = findViewById(R.id.act_name);
        textView.setText(actNAME);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateScreen();
    }

    public void updateScreen(){
        List<String> Pull = DatabaseHelper.getsInstance(getApplicationContext()).tablesInfo.get(actNAME);
        int n = Pull.size();
        String arr[] = new String[n];
        arr = Pull.toArray(arr);
        int size = arr.length;

        String[] ex_array = DatabaseHelper.getsInstance(getApplicationContext()).returnLastEntry(actNAME);

        TextView[] Stats = new TextView[5];
        TextView stat1 = findViewById(R.id.statN1);
        TextView stat2 = findViewById(R.id.statN2);
        TextView stat3 = findViewById(R.id.statN3);
        TextView stat4 = findViewById(R.id.statN4);
        TextView stat5 = findViewById(R.id.statN5);
        Stats[0] = stat1;
        Stats[1] = stat2;
        Stats[2] = stat3;
        Stats[3] = stat4;
        Stats[4] = stat5;

        TextView[] Entries = new TextView[5];
        TextView R1 = findViewById(R.id.entry1);
        TextView R2 = findViewById(R.id.entry2);
        TextView R3 = findViewById(R.id.entry3);
        TextView R4 = findViewById(R.id.entry4);
        TextView R5 = findViewById(R.id.entry5);
        Entries[0] = R1;
        Entries[1] = R2;
        Entries[2] = R3;
        Entries[3] = R4;
        Entries[4] = R5;
        for (int i = 0; i<size; i++) {
            List<String> P = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(actNAME, arr[i]);
            int c = P.size();
            String a[] = new String[c];
            a = P.toArray(a);
            Entry[i] ="";
            for (String j : a) {
                //System.out.println("THIS IS THE ENTRY: "+j);
                Entry[i] = j +"\n" + Entry[i];
            }
            //System.out.println(Entry[i]);
        }
        if (size == 0) {
            for (int i = 0; i<5 ; i++){
                Stats[i].setVisibility(View.GONE);
            }
        }
        else{
            for (int i = 0; i<size ; i++){
                Stats[i].setVisibility(View.VISIBLE);
                Stats[i].setText(arr[i]);

                if (ex_array.length != 0) {
                    Entries[i].setVisibility(View.VISIBLE);
                    Entries[i].setText(Entry[i]);
                }

            }
            for (int j = size; j < 5 ; j++){
                Stats[j].setVisibility(View.GONE);
                Entries[j].setVisibility(View.GONE);
            }
        }

    }

    public void GoogleDriveAPI(View view){
        //System.out.println("I HAVE CLICKED ON THE Export Button");
        Intent intent = new Intent(this, GoogleDriveAPI.class);
        intent.putExtra("Activity", actNAME);
        startActivity(intent);
    }

}
