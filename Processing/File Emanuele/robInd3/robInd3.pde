//il prof tradice gli spostamenti del mouse come rotazioni
//nord-sud rotazione lungo X
//est-ovest rotazione lungo Z
//così facendo si può montare una qualunque rotazione
float angX, angY;        //nuova rotazione
float angXst, angYst;    //sommatorie delle rotazioni effettuate

float q1, q2, q3;
float q1r, q2r, q3r;
float dt=0.02;
float passo=0.5;
int giunto=1;
void setup() {
  size(450, 450, P3D);
}

void draw() {
  background(#96FCFA);
  translate(225, 255, -200);
  rotateY(-angY);
  rotateX(angX);
  rotateX(+PI/2);

  assi(150);

  directionalLight(223, 126, 126, 0, 0, 0.7);  
  ambientLight(200, 200, 200);

  q1=q1-dt*(q1-q1r);
  q2=q2-dt*(q2-q2r);
  q3=q3-dt*(q3-q3r);

  robot(q1, q2, q3, 255);
  robot(q1r, q2r, q3r, 120);
}

void robot(float q1, float q2, float q3, int alpha) {
  pushMatrix();
  link(q1, 100, PI/2, 0, alpha);
  assi(alpha/2);
  link(q2, 0, 0, 100, alpha);
  assi(alpha/2);
  link(0, q3, 0, 100, alpha);
  assi(alpha/2);
  popMatrix();
}

void link(float theta, float d, float alfa, float a, int alpha) {
  //disegno seguendo l'ordine dei passi di D-H
  fill(#52BEFF, alpha);
  rotateZ(theta);
  noStroke();
  sphere(25);
  stroke(0);
  translate(0, 0, d/2);
  box(25, 25, d);
  translate(0, 0, d/2);
  noStroke();
  sphere(25);
  stroke(0);

  fill(#FF5252, alpha);
  rotateX(alfa);
  noStroke();
  sphere(25);
  stroke(0);
  translate(a/2, 0, 0);
  box(a, 25, 25);
  translate(a/2, 0, 0);
  noStroke();
  sphere(25);
  stroke(0);
}




//Camera function:
void mousePressed() {
  angYst=angY+PI*mouseX/float(500); //old ang+ new ang[radianti*mouseX/fattore_di_scala]
  angXst=angX+PI*mouseY/float(500); //old ang+ new ang[radianti*mouseX/fattore_di_scala]
}

void mouseDragged() {
  angY=angYst-PI*mouseX/float(500); //old ang+ new ang[radianti*mouseX/fattore_di_scala]
  angX=angXst-PI*mouseY/float(500); //old ang+ new ang[radianti*mouseX/fattore_di_scala]
}

void keyPressed() {
  if (key=='r') {
    angX=0;
    angY=0;
    angXst=0;
    angYst=0;
  }
  if (key=='1')
    giunto=1;
  if (key=='2')
    giunto=2;
  if (key=='3')
    giunto=3;

  if (giunto==1) {
    if (keyCode==LEFT)
      q1r+=passo;
    if (keyCode==RIGHT)
      q1r-=passo;
  } else if (giunto==2) {
    if (keyCode==LEFT)
      q2r+=passo;
    if (keyCode==RIGHT)
      q2r-=passo;
  } else if (giunto==3) {
    if (keyCode==LEFT)
      q3r+=passo*10;
    if (keyCode==RIGHT)
      q3r-=passo*10;
  }

  //if (key=='q')
  //  q1r+=passo;
  //if (key=='w')
  //  q1r-=passo;
  //if (key=='a')
  //  q2r+=passo;
  //if (key=='s')
  //  q2r-=passo;
  //if (key=='z')
  //  q3r+=passo;
  //if (key=='x')
  //  q3r-=passo;
}


int p=100;   //profondità
int b=10;    //base
int h=10;     //altezza
int lF=10;   //lunghezza semi lato freccia

void assi (float alfa)
{
  pushStyle();
  strokeWeight(0.5);
  fill(255, 0, 0, alfa); // rosso = x
  pushMatrix();
  rotateY(PI/2);
  translate(0, 0, p/2);  //disegno in base
  box(h, b, p); 
  translate(0, 0, p/2);  //sposto origine alla fine
  piramide(lF);
  popMatrix();

  fill(0, 255, 0, alfa); // verde = y
  pushMatrix();
  rotateX(-PI/2);  
  translate(0, 0, p/2);  //disegno in base
  box(h, b, p); 
  translate(0, 0, p/2);  //sposto origine alla fine  
  piramide(lF);
  popMatrix();

  fill(0, 0, 255, alfa); // blu = z
  pushMatrix();
  translate(0, 0, p/2);  //disegno in base
  box(h, b, p); 
  translate(0, 0, p/2);  //sposto origine alla fine  
  piramide(lF);
  popMatrix();
  popStyle();
}

void piramide(int h)
{
  beginShape();
  vertex(-h, -h);
  vertex(+h, -h);
  vertex(0, 0, 2*h);
  endShape(CLOSE);

  beginShape();
  vertex(+h, -h);
  vertex(+h, +h);
  vertex(0, 0, 2*h);
  endShape(CLOSE);

  beginShape();
  vertex( +h, +h);
  vertex( -h, +h);
  vertex(0, 0, 2*h);
  endShape(CLOSE);

  beginShape();
  vertex( -h, +h);
  vertex( -h, -h);
  vertex(0, 0, 2*h);
  endShape(CLOSE);

  beginShape();
  vertex( -h, -h);
  vertex( +h, -h);
  vertex( +h, +h);
  vertex( -h, +h);
  endShape(CLOSE);
}
