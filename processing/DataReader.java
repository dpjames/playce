import java.util.*;
import java.io.*;

public class DataReader{
   public static void main(String[] args) throws Exception{
      ArrayList<Place> places = new ArrayList<Place>();
      //readGoogle(places);
      //readYelp(places);
      readFromFile(places);
      //writeToFile(places);
      System.out.println(places.size());
   }
   private static void readFromFile(ArrayList<Place> places) throws Exception{
      Scanner scan = new Scanner(new File("places.txt"));
      while(scan.hasNextLine()){
         String name = scan.nextLine();
         String address = scan.nextLine();
         String phone = scan.nextLine();
         String hours = scan.nextLine();
         String price = scan.nextLine();
         String rating = scan.nextLine();
         String website = scan.nextLine();
         ArrayList<String> types = new ArrayList<String>();
         String next = scan.nextLine();
         while(!next.equals("||")){
            types.add(next);
            next = scan.nextLine();
         }
         ArrayList<Place.Review> reviews = new ArrayList<Place.Review>();
         next = scan.nextLine();
         while(!next.equals("--")){
            String text = next;
            next = scan.nextLine();
            while(!next.equals("^^")){
               text+=("\n"+next);
               next = scan.nextLine();
            }
            reviews.add(new Place.Review(text, scan.nextLine()));
            next = scan.nextLine();
         }
         places.add(new Place(name,types,address,phone,hours,price,website,rating,reviews));
      }

   }
   private static void readYelp(ArrayList<Place> places){
      Grabber.yelp(places);
   }
   private static void writeToFile(ArrayList<Place> places){
      try(FileWriter fw = new FileWriter("places.txt", true);
          BufferedWriter bw = new BufferedWriter(fw);
          PrintWriter out = new PrintWriter(bw))
      {

         for(Place p : places){
            out.println(p.name);
            out.println(p.address);
            out.println(p.phone);
            out.println(p.hours);
            out.println(p.price);
            out.println(p.rating);
            out.println(p.website);
            for(String t : p.types){
               out.println(t);
            }
            out.println("||");
            try{
               for(Place.Review r : p.reviews){
                  out.println(r.text);
                  out.println("^^");
                  out.println(r.rating);
               }
            }catch(Exception e){
            
            }
            out.println("--");
         }
      } catch (IOException e) {
          //exception handling left as an exercise for the reader
      }
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
