package com.sdm.framework.outils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpSession;

public class MySession {

    private final HttpSession httpSession;
    private final Map<String, Object> attributs = new ConcurrentHashMap<>();

    public MySession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void invalidee() {
        httpSession.invalidate();
    }

    public boolean ifContains(String cle) {
        return attributs.containsKey(cle);
    }

    public void supprimer(String cle) {
        attributs.remove(cle);
    }

    public Object get(String cle) {
        return attributs.get(cle);
    }

    public void put(String cle, Object valeur) {
        attributs.put(cle, valeur);
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public Map<String, Object> getAttributs() {
        return attributs;
    }

}
