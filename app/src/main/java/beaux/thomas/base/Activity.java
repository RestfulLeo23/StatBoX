package beaux.thomas.base;

import java.lang.reflect.Array;

/**
 * Created by Thomas on 3/8/2018.
 */

public class Activity {

    // fields
    private String[] StatType;
    private String[] StatName;
    private String ActivityName;

    // constructor
    public Activity(String activityName, String[] statName, String[] statType) {
        this.ActivityName = activityName;
        this.StatName = statName;
        this.StatType = statType;
    }

    // properties

    //Setting and getting the Activity Name
    public void setActivityName(String actName) {
        this.ActivityName = actName;
    }

    public String getActivityName(){
        return this.ActivityName;
    }


    public String[][] getAll(){
        int len = this.StatType.length;
        String[][] Act = new String[len][len];

        Act[0][0] = this.ActivityName;
        for (int i = 0 ; i < len ; i++){
            Act[1][i] = this.StatName[i];
        }
        for (int i = 0 ; i < len ; i++){
            Act[2][i] = this.StatType[i];
        }
        return Act;
    }

    //Setting and getting the specific Stat Name
    public void setStatName(String OldstatName, String NewstatName) {
        for (int i = 1; i < this.StatName.length;i++){
            if (OldstatName == this.StatName[i]){
                this.StatName[i]= NewstatName;
            }
        }
    }

    public String[] getStatName() {
        return this.StatName;
    }

    //Setting and getting the specific Stat Type
    public void setStatType(String OldstatType, String NewstatType) {
        for (int i = 1; i < this.StatType.length;i++){
            if (OldstatType == this.StatType[i]){
                this.StatType[i]= NewstatType;
            }
        }
    }

    public String getStatType(String WantstatType) {
        for (int i = 1; i< this.StatName.length; i++) {
            if (WantstatType == this.StatType[i]) {
                return this.StatType[i];
            }
        }

        return "ERROR NOT FOUND";
    }


}