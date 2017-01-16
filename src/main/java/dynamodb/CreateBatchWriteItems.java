package dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateTableSpec;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.*;

/**
 * Created by mshrek on 1/14/17.
 */
public class CreateBatchWriteItems {
    static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(
            new ProfileCredentialsProvider()));

    static String tableName = "ForumTable";

    public static void main(String[] args) throws Exception {

        //createForumTable();
        //batchWritePutItem();
        batchReadGetItem();

    }

    static void createForumTable() {

        try {
            System.out.println("Creating forum table ...");
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName("Id")
                    .withAttributeType("N"));
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName("ThreadSubject")
                    .withAttributeType("S"));

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName("Id")//Partition key
                    .withKeyType(KeyType.HASH)
            );
            keySchema.add(new KeySchemaElement()
                    .withAttributeName("ThreadSubject")
                    .withKeyType(KeyType.RANGE)
            );


            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(5L)
                            .withWriteCapacityUnits(6L));

            System.out.println("Issuing create table request for " + tableName);
            Table table = dynamoDB.createTable(request);
            System.out.println("Waiting for " + tableName
                    + " to be created...this may take a while...");
            table.waitForActive();
            System.out.println(tableName + " table activated successfully ...");
        } catch (final Exception e) {
            System.err.println("create forum table request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    static void batchWritePutItem() {
        try {
            Table table = dynamoDB.getTable(tableName);
            TableWriteItems tableWriteItems = new TableWriteItems(tableName)
                    .withItemsToPut(new Item()
                            .withPrimaryKey("Id", 1000, "ThreadSubject", "ThreadSubject_0")
                    );

            System.out.println("Making the first write request.");
            BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(tableWriteItems);

            System.out.println("Making the second write request for multiple items and deleting the previous created item");
            List<Item> items = new ArrayList<Item>();
            items.add(new Item()
                    .withPrimaryKey("Id", 1001, "ThreadSubject", "ThreadSubject_1"));
            items.add(new Item()
                    .withPrimaryKey("Id", 1002, "ThreadSubject", "ThreadSubject_2")
                    .withString("Message", "ElastiCache Thread 2 message"));
            items.add(new Item()
                    .withPrimaryKey("Id", 1003, "ThreadSubject", "ThreadSubject_3")
                    .withString("Message", "ElastiCache Thread 3 message"));
            items.add(new Item()
                    .withPrimaryKey("Id", 1004, "ThreadSubject", "ThreadSubject_4")
                    .withStringSet("Tags", new HashSet<String>(
                            Arrays.asList("cache", "in-memory"))));


            tableWriteItems.withItemsToPut(items)
                    .withHashAndRangeKeysToDelete("Id", "ThreadSubject", 1000, "ThreadSubject_0");
            outcome = dynamoDB.batchWriteItem(tableWriteItems);

            do {

                // Check for unprocessed keys which could happen if you exceed provisioned throughput
                Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
                if (outcome.getUnprocessedItems().size() == 0) {
                    System.out.println("No unprocessed items found");
                } else {
                    System.out.println("Retrieving the unprocessed items");
                    outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
                }
            } while (outcome.getUnprocessedItems().size() > 0);

        } catch (Exception e) {
            System.err.println("Failed to retrieve items: ");
            e.printStackTrace(System.err);
        }
    }

    static void batchReadGetItem() {
        try {
            TableKeysAndAttributes forumTableKeysAndAttributes = new TableKeysAndAttributes(tableName);
            //Add a partition key and a sort key
            forumTableKeysAndAttributes.addHashAndRangePrimaryKeys("Id", "ThreadSubject",
                    1001, "ThreadSubject_1",
                    1002, "ThreadSubject_2",
                    1003, "ThreadSubject_3",
                    1004, "ThreadSubject_4");

            System.out.println("Making the batch get request.");

            BatchGetItemOutcome outcome = dynamoDB.batchGetItem(forumTableKeysAndAttributes);

            Map<String, KeysAndAttributes> unprocessed = null;

            do {
                for (String tableName : outcome.getTableItems().keySet()) {
                    System.out.println("Items in table " + tableName);
                    List<Item> items = outcome.getTableItems().get(tableName);
                    for (Item item : items) {
                        System.out.println(item.toJSONPretty());
                    }
                }

                // Check for unprocessed keys which could happen if you exceed provisioned
                // throughput or reach the limit on response size.
                unprocessed = outcome.getUnprocessedKeys();

                if (unprocessed.isEmpty()) {
                    System.out.println("No unprocessed keys found");
                } else {
                    System.out.println("Retrieving the unprocessed keys");
                    outcome = dynamoDB.batchGetItemUnprocessed(unprocessed);
                }

            } while (!unprocessed.isEmpty());

        }  catch (Exception e) {
            System.err.println("Failed to retrieve items.");
            System.err.println(e.getMessage());
        }

    }
}
