package ch.hsr.navigationmessagingapi.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import ch.hsr.navigationmessagingapi.NavigationMessage;

public class NavigationServiceConnector implements ServiceConnection {
    private ServiceConnection sConn;

    private Messenger messenger;

    public boolean isServiceConnected(){
        return messenger != null;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        messenger = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // remember connection
        messenger = new Messenger(service);
    }

    public NavigationServiceConnector(Context ctx)
    {
        // look for service and bind it
        // TODO: Error message if service not found
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

    private void sendAndForget(Message m) {
        try {
            messenger.send(m);
        } catch (RemoteException e) {} // we don't really care if every single message is sent out
    }
}
