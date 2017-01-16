package instancelaunch;

import awssdkclients.CreateEC2InstanceClient;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import keypairs.CreateKeyPairs;
import org.apache.commons.lang3.RandomStringUtils;
import securitygroups.CreateSecurityGroups;

/**
 * Created by mshrek on 1/6/17.
 */
public class StartAwsEC2Instance {

    private String myKeyPairName;
    private String mySecurityGroupName;
    private String myAmiImageId;
    private String myInstanceType;

    public StartAwsEC2Instance(final String myKeyPairName, final String mySecurityGroupName,
                              final String myAmiImageId, final String myInstanceType) {
        this.myKeyPairName = myKeyPairName;
        this.mySecurityGroupName = mySecurityGroupName;
        this.myAmiImageId = myAmiImageId;
        this.myInstanceType = myInstanceType;
    }

    public String getMyKeyPairName() {
        return myKeyPairName;
    }

    public String getMySecurityGroupName() {
        return mySecurityGroupName;
    }

    public String getMyAmiImageId() {
        return myAmiImageId;
    }

    public String getMyInstanceType() {
        return myInstanceType;
    }

    public static void main(String[] args) {

        //creating security group
        System.out.println("Creating new security group ...");
        final String securityGroupName = "my-aws-java-sdk-security-group" + RandomStringUtils.randomAlphanumeric(10);
        final String securityGroupDescription = "my-aws-java-sdk-security-group-description" +
                RandomStringUtils.randomAlphanumeric(10);

        CreateSecurityGroups mySecurityGroup = new CreateSecurityGroups(securityGroupName, securityGroupDescription);
        mySecurityGroup.createSecurityGroupResult(mySecurityGroup.getMyCSGR()).configureIngressRules().
                authorizeSecurityGroupIngressRequest(mySecurityGroup.getIpPermission());

        //creating key-pair
        final String keyPairName = "My-AWS-JAVA-SDK-KEYPAIR-" + RandomStringUtils.randomAlphanumeric(10);
        CreateKeyPairs myKeyPairs = new CreateKeyPairs(keyPairName);
        myKeyPairs.createKeyPair();

        //printing key-pair details
        System.out.println(myKeyPairs.toString());

        /*
        VPC Exception thrown for t2.micro, m4.large and other types.
        For demo purposes m3.medium is sufficient enough to not cause exceptions
        Also make sure that the AMIID is correct and exists in your region
        */
        final StartAwsEC2Instance newEC2Instance = new StartAwsEC2Instance(keyPairName,
                mySecurityGroup.getMyCSGR().getGroupName(), "ami-9be6f38c", "m3.medium");
        final RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
        runInstancesRequest.withImageId(newEC2Instance.getMyAmiImageId())
                .withInstanceType(newEC2Instance.getMyInstanceType())
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName(newEC2Instance.myKeyPairName)
                .withSecurityGroups(newEC2Instance.getMySecurityGroupName());
        RunInstancesResult result = CreateEC2InstanceClient.getMyEC2InstanceClient().
                runInstances(runInstancesRequest);
        System.out.println("Instance launched successfully !");
    }
}
