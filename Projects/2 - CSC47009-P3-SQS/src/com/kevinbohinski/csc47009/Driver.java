/**
 * @author Kevin Bohinski <bohinsk1@tcnj.edu>
 * @version 1.0
 * @since 2015-4-11
 *  
 * CSC47009-P3-SQS
 * Driver.java
 * Copyright (c) 2015 Kevin Bohinski. All rights reserved.
 */

/*
 * CSC 470 – Special Topics in Computer Science: Cloud Computing
 * Small Project #4 – Simple Queue Service (SQS)
 * 
 * Objective
 * Develop  a  small  Java  application  that  utilizes  the  SQS  service  to  raise  your
 * awareness and comprehension of these services and their documented API.
 */

/* Setting Package */
package com.kevinbohinski.csc47009;

/* Setting Imports */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

public class Driver {

	/* Global Vars */
	private static AmazonSQSClient sqs;

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
		System.out.println("        Amazon SQS Tool");
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
		System.out.println("\nAttempting to connect to Amazon SQS.");
		BasicAWSCredentials AWSUser = new BasicAWSCredentials(keyId, secretKey);
		sqs = new AmazonSQSClient(AWSUser);
		System.out.println("Connected to Amazon SQS!");
		System.out.println("\nAttempting to connect to the US East 1 Region.");
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);
		sqs.setRegion(usEast1);
		System.out.println("Connected to US East!");

		try {
			pause();
			Scanner in = new Scanner(System.in);

			System.out.println("Starting at the D level.");
			pause();

			/*
			 * D - CreateQueue Create two message queues in the SQS service
			 * named 'csc470test' and 'csc470test2'.
			 */
			System.out
					.println("Attempting to create a queue named: csc470test.");
			sqs.createQueue("csc470test");
			System.out.println("Success!");
			System.out
					.println("\nAttempting to create a queue named: csc470test2.");
			sqs.createQueue("csc470test2");
			System.out.println("Success!");
			pause();

			/*
			 * D - ListQueue List the queues associated with your account.
			 */
			System.out.println("Attempting to obtain a list of the queues.");
			String t = "" + sqs.listQueues();
			String tArr[] = t.split(", ");
			String tArr1[];
			ArrayList<String> queueList = new ArrayList<String>();
			for (int i = 0; i < tArr.length; i++) {
				tArr1 = tArr[i].split("/");
				if (i == (tArr.length - 1)) {
					t = tArr1[(tArr1.length - 1)];
					tArr = t.split("]");
					queueList.add(tArr[0]);
				} else {
					queueList.add(tArr1[(tArr1.length - 1)]);
				}
			}
			for (int i = 0; i < queueList.size(); i++) {
				System.out.println(i + ") " + queueList.get(i));
			}
			System.out.println("Success!");
			pause();

			System.out.println("Now at the C level.");
			pause();

			/*
			 * C - GetQueueAttributes Fetch and print (individually with labels)
			 * the following attributes of the 'csc470test' queue: o The
			 * creation time of the queue o The ARN of the queue o The message
			 * retention period (properly labeled with a time label) o The time
			 * the queue was last modified o The current approximate number of
			 * messages
			 */
			System.out
					.println("Attempting to obtain attributes of the csc470test queue.");
			String qurl = "https://sqs.us-east-1.amazonaws.com/580270958987/csc470test";
			ArrayList<String> attribsReq = new ArrayList<String>();
			attribsReq.add("CreatedTimestamp");
			attribsReq.add("QueueArn");
			attribsReq.add("MessageRetentionPeriod");
			attribsReq.add("LastModifiedTimestamp");
			attribsReq.add("ApproximateNumberOfMessages");
			GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(
					qurl, attribsReq);
			String attribs = sqs.getQueueAttributes(getQueueAttributesRequest)
					.toString();
			attribs = attribs.substring(14);
			attribs = attribs.substring(0, (attribs.length() - 2));
			tArr = attribs.split(", ");
			for (int i = 0; i < 5; i++) {
				System.out.println(i + ") " + tArr[i]);
			}
			System.out.println("Success!");
			pause();

			/*
			 * C - DeleteQueue Delete the 'csc470test2' queue.
			 */
			System.out.println("Attempting to delete the csc470test2 queue.");
			String qurl2 = qurl + "2";
			sqs.deleteQueue(qurl2);
			System.out.println("Success!");
			pause();

			System.out.println("Now at the B level.");
			pause();

			/*
			 * B - SendMessage Send five messages to the 'csc470test' queue. The
			 * messages should be five famous quotes of your choice. Include the
			 * quote's originator in the message. With each submission, print
			 * the message's ID (returned from the request).
			 */
			System.out
					.println("Attempting to send 5 quotes to the csc470test queue.");

			SendMessageRequest smr1 = new SendMessageRequest();
			smr1.setMessageBody("\"So often in time it happens, we all live our life in chains, and we never even know we have the key.\" - The Eagles");
			smr1.withQueueUrl(qurl);
			tArr = sqs.sendMessage(smr1).toString().split(",");
			System.out.println("Sending quote 1, with id: "
					+ tArr[1].substring(0, (tArr[1].length() - 1)));
			System.out.println("Quote 1 sent!");

			SendMessageRequest smr2 = new SendMessageRequest();
			smr2.setMessageBody("\"Then one day you find, ten years have got behind you. No one told you when to run, you missed the starting gun.\" - Pink Floyd");
			smr2.withQueueUrl(qurl);
			tArr = sqs.sendMessage(smr2).toString().split(",");
			System.out.println("Sending quote 2, with id: "
					+ tArr[1].substring(0, (tArr[1].length() - 1)));
			System.out.println("Quote 2 sent!");

			SendMessageRequest smr3 = new SendMessageRequest();
			smr3.setMessageBody("\"I'm not the man they think I am at home, Oh no, no, no, I'm a rocket man.\"  - Elton John");
			smr3.withQueueUrl(qurl);
			tArr = sqs.sendMessage(smr3).toString().split(",");
			System.out.println("Sending quote 3, with id: "
					+ tArr[1].substring(0, (tArr[1].length() - 1)));
			System.out.println("Quote 3 sent!");

			SendMessageRequest smr4 = new SendMessageRequest();
			smr4.setMessageBody("\"Would you stay if she promised you heaven? Will you ever win?\" - Fleetwood Mac");
			smr4.withQueueUrl(qurl);
			tArr = sqs.sendMessage(smr4).toString().split(",");
			System.out.println("Sending quote 4, with id: "
					+ tArr[1].substring(0, (tArr[1].length() - 1)));
			System.out.println("Quote 4 sent!");

			SendMessageRequest smr5 = new SendMessageRequest();
			smr5.setMessageBody("\"One more job oughta get it, One last shot 'fore we quit it, One for the road.\" - Boz Scaggs");
			smr5.withQueueUrl(qurl);
			tArr = sqs.sendMessage(smr5).toString().split(",");
			System.out.println("Sending quote 5, with id: "
					+ tArr[1].substring(0, (tArr[1].length() - 1)));
			System.out.println("Quote 5 sent!");

			System.out.println("Success!");
			pause();

			System.out
					.println("Please note that one can not be in the aws console at the same time as this program runs.");
			System.out
					.println("Otherwise this program will not be able to recieve messages.");
			pause();

			/*
			 * B - ReceiveMessage Issue three receive message requests on the
			 * 'csc470test' queue and print the message returned. If no message
			 * is returned by a request, print an informative string to the user
			 * indicating that the queue returned no message.
			 */
			System.out
					.println("Attempting to receive and delete 3 messages from the csc470test queue.");

			try {
				String msg1 = sqs.receiveMessage(qurl).toString();
				System.out.println("Message 1: " + msg1);
				tArr = msg1.split("ReceiptHandle: ");
				tArr1 = tArr[1].split(",MD5OfBody:");
				String rh1 = tArr1[0];
				DeleteMessageRequest dmr1 = new DeleteMessageRequest();
				dmr1.withQueueUrl(qurl);
				dmr1.withReceiptHandle(rh1);
				sqs.deleteMessage(dmr1);
				System.out.println("Message 1 has been deleted.");

				String msg2 = sqs.receiveMessage(qurl).toString();
				System.out.println("Message 2: " + msg2);
				tArr = msg2.split("ReceiptHandle: ");
				tArr1 = tArr[1].split(",MD5OfBody:");
				String rh2 = tArr1[0];
				DeleteMessageRequest dmr2 = new DeleteMessageRequest();
				dmr2.withQueueUrl(qurl);
				dmr2.withReceiptHandle(rh2);
				sqs.deleteMessage(dmr2);
				System.out.println("Message 2 has been deleted.");

				String msg3 = sqs.receiveMessage(qurl).toString();
				System.out.println("Message 3: " + msg3);
				tArr = msg3.split("ReceiptHandle: ");
				tArr1 = tArr[1].split(",MD5OfBody:");
				String rh3 = tArr1[0];
				DeleteMessageRequest dmr3 = new DeleteMessageRequest();
				dmr3.withQueueUrl(qurl);
				dmr3.withReceiptHandle(rh3);
				sqs.deleteMessage(dmr3);
				System.out.println("Message 3 has been deleted.");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err
						.println("For one reason or another, a message could not be recieved. Please restart the program.");
				System.out
						.println("For one reason or another, a message could not be recieved. Please restart the program.");
			}

			System.out.println("Success!");
			pause();

			System.out.println("Now at the A level.");
			pause();

			/*
			 * A - SetQueueAttributes Change the following attributes on the
			 * 'csc470test' queue as follows: o Set the queue's visibility
			 * timeout to 60 minutes o Set the maximum message size to 1024 KiB
			 * o Set the message retention period to 2 days o Set the receive
			 * message wait time attribute to 15 seconds.
			 */
			System.out
					.println("Attempting to set attributes for the csc470test queue.");
			SetQueueAttributesRequest sqar = new SetQueueAttributesRequest();
			sqar.withQueueUrl(qurl);
			Map<String, String> sqarmap = new HashMap<String, String>();
			sqarmap.put("VisibilityTimeout", "3600");
			sqarmap.put("MaximumMessageSize", "131072");
			sqarmap.put("MessageRetentionPeriod", "172800");
			sqarmap.put("ReceiveMessageWaitTimeSeconds", "15");
			sqar.withAttributes(sqarmap);
			sqs.setQueueAttributes(sqar);
			System.out.println("Success!");
			pause();

			/*
			 * A - PurgeQueue – Purge all remaining messages in the 'csc470test'
			 * queue.
			 */
			System.out.println("Attempting to purge the csc470test queue.");
			PurgeQueueRequest pqr = new PurgeQueueRequest();
			pqr.withQueueUrl(qurl);
			sqs.purgeQueue(pqr);
			System.out.println("Success!");
			pause();

			System.out.println("_______________________________");
			System.out.println(" Deleting all that I have done.");
			sqs.deleteQueue(qurl);
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