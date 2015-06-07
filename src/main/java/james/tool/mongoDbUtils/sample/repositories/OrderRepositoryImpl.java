package james.tool.mongoDbUtils.sample.repositories;

import james.tool.mongoDbUtils.jsonCommand.MongoDbJSONCommandEditor;
import james.tool.mongoDbUtils.jsonCommand.MongoDbJsonCommandExecutor;
import james.tool.mongoDbUtils.sample.stats.OrderStats;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * Created by zhanghong on 15/6/6.
 */
public class OrderRepositoryImpl implements OrderRepositoryCustom {


//    @Resource(name = "mongoTemplate")
    @Autowired
    protected MongoOperations mongoOperations;




    public List<OrderStats> groupOrder() {
        MongoDbJSONCommandEditor commandEditor = MongoDbJSONCommandEditor.fromClassPathFile("james/exercise/test/json/orderGroupByHour.json");
        commandEditor.addQueryCriteria(Criteria.where("date").
                gte(new DateTime().minusDays(10).toDate())
                .lte(new DateTime().toDate()));
        return MongoDbJsonCommandExecutor.executeCommand(mongoOperations, commandEditor.toString(), OrderStats.class);
    }
}
