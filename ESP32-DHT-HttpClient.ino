#include <WiFi.h>
#include <WiFiClient.h>
#include <ESPmDNS.h>
#include <HTTPClient.h>
#include <DHT.h>
#include <ArduinoJson.h>
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager 

//Configurações do WIFI
//nome da rede WIFI
//const char *ssid = "bacon";
//senha da rede WIFI
//const char *password = "Chedd4r!";

const int relay_in1 = 5;

//Endereço do servidor da Amazon, que é responsável por armazenar os logs e disparar os alertar de tempertura e umidade
const char *serverAddr = "http://192.168.0.124:8080/api/medicao"; 

//armazena, em milésimos de segundos, a quanto tempo o programa foi executado
unsigned int lastTime = 0;
//em milésimos de segundos, a frequência máxima com que o programa pode ser executado. Serve como segurança para nunca ser menor que 5 segundos.
unsigned int timer_Delay_Minimo = 10000;
//frequência máxima com que o programa pode ser executado
unsigned int timerDelay = timer_Delay_Minimo;

//valor de temperaturá que acionará o relé
unsigned int tempAcionamentoRele = 0;

//Configuração do sensor de temperatura e umidade. No caso, está no pino 4, e o tipo de sensor é "DHT11"
DHT dht(4, DHT11);

