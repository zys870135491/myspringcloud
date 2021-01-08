package com.zys.springcloud.entities;

import lombok.Builder;
import lombok.Data;

@Data

public class GateWay {

    private String gateWayId;
    private String path;
    private String uri;

    public GateWay(String gateWayId, String path, String uri) {
        this.gateWayId = gateWayId;
        this.path = path;
        this.uri = uri;
    }
}
