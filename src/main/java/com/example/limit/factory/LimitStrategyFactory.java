package com.example.limit.factory;

import com.example.limit.service.LimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/8/7 - 23:55
 * 限流策略工厂
 *
 * 加载所有的限流策略
 *  方式1：@Component+ApplicationContextAware  根据策略接口类型从IOC容器中获取实现的策略类;或直接注入ApplicationContext
 *  方式2：通过ServiceLoader加载所有的实现类     （实现类使用到其他IOC组件会报空指针异常）
 *  方式3：自定义注解，实现类加上该注解，通过注解获取所有实现类并实例化它们
 */
@Component
public class LimitStrategyFactory /*implements ApplicationContextAware*/ {

    private static List<LimitService> limitServiceList = null;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        Map<String, LimitService> beansOfType = applicationContext.getBeansOfType(LimitService.class);
        limitServiceList = new ArrayList<>(beansOfType.values());
    }

    /**
     * ServiceLoader 加载所有的 LimitService 实现类
     * 此种方式自动创建实现类实例，它的实例不由IOC容器管理，如果该实现类中使用到了IOC容器的其他组件，则会报空指针异常。
     * 此时就需要自己手动new这个组件才能使用，无法自动导入了。
     * META-INF\services\com.example.limit.service.LimitService
     */
//    static {
//        limitServiceList = new ArrayList<>(10);
//        ServiceLoader<LimitService> serviceLoader = ServiceLoader.load(LimitService.class);
//        for (LimitService limitService : serviceLoader) {
//            limitServiceList.add(limitService);
//        }
//    }

    /**
     * 根据类型获取 LimitService 实现类
     * @param type
     * @return
     */
    public static LimitService getLimitService(int type) {
        return limitServiceList.stream()
                .filter(limitService -> limitService.getSupportedType() == type).findFirst().get();
    }


//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        Map<String, LimitService> beansOfType = applicationContext.getBeansOfType(LimitService.class);
//        limitServiceList = new ArrayList<>(beansOfType.values());
//    }
}
