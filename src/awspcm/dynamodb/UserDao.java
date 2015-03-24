package awspcm.dynamodb;

import java.util.Map;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import awspcm.model.User;

public class UserDao {
	private static AmazonDynamoDB dynamodb;
	private static final String TABLE_NAME = "pcm_user";
	
	static {
		AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
		dynamodb =  new AmazonDynamoDBClient(credentialsProvider);
		Region uswest2 = Region.getRegion(Regions.US_WEST_2);
		dynamodb.setRegion(uswest2);
	}
	
	public static void addUser(User user) {
		
	}
	
	public static void deleteUser(User user){
		
	}
	
	public static int login(User user) {
		QueryResult queryResult = dynamodb.query(new QueryRequest(TABLE_NAME).addKeyConditionsEntry("username", 
				new Condition().withComparisonOperator("EQ").withAttributeValueList(new AttributeValue(user.getName()))));
		if(queryResult.getCount() == 1) {  //if login success, return the user level, else return -1
			Map<String, AttributeValue> item = queryResult.getItems().get(0);
			if(item.get("password").getS().equals(user.getPassword())) return Integer.parseInt(item.get("level").getN());
			else return -1;
		}
		return -2; 	//if no this user return -2;
	}
}
