package s3buckets;

import awssdkclients.CreateS3InstanceClient;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;

import static com.amazonaws.util.ClassLoaderHelper.getResource;

/**
 * Created by mshrek on 1/8/17.
 */
public class CreateAwsS3BucketObjects {
    private static String bucketName     = "ftqsniyxa0";
    private static String keyName        = RandomStringUtils.randomAlphabetic(10);
    private static String uploadFileName = getResource("SampleFile1.txt").getPath();

    public static void main(String[] args) throws IOException {
        AmazonS3 s3client = CreateS3InstanceClient.getMyS3InstanceClient();
        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File(uploadFileName);
            s3client.putObject(new PutObjectRequest(
                    bucketName, keyName, file));
            System.out.println("File " + keyName + " uploaded sucessfully to the "
                    + bucketName + " bucket");

        } catch (final AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
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
