/*
Processing è un sistema sinistro (l'asse y va verso il basso), e l'origine
coincide con l'angolo in alto a sinistra.
-> dobbiamo cambiare la direzione di y per farlo ritornare destro, oppure
   sommare opportunamente pi/2 per ritornare destri.

<>con i colori faccio una combinazione CONVESSA, ovvero trovo tutti i
  colori tra i due punti; avendo tre colori base, allora la porzione di piano
  sarà un triangolo. Ma in realtà è un iper-triangolo l'area dei colori
  visibili (ovvero con i lati convessi). -> I colori che non possono essere
  generati sono le PORPORE. Ci sono altre codifiche per cercare di ricoprire
  più area (es. codifica con quadrilatero CYMK), ma rimangono comunque dei
  colori non coperti.
*/


/** UTILIZZO STATICO DI PROCESSING **/

size(300, 200);  // dimensione finestra
background(#61C6F7); // scala di grigi con un solo numero; con 3, convenzione RGB
line(0, 0, width, height);

fill(255,0,0,128); 
// colore e trasparenza da ora in poi per tutte le figure con area racchiusa
stroke(204,102,0); // colore del bordo
strokeWeight(5);
rect(30,20,55,55);

fill(0,255,0,128); 
noStroke(); // toglie il contorno alla figura
rect(40,40,55,55);
