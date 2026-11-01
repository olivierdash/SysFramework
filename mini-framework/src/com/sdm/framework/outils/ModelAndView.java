package com.sdm.framework.outils;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

    private String view;
    private Map<String, Object> data = new HashMap<>();

    public ModelAndView(String view) {
        this.view = view;
    }

    public void addAttribut(String cle, Object valeur) {
        this.data.put(cle, valeur);
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getData() {
        return data;
    }
}