/* FARE PER TUTTE LE STRUTTURE PORTANTI LA CINEMATICA INVERSA SU PROCESSING */



float angX, angY;  // nuova rotazione
float angXstart, angYstart; // rotazioni effettuate

float q1, q2, q3; // variabili attuali

float q1r = 10;
float q2r = 10;
float q3r = 10;
// variabili di riferimento
float passo = 0.1; // in radianti
int giunto = 1;

float dt = 0.02; // incremento legge di controllo

void setup() {
  size(450, 450, P3D);
  background(#96FCFA);

  float q1r = 10;
  float q2r = 10;
  float q3r = 10;
}

void draw() { // noi abbiamo un disegno dove z è normale al piano di disegno

  background(#96FCFA);
  translate(width/2, height/2, -100);
  rotateY(-angY);
  rotateX(angX);

  rotateX(PI/2); // cosi' abbiamo l'asse z in verticale, e l'asse y normale al piano

  directionalLight(127, 127, 127, 0, 0, -0.7); // i primi 3 dicono il colore, gli altri 3 la direzione
  ambientLight(200, 200, 200);


  q1 -=  dt*(q1-q1r);
  q2 -=  dt*(q2-q2r);
  q3 -=  dt*(q3-q3r);

  //box(25, 25, 100);
  // dobbiamo trovare un modo per assegnare le rotazioni lungo x e lungo y, in modo,
  // come per Eulero, di poter effettuare qualunque rotazione nello spazio.
  // NB: per un asse generico di rotazione, possiamo usare Rodriguez.

  fill(#CEA61F);

  robot(q1, q2, q3);

  fill(#A09B9B);

  robot(q1r, q2r, q3r);
}

void mousePressed() {
  angYstart = angY + PI*mouseX/float(500); /* traduciamo lo spostamento in radianti, 
   e poi divido per un fattore di scala */
  angXstart = angX + PI*mouseY/float(500); /* dato che muovo lungo un asse, devo prendere
   il movimento del mouse normale all'asse */
}

void mouseDragged() {
  angX = angXstart - PI*mouseY/float(500);
  angY = angYstart - PI*mouseX/float(500);
}

void robot(float q1, float q2, float q3) {

  pushMatrix();

  link(q1, 100, PI/2, 0);
  link(q2, 0, 0, 100);
  link(q3, 0, 0, 100);

  popMatrix();
}

void link(float theta, float d, float alpha, float a) {
  // solo i primi due parametri possono variare rispetto al tempo

  rotateZ(theta); // prima rotazione D-H, lungo z
  // creo adesso il link
  sphere(25);
  translate(0, 0, d/2);
  box(25, 25, d); // spostamento lungo z
  translate(0, 0, d/2);
  noStroke();
  sphere(25);
  stroke(0);

  rotateZ(theta); // seconda rotazione D-H, lungo x
  // creo adesso il link
  noStroke();
  sphere(25);
  stroke(0);
  translate(a/2, 0, 0);
  box(a, 25, 25); // spostamento lungo x
  translate(a/2, 0, 0);
  sphere(25);
  // anche se forma una L, tutto è un unico giunto
}

void keyPressed() {

  if (key == '1') giunto = 1;
  if (key == '2') giunto = 2;
  if (key == '3') giunto = 3;

  switch(giunto) { 
  case 1: 
    if (keyCode == LEFT) q1r -= passo;
    else if (keyCode == RIGHT) q1r += passo;
    break;
  case 2: 
    if (keyCode == LEFT) q2r -= passo;
    else if (keyCode == RIGHT) q2r += passo;
    break;
  case 3: 
    if (keyCode == LEFT) q3r -= passo;
    else if (keyCode == RIGHT) q3r += passo;
    break;
  }
}
