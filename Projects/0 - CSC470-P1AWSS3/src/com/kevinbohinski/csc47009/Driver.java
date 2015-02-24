/**
 * @author Kevin Bohinski <bohinsk1@tcnj.edu>
 * @version 1.0
 * @since 2015-2-14
 * 
 * CSC47009-P1-S3
 * Driver.java
 * Copyright (c) 2015 Kevin Bohinski. All rights reserved.
 */

/*
 * CSC 470 – Special Topics in Computer Science: Cloud Computing
 * Small Project #2 – AWS Simple Storage Service (S3)
 *
 * Objective
 * Develop a small Java application that utilizes various S3 services
 * and features to raise your awareness and comprehension of S3 and
 * its documented API.
 * 
 * Some code referenced from:
 * https://github.com/awslabs/aws-java-sample/blob/master/src/main/java/com/amazonaws/samples/S3Sample.java
 */

/* Setting Package */
package com.kevinbohinski.csc47009;

/* Setting Imports */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.StorageClass;

public class Driver {

	/* Global Vars */
	static AmazonS3 s3;

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

		System.out.println("===============");
		System.out.println("Amazon S3 Tool.");
		System.out.println("-Kevin Bohinski");
		System.out.println("===============");
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
		System.out.println("Attempting to connect to Amazon S3.");
		BasicAWSCredentials AWSUser = new BasicAWSCredentials(keyId, secretKey);
		s3 = new AmazonS3Client(AWSUser);
		System.out.println("Connected to Amazon S3!");
		System.out.println("Connected to US East.");
		Region usEast = Region.getRegion(Regions.US_EAST_1);
		s3.setRegion(usEast);

