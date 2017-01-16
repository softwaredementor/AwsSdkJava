package keypairs;

import awssdkclients.CreateEC2InstanceClient;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.KeyPair;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

/**
 * Created by mshrek on 1/5/17.
 */
public class CreateKeyPairs extends Object {
    private CreateKeyPairRequest createKeyPairRequest;
    private CreateKeyPairResult createKeyPairResult;
    private KeyPair keyPair = new KeyPair();

    public CreateKeyPairs(final String keyName) {
        //Create a keypair request
        this.createKeyPairRequest = new CreateKeyPairRequest();

        //The keyname is set here, make sure it is unique else you will get exceptions
        createKeyPairRequest.withKeyName(keyName);
    }

    //Get an amazon ec2 instance and then use it to create a key pair with the provided createkeypair request
    public void createKeyPair() {
        createKeyPairResult = CreateEC2InstanceClient.getMyEC2InstanceClient().
                createKeyPair(createKeyPairRequest);
    }

    //Get the public key of the keypair
    public String getKeyPair() {
        keyPair = createKeyPairResult.getKeyPair();
        return keyPair.getKeyMaterial();
    }

    //Read the created key-pair details
    public String toString() {
        return "The private key value = \n" + getKeyPair() + "\n" +
                "The finger print of the key = " + keyPair.getKeyFingerprint() + "\n" +
                "The key name = " + keyPair.getKeyName();
    }

    public static void main(String[] args) {
        System.out.println("The key-pair is being generated ... ");

        //Give a fixed string for key pair and append some random alphanumeric
        // string to make it unqiue for every key-pair creation
        CreateKeyPairs myNewKeyPair = new CreateKeyPairs("My-AWS-JAVA-SDK-KEYPAIR-" + RandomStringUtils.randomAlphanumeric(10));
        myNewKeyPair.createKeyPair();

        System.out.println("key-pair generated successfully with following content \n" + myNewKeyPair.toString());
    }
}
