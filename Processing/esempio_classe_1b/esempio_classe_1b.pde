// Processing necessita di setup() e draw() per un approccio dinamico
float x, y = 0;
float step = 3;

float xR, yR = 10;// sono le posizioni desiderate, serve per definire una transizione
float dt = 0.02;


void setup() { // eseguita solo UNA volta

  size(500, 500);  // dimensione finestra, NON può essere messa in draw
}


void draw() { // eseguito 30 volte/secondo, si disegna sopra ciò che è 
  // disegnato su setup                 

  background(#61C6F7); // scala di grigi con un solo numero; con 3, convenzione RGB
  line(0, 0, width, height);
  //x += .1;
  //y += .1;
  
  /* Definiamo una legge di controllo per far muovere l'oggetto lungo  */
  // siamo nel discreto -> eig < 1 per essere stabile => dt 
  x = x-dt* (x-xR); 
  y = x-dt* (y-yR);

  /* Fine legge di controllo */
  
  fill(255, 0, 0, 128); 
  // colore e trasparenza da ora in poi per tutte le figure con area racchiusa
  stroke(204, 102, 0); // colore del bordo
  strokeWeight(1);
  rect(x, y, 55, 55);

  fill(0, 255, 0, 128); 
  noStroke(); // toglie il contorno alla figura
  rect(40, 40, 55, 55);

  fill(0);
  stroke(0);
  ellipse(2*x, 2*y, 55, 55);
}

void keyPressed() { // keyPressed prende il fronte di salita, ovvero quando il tasto viene premuto;
  // N.B.: l'avvenuta pressione di un tasto viene salvata anche nella variabile di sistema 'keyPressed'

  if (keyCode == LEFT) x -= step;
  if (keyCode == RIGHT) x += step;

  if (keyCode == UP) y -= step; // questo perchè il sistema è sinistro
  if (keyCode == DOWN) y += step;
}

void mousePressed() { // è evidenziata la funzione, la stiamo sovrascrivendo
  x = mouseX;
  y = mouseY;
}
