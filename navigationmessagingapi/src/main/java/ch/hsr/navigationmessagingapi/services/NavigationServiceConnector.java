package ch.hsr.navigationmessagingapi.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import ch.hsr.navigationmessagingapi.IMessageListener;
import ch.hsr.navigationmessagingapi.NavigationMessage;

public class NavigationServiceConnector implements ServiceConnection {
    private Messenger outgoingMessages;
    private Messenger incomingMessages;
    private List<IMessageListener> listeners;

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch(type)
            {
                case ServiceMessageTypes.MessageReceived:
                    relayNavigationMessageToListeners(msg);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void relayNavigationMessageToListeners(Message msg) {
        Bundle b = msg.getData();
        NavigationMessage out = NavigationMessage.create(b.getString(ServiceDataKeys.MessagePath), b.getByteArray(ServiceDataKeys.Payload));

        for(IMessageListener l : listeners) {
            l.messageReceived(out);
        }
    }

    public boolean isServiceConnected(){
        return outgoingMessages != null;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        outgoingMessages = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // remember connection
        outgoingMessages = new Messenger(service);

        // receiving messenger for incoming messages
        incomingMessages = new Messenger(new IncomingHandler());

        // Register as listener to make sure no listeners were missed before
        // the service was connected
        if (listeners.size() >= 0) {
            RegisterAsListener();
        }
    }

    public NavigationServiceConnector(Context ctx)
    {
        listeners = new ArrayList<IMessageListener>();

        // look for service and bind it
        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName("ch.hsr.smartnaviwatch", "ch.hsr.smartnaviwatch.NavigationMessagingService"));

        ctx.bindService(serviceIntent, this,Context.BIND_AUTO_CREATE);
    }

    public void sendMessage(NavigationMessage msg) {
        if (!isServiceConnected()) return;

        Message envelope = Message.obtain(null, ServiceMessageTypes.SendMessage);
        Bundle b = new Bundle();
        b.putString(ServiceDataKeys.MessagePath, msg.getMessageType());
        b.putByteArray(ServiceDataKeys.Payload, msg.payloadToBytes());
        envelope.setData(b);

        sendAndForget(envelope);
    }

    public void addMessageListener(IMessageListener l) {
        if(!listeners.contains(l)) listeners.add(l);

        if (listeners.size() >= 0) {
            RegisterAsListener();
        }
    }

    private void RegisterAsListener() {
        Message m = Message.obtain(null, ServiceMessageTypes.RegisterListener);
        m.replyTo = incomingMessages;
        sendAndForget(m);
    }

    public void removeMessageListener(IMessageListener l) {
        listeners.remove(l);

        if (listeners.size() == 0) {
            DeRegisterAsListener();
        }
    }

    private void DeRegisterAsListener() {
        Message m = Message.obtain(null, ServiceMessageTypes.RemoveListener);
        m.replyTo = incomingMessages;
        sendAndForget(m);
    }

    private void sendAndForget(Message m) {
        if (outgoingMessages == null) return;

        try {
            outgoingMessages.send(m);
        } catch (RemoteException e) {} // we don't really care if every single message is sent out
    }
}
