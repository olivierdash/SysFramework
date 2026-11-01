package com.sdm.framework.listener;

import java.util.Map;

import com.sdm.framework.outils.MySession;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class SessionCleanupListener implements HttpSessionListener {

    @Override
    @SuppressWarnings("unchecked")
    public void sessionDestroyed(HttpSessionEvent event) {
        Map<String, MySession> sessions = (Map<String, MySession>) event.getSession().getServletContext()
                .getAttribute("mySessions");
        if (sessions != null) {
            sessions.remove(event.getSession().getId());
        }
    }
}
