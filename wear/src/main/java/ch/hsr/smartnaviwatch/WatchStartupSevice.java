package ch.hsr.smartnaviwatch;

import android.content.Intent;

import ch.hsr.navigationmessagingapi.services.StartupOnMessageService;

public class WatchStartupSevice extends StartupOnMessageService {
    @Override
    public void onCreation() {
        Intent startMain = new Intent(this, NavigationMain.class);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(startMain);
    }
}
