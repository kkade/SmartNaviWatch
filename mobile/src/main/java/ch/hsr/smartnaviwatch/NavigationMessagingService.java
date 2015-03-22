package ch.hsr.smartnaviwatch;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import ch.hsr.navigationmessagingapi.IMessageListener;
import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;
import ch.hsr.navigationmessagingapi.services.ServiceDataKeys;
import ch.hsr.navigationmessagingapi.services.ServiceMessageTypes;
// see: http://www.survivingwithandroid.com/2014/01/android-bound-service-ipc-with-messenger.html
public class NavigationMessagingService extends Service implements IMessageListener {
    private Messenger messenger = new Messenger(new MessageHandler());
    private MessageEndPoint endPoint;

    @Override
    public void onCreate() {
        super.onCreate();
        endPoint = new MessageEndPoint(getApplicationContext());
        endPoint.addMessageListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void messageReceived(NavigationMessage message) {
        // TODO: Redirect messages to registered applications
    }

    private class MessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case ServiceMessageTypes.SendMessage:
                    proxyMessage(msg.getData());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void proxyMessage(Bundle data) {
        endPoint.sendMessageRaw(data.getString(ServiceDataKeys.MessagePath), data.getByteArray(ServiceDataKeys.Payload));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
