package s3buckets;

import awssdkclients.CreateS3InstanceClient;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by mshrek on 1/8/17.
 */
public class CreateAwsS3Buckets {
    private static String bucketName = RandomStringUtils.randomAlphanumeric(10).toLowerCase();

    public static void main(String[] args) {
        AmazonS3Client myAmazonS3Client = CreateS3InstanceClient.getMyS3InstanceClient();
        myAmazonS3Client.setRegion(Region.getRegion(Regions.AP_SOUTH_1));

        try {
            // Note that CreateBucketRequest does not specify region. So bucket is
            // created in the region specified in the client.
            System.out.println("Creating the S3 bucket ...");
            if (!myAmazonS3Client.doesBucketExist(bucketName)) {
                myAmazonS3Client.createBucket(new CreateBucketRequest(bucketName));
            }
            // Get location.
            String bucketLocation = myAmazonS3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
            System.out.println("Bucket was successfully created ! Bucket location = " + bucketLocation);
        } catch (final AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("The raw response was :\n" + ase.getRawResponse());
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (final AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
