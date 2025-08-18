package com.redis.replication;

public enum ReplicationState {
    INIT,
    PING_SENT,
    REPLCONF_SENT,
    PSYNC_SENT,
    SYNCED
}
