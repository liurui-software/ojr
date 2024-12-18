package com.ojr.dameng;

import com.ojr.rdb.DbDcAgent;
import com.ojr.rdb.DbDcConfig;

public class DamengAgent extends DbDcAgent<DbDcConfig, DamengDc> {
    public static void main(String[] args) throws Exception {
        DamengAgent agent = new DamengAgent();
        agent.initEnv(DbDcConfig.class, DamengDc.class);
        agent.initOTelEngine();
        agent.start();
    }

}

