package com.ojr.oceanbase;

import com.ojr.rdb.DbDcAgent;
import com.ojr.rdb.DbDcConfig;

public class Oceanbase4Agent extends DbDcAgent<DbDcConfig, Oceanbase4Dc> {
    public static void main(String[] args) throws Exception {
        Oceanbase4Agent agent = new Oceanbase4Agent();
        agent.initEnv(DbDcConfig.class, Oceanbase4Dc.class);
        agent.initOTelEngine();
        agent.start();
    }

}

