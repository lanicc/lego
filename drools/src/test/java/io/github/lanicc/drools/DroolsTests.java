package io.github.lanicc.drools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Created on 2024/9/13.
 *
 * @author lan
 */
@Slf4j
public class DroolsTests {

    @Test
    void order() {
        // 第一步：初始化一个KieServices
        KieServices kieServices = KieServices.get();
        // 第二步：获取KieContainer会话对象
        KieContainer kieContainer = kieServices.getKieClasspathContainer("rules");
        // 第三步：初始化KieSession
        KieSession kieSession = kieContainer.newKieSession();
        // 构造订单对象，设置订单价格，由规则引擎来计算订单积分
        Order order = new Order(500, 1);
        log.info("执行规则前的订单信息：{}",order);
        //第四步：将数据对象交给规则引擎，规则引擎根据提供的数据进行规则匹配
        kieSession.insert(order);

        // 第五步：激活规则引擎，如果符合规则执行规则
        kieSession.fireAllRules();
        // 第六步：关闭会话
        kieSession.dispose();
        // 结果打印
        log.info("执行规则后的订单信息：{}",order);
    }
}
