package awspcm.dynamodb;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class FileDao {
	private static AmazonDynamoDB dynamodb;
	private static AmazonS3 s3;
	private static final String TABLE_NAME = "pcm_files";
	private static final String BUCKET_NAME = "pcm-disk";
	
	static{
		AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
		dynamodb =  new AmazonDynamoDBClient(credentialsProvider);
		s3 = new AmazonS3Client(credentialsProvider);
		Region uswest2 = Region.getRegion(Regions.US_WEST_2);
		dynamodb.setRegion(uswest2);
		s3.setRegion(uswest2);
	}
	
	public static JSONArray getItemsByContent(String content){
		 Map<String,Condition> scanFilter = new HashMap<String,Condition>();
		 scanFilter.put("content", new Condition(). //scan filter
				 withComparisonOperator("EQ").
				 withAttributeValueList(new AttributeValue(content)));
		 List<Map<String,AttributeValue>> list = dynamodb.scan(TABLE_NAME, scanFilter).getItems();
		 
		 JSONArray files = new JSONArray();
		 Iterator<Map<String, AttributeValue>> it = list.iterator();
		 while(it.hasNext()){
			 Map<String, AttributeValue> map = it.next();
			 JSONObject file = new JSONObject();
			 try {
				file.put("filename", map.get("filename").getS());
				file.put("key", map.get("s3_object_key").getS());
				file.put("link", map.get("link").getS());
				file.put("upload_date", map.get("upload_date").getN());
				file.put("size", map.get("size").getN());
				file.put("content", map.get("content").getS());
				
				files.put(file);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		 }
		 return files;
	}
	
	public static String fileUpload(String key, File file){
		s3.putObject(BUCKET_NAME, key, file);
		s3.setObjectAcl(BUCKET_NAME, key,  CannedAccessControlList.PublicRead);
		return "https://s3-us-west-2.amazonaws.com/pcm-disk/" + key;
	}
	
	public static void insert2DynamoDB(String s3_object_key, String content, String filename, long size, long date, String link){
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("s3_object_key", new AttributeValue(s3_object_key));
		item.put("content", new AttributeValue(content));
		item.put("filename", new AttributeValue(filename));
		item.put("size", new AttributeValue().withN(String.valueOf(size)));
		item.put("upload_date", new AttributeValue().withN(String.valueOf(date)));
		item.put("link", new AttributeValue(link));
		
		PutItemRequest putItemRequest = new PutItemRequest(TABLE_NAME, item);
		dynamodb.putItem(putItemRequest);		
	}
	
	public static void deleteObject(String objectKey) {
		s3.deleteObject(BUCKET_NAME, objectKey);
	}
	
	public static void deleteFromDynamoDB(String s3_object_key, String content) {   //delete file record from dynamodb
		Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
		map.put("s3_object_key", new AttributeValue(s3_object_key));
		map.put("content", new AttributeValue(content));
		DeleteItemRequest deleteItemRequest = new DeleteItemRequest(TABLE_NAME, map);
		dynamodb.deleteItem(deleteItemRequest);
	}

}
