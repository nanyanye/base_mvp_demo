package cn.test.demo.app;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityTaskManager {

    private static ActivityTaskManager activityTaskManager = null;

    private List<Activity> activityList = new ArrayList<>();

    private ActivityTaskManager() {
    }

    public static synchronized ActivityTaskManager getInstance() {
        if (activityTaskManager == null) {
            activityTaskManager = new ActivityTaskManager();
        }
        return activityTaskManager;
    }


    public void putActivity(Activity activity) {
        activityList.add(activity);
    }

    public void closeAllActivity() {
        List<Activity> removeList = new ArrayList();
        removeList.addAll(activityList);

        for (Activity activity : removeList) {
            finisActivity(activity);
        }
        activityList.clear();
    }


    public void closeAllActivityExceptOne(String nameSpecified) {

        List<Activity> removeList = new ArrayList<>();
        Activity activitySpecified = null;
        for (Activity activity : activityList) {
            String activityName = activity.getComponentName().getClassName();
            if (activityName.contains(nameSpecified)) {
                activitySpecified = activity;
                continue;
            }
            removeList.add(activity);
        }
        removeListActivity(removeList);

        activityList.clear();

        if(activitySpecified != null) {
            activityList.add(activitySpecified);
        }
    }

    public void backToActivity(String nameSpecified) {
        List<Activity> removeList = new ArrayList<>();
        for (int index = activityList.size() -1;  index >= 0; index--) {
            Activity activity = activityList.get(index);
            String activityName = activity.getComponentName().getClassName();
            if (activityName.contains(nameSpecified)) {
                break;
            }
            if(!activityName.contains("MainActivity")) {
                removeList.add(activity);
            }
        }
        removeListActivity(removeList);
    }

    private void removeListActivity(List<Activity> removeList){
        for(Activity activity: removeList){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    public void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public void removeActivity(String activityClassName){
        for(Activity activity : activityList){
            String className = activity.getComponentName().getClassName();
            if(className.contains(activityClassName)){
                removeActivity(activity);
                break;
            }
        }
    }

    private  void finisActivity(Activity activity) {
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
