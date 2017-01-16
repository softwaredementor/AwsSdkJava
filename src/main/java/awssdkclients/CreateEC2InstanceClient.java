package awssdkclients;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;

/**
 * Created by srikanthmannepalle on 1/5/17.
 */
public class CreateEC2InstanceClient {

    private static ProfileCredentialsProvider profileCredentialsProvider = new ProfileCredentialsProvider();
    private static AmazonEC2Client myEC2InstanceClient = new AmazonEC2Client(profileCredentialsProvider.getCredentials());

    private CreateEC2InstanceClient() {

    }

    public static AmazonEC2Client getMyEC2InstanceClient() {
        return new CreateEC2InstanceClient().myEC2InstanceClient;
    }

    //Comment main method when used for other tests
    public static void main(String[] args) {
        System.out.println("The AWS credentials read by the EC2 instance client are :\n" +
                "AWS secret key id = " + profileCredentialsProvider.getCredentials().getAWSAccessKeyId() + "\n" +
                "AWS secret key = " + profileCredentialsProvider.getCredentials().getAWSSecretKey());
    }
}
