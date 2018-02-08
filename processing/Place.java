import java.util.*;
public class Place{
   public String name;
   public String address;
   public String phone;
   public String[] hours;
   public String price;
   public String rating;
   public ArrayList<String> types;
   public String website;
   public ArrayList<Review> reviews;
   public Place(Scanner scan){
      scan.nextLine();
      this.name = scan.nextLine();
      scan.nextLine();
      this.address = scan.nextLine();
      scan.nextLine();
      this.phone = scan.nextLine();
      scan.nextLine();
      this.hours = parseHours(scan.nextLine());
      scan.nextLine();
      this.price = scan.nextLine();
      scan.nextLine();
      this.rating = scan.nextLine();
      scan.nextLine();
      this.types = parseTypes(scan.nextLine());
      scan.nextLine();
      this.website = scan.nextLine();
      this.reviews = parseReviews(scan);
      System.out.println(this.toString());
   }
   public String toString(){
      String ret = "";
      ret+=this.name + "\n";
      ret+=this.address + "\n";
      ret+=this.phone + "\n";
      for(int i = 0; i < 7 ;i ++){
         if(this.hours != null){
            ret+=this.hours[i] + " ";
         }
      }
      ret+="\n";
      ret+=this.price + "\n";
      ret+=this.rating + "\n";
      if(this.types != null){
         ret+=this.types.toString() + "\n";
      }

      ret+=this.website+"\n";
      if(this.reviews != null){
         ret+=this.reviews.toString();
      }
      return ret;
   }
   /*
   private String[] parseHours(String l){
      if(true) return null;
      if(l.equals("not found")){
         return null;
      }
      String[] ret = new String[7];
      String[] splitup = l.split(",");
      for(int i = 0; i < splitup.length; i++){
         String hours = splitup[i].split(":", 2)[1];
         if(hours.contains("Closed")){
            ret[i] = "Closed";
            continue;
         }
         System.err.println(hours);
         String[] tmp = hours.split(" ");
         //this really should be cleaned up, but I was tired of dealing with the strings -_-
         try{
            tmp[5] = tmp[5].split("'")[0];
            hours = tmp[1] + tmp[2] + " " + tmp[4] + tmp[5];
            ret[i] = hours;
         }catch(Exception e){

         }
      }
      return ret;
   }
   */
   private String[] parseHours(String l){
      System.out.println(l);
      if(l.equals("not found")){
         return null;
      }
      String[] ret = new String[7];
      String[] splitup = l.split("`");
      for(int i = 0; i < splitup.length; i++){
         String s = splitup[i];
         if(s.contains("Closed")){
            ret[i] = "Closed";
         }
         s = s.split(":", 2)[1];
         ret[i] = s;
      }
      return ret;
   }
   private ArrayList<String> parseTypes(String l){
      if(l.equals("not found")){
         return null;
      }
      ArrayList<String> ret = new ArrayList<String>();
      String[] splitup = l.split("`");
      for(String s : splitup){
         ret.add(s);
      }
      return ret;

   }
   private ArrayList<Review> parseReviews(Scanner scan){
      ArrayList<Review> ret = new ArrayList<Review>();
      String line = scan.nextLine();
      line = scan.nextLine();
      if(line.equals("no reviews found")){
         scan.nextLine();
         return null;
      }
      while(!line.equals("========================================================")){
         String temp = "";
         String text = "";
         while(!temp.equals("rating")){
            text+=temp;
            temp = scan.nextLine();
         }
         String rev = scan.nextLine();
         ret.add(new Review(text, rev));
         line = scan.nextLine();
         line = scan.nextLine();
      }
      return ret;

   }
   public class Review{
      String text;
      String rating;
      public Review(String text, String rat){
         this.text = text;
         this.rating = rat;
      }
      public String toString(){
         return this.text + "\n" + this.rating;
      }
   }
}


