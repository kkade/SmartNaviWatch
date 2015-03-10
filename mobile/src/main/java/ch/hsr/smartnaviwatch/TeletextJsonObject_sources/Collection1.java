package ch.hsr.smartnaviwatch.TeletextJsonObject_sources;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Collection1 implements Serializable {

    @Expose
    private String title;
    @Expose
    private String text;

    /**
     *
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

}
