package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the activity and stattype passed in through the intent
        Intent intent = getIntent();
        String activityName = intent.getStringExtra("Activity");
        String statTypeName = intent.getStringExtra("Stat");

        ListView statisticsList = (ListView) findViewById(R.id.statsList);
        String[] statisticsTypes = new String[] { "Mean", "Median", "Mode",
                "Min", "Max"};
        String[] statisticsValues = new String[] {"0", "0", "0", "0", "0"};

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        List<String> statStrList = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat(activityName, statTypeName);
        List<Integer> statList = new ArrayList<>();
        if(statStrList.size() > 0) {
            for (String stat : statStrList) {
                statList.add(Integer.parseInt(stat));
            }

            Map<String, String> datum1 = new HashMap<String, String>(2);
            datum1.put("type", "Mean");
            datum1.put("value", String.format("%.2f", mean(statList)));
            data.add(datum1);

            Map<String, String> datum2 = new HashMap<String, String>(2);
            datum2.put("type", "Median");
            datum2.put("value", String.format("%.2f", median(statList)));
            data.add(datum2);

            Map<String, String> datum3 = new HashMap<String, String>(2);
            datum3.put("type", "Mode");
            datum3.put("value", String.format("%.2f", mode(statList)));
            data.add(datum3);

            Map<String, String> datum4 = new HashMap<String, String>(2);
            datum4.put("type", "Max");
            datum4.put("value", Integer.toString(Collections.max(statList)));
            data.add(datum4);

            Map<String, String> datum5 = new HashMap<String, String>(2);
            datum5.put("type", "Min");
            datum5.put("value", Integer.toString(Collections.min(statList)));
            data.add(datum5);

            SimpleAdapter mAdapter = new SimpleAdapter(this,
                    data,
                    android.R.layout.simple_list_item_2,
                    new String[]{"type", "value"},
                    new int[]{android.R.id.text1, android.R.id.text2});

            statisticsList.setAdapter(mAdapter);
        }else{
            Map<String, String> datum1 = new HashMap<String, String>(2);
            datum1.put("type", "Mean");
            datum1.put("value", "No stats recorded");
            data.add(datum1);

            Map<String, String> datum2 = new HashMap<String, String>(2);
            datum2.put("type", "Median");
            datum2.put("value", "No stats recorded");
            data.add(datum2);

            Map<String, String> datum3 = new HashMap<String, String>(2);
            datum3.put("type", "Mode");
            datum3.put("value", "No stats recorded");
            data.add(datum3);

            Map<String, String> datum4 = new HashMap<String, String>(2);
            datum4.put("type", "Max");
            datum4.put("value", "No stats recorded");
            data.add(datum4);

            Map<String, String> datum5 = new HashMap<String, String>(2);
            datum5.put("type", "Min");
            datum5.put("value", "No stats recorded");
            data.add(datum5);

            SimpleAdapter mAdapter = new SimpleAdapter(this,
                    data,
                    android.R.layout.simple_list_item_2,
                    new String[]{"type", "value"},
                    new int[]{android.R.id.text1, android.R.id.text2});

            statisticsList.setAdapter(mAdapter);
        }
    }

    private double mean(List<Integer> items){
        double sum = 0;
        for (double item: items){
            sum += item;
        }
        return sum / items.size();
    }

    private double median(List<Integer> items){
        int middle = items.size()/2;
        if (items.size()%2 == 1) {
            return items.get(middle);
        } else {
            return (items.get(middle-1) + items.get(middle)) / 2.0;
        }
    }

    private double mode(List<Integer> items){
        Map<Integer, Integer> hm = new HashMap<>();

        for(int item: items){
            if(hm.containsKey(item)){
                hm.put(item, hm.get(item) + 1);
            }else{
                hm.put(item, 1);
            }
        }

        int maxValue = 0;
        int highestSoFar = 0;
        for(int key: hm.keySet()){
            if(hm.get(key) > maxValue){
                maxValue = hm.get(key);
                highestSoFar = key;
            }
        }
        return highestSoFar;
    }
}
