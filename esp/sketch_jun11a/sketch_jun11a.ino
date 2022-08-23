 #include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>

#define HTTP_REST_PORT 80
ESP8266WebServer server(HTTP_REST_PORT);

byte counter = 0;
byte addresses[10];
byte keyStates[10][3];
byte resistances[10][2];

void setKey(byte addrCounter, byte key){
  Serial.print(addresses[addrCounter], DEC);
  String keyValue = server.arg("keyValue");
  keyStates[addrCounter][key] = server.arg("keyValue").toInt();
  byte com = 0;
  com |= (keyStates[addrCounter][0] == 1);
  com |= (keyStates[addrCounter][0] == 2) << 1;
  com |= (keyStates[addrCounter][1] == 4) << 2;
  com |= (keyStates[addrCounter][2] == 2) << 3;
  com |= (keyStates[addrCounter][2] == 1) << 4;
  Serial.print(" ");
  Serial.println(com, BIN);
//  return "{\"address\": " + String(addresses[addrCounter]) + ",\"keys\": [" +
//         keyStates[addrCounter][0] + ", " + keyStates[addrCounter][1] + ", " +
//         keyStates[addrCounter][2] + "], \"resistances\": [" +
//         resistances[addrCounter][0] + ", " + resistances[addrCounter][1] + "]}";
}

void setRes(byte addrCounter, byte res){
  Serial.print(addresses[addrCounter], DEC);
  int resValue = server.arg("resValue").toInt();
  resistances[addrCounter][res] = resValue;
  byte com = 0;
  if (res == 0) com = 0b01000000;
  if (res == 1) com = 0b10000000;
  for (byte i = 0; i < 2; i++) com |= resistances[addrCounter][res];
  Serial.print(" ");
  Serial.println(com, BIN);
//  return "{\"address\": " + String(addresses[addrCounter]) + ",\"keys\": [" +
//         keyStates[addrCounter][0] + ", " + keyStates[addrCounter][1] + ", " +
//         keyStates[addrCounter][2] + "], \"resistances\": [" +
//         resistances[addrCounter][0] + ", " + resistances[addrCounter][1] + "]}";
}

String getIds(){
  String ids = "{\"ids\": [";
  for (byte i = 0; i < counter; i++){
    ids += String(addresses[i]);
    ids += (i < (counter - 1)) ? ", " : "]}";
  }
  return ids;
}

String getValues(byte addrCounter) {
  return "{\"address\": " + String(addresses[addrCounter]) + ",\"keys\": [" +
         keyStates[addrCounter][0] + ", " + keyStates[addrCounter][1] + ", " +
         keyStates[addrCounter][2] + "], \"resistances\": [" +
         resistances[addrCounter][0] + ", " + resistances[addrCounter][1] + "]}";
}

void restServerRouting() {
    server.on(F("/count"), HTTP_GET, []() { server.send(200, F("text/json"), "{\"counter\": " + String(counter) + "}"); });
    server.on(F("/ids"), HTTP_GET, []() { server.send(200, F("text/json"), getIds()); });
    for (byte i = 0; i < counter; i++){
      char buff[20];
      sprintf(buff, "/oins/%d", addresses[i]);
      String p = String(buff);
      server.on(p, HTTP_GET, [i]() {server.send(200, "text/json", getValues(i));});
      for (byte k = 0; k < 3; k++){
        sprintf(buff, "/oins/%d/key%d", addresses[i], k);
        String p = String(buff);
        server.on(p, HTTP_POST, [i,k]() {
          setKey(i, k);
          server.send(200, "text/json", getValues(i));
          });
      }
      for (byte r = 0; r < 2; r++){
        sprintf(buff, "/oins/%d/res%d", addresses[i], r);
        //Uri u = Uri(String(buff));
        String p = String(buff);
        server.on(p, HTTP_POST, [i,r]() {
          setRes(i, r);
          server.send(200, "text/json", getValues(i));
          });
      } 
    }
}

void handleNotFound() {
  String message = "{\"title\": \"File not found\", \"uri\": \"" + server.uri() + "\"" +
                   ", \"method\": " + ((server.method() == HTTP_GET) ? "GET" : "POST") + 
                   ", \"args\": " + server.args() + "}";
  server.send(404, "text/json", message);
}

void setup() {
  WiFi.begin("ssid", "ssid-password");
  Serial.begin(115200);
  Serial.setTimeout(20);
  Serial.println("Connection");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println("Nhome");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  restServerRouting();
  
  server.onNotFound(handleNotFound);
  server.begin();
}

void loop() {
  server.handleClient();
  if (Serial.available() > 0) {
    addresses[counter] = Serial.parseInt();
    counter++;
    restServerRouting();
  }
}
