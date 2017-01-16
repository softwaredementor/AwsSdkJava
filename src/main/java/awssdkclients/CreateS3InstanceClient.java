package awssdkclients;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Created by mshrek on 1/8/17.
 */
public class CreateS3InstanceClient {
    private static ProfileCredentialsProvider profileCredentialsProvider = new ProfileCredentialsProvider();
    private static AmazonS3Client amazonS3Client = new AmazonS3Client(profileCredentialsProvider.getCredentials());

    private CreateS3InstanceClient() {

    }

    public static AmazonS3Client getMyS3InstanceClient() {
        return new CreateS3InstanceClient().amazonS3Client;
    }

    //Comment main method when used for other tests
    public static void main(String[] args) {
        System.out.println("The AWS credentials read by the S3 instance client are :\n" +
                "AWS secret key id = " + profileCredentialsProvider.getCredentials().getAWSAccessKeyId() + "\n" +
                "AWS secret key = " + profileCredentialsProvider.getCredentials().getAWSSecretKey());
    }
}
