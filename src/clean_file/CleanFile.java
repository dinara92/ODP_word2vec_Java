package clean_file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class CleanFile {
	 public void removeLineFromFile(String file, String lineToRemove) {
		 
	        try {
	            File inFile = new File(file);
	            if (!inFile.isFile()) {
	                System.out.println("Parameter is not an existing file");
	                return;
	            }
	            //Construct the new file that will later be renamed to the original filename. 
	            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
	            BufferedReader br = new BufferedReader(new FileReader(file));
	            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
	            String line ;
	            //Read from the original file and write to the new 
	            //unless content matches data to be removed.
	            while ((line = br.readLine()) != null) {
	                if (!line.trim().equals(lineToRemove)) {
	                    pw.println(line);
	                    pw.flush();
	                }
	            }
	            pw.close();
	            br.close();
	 
	            //Delete the original file
	            if (!inFile.delete()) {
	                System.out.println("Could not delete file");
	                return;
	            }
	            //Rename the new file to the filename the original file had.
	            if (!tempFile.renameTo(inFile))
	                System.out.println("Could not rename file");
	 
	        } catch (FileNotFoundException ex) {
	            ex.printStackTrace();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	 
	    public static void main(String[] args) {
	    	CleanFile util = new CleanFile();
	        //util.removeLineFromFile("C:/Users/dinaraDILab/java_projects/ODP-Classifier/trainPages_bigSet_no_empty_strings.txt", "[]");
	    }
	
	
	
}
