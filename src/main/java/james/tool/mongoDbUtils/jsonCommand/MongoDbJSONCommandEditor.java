package james.tool.mongoDbUtils.jsonCommand;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;

import java.io.IOException;

/**
 * Created by zhanghong on 15/6/7.
 * 命令编辑器，并返回json命令字符串
 * 1. 插入查询条件 addQueryCriteria
 */
public class MongoDbJSONCommandEditor {

    //commands
    static final String COMMAND_AGGREGATE = "aggregate";

    static final String PIPELINE = "pipeline";

    //pipeline stages
    static final String STAGE_MATCH = "$match";
    static final String STAGE_GROUP = "$group";

    BasicDBObject command;
    public static MongoDbJSONCommandEditor fromClassPathFile(String classPathFile){
        try {
            Resource resource = new ClassPathResource(classPathFile);
            return new MongoDbJSONCommandEditor((BasicDBObject)JSON.parse(IOUtils.toString(resource.getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private MongoDbJSONCommandEditor(BasicDBObject command){
        this.command = command;
    }



    public MongoDbJSONCommandEditor addQueryCriteria(Criteria criteria){

        BasicDBList pipeline = (BasicDBList)command.get(PIPELINE);
        if(pipeline != null){
            for(Object stage : pipeline){
                BasicDBObject dbo = (BasicDBObject)stage;
                if(dbo.get(STAGE_MATCH) != null){
                    BasicDBObject match = (BasicDBObject)dbo.get(STAGE_MATCH);
                    match.putAll(criteria.getCriteriaObject());
                    return this;
                }
            }
        }

        //没有返回，表示仍为添加match
        addMatchStage(pipeline).putAll(criteria.getCriteriaObject());
        return this;
    }

    private BasicDBObject addMatchStage(BasicDBList pipeline){
        BasicDBObject stage = new BasicDBObject();
        BasicDBObject match = new BasicDBObject();
        stage.put(STAGE_MATCH, match);
        pipeline.add(0, stage);//查询条件步骤放在最前面
        return match;
    }



    public String toString(){
        return command.toString();
    }

}
