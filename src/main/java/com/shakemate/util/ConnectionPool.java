package com.shakemate.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionPool {
    private static final String DB_NAME = "testshakemate";

    public DataSource getConnPool()  {
        DataSource ds = null;
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/"+ DB_NAME);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return ds;

    }
}
