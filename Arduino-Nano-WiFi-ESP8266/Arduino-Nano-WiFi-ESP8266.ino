/*
  Arduino Nano - ESP 8266 WiFi Module



*/

#include <SoftwareSerial.h>
#include <LiquidCrystal.h>

SoftwareSerial ESP8266(2, 3);
LiquidCrystal lcd(12, 11, 10, 9, 8, 7); // create lcd//
//Parameters: (rs, enable, d4, d5, d6, d7)

char ssid[] = "HomeCon Box";         // your network SSID (name)
char pass[] = "12345678";        // your network password

int device1 = 13;
int device2 = 6;
int device3 = 5;
int device4 = 4;

boolean FAIL_8266 = false;

#define BUFFER_SIZE 128
char buffer[BUFFER_SIZE];

char on[] = " ON";
char off[] = "OFF";

bool status1 = false;
bool status2 = false;
bool status3 = false;
bool status4 = false;


void setup() {
  pinMode(device1, OUTPUT);
  pinMode(device2, OUTPUT);
  pinMode(device3, OUTPUT);
  pinMode(device4, OUTPUT);
  digitalWrite(device1, HIGH);
  digitalWrite(device2, HIGH);
  digitalWrite(device3, HIGH);
  digitalWrite(device4, HIGH);

  lcd.begin(16, 2); //set lcd
  lcd.setCursor(0, 0); // Sets the location to display text
  lcd.print("SYSTEM SETUP");
  lcd.setCursor(0, 1); // Sets the location to display text
  lcd.print("PLEASE WAIT...");

  Serial.begin(9600);
  ESP8266.begin(9600);
  Serial.println("--- Start ---");
  ESP8266.println(F("AT+RST"));
  delay(3000);

  ESP8266.println(F("AT+CWMODE=2"));
  delay(200);
  clearBuffer();

  //  //Get and display my IP
  sendESP8266Cmdln("AT+CIFSR", 1000);
 
  //set multiple connection mode
  ESP8266.println(F("AT+CIPMUX=1"));
  // Show remote IP and port with "+IPD"
  ESP8266.println(F("AT+CIPDINFO=1"));

  // Disable autoconnect
  // Automatic connection can create problems during initialization phase at next boot
  ESP8266.println(F("AT+CWAUTOCONN=0"));

  // enable DHCP
  ESP8266.println(F("AT+CWDHCP=1,1"));
  delay(200);

  sendESP8266Cmdln(F("AT+CIPSERVER=1,80"), 1000);

  sendESP8266Cmdln(F("AT+CWMODE_CUR=3"), 10000);

  //set the network name and password
  sendESP8266Cmdln(F("AT+CWSAP_CUR=\"HomeCon Box\",\"12345678\",10,3"), 10000);

  ESP8266.println(F("AT+CWDHCP_CUR=2,1"));
  delay(3000);
  Serial.println("Module is ready");

  lcd.clear();
  lcd.setCursor(0, 0); // Sets the location to display text
  lcd.print("HOME AUTOMATION");
  lcd.setCursor(0, 1); // Sets the location to display text
  lcd.print("SYSTEM WITH WIFI");
  delay(2000);
  Serial.println("The system is ready");
  ESP8266.setTimeout(5000);

  lcd.setCursor(0, 0); // Sets the location to display text
  lcd.print("D1 OFF | D2 OFF");
  lcd.setCursor(0, 1); // Sets the location to display text
  lcd.print("D3 OFF | D4 OFF");

}

