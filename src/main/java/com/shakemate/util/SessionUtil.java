package com.shakemate.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    public static Integer getCurrentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false); // false 代表不會自動建立
        if (session == null) return null;

        Object obj = session.getAttribute("account");
        if (obj == null) return null;

        try {
            return Integer.valueOf(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

