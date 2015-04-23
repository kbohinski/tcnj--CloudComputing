/**
 * @author Kevin Bohinski <bohinsk1@tcnj.edu>
 * @version 1.0
 * @since 2015-4-17
 *  
 * CSC47009-P4-SNS
 * Driver.java
 * Copyright (c) 2015 Kevin Bohinski. All rights reserved.
 */

/* 
 * CSC 470 – Special Topics in Computer Science: Cloud Computing
 * Small Project #5 – AWS Simple Notification Service (SNS)
 * 
 * Objective
 * Develop  a  small  Java  application  that  utilizes  the  SNS  features  to  raise  your
 * awareness and comprehension of these services and their documented API.
 */

/* Setting Package */
package com.kevinbohinski.csc47009;

/* Setting Imports */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;

public class Driver {

	/* Global Vars */
	private static AmazonSNSClient sns;

	/**
	 * main method, executes the SQS program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String user = "";
		String keyId = "";
		String secretKey = "";
		int lineNum = 0;

		System.out.println("===============================");
		System.out.println("        Amazon SNS Tool");
		System.out.println("        Kevin Bohinski");
		System.out.println("===============================");
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
		System.out.println("\nAttempting to connect to Amazon SNS.");
		BasicAWSCredentials AWSUser = new BasicAWSCredentials(keyId, secretKey);
		sns = new AmazonSNSClient(AWSUser);
		System.out.println("Connected to Amazon SNS!");
		System.out.println("\nAttempting to connect to the US East 1 Region.");
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		sns.setRegion(usEast1);
		System.out.println("Connected to US East!");

		try {
			pause();
			Scanner in = new Scanner(System.in);

			System.out.println("Starting at the D level.");
			pause();

			System.out.println("Attempting to create two topics.");
			sns.createTopic("CSC470Test-Alpha");
			sns.createTopic("CSC470Test-Beta");
			System.out.println("Success!");
			pause();

			System.out.println("Attempting to list all topics.");
			listTopics(true);
			System.out.println("Success!");
			pause();

			System.out
					.println("Attempting to delete the CSC470Test-Beta topic.");
			String garn = getGeneralArn();
			sns.deleteTopic(garn + "CSC470Test-Beta");
			listTopics(true);
			System.out.println("Success!");
			pause();

			System.out.println("Now at the C level.");
			pause();

			System.out
					.println("Attempting to change the display name of the alpha topic.");
			System.out.println("Printing attributes:");
			String attribs = "" + sns.getTopicAttributes((garn + "CSC470Test-Alpha"));
			String[] tmp = attribs.split(",");
			String owner = tmp[18].substring(10);
			String tarn = tmp[16].substring(7);
			String policy = tmp[19].substring(62) + ", " + tmp[20] + ", " +  tmp[21] + ", " +  tmp[22] + ", " +  tmp[23] + ", " +  tmp[24] + ", " +  tmp[25];
			String dispName = tmp[28].substring(13);
			String subsPend = tmp[17].substring(22);
			String subsDel = tmp[29].substring(22);
			subsDel = subsDel.substring(0, 1);
			String subsCon = tmp[27].substring(24);
			System.out.println("    TopicARN:               " + tarn);
			System.out.println("    Owner:                  " + owner);
			System.out.println("    Policy:                 " + policy);
			System.out.println("    DisplayName:            " + dispName);
			System.out.println("    SubscriptionsPending:   " + subsPend);
			System.out.println("    SubscriptionsDeleted:   " + subsDel);
			System.out.println("    SubscriptionsConfirmed: " + subsCon);
			System.out.println("Success!");
			System.out.println("\nChanging DisplayName");
			sns.setTopicAttributes((garn + "CSC470Test-Alpha"), "DisplayName", "CSC470Testing");
			System.out.println("Success!");
			System.out.println("\nPrinting attributes:");
			attribs = "" + sns.getTopicAttributes((garn + "CSC470Test-Alpha"));
			tmp = attribs.split(",");
			owner = tmp[18].substring(10);
			tarn = tmp[16].substring(7);
			policy = tmp[19].substring(62) + ", " + tmp[20] + ", " +  tmp[21] + ", " +  tmp[22] + ", " +  tmp[23] + ", " +  tmp[24] + ", " +  tmp[25];
			dispName = tmp[28].substring(13);
			subsPend = tmp[17].substring(22);
			subsDel = tmp[29].substring(22);
			subsDel = subsDel.substring(0, 1);
			subsCon = tmp[27].substring(24);
			System.out.println("    TopicARN:               " + tarn);
			System.out.println("    Owner:                  " + owner);
			System.out.println("    Policy:                 " + policy);
			System.out.println("    DisplayName:            " + dispName);
			System.out.println("    SubscriptionsPending:   " + subsPend);
			System.out.println("    SubscriptionsDeleted:   " + subsDel);
			System.out.println("    SubscriptionsConfirmed: " + subsCon);
			System.out.println("Success!");
			pause();
			
			System.out.println("Now at the B level.");
			pause();
			
			System.out.println("Attempting to add subscriptions.");
			System.out.println("    Please enter your email:");
			String email = in.nextLine();
			System.out.println("    Please enter your phone number (please include the 1 for us numbers):");
			String pnum = in.nextLine();
			sns.subscribe(garn + "CSC470Test-Alpha", "email", email);
			sns.subscribe(garn + "CSC470Test-Alpha", "sms", pnum);
			System.out.println("Success!");
			pause();
			
			System.out.println("Attempting to list subscriptions.");
			String subs = "" + sns.listSubscriptions();
			String[] tmp1 = subs.split("SubscriptionArn: ");
			String[] subArn = new String[(tmp1.length - 1)];
			String[] topArn = new String[(tmp1.length - 1)];
			String[] protoc = new String[(tmp1.length - 1)];
			String[] sOwner = new String[(tmp1.length - 1)];
			String[] endpnt = new String[(tmp1.length - 1)];
			for (int i = 1; i < tmp1.length; i++) {
				String[] tmp2 = tmp1[i].split(",");
				subArn[i - 1] = tmp2[0];
				sOwner[i - 1] = tmp2[1].substring(7);
				protoc[i - 1] = tmp2[2].substring(10);	
				endpnt[i - 1] = tmp2[3].substring(10);
				topArn[i - 1] = tmp2[4].substring(10);
			}
			for (int i = 0; i < subArn.length; i++) {
				System.out.println("    SubscriptionARN: " + subArn[i]);
				tmp = topArn[i].split("}");
				System.out.println("    TopicARN       : " + tmp[0]);
				System.out.println("    Protocol       : " + protoc[i]);
				System.out.println("    Owner          : " + sOwner[i]);
				System.out.println("    Endpoint       : " + endpnt[i]);
				System.out.println("");
			}
			System.out.println("Success!");
			pause();
			
			System.out.println("Now at the A level.");
			pause();
			
			System.out.println("Attempting to publish a message.");
			sns.publish(garn + "CSC470Test-Alpha", "RIP in peace desk.", "A great loss to Forcina Hall...");
			System.out.println("Success!");
			pause();
			
			System.out.println("Attempting to remove the subscription of the cell phone entered eariler.");
			int target = 0;
			for (int i = 0; i < endpnt.length; i++) {
				if (endpnt[i].equals(pnum)) {
					target = i;
					break;
				}
			}
			sns.unsubscribe(subArn[target]);
			System.out.println("Success!");
			pause();

			System.out.println("_______________________________");
			System.out.println(" Deleting all that I have done.");
			sns.deleteTopic(garn + "CSC470Test-Alpha");
			for (int i = 0; i < endpnt.length; i++) {
				if (endpnt[i].equals(email)) {
					target = i;
					break;
				}
			}
			sns.unsubscribe(subArn[target]);
			System.out.println("     Done, exiting!");
			in.close();
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

	public static String[] listTopics(boolean print) {
		if (print) {
			System.out.println("List of topic ARN's:");
		}
		String topics = "" + sns.listTopics();
		String tmp[] = topics.split("TopicArn: ");
		String topicArns[] = new String[(tmp.length - 1)];
		for (int i = 1; i < tmp.length; i++) {
			topicArns[(i - 1)] = tmp[i].substring(0, (tmp[i].length() - 4));
			if (print) {
				System.out.println("    " + topicArns[(i - 1)]);
			}
		}
		return topicArns;
	}

	public static String getGeneralArn() {
		String topicArns[] = listTopics(false);
		String tmp[] = topicArns[0].split("CSC470Test-Alpha");
		return tmp[0];
	}

	/**
	 * Adds a delay in the console for clarity.
	 */
	private static void pause() {
		System.out.println("\n_______________________________");
		System.out.println("     Press enter to continue...");
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		in.nextLine();
		for (int i = 0; i < 100; i++) {
			System.out.println("\n");
		} /* for */
	} /* pause() */

} /* Driver.java */