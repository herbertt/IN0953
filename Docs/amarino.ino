#include <MeetAndroid.h>
int EstadoBotao = 0;      //Variavel para ler o status do pushbutton - Inicializado com zero (Botão sem pressionar)
MeetAndroid meetAndroid;  //cria um objeto do tipo MeetAndroid
int ledPin = 13; //led no pino 13 
int onOff;

void setup(){  
    Serial.begin(9600);
     // register callback functions, which will be called when an associated event occurs.
    meetAndroid.registerFunction(lerLed, 'A');
    pinMode(ledPin, OUTPUT); //Pino do led será saída 
    digitalWrite(ledPin, HIGH);  
}

void loop(){
    
    meetAndroid.receive(); //Verifica se existem comandos a serem recebidos e chama a função registrada caso receba algum comando. 
}

/*
 * Whenever the multicolor lamp app changes the red value
 * this function will be called
 */
void lerLed(byte flag, byte numOfValues)
{
     onOff = meetAndroid.getInt();
     EstadoBotao = digitalRead(onOff); /*novo estado do botão vai ser igual ao que
                                        Arduino ler no pino onde está o botão. Poderá ser ALTO (HIGH)se o botão estiver
                                        Pressionado, ou BAIXO (LOW),se o botão estiver solto */
                                        
   if (onOff == 1){   //Se botão estiver pressionado (HIGH) 
   digitalWrite(ledPin, HIGH);// acende o led do pino 13. 
     //    meetAndroid.send(1);  //Envia um valor (numérico ou string) para o dispositivo android
        
    } else{                  //se não estiver pressionado
      //  meetAndroid.send(0); //Envia um valor (numérico ou string) para o dispositivo android
                     
   digitalWrite(ledPin, 0); //deixa o led do pino 13 apagado 
    }
}



 
