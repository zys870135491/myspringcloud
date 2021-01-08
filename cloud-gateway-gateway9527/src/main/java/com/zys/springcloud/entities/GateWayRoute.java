package com.zys.springcloud.entities;

import lombok.Data;

@Data
public class GateWayRoute {
    private String id;
    private String name;
    private String uri;
    private String predicates;
    private String filters;

    public GateWayRoute(String id, String name, String uri, String predicates, String filters) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.predicates = predicates;
        this.filters = filters;
    }
}