		try {

			clearScreen();
			System.out.println("Starting at the D level");
			System.out.println("Making a new bucket to put the objects in\n");
			pause();
			newBucket("tcnj-csc470-s3java-bohinsk1");
			pause();
			System.out.println("Making a new object\n");
			pause();
			putObject("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1.txt");
			pause();
			System.out.println("Downloading the object\n");
			pause();
			getObject("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1.txt");
			pause();
			System.out.println("Deleting the object\n");
			pause();
			deleteObject("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1.txt");
			pause();
			System.out.println("Now at the C level");
			System.out.println("Making a new bucket\n");
			pause();
			newBucket("tcnj-csc470-s3java-bohinsk1-2");
			pause();
			System.out.println("Deleting that bucket\n");
			pause();
			deleteBucket("tcnj-csc470-s3java-bohinsk1-2");
			pause();
			System.out
					.println("Checking if the first bucket exists, or if you have access to it\n");
			pause();
			headBucket("tcnj-csc470-s3java-bohinsk1");
			pause();
			System.out.println("Now at the B level");
			System.out.println("Copying an object\n");
			pause();
			System.out.println("We need another bucket for this.\n");
			pause();
			newBucket("tcnj-csc470-s3java-bohinsk1-2");
			pause();
			System.out.println("We need another file for this.\n");
			putObject("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1-2.txt");
			copyObj("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1-2.txt",
					"tcnj-csc470-s3java-bohinsk1-2");
			pause();
			System.out.println("Listing all buckets\n");
			pause();
			listBuckets();
			pause();
			System.out.println("Creating a text file with time expiration\n");
			pause();
			expireObj("tcnj-csc470-s3java-bohinsk1",
					"testFile-bohinsk1-expire.txt");
			pause();
			System.out.println("Now at the A level");
			System.out.println("Enabling versioning on a bucket\n");
			pause();
			bucketVers("tcnj-csc470-s3java-bohinsk1");
			pause();
			System.out.println("Listing versions of an object");
			System.out
					.println("To do this we need to overwrite an object first\n");
			pause();
			putObject("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1-2.txt");
			pause();
			System.out.println("Now we will list versions\n");
			pause();
			objVers("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1-2.txt");
			pause();
			System.out.println("Deleting a version\n");
			pause();
			deleteVers("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1-2.txt");
			pause();
			System.out.println("Viewing the metadata of an object\n");
			pause();
			getMeta("tcnj-csc470-s3java-bohinsk1", "testFile-bohinsk1-2.txt");
			pause();
			System.out.println("Done!");
			System.exit(0);

		} catch (AmazonServiceException ase) {
			System.out.println();
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon S3, but was rejected with an error response for some reason.");
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
							+ "a serious internal problem while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			System.out.println();
			System.out.println("Error Message: " + ace.getMessage());
		}

	}

	private static void getMeta(String bucket, String fname) {
		System.out.println("Attempting to get the metadata of " + fname
				+ " in bucket " + bucket);

		String meta = s3.getObjectMetadata(bucket, fname).getContentType();
		System.out.println(meta);
	}

	private static void deleteVers(String bucket, String fname) {
		System.out.println("Attempting to delete the second version of "
				+ fname + " in bucket " + bucket);
		ListIterator<S3VersionSummary> verList = s3.listVersions(bucket, fname)
				.getVersionSummaries().listIterator();
		String[] vids = new String[10];
		int num = 0;
		while (verList.hasNext()) {
			String vid1 = ((S3VersionSummary) verList.next()).getVersionId();
			vids[num] = vid1;
			System.out.println(num + ") Version ID: " + vid1);
			num++;
		}
		System.out.println("");
		s3.deleteVersion(bucket, fname, vids[0]);
		System.out.println("Version " + vids[num] + " of object " + fname
				+ " in bucket " + bucket + " deleted");
	}

	private static void objVers(String bucket, String fname) {
		System.out.println("Attemting to retrieve a list of versions for "
				+ fname + " in bucket " + bucket);
		ListIterator<S3VersionSummary> verList = s3.listVersions(bucket, fname)
				.getVersionSummaries().listIterator();
		while (verList.hasNext()) {
			String vid1 = ((S3VersionSummary) verList.next()).getVersionId();
			System.out.println("Version ID: " + vid1);
		}
	}

	private static void bucketVers(String bucket) {
		System.out.println("Enabling versioning on " + bucket);
		SetBucketVersioningConfigurationRequest request = new SetBucketVersioningConfigurationRequest(
				bucket, new BucketVersioningConfiguration(
						BucketVersioningConfiguration.ENABLED));
		s3.setBucketVersioningConfiguration(request);
		System.out.println("Versioning enabled on bucket " + bucket);
	}

	private static void expireObj(String bucket, String fname) {
		System.out.println("Attempting to create a random text file named "
				+ fname + " and placing it in " + bucket);
		System.out
				.println("Note: This will overwrite any existing file called "
						+ fname + " !");
		System.out.println("Generating File!");
		String stuff = "Yyyyyhhhhhhhhhyhhhhhhhhhhhhhhhhhhhyyyyyyyyhhhhhhyyyhhhhyhhhhhhhhhhhhhhhhhhhhhhhh\nyyyyyyyyyyyyyyyyyyyyyyyyyyyyso++++//++++///+oyyhyyhyyyyyyyyyyyyyyyyyyyyyyyyyyyyy\nyyyyyyyyyyyyyyyyyyyyyyyyyyo/::::::::::/:::::://+syyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy\nyyyyyyhyyyyyyyyyyyyyyyhhy+:::::::::::::-:::::::::/oyyyyyyyyyyyyyyyyyyyyyyyyyyyyy\nyyyyyyyyyyyyysssyyyyhddh+:::::::::::::-----:::-::::/syyyyyyyyyyyyyyyyyyyyyyyyyyy\nyyyyyyyyyyyyysssssyhmmh+::::::::::::::----------::::/syyyyyyyyyyyyyyyyyyysyyyyyy\nssssssyyyyyysssssshdmdo:---:::::::::::----..----:::::+hysyyyyyyyyyyyyyyyyyyyyyyy\nsssssyyyyyssssssshdddy/----::::::::::----...----:::://ydysssssssyyysyyyyyyyyysss\nyyyyyyyyyyyyysssyhddy+:----::::::::------.------:::://omdyyyyyyyyyyyyhhhhyyyyyyy\nyyyyyyyyyyyyyyysyhhyo:------::::::-------.-------::::/+dmhyyyyyyyyyyyhhhyyyyyyyy\nyyyyyyhyyyyyyys+ydhy+------/syyyyo+/:--.---...----:::/+dmdyyyyyyyysyyyhyyssyyyyy\nyyyyyyyyyyyyys+sydhs+oso+/osooooshhhs+:--:----:::::://+hmhyyyyyyysyyyyyyyyyyyyyy\nssssssssssssso/+yhho---/+dhsyyyhyyysso+////+syyhhhhs+/odmyyyyyyyyyyyyyyyhyyyyyyy\nsssssssssssss+/shdd+..-:/++oooshsoyyyo/::/+ssso+//oyy+omdsssssssssssssssssssssss\nyyyyyyyyyyyyy+/soo+-..-::////++++//+os/-:osyhdyyysosyoydhyyyyysyyyyyyyyyssyyyyyy\nyyyyyyyyyyyys+-+:.-...--://+//::::/oo+:-/o/+oooosyysyddyyyyyyyyyyyyyyyyyyyyyyysy\nsssssyyssss//-/:..-..---:///////+++o/:--:+/:////++//+oddyyyyyyyyyyyyyyyyyyyyyyyy\nssyyyyyyss.-o:/:....----/////++oy///::--:/+//:://////yhyyyyyyyyyyyyyyyyyyyyyyyyy\nsssssssso.`:os+-....-:::/++oooosyyyso++++//++/:::///+/+yyyyyyyyyyyyyyyyyyyyyyyyy\nssssoo+/` .+ss:.-..-:///+/+ssssoooooooooo++/oo/////++:osssssssssyyysssssssssssss\n-....``  `:od/------://////oydysooo+++//+++++os+//+++:+yyyyyyyyyyyysyyyyyyyyyyyy\n``````   `/oy------::////:/+oymhys+///+osssoo+++++++++syyyyyyyyyyyyyyyyyyyyyyyyy\n```````  `/ss:-----:///++///++oooooo+++oyyo//+//++++oyyyyyyyyyyyyyyyyyyyyyyyyyyy\n `````````:sy+:----::/++///////+++/////++/::///++++:./osyyyyyyyhhhyyyyyyyyyyyyyy\n``````````-+ss+/--::://///////++++++++++/::///++++:````..-/ssyyyyyysyyyyyyyyyyyy\n``````````./ssso/::::////:///+///////////://+++++:`````` ```.-/+ssyyyyysyyyyyyyy\n```````````-oysso+/:///+++///+++//:://////++++++-`   ``  ``   ```.:+syyyyyyyyyyy\n`   ```````./yssso+///++++ooo+ooo+++/+/+++++++/.``    `` ````      ``-/oyyyyyyyy\n     ```````-sysssoo++++++ooosssssssooo+o++++/.```     `  ``           `-oyyyyyy\n      ```````:sssosooo++++oooosssoooooo++++//-````        ``        `    `:syyyy\n      ```````.-/oooooooooo+oooooooooooo++/:::.``          ``               .+yyy\n      ``.--:-...-/ooooooooooooooooo++++:-::-.`````     `   `  ``             .oy\n  ```...--------.../+o+++++oooooo++/:-..-:-.```.:-.```        ``    `        `.s\n`````.....---------..:++++++//////-.``...-.````..------...``` ``    `       ```:\n`````````.......-----..-::////++:.``......`...```..```....```````  ``     ``````\n``````````````........---.../o/.`````.````...```````````````````  `     ``` ````\n````````````````````````````..`````````````````````````````````` ``   ``````````\n```````````````````````````.`````````````````````````````````````` `````````````\n``````````````````````````.`````````````````````````````````````````````````````\n ```````````````````````````````````````````````````````````````````````````````\n";
		try {
			File testFile = new File(fname);
			FileWriter out = new FileWriter(testFile);
			String contents = "" + UUID.randomUUID() + "\n\n\n\n" + stuff;
			out.write(contents);
			out.close();
			System.out.println("Printing File: \n");
			System.out.println(contents);

			Date expirationTime = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(expirationTime);
			cal.add(Calendar.DATE, 5);
			expirationTime = cal.getTime();
			System.out.println("This file will expire on: "
					+ expirationTime.toString());
			System.out
					.println("This file will be stored with reduced redundancy.");

			BucketLifecycleConfiguration blconfig = new BucketLifecycleConfiguration();
			BucketLifecycleConfiguration.Rule expire5d = new BucketLifecycleConfiguration.Rule()
					.withPrefix(fname)
					.withExpirationInDays(5)
					.withStatus(BucketLifecycleConfiguration.ENABLED.toString());
			blconfig.withRules(expire5d);
			s3.setBucketLifecycleConfiguration(bucket, blconfig);

			PutObjectRequest request = new PutObjectRequest(bucket, fname,
					testFile).withStorageClass(StorageClass.ReducedRedundancy);
			s3.putObject(request).setExpirationTime(expirationTime);

			System.out.println("File " + fname
					+ " has been created, uploaded to " + bucket
					+ " with an expiration, and reduced redundancy storage.");

		} catch (IOException e) {
			System.out.println("Error writing file.");
		}
	}

	private static void copyObj(String sBucket, String fname, String dBucket) {
		System.out.println("Attempting to copy " + fname + " from bucket "
				+ sBucket + " to " + dBucket);
		s3.copyObject(sBucket, fname, dBucket, fname);
		System.out.println(fname + " from bucket " + sBucket
				+ " has been copied into " + dBucket);
	}

	private static void headBucket(String bucket) {
		System.out.println("Attempting to access bucket " + bucket);
		if (s3.doesBucketExist(bucket)) {
			System.out.println("\nThe bucket " + bucket
					+ " does exist, and your account can access it.");
		} else {
			System.out
					.println("\nThe bucket "
							+ bucket
							+ " does not exist, or your account does not have access to it.");
		}
	}

	private static void deleteBucket(String bucket) {
		System.out.println("Attempting to delete bucket " + bucket);
		s3.deleteBucket(bucket);
		System.out.println("Bucket " + bucket + " has been deleted.");
	}

	private static void deleteObject(String bucket, String fname) {
		System.out.println("Attempting to delete " + fname + " from bucket "
				+ bucket);
		s3.deleteObject(bucket, fname);
		System.out.println(fname + " in bucket " + bucket
				+ " has been deleted.");
	}

	private static void getObject(String bucket, String fname) {
		System.out.println("Attempting to download " + fname + " from bucket "
				+ bucket);
		File testFile = new File(fname);
		s3.getObject(new GetObjectRequest(bucket, fname), testFile);
		System.out.println("Object " + fname + " from bucket " + bucket
				+ " downloaded.");
	}

	private static void putObject(String bucket, String fname) {
		System.out.println("\nOk, creating a random text file named " + fname
				+ " and placing it in " + bucket);
		System.out
				.println("Note: This will overwrite any existing file called "
						+ fname + " !");
		System.out.println("Generating File!");
		String stuff = "Yyyyyhhhhhhhhhyhhhhhhhhhhhhhhhhhhhyyyyyyyyhhhhhhyyyhhhhyhhhhhhhhhhhhhhhhhhhhhhhh\nyyyyyyyyyyyyyyyyyyyyyyyyyyyyso++++//++++///+oyyhyyhyyyyyyyyyyyyyyyyyyyyyyyyyyyyy\nyyyyyyyyyyyyyyyyyyyyyyyyyyo/::::::::::/:::::://+syyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy\nyyyyyyhyyyyyyyyyyyyyyyhhy+:::::::::::::-:::::::::/oyyyyyyyyyyyyyyyyyyyyyyyyyyyyy\nyyyyyyyyyyyyysssyyyyhddh+:::::::::::::-----:::-::::/syyyyyyyyyyyyyyyyyyyyyyyyyyy\nyyyyyyyyyyyyysssssyhmmh+::::::::::::::----------::::/syyyyyyyyyyyyyyyyyyysyyyyyy\nssssssyyyyyysssssshdmdo:---:::::::::::----..----:::::+hysyyyyyyyyyyyyyyyyyyyyyyy\nsssssyyyyyssssssshdddy/----::::::::::----...----:::://ydysssssssyyysyyyyyyyyysss\nyyyyyyyyyyyyysssyhddy+:----::::::::------.------:::://omdyyyyyyyyyyyyhhhhyyyyyyy\nyyyyyyyyyyyyyyysyhhyo:------::::::-------.-------::::/+dmhyyyyyyyyyyyhhhyyyyyyyy\nyyyyyyhyyyyyyys+ydhy+------/syyyyo+/:--.---...----:::/+dmdyyyyyyyysyyyhyyssyyyyy\nyyyyyyyyyyyyys+sydhs+oso+/osooooshhhs+:--:----:::::://+hmhyyyyyyysyyyyyyyyyyyyyy\nssssssssssssso/+yhho---/+dhsyyyhyyysso+////+syyhhhhs+/odmyyyyyyyyyyyyyyyhyyyyyyy\nsssssssssssss+/shdd+..-:/++oooshsoyyyo/::/+ssso+//oyy+omdsssssssssssssssssssssss\nyyyyyyyyyyyyy+/soo+-..-::////++++//+os/-:osyhdyyysosyoydhyyyyysyyyyyyyyyssyyyyyy\nyyyyyyyyyyyys+-+:.-...--://+//::::/oo+:-/o/+oooosyysyddyyyyyyyyyyyyyyyyyyyyyyysy\nsssssyyssss//-/:..-..---:///////+++o/:--:+/:////++//+oddyyyyyyyyyyyyyyyyyyyyyyyy\nssyyyyyyss.-o:/:....----/////++oy///::--:/+//:://////yhyyyyyyyyyyyyyyyyyyyyyyyyy\nsssssssso.`:os+-....-:::/++oooosyyyso++++//++/:::///+/+yyyyyyyyyyyyyyyyyyyyyyyyy\nssssoo+/` .+ss:.-..-:///+/+ssssoooooooooo++/oo/////++:osssssssssyyysssssssssssss\n-....``  `:od/------://////oydysooo+++//+++++os+//+++:+yyyyyyyyyyyysyyyyyyyyyyyy\n``````   `/oy------::////:/+oymhys+///+osssoo+++++++++syyyyyyyyyyyyyyyyyyyyyyyyy\n```````  `/ss:-----:///++///++oooooo+++oyyo//+//++++oyyyyyyyyyyyyyyyyyyyyyyyyyyy\n `````````:sy+:----::/++///////+++/////++/::///++++:./osyyyyyyyhhhyyyyyyyyyyyyyy\n``````````-+ss+/--::://///////++++++++++/::///++++:````..-/ssyyyyyysyyyyyyyyyyyy\n``````````./ssso/::::////:///+///////////://+++++:`````` ```.-/+ssyyyyysyyyyyyyy\n```````````-oysso+/:///+++///+++//:://////++++++-`   ``  ``   ```.:+syyyyyyyyyyy\n`   ```````./yssso+///++++ooo+ooo+++/+/+++++++/.``    `` ````      ``-/oyyyyyyyy\n     ```````-sysssoo++++++ooosssssssooo+o++++/.```     `  ``           `-oyyyyyy\n      ```````:sssosooo++++oooosssoooooo++++//-````        ``        `    `:syyyy\n      ```````.-/oooooooooo+oooooooooooo++/:::.``          ``               .+yyy\n      ``.--:-...-/ooooooooooooooooo++++:-::-.`````     `   `  ``             .oy\n  ```...--------.../+o+++++oooooo++/:-..-:-.```.:-.```        ``    `        `.s\n`````.....---------..:++++++//////-.``...-.````..------...``` ``    `       ```:\n`````````.......-----..-::////++:.``......`...```..```....```````  ``     ``````\n``````````````........---.../o/.`````.````...```````````````````  `     ``` ````\n````````````````````````````..`````````````````````````````````` ``   ``````````\n```````````````````````````.`````````````````````````````````````` `````````````\n``````````````````````````.`````````````````````````````````````````````````````\n ```````````````````````````````````````````````````````````````````````````````\n";
		try {
			File testFile = new File(fname);
			FileWriter out = new FileWriter(testFile);
			String contents = "" + UUID.randomUUID() + "\n\n\n\n" + stuff;
			out.write(contents);
			out.close();

			System.out.println("Printing File: \n");
			System.out.println(contents);
			s3.putObject(bucket, fname, testFile);
			System.out.println("File " + fname
					+ " has been created, uploaded to " + bucket);
		} catch (IOException e) {
			System.out.println("Error writing file.");
		}

	}

	private static void newBucket(String string) {
		System.out.println("Making a bucket named: " + string);
		s3.createBucket(string);
		System.out.println("Bucket " + string + " created!");
	}

	public static void listBuckets() {
		System.out
				.println("Attempting to retrieve a list of all of your buckets");
		int count = 0;
		for (Bucket bucket : s3.listBuckets()) {
			System.out.println(count + ") " + bucket.getName());
		} /* for */
	}

	public static void pause() {
		System.out.println("\n     Press enter to continue...");
		Scanner in = new Scanner(System.in);
		in.nextLine();
		for (int i = 0; i < 100; i++) {
			System.out.println("\n");
		}
	}

	public static void clearScreen() {
		System.out.println("\nOk!");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < 100; i++) {
			System.out.println("\n");
		}
	}

} /* Driver.java */