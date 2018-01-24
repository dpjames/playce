import json

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
#    output.write(fullList[x]['reviews'][0]['text'].encode("utf-8")+"\n");
output.close();

