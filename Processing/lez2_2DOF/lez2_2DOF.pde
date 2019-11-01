
/* PER ESERCIZIO/ ESAME, MODIFICARE CON ELEMENTI UTILI 
(POSIZIONE ATTUALE E DESIDERATA, 
OSCILLOSCOPIO DELLE VARIABILI Q1 E Q2, PER VEDERE L'ANDAMENTO DELLE VARIABILI)*/

int L = 100; // dimensione del link
int R = 30;
float q1 = 0;
float q2 = 0;
float q1r = 0;
float q2r = 0;
float incr = 50;
float step = 0.02; //valore compreso tra 0 e 1, piccolo per eig lento 

/* CINEMATICA INVERSA */

float x = 60;
float y = 60;
float gomito = 1;

float a = 1; 

void setup() {
  size(450, 450);
  background(#96FCFA);
}

void draw() {
  background(#96FCFA); // per evitare che si sporchi con le scie lo sfondo
  translate(225, 225); // sposta le coordinate dell'origine da in alto a sinistra, al centro
  SR(50);
  fill(255, 255, 255, 127);
  ellipse(0, 0, 4*L, 4*L); // in quanto x2 perché è un raggio, e x2 essendoci 2 link

  if (pow(a, 2) <= 1) fill(0, 0, 255, 127); // faccio la verifica che mi trovi nello spazio operativo
  else fill (255, 0, 0);
  ellipse(x, y, R, R);


  a = (pow(x, 2)+ pow(y, 2) - pow(L, 2)- pow(L, 2))/ (2*L*L); // definita globale
  q2r = atan2(gomito*sqrt(abs(1-pow(a, 2))), a); 
  // con l'abs evitiamo che il programma termini, quando facciamo la radice di un numero negativo
  float b1 = L+cos(q2r)*L;
  float b2 = sin(q2r)*L;
  q1r= atan2(-b2*x+b1*y, b1*x+b2*y);





  //q1 += 0.01; // NB: avendo Processing una terna SINISTRA, gli angoli positivi 
  //q2 += 0.01; //     hanno senso ORARIO.

  q1 = q1-step*(q1-q1r);
  q2 = q2-step*(q2-q2r);

  pushMatrix(); //viene messo in uno STACK il SdR corrente
  robot(q1 -PI/2, q2, 255, 0, 0, 127);
  popMatrix(); // così riottengo il SdR precedente, senza le traslazioni dei link
  robot(q1r - PI/2, q2r, 0, 255, 0, 127);
}

void SR(int unit) { // definiamo il sistema di riferimento /* usiamo la codifica RGB per la terna XYZ */ 
  stroke (255, 0, 0);
  line(0, 0, unit, 0);
  stroke (0, 255, 0);
  line(0, 0, 0, unit);
  stroke(0);
}

void robot(float q1, float q2, int rr, int gg, int bb, int tt) {
  fill(rr, gg, bb, tt);
  rotate(q1);
  link(R, L);
  rotate(q2);
  link(R, L);
}

void link (float r, float l) { // non devo passare parametri di movimento, ma solo di forma

  ellipse(0, 0, r, r);
  translate(-r/2, 0); // traslo il SdR corrente, per poter disegnare i pezzi del link
  rect(0, 0, r, l);
  translate(r/2, l);
  ellipse(0, 0, r, r);
  SR(30);
}

void keyPressed() {
  if (keyCode == LEFT) x -= incr;
  if (keyCode == RIGHT) x += incr;
  if (keyCode == UP) y += incr;
  if (keyCode == DOWN) y -= incr;
  if (key == 'g') gomito = -gomito;
}

void mousePressed() {

  x = mouseX-width/2;
  y = mouseY-height/2;
}
