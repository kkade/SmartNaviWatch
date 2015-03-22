package ch.hsr.navigationmessagingapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;


public class MessageEndPoint implements GoogleApiClient.ConnectionCallbacks
                                      , GoogleApiClient.OnConnectionFailedListener
                                      , MessageApi.MessageListener
{
// TODO:http://toastdroid.com/2014/08/18/messageapi-simple-conversations-with-android-wear/
    // Try registering

    /**
     * <service android:name=".DataLayerListenerService">
     <intent-filter>
     <action android:name="com.google.android.gms.wearable.BIND_LISTENER" />
     </intent-filter>
     </service>
    */
    private GoogleApiClient messageApi;
    private List<IConnectionStateChanged> connectionListeners;
    private List<IMessageListener> messageListeners;
    private Node wearNode = null;

    public MessageEndPoint(Context context) {
        messageApi = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        messageApi.registerConnectionCallbacks(this);
        messageApi.registerConnectionFailedListener(this);
        messageApi.connect();

        connectionListeners = new ArrayList<IConnectionStateChanged>();
        messageListeners = new ArrayList<IMessageListener>();
    }

    public boolean isApiReady() {
        return messageApi != null && messageApi.isConnected() && wearNode != null;
    }

    public void addConnectionListener(IConnectionStateChanged l) {
        if(!connectionListeners.contains(l)) connectionListeners.add(l);
    }

    public void removeConnectionListener(IConnectionStateChanged l) {
        connectionListeners.remove(l);
    }

    public void addMessageListener(IMessageListener l) {
        if(!messageListeners.contains(l)) messageListeners.add(l);
    }

    public void removeMessageListener(IMessageListener l) {
        messageListeners.remove(l);
    }

    public void sendMessage(NavigationMessage msg) {
        if (!isApiReady()) return;

        sendMessageRaw(msg.getMessageType(), msg.payloadToBytes());
    }

    public void sendMessageRaw(String path, byte[] data) {
        Wearable.MessageApi.sendMessage(messageApi, wearNode.getId(), path, data);
    }

    private void notifyListeners() {
        for (IConnectionStateChanged l : connectionListeners) {
            l.onConnectionChanged(this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener( messageApi, this);

        Wearable.NodeApi.getConnectedNodes(messageApi).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult result) {
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0)
                {
                    wearNode = nodes.get(0);
                }
                notifyListeners();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        wearNode = null;
        notifyListeners();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        wearNode = null;
        notifyListeners();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        NavigationMessage msg = new NavigationMessage();
        msg.setMessageType(messageEvent.getPath());
        msg.fillPayloadFrom(messageEvent.getData());

        for(IMessageListener l : messageListeners) {
            l.messageReceived(msg);
        }
    }
}
