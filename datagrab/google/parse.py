import json
goodTypes = [
"amusement_park",
"aquarium",
"art_gallery",
"bakery",
"bar",
"book_store",
"bowling_alley",
"cafe",
"campground",
"casino",
"clothing_store",
"department_store",
"gym",
"home_goods_store",
"library",
"meal_delivery",
"meal_takeaway",
"movie_rental",
"movie_theater",
"museum",
"night_club",
"park",
"pet_store",
"restaurant",
"school",
"shoe_store",
"shopping_mall",
"spa",
"stadium",
"store",
"university",
"zoo"];
def checkprint(val):
    try:
        print(val)
    except:
        print("not found")

f1 = open("100.txt")
f2 = open("200.txt")
f3 = open("300.txt")
f4 = open("400.txt")
f5 = open("500.txt")
f6 = open("600.txt")
f7 = open("700.txt")
f8 = open("800.txt")
f9 = open("900.txt")
f10 =open("1000.txt")
files = [
open("100.txt"),
open("200.txt"),
open("300.txt"),
open("400.txt"),
open("500.txt"),
open("600.txt"),
open("700.txt"),
open("800.txt"),
open("900.txt"),
open("1000.txt")
];
fullList = [];
for i in range(0, len(files)):
    curstr = files[i].read()
    curjs = json.loads(curstr);
    fullList+=curjs
    files[i].close();
output = open("output", "w+");

for x in range(0,len(fullList)):
    if(fullList[x]['types'][0] not in goodTypes):
        continue
    print("name")
    try:
        print(fullList[x]['name'].encode("utf-8"))
    except:
        print("not found")
    print("address")
    try:
        print(fullList[x]['formatted_address'])
    except:
        print("not found")
    print("phone")
    try:
        print(fullList[x]['formatted_phone_number'])
    except:
        print("not found")
    print("hours")
    try:
        print(fullList[x]['opening_hours']['weekday_text'])
        #print(fullList[x]['opening_hours']['weekday_text'][0])
        #print(fullList[x]['opening_hours']['weekday_text'][1])
        #print(fullList[x]['opening_hours']['weekday_text'][2])
        #print(fullList[x]['opening_hours']['weekday_text'][3])
        #print(fullList[x]['opening_hours']['weekday_text'][4])
        #print(fullList[x]['opening_hours']['weekday_text'][5])
        #print(fullList[x]['opening_hours']['weekday_text'][6])
    except:
        print("not found")
    print("price")
    try:
        print(fullList[x]['price_level']) 
    except:
        print("not found")
    print("rating")
    try:
        print(fullList[x]['rating'])
    except:
        print("not found")
    print("types")
    try:
        print(fullList[x]['types'])
    except:
        print("not found")
    print("website")
    try:
        print(fullList[x]['website'])
    except:
        print("not found")
    print("reviews")
    try:
        revs = fullList[x]['reviews']
        for r in range(0,len(revs)):
            print("*****")
            print("text")
            print(revs[r]["text"].encode("utf-8"))
            print("rating")
            try:
                print(revs[r]['rating'])
            except:
                print("not found")
            print("*****")
    except:
        print "no reviews found";
    print "========================================================";
'''
for a in range(0,len(fullList)):
    print(fullList[a]['name'].encode("utf-8"));
    try:
        revs = fullList[a]['reviews']
        for r in range(0,len(revs)):
            print(revs[r]["text"].encode("utf-8"))
            print("");
    except:
        print "no reviews found";
    print "========================================================";
#for x in range(0, len(fullList)):
'''
#    output.write(fullList[x]['reviews'][0]['text'].encode("utf-8")+"\n");
output.close();






