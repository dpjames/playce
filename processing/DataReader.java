import java.util.*;
import java.io.*;

public class DataReader{
   public static void main(String[] args){
      ArrayList<Place> places = new ArrayList<Place>();
      System.out.println(places.size());
      readGoogle(places);
      //System.out.println(places);
      System.out.println(places.size());
      readYelp(places);
      System.out.println(places.size());
   }
   private static void readYelp(ArrayList<Place> places){
      Grabber.yelp(places);
   }
   private static void readGoogle(ArrayList<Place> places){
      try{
         File f = new File("googleData");
         Scanner scan = new Scanner(f);
         while(scan.hasNextLine()){
            places.add(new Place(scan));
         }
      }catch(Exception e){
         e.printStackTrace();  
         System.out.println("googleData could not be parsed. check the file");
      }
   }


   private static void makeExcelSheet(ArrayList<Place> places){
      for(Place p : places){
         System.out.print(p.name.replace(",","").replace("|","") + ",");
         System.out.print((p.price.equals("not found") ? "" : p.price.replace(",","").replace("|","") )+ ",");
         System.out.print((p.rating.equals("not found") ? "" : p.rating.replace(",","").replace("|","") ));
         for(String type : p.types){
            System.out.print(","+type.replace(",","").replace("|",""));
         }
         System.out.println();
      }
   }
}
