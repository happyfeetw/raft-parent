package com.spike.raft.net;

import java.util.Objects;

public class Address {
    private final String host;
    private final int port;

    public Address (String host, int port) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(port);
        this.host = host;
        this.port = port;
    }
}
