package ch.hsr.smartnaviwatch;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;

import ch.hsr.navigationmessagingapi.services.StartupOnMessageService;

public class WatchStartupSevice extends StartupOnMessageService {
    @Override
    public void onCreation() {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Intent startMain = new Intent(this, NavigationMain.class);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        StartupOnMessageService.fillMessageToIntent(startMain, messageEvent.getPath(), messageEvent.getData());
        getApplication().startActivity(startMain);

        super.onMessageReceived(messageEvent);
    }
}
