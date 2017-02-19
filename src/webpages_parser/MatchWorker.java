package webpages_parser;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.concurrent.Callable;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author linski
 */
public class MatchWorker implements Callable<Match>{

private String element;
String all_paragraphs;

public MatchWorker(String element) {
    this.element = element;
    
}

    @Override
    public Match call() throws Exception {
        //Match match = null;
        Match allTextFromPage = null;
        
        //match = new Match(App.connect(element).title());
        Elements divs = App.connect(element).select("div, p, b"); 
	      for (Element div : divs) {
	          //System.out.println(div.ownText());
	    	  String new_div = div.ownText() + " ";
	          all_paragraphs+=new_div;
	          //System.out.println("This is text from page: " + all_paragraphs);
	      }
	     allTextFromPage = new Match(all_paragraphs);
	     //System.out.println("This is text from page: " + allTextFromPage);
        return allTextFromPage;
    }
    
    /*@Override
    public Match call() throws Exception {
        Match match = null;
        match = new Match(App.connect(element).title());
       
        return match;
    }*/
}