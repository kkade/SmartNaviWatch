package ch.hsr.smartnaviwatch;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.navigationmessagingapi.IMessageListener;
import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;
import ch.hsr.navigationmessagingapi.services.ServiceDataKeys;
import ch.hsr.navigationmessagingapi.services.ServiceMessageTypes;
// see: http://www.survivingwithandroid.com/2014/01/android-bound-service-ipc-with-messenger.html
public class NavigationMessagingService extends Service implements IMessageListener {
    private Messenger messenger = new Messenger(new MessageHandler());
    private MessageEndPoint endPoint;
    private List<Messenger> listener;

    @Override
    public void onCreate() {
        super.onCreate();

        endPoint = new MessageEndPoint(getApplicationContext());
        endPoint.addMessageListener(this);

        listener = new ArrayList<Messenger>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void messageReceived(NavigationMessage message) {
        Bundle data = new Bundle();
        data.putString(ServiceDataKeys.MessagePath, message.getMessageType());
        data.putByteArray(ServiceDataKeys.Payload, message.payloadToBytes());

        Message m = Message.obtain(null, ServiceMessageTypes.MessageReceived);
        m.setData(data);
        for(Messenger lst : listener) {
            try { lst.send(m); } catch (RemoteException e) {}
        }
    }

    private class MessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case ServiceMessageTypes.SendMessage:
                    proxyMessage(msg.getData());
                    break;
                case ServiceMessageTypes.RegisterListener:
                    registerCallBack(msg);
                    break;
                case ServiceMessageTypes.RemoveListener:
                    removeCallBack(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void removeCallBack(Message msg) {
        listener.remove(msg.replyTo);
    }

    private void registerCallBack(Message msg) {
        if (!listener.contains(msg.replyTo)) listener.add(msg.replyTo);
    }

    private void proxyMessage(Bundle data) {
        endPoint.sendMessageRaw(data.getString(ServiceDataKeys.MessagePath), data.getByteArray(ServiceDataKeys.Payload));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
