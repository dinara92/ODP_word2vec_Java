package webpages_parser;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author linski
 */
public class Match {
    
    private String url;


    public Match(String url) {
        this.url = url;
    }

    /*@Override
    public String toString() {
        return "Match{" + "url=" + url + '}';
    }*/
    
    @Override
    public String toString() {
        return url;
    }
    
    
    
    
}