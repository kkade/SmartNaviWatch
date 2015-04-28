package ch.hsr.navigationmessagingapi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class NavigationMessage implements Serializable{
    private static final long serialVersionUID = 1L;

    protected String messageType;

    private NavigationMessage() {}

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void fillPayloadFrom(byte[] data){
        this.payload = null;

        try {
            this.payload = fromBytesInternal(data);
        }
        catch (IOException e) {}
        catch (ClassNotFoundException e) {}
    }

    protected Object payload;

    public byte[] payloadToBytes(){
        byte[] d = new byte[0];

        try {
            d = toByteInternal();
        }
        catch (IOException e) {}

        return d;
    }

    protected Object fromBytesInternal(byte[] bytes) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
        ObjectInputStream s =  new ObjectInputStream(bs);
        Object data = s.readObject();
        s.close();
        bs.close();
        return data;
    }

    protected byte[] toByteInternal() throws IOException
    {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream s =  new ObjectOutputStream(bs);
        s.writeObject(payload);
        byte[] data = bs.toByteArray();
        s.close();
        bs.close();
        return data;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public static NavigationMessage create(String messageType, Object payload) {
        NavigationMessage msg = new NavigationMessage();
        msg.setMessageType(messageType);
        msg.setPayload(payload);
        return msg;
    }

    public static NavigationMessage create(String messageType) {
        NavigationMessage msg = new NavigationMessage();
        msg.setMessageType(messageType);
        return msg;
    }

    public static NavigationMessage create(String messageType, byte[] payload) {
        NavigationMessage msg = new NavigationMessage();
        msg.setMessageType(messageType);
        msg.fillPayloadFrom(payload);
        return msg;
    }
}
