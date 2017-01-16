package dynamodb;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;

/**
 * Created by mshrek on 1/8/17.
 */
public class CreateDynamoDBTableInstance {

    private DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient(new ProfileCredentialsProvider()));
    String tableName;

    public DynamoDB getDynamoDB() {
        return dynamoDB;
    }

    public CreateDynamoDBTableInstance(final String tableName) {
        this.tableName = tableName;
    }
    public static void main(String[] args) {
        final String tableName = "TABLE".concat(RandomStringUtils.randomAlphanumeric(5));
        CreateDynamoDBTableInstance dynamoDBInstance = new CreateDynamoDBTableInstance(tableName);

        ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("Id").withAttributeType("N"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("Name").withAttributeType("S"));

        ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH));
        keySchema.add(new KeySchemaElement().withAttributeName("Name").withKeyType(KeyType.RANGE));

        System.out.println("Creating table " + tableName + " in dynamo DB");
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withKeySchema(keySchema)
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(5L)
                        .withWriteCapacityUnits(6L));

        Table table = dynamoDBInstance.getDynamoDB().createTable(request);
        System.out.println("Table was created successfully !");
        try {
            table.waitForActive();
        } catch (final InterruptedException iex) {
            System.out.println("Dynamo DB activation was interrupted ... ");
        } catch (final Exception ex) {
            System.out.println("Exception occurred in DB initialization");
        } finally {
            System.out.println("Table was activated successfully");
            TableDescription tableDescription =
                    dynamoDBInstance.getDynamoDB().getTable(tableName).describe();

            System.out.printf("%s: %s \t ReadCapacityUnits: %d \t WriteCapacityUnits: %d",
                    tableDescription.getTableStatus(),
                    tableDescription.getTableName(),
                    tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
                    tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
        }
    }
}
