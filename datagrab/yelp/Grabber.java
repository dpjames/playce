import java.util.*;
import java.net.*;
import java.io.*;
import javax.json.*;
public class Grabber{
   private static ArrayList<Place> places = new ArrayList<Place>(); 
   public static void main(String[] args) throws Exception{

      sendGet();
      System.out.println(places);
   }
	private static void sendGet() throws Exception {
      for(int offset = 0; offset < 350; offset+=50){      
         String url = "https://api.yelp.com/v3/businesses/search?location=93405&limit=50&radius=5000&offset="+offset;
         URL obj = new URL(url);
         HttpURLConnection con = (HttpURLConnection) obj.openConnection();
         con.setRequestMethod("GET");
         con.setRequestProperty("Authorization", "Bearer glpSGemo5dTZBl2LonEgLTDuiYZa_4p0QroONqgHxOnX1wFOAib951FMxOs_w6-gQoziXG4lPsX6pK6mX5b1JO0W6ZL_kNBDIPHKt9mJGMhLytwkt5OhPW7BYVpvWnYx");
         System.out.println("\nSending 'GET' request to URL : " + url);
         int responseCode = con.getResponseCode();
         System.out.println("Response Code : " + responseCode);

         BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
         String inputLine;
         StringBuffer response = new StringBuffer();
         while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
         }
         in.close();

         //print result
         //System.out.println(response.toString());
         JsonReader jsonread = Json.createReader(new StringReader(response.toString()));
         JsonObject ob = jsonread.readObject();
         jsonread.close();
         //System.out.println(ob);
         handleObject(ob);
      }
	}
   private static void handleObject(JsonObject ob) throws Exception{
      JsonArray buisnesses = ob.getJsonArray("businesses");
      for(JsonValue cur : buisnesses){
         JsonObject curbiz = (JsonObject)cur;
         

         //extract the types
         ArrayList<String> types = new ArrayList<String>();
         JsonArray cats = curbiz.getJsonArray("categories");
         if(cats == null){
            System.out.println("error in extract"); 
         }
         for(JsonValue thec : cats){
            JsonObject c = (JsonObject)thec;
            types.add(c.getJsonString("title").toString());
         }

         //get name
         String name = curbiz.getJsonString("name").toString();
         
         //getAddress
         JsonObject location = (JsonObject)(curbiz.getJsonObject("location"));
         String address = location.getJsonString("address1").toString();
         address += (" " + location.getJsonString("city").toString());
         address += (" " + location.getJsonString("state").toString());
         address += (" " + location.getJsonString("zip_code").toString());
         address = address.replace("\"",""); 

         //get phone
         String phone = curbiz.getJsonString("phone").toString().replace("\"", "");
         if(phone.equals("")) phone = "not found";
         
         //hours :(
         String hours = "TODO"; //TODO

         //price
         JsonString p = curbiz.getJsonString("price");
         String price = "";
         if(p == null){
            price = "not found";
         }else{
            price = getPrice(p.toString().replace("\"",""));
         }

         //website
         //
         String website = curbiz.getJsonString("url").toString().replace("\"","");

         //rating
         String rating = ""+curbiz.getJsonNumber("rating").doubleValue();
         //reviews
            ArrayList<Place.Review> reviews;
         try{
            reviews = getReviews(curbiz.getJsonString("id").getString());
         }catch(Exception e){
            reviews = null;
         }
         places.add(new Place(name, types, address, phone, hours, price, website, rating, reviews));
      }
   }
   private static ArrayList<Place.Review> getReviews(String id) throws Exception{
      String url = "https://api.yelp.com/v3/businesses/"+id+"/reviews";
         URL obj = new URL(url);
         HttpURLConnection con = (HttpURLConnection) obj.openConnection();
         con.setRequestMethod("GET");
         con.setRequestProperty("Authorization", "Bearer glpSGemo5dTZBl2LonEgLTDuiYZa_4p0QroONqgHxOnX1wFOAib951FMxOs_w6-gQoziXG4lPsX6pK6mX5b1JO0W6ZL_kNBDIPHKt9mJGMhLytwkt5OhPW7BYVpvWnYx");
         int responseCode = con.getResponseCode();
         BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
         String inputLine;
         StringBuffer response = new StringBuffer();
         while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
         }
         in.close();

         JsonReader jsonread = Json.createReader(new StringReader(response.toString()));
         JsonObject ob = jsonread.readObject();
         jsonread.close();

         JsonArray revs = ob.getJsonArray("reviews");
         ArrayList<Place.Review> ret = new ArrayList<Place.Review>();

         for(JsonValue r : revs){
            JsonObject therev = (JsonObject) r;
            ret.add(new Place.Review(therev.getJsonString("text").getString(), ""+therev.getJsonNumber("rating").doubleValue()));
         }

         return ret;
   }
   private static String getPrice(String p){
      return ""+(p.length() - p.replace("$", "").length());
   }
}
