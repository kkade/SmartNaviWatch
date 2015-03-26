package ch.hsr.navigationmessagingapi.services;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;

public abstract class StartupOnMessageService extends WearableListenerService {

    @Override
    public final void onCreate() {
        super.onCreate();
        onCreation();
    }

    public abstract void onCreation();

    public static NavigationMessage getMessageFromIntent(Intent intent) {
        String path = intent.getStringExtra(ServiceDataKeys.MessagePath);
        byte[] data = intent.getByteArrayExtra(ServiceDataKeys.Payload);
        return (path != null && data != null) ? NavigationMessage.create(path, data) : null;
    }

    public static void fillMessageToIntent(Intent intent, String type, byte[] data) {
        intent.putExtra(ServiceDataKeys.MessagePath, type);
        intent.putExtra(ServiceDataKeys.Payload, data);
    }
}
