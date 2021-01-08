package com.zys.springcloud.config;

import com.zys.springcloud.entities.GateWay;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder){

        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
/*        routes.route("path_rote_zys",
                r->r.path("/*")
                        .uri("http://news.baidu.com"));*/


        GateWay gateWay = new GateWay("path_rote_zys","/guonei","http://news.baidu.com");
        GateWay gateWay1 = new GateWay("path_rote_zys1","/game","http://news.baidu.com");
        List<GateWay> list = new ArrayList<>();
        list.add(gateWay);
        list.add(gateWay1);

        for (GateWay way : list) {
         /*   routes.route(way.getGateWayId(),
                    r->r.path(way.getPath())
                            .uri(way.getUri()));*/

            //生成比当前时间早的时间
            //ZonedDateTime minusTime = LocalDateTime.now().minusSeconds (60).atZone(ZoneId.systemDefault());
            //设定时间
            //ZonedDateTime minusTime = LocalDateTime.of(2020, 5, 20, 5, 20, 10,256).atZone(ZoneId.systemDefault());;
            //生成比当前时间晚的时间
            ZonedDateTime minusTime = LocalDateTime.now().plusSeconds(60).atZone(ZoneId.systemDefault());
            routes.route(way.getGateWayId(),
                    r->r.path(way.getPath()).and().after(minusTime)
                            .uri(way.getUri()));
        }

        return routes.build();
    }

}
