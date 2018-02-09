import java.util.*;
import java.io.*;
/*
 * the main program to read in data and parse it into a Place object
 * Data needs to be in the format given by the python file in ../datagrab/google/parse.py
 * see betaDataGrab for example.
 */
public class DataReader{
   public static void main(String[] args) throws Exception{
      //System.out.println("readin file " + args[0]);
      File f = new File(args[0]);
      ArrayList<Place> places = new ArrayList<Place>();
      Scanner scan = new Scanner(f);
      while(scan.hasNextLine()){
         places.add(new Place(scan));
      }
      makeExcelSheet(places);
   }
   private static void makeExcelSheet(ArrayList<Place> places){
   //String[] goodTypes = {"amusement_park","aquarium","art_gallery","bakery","bar","book_store","bowling_alley","cafe","campground","casino","clothing_store","department_store","gym","home_goods_store","library","meal_delivery","meal_takeaway","movie_rental","movie_theater","museum","night_club","park","pet_store","restaurant","school","shoe_store","shopping_mall","spa","stadium","store","university","zoo"};
      //for(int i = 0 ; i < goodTypes.length-1; i++){
      //   System.out.print(goodTypes[i] + ",");
      //}
      //System.out.println(goodTypes[goodTypes.length-1]);
      for(Place p : places){
         System.out.print(p.name);
         for(String type : p.types){
            System.out.print(","+type);
         }
         System.out.println();
      }
   }
}
