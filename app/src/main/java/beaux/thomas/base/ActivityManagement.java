package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Arrays;

public class ActivityManagement extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "beaux.thomas.base.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

    }

    public void GatherStatInfo(View view){

        EditText StatName = (EditText) findViewById(R.id.StatName);
        EditText StatType = (EditText) findViewById(R.id.StatType);
        String SN = StatName.getText().toString();
        String ST = StatType.getText().toString();


        // Capture the layout's TextView and set the string as its text
        final TextView textView = findViewById(R.id.textView2);
        textView.setText(SN+ST);

    }

}
