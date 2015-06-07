package james.tool.mongoDbUtils.sample.domain;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by zhanghong on 15/6/6.
 */
@Document
@Data
public class Order {
    @Id
    Long id;
    DateTime date;
    double total;
}
