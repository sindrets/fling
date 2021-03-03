package io.github.sindrets.fling;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ArgObject {
    public LinkedHashMap<String, String> flags;
    public ArrayList<String> params;

    public ArgObject(LinkedHashMap<String, String> flags, ArrayList<String> params) {
        this.flags = flags;
        this.params = params;
    }
}
