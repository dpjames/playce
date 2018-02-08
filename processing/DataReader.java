import java.util.*;
import java.io.*;
/*
 * the main program to read in data and parse it into a Place object
 * Data needs to be in the format given by the python file in ../datagrab/google/parse.py
 * see betaDataGrab for example.
 */
public class DataReader{
   public static void main(String[] args) throws Exception{
      System.out.println("readin file " + args[0]);
      File f = new File(args[0]);
      ArrayList<Place> places = new ArrayList<Place>();
      Scanner scan = new Scanner(f);
      while(scan.hasNextLine()){
         places.add(new Place(scan));
      }
   }
}