void loop() {

  if (ESP8266.available()) // check if the esp is sending a message
  {
    Serial.println("Something received");
    //delay(200);
    if (ESP8266.find("+IPD,"))
    {
      String action;

      Serial.println("+IPD, found");
      int connectionId = ESP8266.read() - 48;
      Serial.println("connectionId: " + String(connectionId));

      ESP8266.find("dev=");
      Serial.println("dev found");
      delay(500);
      char s = ((ESP8266.read()));
      Serial.print("s is: ");
      Serial.println(s);
      String dev1;
      String dev2;
      String dev3;
      String dev4;
      switch (s) {
        case 48:
          action = "device1 On";
          digitalWrite(device1, LOW);
          status1 = true;
          Serial.println(action);
          lcd.setCursor(3, 0); // Sets the location to display text
          lcd.print(on);
          break;
        case 49:
          action = "device1 On";
          digitalWrite(device1, HIGH);
          status1 = false;
          Serial.println(action);
          lcd.setCursor(3, 0); // Sets the location to display text
          lcd.print(off);
          break;
        case 50:
          action = "device2 On";
          digitalWrite(device2, LOW);
          status2 = true;
          Serial.println(action);
          lcd.setCursor(12, 0); // Sets the location to display text
          lcd.print(on);
          break;
        case 51:
          action = "device2 Off";
          digitalWrite(device2, HIGH);
          status2 = false;
          Serial.println(action);
          lcd.setCursor(12, 0); // Sets the location to display text
          lcd.print(off);
          break;
        case 52:
          action = "device3 On";
          digitalWrite(device3, LOW);
          status3 = true;
          Serial.println(action);
          lcd.setCursor(3, 1); // Sets the location to display text
          lcd.print(on);
          break;
        case 53:
          action = "device3 Off";
          digitalWrite(device3, HIGH);
          status3 = false;
          Serial.println(action);
          lcd.setCursor(3, 1); // Sets the location to display text
          lcd.print(off);
          break;
        case 54:
          action = "device4 On";
          digitalWrite(device4, LOW);
          status4 = true;
          Serial.println(action);
          lcd.setCursor(12, 1); // Sets the location to display text
          lcd.print(on);
          break;
        case 55:
          action = "device4 Off";
          digitalWrite(device4, HIGH);
          status4 = false;
          Serial.println(action);
          lcd.setCursor(12, 1); // Sets the location to display text
          lcd.print(off);
          break;
        case 56:
          Serial.print("Device1 pin: ");
          Serial.println(digitalRead(device1));
          if (status1) {
            dev1 = "1";
          }
          else {
            dev1 = "0";
          }

          if (status2) {
            dev2 = "1";
          }
          else {
            dev2 = "0";
          }

          if (status3) {
            dev3 = "1";
          }
          else {
            dev3 = "0";
          }

          if (status4) {
            dev4 = "1";
          }
          else {
            dev4 = "0";
          }
          action = dev1 + "" + dev2 + "" +  dev3 + "" +  dev4;
          Serial.println(action);
          break;
        default:
          Serial.println("invalid data!!!!!!");
          break;
      }



      Serial.println(action);
      sendHTTPResponse(connectionId, action);
    }
  }

}

void sendHTTPResponse(int id, String content)
{
  String response;
  response = "HTTP/1.1 200 OK\r\n";
  response += "Content-Type: text/html; charset=UTF-8\r\n";
  response += "Content-Length: ";
  response += content.length();
  response += "\r\n";
  response += "Connection: close\r\n\r\n";
  response += content;

  String cmd = "AT+CIPSEND=";
  cmd += id;
  cmd += ",";
  cmd += response.length();

  Serial.println("--- AT+CIPSEND ---");
  sendESP8266Cmdln(cmd, 500);

  Serial.println("--- data ---");
  sendESP8266Data(response, 500);
}

boolean waitOKfromESP8266(int timeout)
{
  do {
    Serial.println("wait OK...");
    delay(500);
    if (ESP8266.find("OK"))
    {
      return true;
    }

  } while ((timeout--) > 0);
  return false;
}

//Send command to ESP8266, assume OK, no error check
//wait some time and display respond
void sendESP8266Cmdln(String cmd, int waitTime)
{
  ESP8266.println(cmd);
  delay(waitTime);
  //clearESP8266SerialBuffer();
}

//Basically same as sendESP8266Cmdln()
//But call ESP8266.print() instead of call ESP8266.println()
void sendESP8266Data(String data, int waitTime)
{
  ESP8266.println(data);
  delay(waitTime);
  clearBuffer();
}

//Clear and display Serial Buffer for ESP8266
void clearBuffer()
{
  while (ESP8266.available() > 0) {
    char a = ESP8266.read();
    Serial.write(a);
  }
}

