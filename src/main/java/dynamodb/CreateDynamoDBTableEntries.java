package dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.*;

/**
 * Created by mshrek on 1/10/17.
 */
public class CreateDynamoDBTableEntries {
    static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(
            new ProfileCredentialsProvider()));

    static String tableName = "ProductCatalog";

    public static void main(String[] args) throws Exception {

        createExampleTable();
        createItems();
        retrieveItem();
        updateAddNewAttribute();
        updateMultipleAttributes();
        updateExistingAttributeConditionally();
        deleteItem();
    }

    static void createExampleTable() {

        try {
            System.out.println("Creating table ...");
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName("Id")
                    .withAttributeType("N"));

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName("Id")
                    .withKeyType(KeyType.HASH)); //Partition key
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(5L)
                            .withWriteCapacityUnits(6L));

            System.out.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);

            System.out.println("Waiting for " + tableName
                    + " to be created...this may take a while...");
            table.waitForActive();
            System.out.println(tableName + " table activated successfully ...");
            getTableInformation();

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    static void getTableInformation() {

        System.out.println("Describing " + tableName);

        TableDescription tableDescription = dynamoDB.getTable(tableName).describe();
        System.out.format("Name: %s:\n" + "Status: %s \n"
                        + "Provisioned Throughput (read capacity units/sec): %d \n"
                        + "Provisioned Throughput (write capacity units/sec): %d \n",
                tableDescription.getTableName(),
                tableDescription.getTableStatus(),
                tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
                tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
    }

    static void createItems() {
        Table table = dynamoDB.getTable(tableName);
        // Build a list of related items
        System.out.println("Build a list of related items\n");
        List<Number> relatedItems = new ArrayList<Number>();
        relatedItems.add(341);
        relatedItems.add(472);
        relatedItems.add(649);

        //
        System.out.println("Build a map of product pictures");
        Map<String, String> pictures = new HashMap<String, String>();
        pictures.put("FrontView", "http://example.com/products/206_front.jpg");
        pictures.put("RearView", "http://example.com/products/206_rear.jpg");
        pictures.put("SideView", "http://example.com/products/206_left_side.jpg");

        //Build a map of product reviews
        System.out.println("Build a map of product reviews");
        Map<String, List<String>> reviews = new HashMap<String, List<String>>();

        List<String> fiveStarReviews = new ArrayList<String>();
        fiveStarReviews.add("Excellent! Can't recommend it highly enough!  Buy it!");
        fiveStarReviews.add("Do yourself a favor and buy this");
        reviews.put("FiveStar", fiveStarReviews);

        List<String> oneStarReviews = new ArrayList<String>();
        oneStarReviews.add("Terrible product!  Do not buy this.");
        reviews.put("OneStar", oneStarReviews);

        // Build the item
        System.out.println("Build the item");
        PutItemSpec putItemSpec = new PutItemSpec().withItem(new Item()
                .withPrimaryKey("Id", 120)
                .withString("Title", "20-Bicycle 206")
                .withString("Description", "206 description")
                .withString("BicycleType", "Hybrid")
                .withString("Brand", "Brand-Company C")
                .withNumber("Price", 500)
                .withStringSet("Color", new HashSet<String>(Arrays.asList("Red", "Black")))
                .withString("ProductCategory", "Bike")
                .withBoolean("InStock", true)
                .withNull("QuantityOnHand")
                .withList("RelatedItems", relatedItems)
                .withMap("Pictures", pictures)
                .withMap("Reviews", reviews))
                .withReturnValues(ReturnValue.ALL_OLD);
        //ReturnValue.ALL_NEW for putItem is not supported and ReturnValue.ALL_OLD/ReturnValue.NONE do
        // not return the output properly


        // Write the item to the table
        System.out.println("Write the item to the table");
        PutItemOutcome outcome = table.putItem(putItemSpec);
        System.out.println("The item information :\n" + outcome.toString());
    }

    private static void retrieveItem() {
        Table table = dynamoDB.getTable(tableName);

        try {

            Item item = table.getItem("Id", 120, "Id, ISBN, Title, Authors", null);

            System.out.println("Printing item after retrieving it....");
            System.out.println(item.toJSONPretty());

        } catch (Exception e) {
            System.err.println("GetItem failed.");
            System.err.println(e.getMessage());
        }

    }

    private static void updateAddNewAttribute() {
        Table table = dynamoDB.getTable(tableName);

        try {

            Map<String, String> expressionAttributeNames = new HashMap<String, String>();
            expressionAttributeNames.put("#na", "NewAttribute");

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("Id", 120)
                    .withUpdateExpression("set #na = :val1")
                    .withNameMap(new NameMap()
                            .with("#na", "NewAttribute"))
                    .withValueMap(new ValueMap()
                            .withString(":val1", "Some value"))
                    .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            // Check the response.
            System.out.println("Printing item after adding new attribute...");
            System.out.println(outcome.getItem().toJSONPretty());

        } catch (Exception e) {
            System.err.println("Failed to add new attribute in " + tableName);
            System.err.println(e.getMessage());
        }
    }

    private static void updateMultipleAttributes() {

        Table table = dynamoDB.getTable(tableName);

        try {

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("Id", 120)
                    .withUpdateExpression("add #a :val1 set #na=:val2")
                    .withNameMap(new NameMap()
                            .with("#a", "Type")
                            .with("#na", "Manufacturer"))
                    .withValueMap(new ValueMap()
                            .withStringSet(":val1", "Mountain Terrain", "City")
                            .withString(":val2", "Hero Corp"))
                    .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            // Check the response.
            System.out
                    .println("Printing item after multiple attribute update...");
            System.out.println(outcome.getItem().toJSONPretty());

        } catch (Exception e) {
            System.err.println("Failed to update multiple attributes in "
                    + tableName);
            System.err.println(e.getMessage());

        }
    }

    private static void updateExistingAttributeConditionally() {

        Table table = dynamoDB.getTable(tableName);

        try {

            // Specify the desired price (25.00) and also the condition (price =
            // 20.00)

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("Id", 120)
                    .withReturnValues(ReturnValue.ALL_NEW)
                    .withUpdateExpression("set #p = :val1")
                    .withConditionExpression("#p = :val2")
                    .withNameMap(new NameMap()
                            .with("#p", "Price"))
                    .withValueMap(new ValueMap()
                            .withNumber(":val1", 600)
                            .withNumber(":val2", 500));

            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            // Check the response.
            System.out
                    .println("Printing item after conditional update to new attribute...");
            System.out.println(outcome.getItem().toJSONPretty());

        } catch (Exception e) {
            System.err.println("Error updating item in " + tableName);
            System.err.println(e.getMessage());
        }
    }

    private static void deleteItem() {

        Table table = dynamoDB.getTable(tableName);

        try {

            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey("Id", 120)
                    .withConditionExpression("#ip = :val")
                    .withNameMap(new NameMap()
                            .with("#ip", "BicycleType"))
                    .withValueMap(new ValueMap()
                            .withString(":val", "Hybrid"))
                    .withReturnValues(ReturnValue.ALL_OLD);

            DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);

            // Check the response.
            System.out.println("Printing item that was deleted...");
            System.out.println(outcome.getItem().toJSONPretty());

        } catch (Exception e) {
            System.err.println("Error deleting item in " + tableName);
            System.err.println(e.getMessage());
        }

    }
}
