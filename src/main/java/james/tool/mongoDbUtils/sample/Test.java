package james.tool.mongoDbUtils.sample;

import james.tool.mongoDbUtils.jsonCommand.MongoDbJSONCommandEditor;
import james.tool.mongoDbUtils.jsonCommand.MongoDbJsonCommandExecutor;
import james.tool.mongoDbUtils.sample.domain.Order;
import james.tool.mongoDbUtils.sample.stats.OrderStats;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanghong on 15/6/6.
 */
@ContextConfiguration("spring-config.xml")
public class Test {

    @Resource(name="mongoTemplate")
    protected MongoOperations mongoOperations;

    public void insertOrder(){
        Long i = 0l;
        List<Order> orders = new ArrayList<Order>();
        while(i < 1000) {
            Order order = new Order();
            order.setId(i++);
            order.setDate(new DateTime().minusDays((int) (60 * Math.random())));
            order.setTotal(Math.random() * 100);
            orders.add(order);
        }
        mongoOperations.save(orders);
//        orderRepository.save(orders);
    }

    public void statsOrder(){
        MongoDbJSONCommandEditor commandEditor = MongoDbJSONCommandEditor.fromClassPathFile(
                "james/tool/mongoDbUtils/sample/json/orderGroupByHour.json");

        commandEditor.addQueryCriteria(Criteria.where("date").
                gte(new DateTime().minusDays(10).toDate())
                .lte(new Date()));

        MongoDbJsonCommandExecutor.executeCommand(mongoOperations, commandEditor.toString(), OrderStats.class);

        System.out.println("done");
    }

    public static void main(String[] args){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("james/tool/mongoDbUtils/sample/spring-config.xml");
        Test test = new Test();
        test.mongoOperations = (MongoOperations)applicationContext.getBean("mongoTemplate");

//        test.insertOrder();
        test.statsOrder();
    }
}
