package securitygroups;

import awssdkclients.CreateEC2InstanceClient;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.IpPermission;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by mshrek on 1/5/17.
 */
public class CreateSecurityGroups {

    private CreateSecurityGroupRequest myCSGR;
    private IpPermission ipPermission;
    private AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest;
    private CreateSecurityGroupResult createSecurityGroupResult;

    public CreateSecurityGroupRequest getMyCSGR() {
        return myCSGR;
    }

    public IpPermission getIpPermission() {
        return ipPermission;
    }

    //Create security group with the given security group name and description
    //You must use US-ASCII characters for the security group name and description.
    public CreateSecurityGroups(final String securityGroupName, final String description) {
        this.myCSGR = new CreateSecurityGroupRequest();
        this.myCSGR.withGroupName(securityGroupName).withDescription(description);
    }

    public CreateSecurityGroups createSecurityGroupResult(final CreateSecurityGroupRequest myCSGR) {
        createSecurityGroupResult = new CreateSecurityGroupResult();
        createSecurityGroupResult = CreateEC2InstanceClient.getMyEC2InstanceClient().createSecurityGroup(myCSGR);
        return this;
    }

    //This will configure the rules for incoming requests
    public CreateSecurityGroups configureIngressRules() {
        ipPermission = new IpPermission();
        ipPermission.withIpRanges("0.0.0.0/0")
                .withIpProtocol("tcp")
                .withFromPort(22)
                .withToPort(22);
        return this;
    }

    //This will actually set the rules for incoming requests to the instance
    public void authorizeSecurityGroupIngressRequest(final IpPermission ipPermission) {
        authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest();
        authorizeSecurityGroupIngressRequest.withGroupName(myCSGR.getGroupName())
                .withIpPermissions(ipPermission);
        CreateEC2InstanceClient.getMyEC2InstanceClient().authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
    }

    //Comment main method when other tests use this class
    public static void main(String[] args) {
        System.out.println("Creating new security group ...");
        final String securityGroupName = "my-aws-java-sdk-security-group" + RandomStringUtils.randomAlphanumeric(10);
        final String securityGroupDescription = "my-aws-java-sdk-security-group-description" +
                RandomStringUtils.randomAlphanumeric(10);

        //creating security group
        CreateSecurityGroups mySecurityGroup = new CreateSecurityGroups(securityGroupName, securityGroupDescription);
        mySecurityGroup.createSecurityGroupResult(mySecurityGroup.getMyCSGR()).configureIngressRules().
                authorizeSecurityGroupIngressRequest(mySecurityGroup.getIpPermission());
        System.out.println("New security group created successfully !");
    }

}
