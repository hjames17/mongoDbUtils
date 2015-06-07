package james.tool.mongoDbUtils.sample.repositories;

import james.tool.mongoDbUtils.sample.stats.OrderStats;

import java.util.List;

/**
 * Created by zhanghong on 15/6/6.
 */
public interface OrderRepositoryCustom {

    List<OrderStats> groupOrder();
}
