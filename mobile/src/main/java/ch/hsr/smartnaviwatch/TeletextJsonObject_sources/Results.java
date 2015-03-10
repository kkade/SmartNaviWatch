package ch.hsr.smartnaviwatch.TeletextJsonObject_sources;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Results {

    @Expose
    private List<Collection1> collection1 = new ArrayList<>();

    /**
     *
     * @return
     *     The collection1
     */
    public List<Collection1> getCollection1() {
        return collection1;
    }

    /**
     *
     * @param collection1
     *     The collection1
     */
    public void setCollection1(List<Collection1> collection1) {
        this.collection1 = collection1;
    }

}
