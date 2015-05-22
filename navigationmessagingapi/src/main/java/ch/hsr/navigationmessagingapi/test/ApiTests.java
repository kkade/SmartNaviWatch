import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;

import junit.framework.Assert;

import java.util.List;

import ch.hsr.navigationmessagingapi.IConnectionStateChanged;
import ch.hsr.navigationmessagingapi.IMessageListener;
import ch.hsr.navigationmessagingapi.MapPolygon;
import ch.hsr.navigationmessagingapi.MapPolygonCollection;
import ch.hsr.navigationmessagingapi.MapPolygonTypes;
import ch.hsr.navigationmessagingapi.MessageEndPoint;
import ch.hsr.navigationmessagingapi.NavigationMessage;
import ch.hsr.navigationmessagingapi.PolygonPoint;

public class ApiTests extends AndroidTestCase {

    private class MessageListenerFake implements IMessageListener {
        public String messagePath = "";
        public Object dataFound;

        @Override
        public void messageReceived(NavigationMessage message) {
            messagePath = message.getMessageType();
            dataFound = message.getPayload();
        }
    }

    private class ConnectionListenerFake implements IConnectionStateChanged {
        public boolean EventTriggered = false;

        @Override
        public void onConnectionChanged(MessageEndPoint endPoint) {
            EventTriggered = true;
        }
    }

    public void testEndPointMessageEvents() throws Exception {
        Integer msgData = 9;

        MessageEndPoint ep = new MessageEndPoint(getContext());

        MessageListenerFake listener = new MessageListenerFake();
        ep.addMessageListener(listener);

        NavigationMessage targetMessage = NavigationMessage.create("testPath", msgData);

        ep.onMessageReceived(createFakeEvent(targetMessage.getMessageType(), targetMessage.payloadToBytes()));

        Assert.assertEquals(targetMessage.getMessageType(), listener.messagePath);
        Assert.assertEquals(targetMessage.getPayload(), listener.dataFound);
    }

    public void testEndPointConnectionFailureEvents() throws Exception {
        MessageEndPoint ep = new MessageEndPoint(getContext());

        ConnectionListenerFake fake = new ConnectionListenerFake();
        ep.addConnectionListener(fake);

        // Test failed
        fake.EventTriggered = false;
        ep.onConnectionFailed(null);
        Assert.assertTrue(fake.EventTriggered);

        // Test suspended
        fake.EventTriggered = false;
        ep.onConnectionSuspended(0);
        Assert.assertTrue(fake.EventTriggered);

        // Connected event cannot be testet as it has to be handled by the Google API
    }

    public void testEmptyMessage() throws Exception {
        NavigationMessage msg = NavigationMessage.create("testpath");
        msg.setPayload(null);
        Assert.assertEquals("testpath", msg.getMessageType());
        Assert.assertEquals(msg.getPayload(), null);
    }

    public void testPolygonPoint() throws Exception {
        PolygonPoint p = new PolygonPoint(12,34);
        Assert.assertEquals(12, p.x);
        Assert.assertEquals(34, p.y);
    }

    public void testPolygonBounds() throws Exception {
        PolygonPoint[] out = new PolygonPoint[] { new PolygonPoint(-1,-2), new PolygonPoint(3,4) };
        MapPolygon p = new MapPolygon(MapPolygonTypes.UNKNOWN, out , new PolygonPoint[0][0]);
        Assert.assertEquals(-1, p.getOuterXMin());
        Assert.assertEquals(-2, p.getOuterYMin());
        Assert.assertEquals(3, p.getOuterXMax());
        Assert.assertEquals(4, p.getOuterYMax());
    }

    public void testMapPolygonCollection() throws Exception {
        MapPolygonCollection c = new MapPolygonCollection();
        c.add(new MapPolygon(MapPolygonTypes.UNKNOWN, new PolygonPoint[0], new PolygonPoint[0][0]));
        c.add(new MapPolygon(MapPolygonTypes.BUILDING, new PolygonPoint[0], new PolygonPoint[0][0]));

        List<MapPolygon> l = c.getPolygons();
        Assert.assertEquals(2, l.size());
        Assert.assertEquals(MapPolygonTypes.BUILDING, l.get(1).getType());
        Assert.assertEquals(MapPolygonTypes.UNKNOWN, l.get(0).getType());
    }

    private MessageEvent createFakeEvent(String path, final byte[] data) {
        return new MessageEvent() {
            @Override
            public int getRequestId() {
                return 0;
            }

            @Override
            public String getPath() {
                return "testPath";
            }

            @Override
            public byte[] getData() {
                return data;
            }

            @Override
            public String getSourceNodeId() {
                return null;
            }
        };
    }
}