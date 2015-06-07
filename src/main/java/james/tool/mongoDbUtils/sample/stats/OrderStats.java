package james.tool.mongoDbUtils.sample.stats;

import lombok.Data;

/**
 * Created by zhanghong on 15/6/6.
 */
@Data
public class OrderStats {
    int count;
    int hour;
    int weekDay;
    String date;
}
