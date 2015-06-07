package james.tool.mongoDbUtils.jsonCommand;

import com.mongodb.CommandResult;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;

import java.io.IOException;
import java.util.List;

/**
 * Created by zhanghong on 15/6/7.
 */
public class MongoDbJsonCommandExecutor {

    public static <o> List<o> executeCommand(MongoOperations mongoOperations, String command, Class<o> outputType){
        System.out.println("executing command "  + command);
        CommandResult commandResult = mongoOperations.executeCommand(command);
        MongoDbCommandResultConverter converter = new MongoDbCommandResultConverter(mongoOperations);
        return converter.convertAggregationCommandResult(outputType, commandResult);
    }

    public static <o> List<o> executeFromClassPathFile(MongoOperations mongoOperations, String jsonCommandClassPathFile, Class<o> outputType){
        try {
            Resource resource = new ClassPathResource(jsonCommandClassPathFile);
            return executeCommand(mongoOperations, IOUtils.toString(resource.getInputStream()), outputType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
