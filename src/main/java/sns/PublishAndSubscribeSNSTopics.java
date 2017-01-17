package sns;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;

/**
 * Created by mshrek on 1/17/17.
 */
public class PublishAndSubscribeSNSTopics {
    private static AmazonSNSClientBuilder snsClientBuilder = AmazonSNSClientBuilder.standard()
            .withRegion(Regions.AP_SOUTH_1)
            .withCredentials(new ProfileCredentialsProvider());

    private static AmazonSNSClient  snsClient = (AmazonSNSClient) snsClientBuilder.build();
    private static String topicArn;

    public static void main (String[] args) {
        createTopic();
        subscribeTopic();
    }

    /**
     * Call this method to create a SNS topic
     */
    static void createTopic() {
        snsClient.withRegion(Regions.AP_SOUTH_1);
        //create a new SNS topic request and call createTopic
        CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyNewSNSTopic");
        CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);

        //print TopicArn
        topicArn = createTopicResult.getTopicArn();
        System.out.println("The Topic details are :" + createTopicResult.toString());

        //get request id for CreateTopicRequest from SNS metadata
        System.out.println("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
    }

    /**
     * Call this method to create subscribers for the created topic
     */
    static void subscribeTopic() {
        //subscribe to an SNS topic
        SubscribeRequest mailSubRequest = new SubscribeRequest("arn:aws:sns:ap-south-1:359523841908:MyNewSNSTopic", "email", "sippy007@gmail.com");
        SubscribeRequest sqsSubRequest = new SubscribeRequest("arn:aws:sns:ap-south-1:359523841908:MyNewSNSTopic", "sqs", "arn:aws:sqs:ap-south-1:359523841908:MySQSQueue");

        System.out.println("Sending subscription request to both subscribers : mail and sqs");
        snsClient.subscribe(mailSubRequest);
        snsClient.subscribe(sqsSubRequest);

        //get request id for SubscribeRequest from SNS metadata
        System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(mailSubRequest));
        System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(sqsSubRequest));

    }
}
