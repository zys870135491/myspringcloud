package com.zys.springcloud.config;

import com.alibaba.fastjson.JSONArray;
import com.zys.springcloud.entities.GateWay;
import com.zys.springcloud.entities.GateWayRoute;
import lombok.SneakyThrows;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class GatewayRoutersConfiguration implements RouteDefinitionRepository {

    public static final ZonedDateTime minusTime = LocalDateTime.now().plusSeconds(10).atZone(ZoneId.systemDefault());
    public static final String weifuwuming = "sentinel-nacos-payment-provider";
    public static final String uri = "lb://sentinel-nacos-payment-provider";
    //断言里写了Path和After通过（[{'args':{'_genkey_i':'','_genkey_i':''},'name':断言}]）
    public static final String preidcates =
            " [{'args':{'_genkey_0':'/payment/get/**','_genkey_1':'/paymentSQL/**'},'name':'Path'},{'args':{'_genkey_0':'"+minusTime+"'},'name':'After'}] ";


    //https://blog.csdn.net/yu_kang/article/details/100092967
    @SneakyThrows
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> ls = new ArrayList<>();
        GateWayRoute gateWay = new GateWayRoute
                (weifuwuming,weifuwuming,uri,preidcates,"");
        List<GateWayRoute> list = new ArrayList<>();
        list.add(gateWay);
        for (GateWayRoute way : list) {
            RouteDefinition route = new RouteDefinition();
            route.setId(way.getName());
            route.setUri(new URI(way.getUri()));

            JSONArray predicatesArray = JSONArray.parseArray(gateWay.getPredicates());
            List<PredicateDefinition> predicateDefinitionList =
                    predicatesArray.toJavaList(PredicateDefinition.class);
            route.setPredicates(predicateDefinitionList);
           /* if (gateWay.getFilters() != null || gateWay.getFilters() != "") {
                JSONArray filtersArray = JSONArray.parseArray(gateWay.getFilters());
                List<FilterDefinition> filterDefinitionList
                        = filtersArray.toJavaList(FilterDefinition.class);
                route.setFilters(filterDefinitionList);
            }*/
            ls.add(route);
        }
        System.out.println("getRouteDefinitions：-----------");
        return Flux.fromIterable(ls);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return null;
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return null;
    }
}
