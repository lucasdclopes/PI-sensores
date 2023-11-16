#include <WiFi.h>
#include <WiFiClient.h>
#include <ESPmDNS.h>
#include <HTTPClient.h>
#include <DHT.h>

const char *ssid = "your_wifi_ssid";
const char *password = "your_wifi_password";

const char *serverAddr = "http://YOUR_BACKEND_ADDRESS/api/medicao?showIntervalo=true";

unsigned int lastTime = 0;
unsigned int timer_Delay_Minimo = 5000;
unsigned int timerDelay = timer_Delay_Minimo;

DHT dht(4, DHT11);

void setup(void) {

  Serial.begin(115200);
  dht.begin();
  
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.println("");

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  if (MDNS.begin("esp32")) {
    Serial.println("MDNS responder started");
  }
}

void loop(void) {
  //Send an HTTP POST request every x minutes
  if (!((millis() - lastTime) > timerDelay)) {
    return;
  }
  //Check WiFi connection status
  if(!(WiFi.status()== WL_CONNECTED)){
    Serial.println("WiFi Disconnected");
    return;
  }
  
  HTTPClient http;
  
  // endereço completo da requisição
  http.begin(serverAddr);
  
  //caso precise de autorixação http
  //http.setAuthorization("REPLACE_WITH_SERVER_USERNAME", "REPLACE_WITH_SERVER_PASSWORD");
  
  http.addHeader("Content-Type", "application/json");
  char json[60] = "";

  //monta o json, convertendo os floats em string
  snprintf(json,sizeof(json),"{\"vlTemperatura\": %.2f ,\"vlUmidade\": %.2f }",readDHTTemperature(),readDHTHumidity());

  Serial.print("json request: ");
  Serial.println(json);
  
  // envia o POST e le o código de resposta
  int httpResponseCode = http.POST(json);
  
  if (httpResponseCode=201) { //o OK do serviço responde um http 201
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    //o payload do 201 responde com o delay configurado pelo servidor
    timerDelay = http.getString().toInt();
    if (timerDelay < timer_Delay_Minimo) //não deixa nunca ser menor que o delay minimo, o hardware não aguenta
      timerDelay = timer_Delay_Minimo; 
    //Serial.println(payload);
  }
  else {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
  // Free resources
  http.end();
  
  lastTime = millis();
  
}


float readDHTTemperature() {
  // Sensor readings may also be up to 2 seconds
  // Read temperature as Celsius (the default)
  float t = dht.readTemperature();
  if (isnan(t)) {    
    Serial.println("Failed to read from DHT sensor!");
    return -1;
  }
  else {
    //Serial.println(t);
    return t;
  }
}

float readDHTHumidity() {
  // Sensor readings may also be up to 2 seconds
  float h = dht.readHumidity();
  if (isnan(h)) {
    Serial.println("Failed to read from DHT sensor!");
    return -1;
  }
  else {
    //Serial.println(h);
    return h;
  }
}