//Este método é executado quando o dispositivo é ligado
void setup(void) {

  //Inicializa a comunicação com a porta serial 115200, caso esteja ligado em um computador 
  Serial.begin(115200);

  //Inicializa os sensores de temperatura
  dht.begin();

  //Inicializa o relé
  pinMode(relay_in1, OUTPUT);
  
    //Gerenciador de wifi, para não ter que usar hardcoded
  WiFiManager wifiManager;
  wifiManager.autoConnect("ESP32-PI6");

  //Configura a conexão Wifi, preparando para se conectar
  /*WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  Serial.println("");

  //Se mantém no laço enquanto o status da conexão NÃO for WL_CONNECTED. Ou seja, ficará aqui até estabelecer a conexão wifi com sucesso
  while (WiFi.status() != WL_CONNECTED) {
    delay(500); //aguarda 500 milésimos de segundos entre cada tentativa de se conectar no wifi
    Serial.print(".");
  }*/

  //Escreve na porta serial o IP com que se conectou na rede wifi
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

//Este método é executado após o fim do setup. Sempre que é finalizado, é executado novamente.
void loop(void) {
  /*
  Verifica quanto tempo faz que a última execução foi feita. Se o tempo não for maior que a frequência máxima, encerra a execução do método.
  Isto é feito para evitar uma sobrecarga no servidor e no sensor
  */
  if (!((millis() - lastTime) > timerDelay)) {
    return;
  }
  //Verifica status do WiFi. Só prossegue a execução se estiver conectado
  if(!(WiFi.status()== WL_CONNECTED)){
    Serial.println("WiFi Disconnected");
    return;
  }
  
  //Declara o HTTPClient. Este objeto serve para executar requisições HTTP
  HTTPClient http;
  
  //Configura o endereço do servidor
  http.begin(serverAddr);
  

  //Configura os cabeçalhos da requisição HTTP. No caso, para informar que é uma requisição com um payload do tipo JSON
  http.addHeader("Content-Type", "application/json");

  //Variável que armazenará o JSON. Esta pode armazenar, no máximo 60 caracteres. É mais do que o suficiente para o payload que será montado.
  char json[60] = "";

  //readDHTTemperature() e readDHTHumidity() são os métodos que obtem o valor da temperatura e umidade com o sensor.
  float temperatura = readDHTTemperature();
  float umidade = readDHTHumidity();
  /*
    Monta a String com o payload e o armazena na variável json, usando a função snprintf
    %.2f é o tipo de formatação. Indica que o valor é do tipo float, com duas casas decimais
    O Json ficará, por exemplo, desta forma: {"vlTemperatura": 20.50 ,"vlUmidade": 80 }
  */
  snprintf(json,sizeof(json),"{\"vlTemperatura\": %.2f ,\"vlUmidade\": %.2f }",temperatura,umidade);
  
  //Envia uma requisição HTTP, com verbo POST, para o servidor, contendo o payload JSON montando anteriormente, aguarda a resposta do servidor.
  Serial.println("Invocando WS");
  int httpResponseCode = http.POST(json);
  
  //A aplicação no servidor responde com o código HTTP 201 se houver sucesso no recebimento das informações. Portanto, verifica se a resposta foi 201.
  if (httpResponseCode==201) { 
    Serial.println("Response com 201");
    //Executa este código se a resposta foi 201.

    //carrega os dados da resposta e converte para char
    String wsResponse = "";
    wsResponse = http.getString();
    int responseLen = 1;
    responseLen = wsResponse.length();
    char responseData[responseLen+1]; 
    wsResponse.toCharArray(responseData, responseLen);

    //Finaliza a comunicação http. É necessário para evitar que a conexão com o servidor fique aberta indefinidamente, o que pode tomar muitos recursos do servidor e do dispositivo.
    http.end();

    JsonDocument doc;
    deserializeJson(doc, responseData);
    /*
    Além do código de resposta, a aplicação no servidor responde com um valor numérico, que é a frequência de execução deste programa, em milésimos de segundos.
    Desta forma é possível que o usuário, por meio da aplicação do servidor, configure esta frequência, não sendo necessário alterar o programa que roda no dispostivo
    */
    timerDelay = doc["intervalo"];
    float temperaturaRele = doc["tempAcionamento"];

    Serial.println("timerDelay: ");
    Serial.println(timerDelay);

    setarRelay(temperatura,temperaturaRele);
    /*
    Aqui verifica se a frequência configurada é menor que a frequência máxima. Se for, assume a frequência máxima ao invés da configurada. 
    É uma proteção contra uma configuração imprópria
    */
    if (timerDelay < timer_Delay_Minimo) 
      timerDelay = timer_Delay_Minimo; 

  } else {
    Serial.println("Response com erro: ");
    Serial.println(httpResponseCode);
  }
  
  //Carrega o momento em que a execução foi executada. 
  lastTime = millis();
  
}

void setarRelay(float temperatura, float temperaturaRele){

  Serial.println("temperaturaRele: ");
  Serial.println(temperaturaRele);

  tempAcionamentoRele = temperaturaRele;
  if (tempAcionamentoRele == 0) //0 indica que não tem alerta para acionar o dispositivo, sai do método
    return;

  int relayState = digitalRead(relay_in1);
  Serial.println("relayState: ");
  Serial.println(relayState);
  if ( temperatura > tempAcionamentoRele ){
    if (relayState == LOW) //já está LOW, nem roda o write
      return; 
    digitalWrite(relay_in1, LOW);
    Serial.println("Current Flowing");
  } else {
    if (relayState == HIGH) //já está HIGH, nem roda o write
      return; 
    //bota uma margem de erro de 1C antes de desligar o relé, pra evitar ficar ligando / desligando constantemente por pequenas variações de temperatura
    if (temperatura < tempAcionamentoRele - 1) {
      digitalWrite(relay_in1, HIGH);
      Serial.println("Current not Flowing");
    }
  }

}




//Funcação que obtem o valor da temperatura do sensor
float readDHTTemperature() {

  //carrega a temperatura, em graus celcius
  float t = dht.readTemperature();
  if (isnan(t)) {    
    //se houver um erro na leitura, retorna com valor -1000. Ao receber este valor, fica óbvio que houve um problema com o sensor
    return -1000;
  }
  else {
    //Não tendo erros, retorna o valor obtido do sensor
    return t;
  }
}

//Função que obtem o valor de umidade do sensor
float readDHTHumidity() {
  //carrega a umidade, em %
  float h = dht.readHumidity();
  if (isnan(h)) {
    //se houver um erro na leitura, retorna com valor -1. Ao receber este valor, fica óbvio que houve um problema com o sensor
    return -1;
  }
  else {
    //Não tendo erros, retorna o valor obtido do sensor
    return h;
  }
}