# myspringcloud
1.项目顺序
1.cloud-provider-payment8001(微服务模块的提供者)
2.cloud-consumer-order80(微服务模块的消费者)
2.项目解释
1.cloud-provider-payment8001(微服务模块的提供者)
提供服务的
2.cloud-consumer-order80(微服务模块的消费者)
####2.cloud-consumer-order80(微服务模块的消费者)

用来消费cloud-provider-payment8001提供的服务
通过restTemplate调用服务
public static final String PAYMENT_URL = "http://localhost:8001";

   @Resource
   private RestTemplate restTemplate;

   @GetMapping("/consumer/payment/create")
   public CommonResult<Payment>   create(Payment payment){
       return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);  //写操作
   }

   @GetMapping("/consumer/payment/get/{id}")
   public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
       return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
   }
3.cloud-api-common(微服务模块的公用包)
发现cloud-provider-payment8001(微服务模块的提供者)和cloud-consumer-order80(微服务模块的消费者)有代码重复

cloud-api-common为服务提供公共的包

在其它服务的pom.xml中引入

<dependency>
    <groupId>com.zys.springcloud</groupId>
    <artifactId>cloud-api-commons</artifactId>
    <version>${project.version}</version>
</dependency>
4.cloudalibaba-nacos-provider-payment9001(Nacos 微服务的提供者)
启动类

package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient //服务注册
@SpringBootApplication
public class PaymentMain9001 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain9001.class,args);
    }
}
配置文件

server:
  port: 9001

spring:
  application:
    name: nacos-payment-provider    # 服务名称
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848 #配置Nacos地址

management:
  endpoints:
    web:
      exposure:
        include: '*'
Nacos启动之后（图片：4-1）

image-20201229200232981

5.cloudalibaba-nacos-provider-payment9002(Nacos 微服务的提供者)
启动类

package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class PaymentMain9002 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain9002.class,args);
    }
}
配置文件

server:
  port: 9002

spring:
  application:
    name: nacos-payment-provider    # 服务名称
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848 #配置Nacos地址

management:
  endpoints:
    web:
      exposure:
        include: '*'
Nacos启动之后（图片：5-1）

启动了cloudalibaba-nacos-provider-payment9001和cloudalibaba-nacos-provider-payment9002，nacos里的微服务nacos-payment-provider有两个提供者，消费者在访问时会轮询访问这两个提供者(ribbon的默认轮询机制)

5-1

6.cloudalibaba-nacos-consumer-order90(Nacos 微服务的消费者)
启动类
package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient //服务注册
@SpringBootApplication
public class OrderNacosMain90
{
    public static void main(String[] args)
    {
        SpringApplication.run(OrderNacosMain90.class,args);
    }
}
配置文件
server:
  port: 90

spring:
  application:
    name: nacos-order-consumer
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848

service-url:
  nacos-user-service: http://nacos-payment-provider    // 提供者的服务名
7.cloudalibaba-nacos-config-client3377(Nacos 作为配置中心)
nacos作为config配置中心来使用，通过namesapce和group区分，并且通过springcloud原生注解实现配置自动更新(仅适用于当前bean，属于懒加载)

启动类

package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient      //服务注册
@SpringBootApplication
public class NacosConfigClientMain3377
{
    public static void main(String[] args) {
        SpringApplication.run(NacosConfigClientMain3377.class, args);
    }
}
controller

package com.zys.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope       //通过springcloud原生注解实现配置自动更新(仅适用于当前bean，属于懒加载)
public class ConfigClientController
{
    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/config/info")
    public String getConfigInfo() {
        return configInfo;
    }
}
配置文件

server:
  port: 3377

spring:
  application:
    name: nacos-config-client
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 #服务注册中心地址
      config:
        server-addr: localhost:8848 #配置中心地址
        file-extension: yaml #指定yaml格式的配置
        group: DEV_GROUP      #通过group区分环境
        namespace: e4391d6d-04ec-4083-bc54-f5f532f4268b  #通过namespace区分环境
  profiles:
    active: dev     #文件的后缀

    #通过这样的形式拼接{spring.application.name}-#{spring.profiles.active}-#{spring.cloud.nacos.config.file-extension}
nacos配置

7-1

8.cloudalibaba-sentinel-service8401(sentinel和nacos一起实现熔断和限流)
sentinel属于懒加载，只有访问了接口才能在控制台看出结果(同时可以自定义sentinel的兜底方法)

启动类

package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient   //服务注册
@SpringBootApplication
public class MainApp8401
{
    public static void main(String[] args) {
        SpringApplication.run(MainApp8401.class, args);
    }
}
配置文件

server:
  port: 8401

spring:
  application:
    name: cloudalibaba-sentinel-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    sentinel:
      transport:
        dashboard: localhost:8080
        port: 8719  #默认8719，假如被占用了会自动从8719开始依次+1扫描。直至找到未被占用的端口

management:
  endpoints:
    web:
      exposure:
        include: '*'
9.cloudalibaba-sentinel-nacos-provider-payment9003(sentinel整合openfeign+fallback的提供者)
启动类

package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class PaymentMain9003
{
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain9003.class, args);
    }
}
配置文件

server:
  port: 9003

spring:
  application:
    name: sentinel-nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 #配置Nacos地址

management:
  endpoints:
    web:
      exposure:
        include: '*'
springcloud 微服务之间传递token解决方案

//springcloud 微服务之间传递token解决方案
//这里可以使用Feign的RequestInterceptor，把request里的请求参数包括请求头全部复制到feign的request里
@Configuration
public class FeginInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            Map<String,String> headers = getHeaders();
            for(String headerName : headers.keySet()){
                requestTemplate.header(headerName, headers.get(headerName));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 调用的微服务里可以这样取到header里的数据
    private Map<String, String> getHeaders(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
10.cloudalibaba-sentinel-nacos-provider-payment9004(sentinel整合openfeign+fallback的提供者)
启动类

package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class PaymentMain9004
{
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain9004.class, args);
    }
}
配置文件

