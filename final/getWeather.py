import urllib2
import json

f = urllib2.urlopen('http://api.wunderground.com/api/eff6f60613b31fc1/conditions/q/CA/San_Luis_Obispo.json')

json_string = f.read()
parsed_json = json.loads(json_string)
location = parsed_json['current_observation']['display_location']['city']
temp_f = parsed_json['current_observation']['temperature_string']
#print "Current temperature in %s is: %s" % (location, temp_f)
f.close()

g = urllib2.urlopen('http://api.wunderground.com/api/eff6f60613b31fc1/conditions/q/CA/San_Luis_Obispo.json')

json_string = g.read()
parsed_json = json.loads(json_string)


'''
for day in parsed_json['forecast']['txt_forecast']['forecastday']:
   title = day['title']
   weather_txt = day['fcttext']
   weather_icon = day['icon']
   weather_icon_url = day['icon_url']
   print "Weather for %s is %s" % (title, weather_txt)
'''
print parsed_json['current_observation']['precip_today_in']

g.close()
