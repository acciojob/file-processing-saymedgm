package com.driver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileProcessor {

	private List<String> fileNames;
	public ConcurrentHashMap<String, Integer> wordCounts = new ConcurrentHashMap<>();

	public FileProcessor(List<String> fileNames) {
		this.fileNames = fileNames;
	}

	public void processFiles() {
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		for (String fileName : fileNames) {
			executor.execute(new FileProcessorTask(fileName));
		}

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void displayWordCounts() {
		wordCounts.forEach((fileName, count) -> System.out.println(fileName + ": " + count + " words"));
	}


	private class FileProcessorTask implements Runnable {
		private String fileName;

		public FileProcessorTask(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void run() {
			int wordCount = 0;
			try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] words = line.trim().split("\\s+"); // Adjust regex if needed
					wordCount += words.length;
				}
			} catch (IOException e) {
				System.err.println("Error processing file: " + fileName);
				e.printStackTrace();
			}

			wordCounts.put(fileName, wordCount); // Thread-safe update
		}
	}

	public static void main(String[] args) {
		List<String> fileNames = Arrays.asList("file1", "file2", "file3");
		FileProcessor fileProcessor = new FileProcessor(fileNames);
		fileProcessor.processFiles();
		fileProcessor.displayWordCounts();
	}


}