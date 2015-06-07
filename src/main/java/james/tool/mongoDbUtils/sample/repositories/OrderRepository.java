package james.tool.mongoDbUtils.sample.repositories;


import james.tool.mongoDbUtils.sample.domain.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
/**
 * Created by zhanghong on 15/6/6.
 */
public interface OrderRepository extends MongoRepository<Order, Long> , OrderRepositoryCustom{


}
