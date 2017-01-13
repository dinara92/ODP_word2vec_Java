/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tfidfDocument;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import document_node.Document;
import inverted_index.InvertedIndex;

//<editor-fold defaultstate="collapsed" desc="TFIDF calculator">
/**
 * Class to calculate TfIdf of term.
 * @author Mubin Shrestha
 */
public class TfIdf {
    
    //<editor-fold defaultstate="collapsed" desc="TF Calculator">
    /**
     * Calculated the tf of term termToCheck
     * @param totalterms : Array of all the words under processing document
     * @param termToCheck : term of which tf is to be calculated.
     * @return tf(term frequency) of term termToCheck
     */
    public double tfCalculator(List<String> totalterms, String termToCheck) {
        double count = 0;  //to count the overall occurrence of the term termToCheck
        for (String s : totalterms) {
            if (s.equalsIgnoreCase(termToCheck)) {
                count++;
            }
        }
        //return (1 + Math.log(count / (double) totalterms.size()));
        return count / (double) totalterms.size();
        //return Math.pow((Math.E - 1.7), (count / (double) totalterms.size()));

    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Idf Calculator">
    /**
     * Calculated idf of term termToCheck
     * @param allTerms : all the terms of all the documents
     * @param termToCheck
     * @return idf(inverse document frequency) score
     */
    public double idfCalculator(List<List<String>> allTerms, String termToCheck) {
        double count = 0;
        for (List<String> ss : allTerms) {
            for (String s : ss) {
                if (s.equalsIgnoreCase(termToCheck)) {
                    count++; //here, count of df - document frequency
                    break;
                }
            }
        }
        return Math.log(allTerms.size() / count);
    }

    //public void idfCalculatorNew(List<String> allTerms, String termToCheck) throws IOException {
    public double idfCalculatorNew(String termToCheck, InvertedIndex index) throws IOException {
        int df = index.searchOccurences(termToCheck);
        return Math.log(index.getAllDocsSize() / (double) df);
    }
    
//</editor-fold>
}
//</editor-fold>
