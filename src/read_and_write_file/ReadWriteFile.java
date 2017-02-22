package read_and_write_file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

public class ReadWriteFile {

	static int i =0;
	
	public static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	            readWriteOneFile(fileEntry.getName());
	            i++;
	        }
	    }
	}
	
	public static void readWriteOneFile(String file_name){


		try {
			PrintStream clean_pages = new PrintStream(new FileOutputStream("/home/dinara/word2vec/"
					+ "1-billion-word-language-modeling-benchmark-r13output/cleaned_files/" + file_name + "_cleaned" + ".txt"));

			File file = new File("/home/dinara/word2vec/1-billion-word-language-modeling-benchmark-r13output/training-monolingual.tokenized.shuffled/" + file_name);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {


				clean_pages.println(utils.StringProcessingUtils.removeStemmedStopWords(line));
			}
			fileReader.close();
	        clean_pages.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    
	 public static void main( String[] args ) {
		
		final File folder = new File("/home/dinara/word2vec/1-billion-word-language-modeling-benchmark-r13output/training-monolingual.tokenized.shuffled");
		listFilesForFolder(folder);
		System.out.println("Files in folder " + i);

		

	    }
	    
	
	
	
	
	
	
}
