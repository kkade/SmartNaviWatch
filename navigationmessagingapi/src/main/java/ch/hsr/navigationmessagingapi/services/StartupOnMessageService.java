package ch.hsr.navigationmessagingapi.services;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import ch.hsr.navigationmessagingapi.MessageEndPoint;

public abstract class StartupOnMessageService extends WearableListenerService {

    @Override
    public final void onCreate() {
        super.onCreate();
        onCreation();
    }

    public abstract void onCreation();
}
