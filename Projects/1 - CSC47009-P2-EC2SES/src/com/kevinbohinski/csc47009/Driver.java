/**
 * @author Kevin Bohinski <bohinsk1@tcnj.edu>
 * @version 1.0
 * @since 2015-3-3
 *  
 * CSC47009-P2-EC2SES
 * Driver.java
 * Copyright (c) 2015 Kevin Bohinski. All rights reserved.
 */

/*
 * CSC  470  –  Special  Topics  in  Computer  Science:  Cloud  Computing
 * Small  Project  #3  –  AWS  EC2  and  Simple  Email  Service  (SES) 
 *
 * Objective
 * Develop  a  small  Java  application  that  utilizes  various  EC2  and  SES  services  and 
 * features  to  raise  your  awareness  and  comprehension  of  these  services  and  their 
 * documented  API.
 */

/* Setting Package */
package com.kevinbohinski.csc47009;

/* Setting Imports */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.DeleteVerifiedEmailAddressRequest;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;

public class Driver {

	/* Global Vars */
	private static AmazonSimpleEmailServiceClient ses;
	private static AmazonEC2Client ec2;

	/**
	 * main method, executes the S3 program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String user = "";
		String keyId = "";
		String secretKey = "";
		int lineNum = 0;

		System.out.println("=========================");
		System.out.println("   Amazon EC2 SES Tool");
		System.out.println("     Kevin Bohinski");
		System.out.println("=========================");
		System.out.println("\nAttempting to read credentials from keys.txt.");

		/* Reading AWS IAM info from external file. */
		try {
			FileReader inF = new FileReader("keys.txt");
			BufferedReader in = new BufferedReader(inF);
			String inLine = "";

			while ((inLine = in.readLine()) != null) {

				lineNum++;

				try {

					if (lineNum == 1) {
						user = inLine;
					} else if (lineNum == 2) {
						keyId = inLine;
					} else if (lineNum == 3) {
						secretKey = inLine;
					} else {
						IOException e = new IOException();
						throw (e);
					} /* if block */

				} catch (IOException e) {
					System.err.println("Unexpected File.");
				} /* try catch */

			} /* while */

			in.close();

		} catch (IOException e) {
			System.err.println("Trouble reading file.");
		} /* try catch */
		System.out.println("Finished reading credentials.\n");
		System.out.println("Hello " + user + " !");

		/* Connecting to AWS using the IAM info */
		System.out.println("\nAttempting to connect to Amazon SES.");
		BasicAWSCredentials AWSUser = new BasicAWSCredentials(keyId, secretKey);
		ses = new AmazonSimpleEmailServiceClient(AWSUser);
		System.out.println("Connected to Amazon SES!");
		System.out.println("\nAttempting to connect to Amazon EC2.");
		ec2 = new AmazonEC2Client(AWSUser);
		System.out.println("Connected to Amazon EC2!");
		System.out.println("\nAttempting to connect to the US East 1 Region.");
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		ses.setRegion(usEast1);
		ec2.setRegion(usEast1);
		ec2.setEndpoint("ec2.us-east-1.amazonaws.com");
		System.out.println("Connected to US East.");

		try {
			pause();

			Scanner in = new Scanner(System.in);

			System.out.println("Starting at the D level.");

			pause();

			System.out
					.println("Attempting to obtain a list of verified email addresses from SES.\n");
			System.out.println(ses.listVerifiedEmailAddresses().toString());

			pause();

			System.out.println("Now at the C level.");

			pause();

			System.out.println("Attempting to send an email from SES.");
			System.out
					.println("\nPlease enter an email to send our test email to:");
			String toEmail = in.nextLine();
			List<String> tos = Arrays.asList(toEmail);
			Destination d = new Destination(tos);
			Content subject = new Content("Hello from SES");
			Content bodyContent = new Content("Greetings " + toEmail
					+ ", from Amazon SES!");
			Body body = new Body(bodyContent);
			Message m = new Message(subject, body);
			SendEmailRequest sendEmailRequest = new SendEmailRequest(
					"bohinsk1@tcnj.edu", d, m);
			ses.sendEmail(sendEmailRequest);
			System.out.println("Email Sent!");

			pause();

			System.out
					.println("Attempting to obtain the sending quota and stats.\n");
			System.out.println("Quota:");
			System.out.println(ses.getSendQuota().toString());
			System.out.println("Stats:");
			System.out.println(ses.getSendStatistics().toString());

			pause();

			System.out.println("Attempting to start an EC2 instance");
			System.out
					.println("This instance will be t2.micro, and will use the Ubuntu Server 14.04 LTS image.");
			System.out.println("One second please... \n");
			String ubuntu = "ami-9a562df2";
			RunInstancesRequest runInstancesRequest = new RunInstancesRequest(
					ubuntu, 1, 1);
			runInstancesRequest.withInstanceType("t2.micro");
			RunInstancesResult result = ec2.runInstances(runInstancesRequest);
			String resultInfo = result.toString();
			String[] resultInfoSplit = resultInfo.split(",");
			String temp = resultInfoSplit[4];
			String[] temp2 = temp.split(" ");
			String iid = temp2[2];
			System.out.println("Instance ID: " + iid);
			System.out.println("\n     Waiting a few seconds for a public ip.");
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
			}
			DescribeInstancesRequest disReq = new DescribeInstancesRequest()
					.withInstanceIds(iid);
			DescribeInstancesResult disRes = ec2.describeInstances(disReq);
			resultInfo = disRes.toString();
			resultInfoSplit = resultInfo.split(",");
			temp = resultInfoSplit[22];
			temp2 = temp.split(" ");
			String ip = temp2[1];
			System.out.println("VM IP: " + ip);

