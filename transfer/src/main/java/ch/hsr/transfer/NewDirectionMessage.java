package ch.hsr.transfer;

/**
 * Created by Admin on 22.02.2015.
 */
public class NewDirectionMessage {
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public byte[] getBytes() {
        return msg.getBytes();
    }

    public NewDirectionMessage(byte[] bytes) {
        setMsg(new String(bytes));
    }

    public NewDirectionMessage() {
    }

    private String msg;
}
