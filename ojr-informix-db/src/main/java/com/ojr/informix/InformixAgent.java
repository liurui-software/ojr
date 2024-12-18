package com.ojr.informix;

import com.ojr.rdb.DbDcAgent;
import com.ojr.rdb.DbDcConfig;

public class InformixAgent extends DbDcAgent<DbDcConfig, InformixDc> {
    public static void main(String[] args) throws Exception {
        InformixAgent agent = new InformixAgent();
        agent.initEnv(DbDcConfig.class, InformixDc.class);
        agent.initOTelEngine();
        agent.start();
    }

}