			pause();

			System.out.println("Now at the B level.");

			pause();

			System.out
					.println("Attempting to send an email with cc, bcc from SES.");
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					System.in));
			System.out
					.println("\nPlease enter an email to send our test email to:");
			System.out
					.println("To send to multiple, use a comma (no spaces please). Ex: a@b.com,b@a.com");
			System.out
					.println("Please note the To address is required, while cc and bcc are optional.");
			String toEmail2 = "";
			try {
				toEmail2 = bin.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> tos2 = Arrays.asList(toEmail2.split(","));
			System.out
					.println("\nPlease enter any email addresses you wish to cc.");
			System.out
					.println("To send to multiple, use a comma (no spaces please). Ex: a@b.com,b@a.com");
			String ccEmail2 = "";
			try {
				ccEmail2 = bin.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> ccs2 = Arrays.asList(ccEmail2.split(","));
			System.out
					.println("\nPlease enter any email addresses you wish to bcc.");
			System.out
					.println("To send to multiple, use a comma (no spaces please). Ex: a@b.com,b@a.com");
			String bccEmail2 = "";
			try {
				bccEmail2 = bin.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> bccs2 = Arrays.asList(bccEmail2.split(","));
			Destination d2;
			if (toEmail2.equals("")) {
				tos2 = Arrays.asList("bohinsk1@tcnj.edu");
				d2 = new Destination(tos2);
			} else {
				d2 = new Destination(tos2);
			}
			if (!(ccEmail2.equals(""))) {
				d2.withCcAddresses(ccs2);
			}
			if (!(bccEmail2.equals(""))) {
				d2.withBccAddresses(bccs2);
			}
			Content subject2 = new Content("Hello from SES - CC/BCC");
			Content bodyContent2 = new Content("Greetings " + toEmail2
					+ ", from Amazon SES!");
			Body body2 = new Body(bodyContent2);
			Message m2 = new Message(subject2, body2);
			SendEmailRequest sendEmailRequest2 = new SendEmailRequest(
					"bohinsk1@tcnj.edu", d2, m2);
			ses.sendEmail(sendEmailRequest2);
			System.out.println("Email Sent!");

			pause();

			System.out.println("Attempting to add a verified email to SES.\n");
			System.out.println("Please enter an email to verify:");
			String vEmail = in.nextLine();
			VerifyEmailAddressRequest verReq = new VerifyEmailAddressRequest();
			verReq.withEmailAddress(vEmail);
			ses.verifyEmailAddress(verReq);
			System.out.println("Email Added!");

			pause();

			System.out.println("Attempting to get an instance description.\n");
			System.out.println("Please enter a VM Instance ID to verify:");
			String descriptions = ec2.describeInstances().toString();
			temp2 = descriptions.split(" i-");
			String[] temp3;
			int num = 0;
			String[] instanceIds = new String[50];
			for (int i = 1; i < temp2.length; i++) {
				temp3 = temp2[i].split(",");
				instanceIds[i - 1] = "i-" + temp3[0];
				num++;
			}
			String[] iids = new String[num];
			for (int i = 0; i < num; i++) {
				iids[i] = instanceIds[i];
				System.out.println(i + ") " + iids[i]);
			}
			int sel = in.nextInt();
			DescribeInstancesRequest disReq2 = new DescribeInstancesRequest()
					.withInstanceIds(iids[sel]);
			DescribeInstancesResult disRes2 = ec2.describeInstances(disReq2);
			System.out.println(disRes2.toString());

			pause();

			System.out.println("Now at the A level.");

			pause();

			System.out
					.println("Attempting to delete a verified email from SES.\n");
			System.out.println("Please enter an email to delete:");
			String dEmail = "";
			try {
				dEmail = bin.readLine();
			} catch (IOException e) {
				
			}
			DeleteVerifiedEmailAddressRequest delVerEmail = new DeleteVerifiedEmailAddressRequest();
			delVerEmail.withEmailAddress(dEmail);
			ses.deleteVerifiedEmailAddress(delVerEmail);
			System.out.println("Email Deleted!");

			pause();

			System.out.println("Attempting to terminate an instance.\n");
			System.out.println("Please select your instance:");
			descriptions = ec2.describeInstances().toString();
			temp2 = descriptions.split(" i-");
			num = 0;
			instanceIds = new String[50];
			for (int i = 1; i < temp2.length; i++) {
				temp3 = temp2[i].split(",");
				instanceIds[i - 1] = "i-" + temp3[0];
				num++;
			}
			String[] iids2 = new String[num];
			for (int i = 0; i < num; i++) {
				iids2[i] = instanceIds[i];
				System.out.println(i + ") " + iids2[i]);
			}
			int del = in.nextInt();
			TerminateInstancesRequest termReq = new TerminateInstancesRequest();
			termReq.withInstanceIds(iids2[del]);
			ec2.terminateInstances(termReq);
			System.out.println("Instance " + iids2[del] + " terminated.");

			pause();

			System.out.println("Done!");
			in.close();
			try {
				bin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);

		} catch (AmazonServiceException ase) {
			System.out.println();
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println();
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println();
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with AWS, "
							+ "such as not being able to access the network.");
			System.out.println();
			System.out.println("Error Message: " + ace.getMessage());
		}

	}

	private static void pause() {
		System.out.println("\n     Press enter to continue...");
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		in.nextLine();
		for (int i = 0; i < 100; i++) {
			System.out.println("\n");
		}
	}

} /* Driver.java */