server:
  port: 9004

spring:
  application:
    name: sentinel-nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 #配置Nacos地址

management:
  endpoints:
    web:
      exposure:
        include: '*'
11.cloudalibaba-sentinel-nacos-consumer-order84(sentinel整合openfeign+fallback的消费者)
sentinel自定义兜底方法（一定要和controller层方法的参数保持一致(@PathVariable("id") Long id 一定要加)，方法一定要加static）

启动类

package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableDiscoveryClient      //服务注册
@SpringBootApplication
@EnableFeignClients         //openfeign
public class OrderNacosMain84
{
    public static void main(String[] args) {
        SpringApplication.run(OrderNacosMain84.class, args);
    }
}
配置文件

server:
  port: 84


spring:
  application:
    name: sentinel-nacos-order-consumer
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    sentinel:
      transport:
        dashboard: localhost:8080
        port: 8719

service-url:
  nacos-user-service: http://sentinel-nacos-payment-provider

#对Feign的支持
feign:
  sentinel:
    enabled: true
controller层

@RestController
public class PaymentController {

    // OpenFeign
    @Resource
    private PaymentService paymentService;
    
    @GetMapping(value = "/consumer/paymentSQL/{id}")
    @SentinelResource(value = "customerBlockHandler",
            blockHandlerClass = CustomerBlockHandler.class,
            blockHandler = "handleException")
    public CommonResult<Payment> paymentSQL(@PathVariable("id") Long id) {
        return paymentService.paymentSQL(id);
    }

    public CommonResult handleException(BlockException exception) {
        return new CommonResult(444, exception.getClass().getCanonicalName() + "\t 服务不可用");
    }

}
handler（sentinel自定义兜底方法）

package com.zys.springcloud.fallbackService;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zys.springcloud.entities.*;
import org.springframework.web.bind.annotation.PathVariable;

public class CustomerBlockHandler {

    //一定要和controller层方法的参数保持一致(@PathVariable("id") Long id 一定要加)，方法一定要加static
    public static CommonResult handleException(@PathVariable("id") Long id,BlockException exception) {
        return new CommonResult(2020, "自定义限流处理信息....CustomerBlockHandler");

    }
}
fallback

package com.zys.springcloud.fallbackService;

import com.zys.springcloud.entities.CommonResult;
import com.zys.springcloud.entities.Payment;
import com.zys.springcloud.service.PaymentService;
import org.springframework.stereotype.Component;

@Component
public class PaymentFallbackService implements PaymentService
{
    @Override
    public CommonResult<Payment> paymentSQL(Long id)
    {
        return new CommonResult<>(44444,"服务降级返回,---PaymentFallbackService",new Payment(id,"errorSerial"));
    }
}
12.cloud-gateway-gateway9527(gateWay网关)
启动类

package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient      //服务注册
public class GateWayMain9527 {
    public static void main(String[] args) {
        SpringApplication.run( GateWayMain9527.class,args);
    }
}
配置文件书写网关

server:
  port: 9527
spring:
  application:
    name: cloud-gateway
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      routes:
      - id: payment_routh #路由的ID，没有固定规则但要求唯一，建议配合服务名
        uri: http://localhost:8001   #匹配后提供服务的路由地址
        predicates:
        - Path=/payment/get/**   #断言,路径相匹配的进行路由

      - id: payment_routh3
        uri: http://localhost:8001
        predicates:
        - Path=/payment/find/**   #断言,路径相匹配的进行路由
通过java代码书写网关

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
            System.out.println(minusTime);
            routes.route(way.getGateWayId(),
                    r->r.path(way.getPath()).and().after(minusTime)
                            .uri(way.getUri()));
        }

        return routes.build();
    }

}
通过实现RouteDefinitionRepository类书写网关

@Component
public class GatewayRoutersConfiguration implements RouteDefinitionRepository {

    public static final ZonedDateTime minusTime = LocalDateTime.now().plusSeconds(10).atZone(ZoneId.systemDefault());
    public static final String weifuwuming = "sentinel-nacos-payment-provider";
    public static final String uri = "lb://sentinel-nacos-payment-provider";
    //断言里写了Path和After通过（[{'args':{'_genkey_i':'','_genkey_i':''},'name':断言}]）
    public static final String preidcates =
            " [{'args':{'_genkey_0':'/payment/get/**','_genkey_1':'/paymentSQL/**'},'name':'Path'},{'args':	{'_genkey_0':'"+minusTime+"'},'name':'After'}] ";


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
开启动态路由，利用微服务名进行路由

server:
  port: 9527
spring:
  application:
    name: cloud-gateway
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    gateway:
      discovery:
        locator:
          enabled: true  #开启从注册中心动态创建路由的功能，利用微服务名进行路由
      routes:
      - id: payment_routh #路由的ID，没有固定规则但要求唯一，建议配合服务名
        uri: lb://sentinel-nacos-payment-provider
        predicates:
        - Path=/paymentSQL/*   #断言,路径相匹配的进行路由
​

gateWay的filter使用

必须实现GlobalFilter,Ordered两个接口

@Component
@Slf4j
public class MyLogGateWayFilter implements GlobalFilter,Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("*********come in MyLogGateWayFilter: "+new Date());
        String uname = exchange.getRequest().getQueryParams().getFirst("username");
        if(StringUtils.isEmpty(uname)){
            log.info("*****用户名为Null 非法用户,(┬＿┬)");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);//给人家一个回应
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
