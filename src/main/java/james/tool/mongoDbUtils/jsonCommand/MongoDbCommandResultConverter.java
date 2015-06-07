package james.tool.mongoDbUtils.jsonCommand;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.convert.EntityReader;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhanghong on 15/6/7.
 * 把CommandResult转化成List\<outputType\>
 */
public class MongoDbCommandResultConverter {

    MongoDbCommandResultConverter(MongoOperations mongoOperations){
        this.mongoConverter = mongoOperations.getConverter();
    }

    private ApplicationEventPublisher eventPublisher;
    private MongoConverter mongoConverter;

    public <O> List<O> convertAggregationCommandResult(Class<O> outputType, CommandResult commandResult){
        @SuppressWarnings("unchecked")
        Iterable<DBObject> resultSet = (Iterable<DBObject>) commandResult.get("result");
        if (resultSet == null) {
            return Collections.emptyList();
        }

        DbObjectCallback<O> callback = new UnwrapAndReadDbObjectCallback<O>(mongoConverter, outputType);

        List<O> mappedResults = new ArrayList<O>();
        for (DBObject dbObject : resultSet) {
            mappedResults.add(callback.doWith(dbObject));
            System.out.println(mappedResults.get(mappedResults.size() - 1));
        }

        System.out.println("aggregation command result size " + mappedResults.size());

        return mappedResults;
    }

//    public List<? extends Object> convertGroupbyCommandResult(){
//
//    }

    /**
     * 从mongoTemplate中复制
     */

    /**
     * Simple internal callback to allow operations on a {@link DBObject}.
     *
     * @author Oliver Gierke
     * @author Thomas Darimont
     */

    static interface DbObjectCallback<T> {

        T doWith(DBObject object);
    }

    /**
     * Simple {@link DbObjectCallback} that will transform {@link DBObject} into the given target type using the given
     *
     * @author Oliver Gierke
     */
    private class ReadDbObjectCallback<T> implements DbObjectCallback<T> {

        private final EntityReader<? super T, DBObject> reader;
        private final Class<T> type;

        public ReadDbObjectCallback(EntityReader<? super T, DBObject> reader, Class<T> type) {
            Assert.notNull(reader);
            Assert.notNull(type);
            this.reader = reader;
            this.type = type;
        }

        public T doWith(DBObject object) {
            if (null != object) {
                maybeEmitEvent(new AfterLoadEvent<T>(object, type));
            }
            T source = reader.read(type, object);
            if (null != source) {
                maybeEmitEvent(new AfterConvertEvent<T>(object, source));
            }
            return source;
        }
    }

    class UnwrapAndReadDbObjectCallback<T> extends ReadDbObjectCallback<T> {

        public UnwrapAndReadDbObjectCallback(EntityReader<? super T, DBObject> reader, Class<T> type) {
            super(reader, type);
        }

        @Override
        public T doWith(DBObject object) {

            Object idField = object.get(Fields.UNDERSCORE_ID);

            if (!(idField instanceof DBObject)) {
                return super.doWith(object);
            }

            DBObject toMap = new BasicDBObject();
            DBObject nested = (DBObject) idField;
            toMap.putAll(nested);

            for (String key : object.keySet()) {
                if (!Fields.UNDERSCORE_ID.equals(key)) {
                    toMap.put(key, object.get(key));
                }
            }

            return super.doWith(toMap);
        }
    }

    protected <T> void maybeEmitEvent(MongoMappingEvent<T> event) {
        if (null != eventPublisher) {
            eventPublisher.publishEvent(event);
        }
    }
}
