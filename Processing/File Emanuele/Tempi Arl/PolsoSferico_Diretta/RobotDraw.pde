/* Tutte le funzioni partono supponendo di essere già all'estremità del componente precedente*/

void axisReset()
{  
  colorMode(RGB, 255, 255, 255);
  translate(xBase, yBase);  //sposta origine nel modo di progessing
  /* MODIFICA ASSI per rendere il sistema Orto-normale Destro*/
  scale(1, -1, 1);
  rotateY(-PI/2);
  rotateX(-PI/2);
}

void polsoDraw(float angoli[])
{
  strokeWeight(1);
  stroke(0);
  colorMode(RGB, 255, 255, 255);
  assi(255);
  gRotCoassiale(50, d4, angoli[0]); //theta4
  gRotAngolare(25, 50, angoli[1]); //theta5
  gRotCoassiale(25, d6, angoli[2]); //theta6
  pinza(80, 20, 60, apPinza);
  assi(150);
}

void gRotCoassiale(float b, float d, float thetha)  
{
  //la funzione suppene di essere già con l'origine ben orientata
  //e ben posizionata alla fine di una giuntura
  //b=larghezza/altezza base
  //d=lunghezza totale giunto
  //thetha= angolo di rotazione finale in rad
  drawBoxBase(d*23/48, b, b);
  drawBoxBase(d/48, b*1.1, b*1.1);
  rotateZ(thetha);
  drawBoxBase(d/48, b*1.1, b*1.1);
  drawBoxBase(d*23/48, b, b);
}

void gRotAngolare(float r, float p, float thetha)  
{
  //la funzione suppene di essere già con l'origine ben orientata
  //e ben posizionata alla fine di una giuntura
  //r= raggio cilindro
  //p= profondità giunto 
  //thetha= angolo di rotazione finale in rad
  /*Posiziono assi origine come denavit*/
  rotateY(PI/2);
  rotateZ(-PI/2);
  rotateY(-PI/2);
  assi(150);

  rotateZ(thetha);//ruoto il theta del pezzo per motivi di disegno
  assi(255);

  float yT = drawCylinder(20, r, p, true);
  translate(yT, 0, 0);
  rotateZ(-PI/2);
  rotateX(-PI/2);
  rotateZ(PI);
}

void pinza (float w, float h, float p, float open)
{
  translate(0, 0, h/2);  
  box(h, w, h); // Disegno il primo elemento della pinza
  translate(0, 0, h/2);  

  float distP= map(open, 0, 1, h/2, w/2  - h/2);
  pushMatrix(); // Memorizzo il sistema attuale
  translate(0, distP, (p-h)/2);
  fill(255, 0, 0);
  box(h, h, p-h); // Disegno il primo elemento della pinza
  popMatrix();  // Ritorno al sistema di riferimento memorizzato
  pushMatrix(); // Memorizzo il sistema attuale
  translate(0, -distP, (p-h)/2);
  fill(0, 255, 0);
  box(h, h, p-h); // Disegno il primo elemento della pinza
  popMatrix();  // Ritorno al sistema di riferimento memorizzato
  translate(0, 0, p-h);
}


int p=50;   //profondità
int b=10;    //base
int h=10;     //altezza
int lF=10;   //lunghezza semi lato freccia

void assi (float alfa)
{
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

  /*
   pushMatrix();
   translate(0, 0, 50);
   fill(0, 0, 255); // blu = z
   box(5, 5, 100); 
   popMatrix();
   
   pushMatrix();
   translate(0, 50, 0);
   fill(0, 255, 0); // verde = y
   box(5, 100, 5); 
   popMatrix();
   
   pushMatrix();
   translate(50, 0, 0);
   fill(255, 0, 0); // rosso = x
   box(100, 5, 5); 
   popMatrix();
   */
}

void drawBoxBase(float p, float lb, float lh)
{
  //p= lunghezza profondità, in Z
  //lp= larghezza base
  //lh= altezza base
  lb=lb/2;
  strokeWeight(1);
  fill(255, 0, 0);
  beginShape();
  vertex(+lb, +lb, 0);
  vertex(+lb, +lb, p);
  vertex(+lb, -lb, p);
  vertex(+lb, -lb, 0);
  endShape(CLOSE);

  fill(255, 255, 0);
  beginShape();
  vertex(+lb, -lb, 0);
  vertex(+lb, -lb, p);
  vertex(-lb, -lb, p);
  vertex(-lb, -lb, 0);
  endShape(CLOSE);

  fill(0, 255, 0);
  beginShape();
  vertex(-lb, -lb, 0);
  vertex(-lb, -lb, p);
  vertex(-lb, +lb, p);
  vertex(-lb, +lb, 0);  
  endShape(CLOSE);

  fill(0, 255, 255);
  beginShape();
  vertex(-lb, +lb, 0);
  vertex(-lb, +lb, p);
  vertex(+lb, +lb, p);
  vertex(+lb, +lb, 0);  
  endShape(CLOSE);

  //bot face
  fill(0, 0, 255);
  beginShape();
  vertex(+lb, +lb, 0);
  vertex(+lb, -lb, 0);
  vertex(-lb, -lb, 0);
  vertex(-lb, +lb, 0);
  endShape(CLOSE);

  //top face
  beginShape();
  vertex(+lb, +lb, p);
  vertex(+lb, -lb, p);
  vertex(-lb, -lb, p);
  vertex(-lb, +lb, p);
  endShape(CLOSE);

  translate(0, 0, p);  //sposto origine alla fine
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

float drawCylinder( int sides, float r, float h, boolean plane)
{
  //ritorna distanza tra origine e lato piano
  noStroke();
  colorMode(HSB, 360, 100, 100);
  int n = 4 * int(plane); //45° piatti
  float angle = 360 / sides;
  float halfHeight = h / 2;
  rotateZ(-PI/2);

  // draw top of the tube
  beginShape();
  for (int i = n/2; i < sides - n/2 + 1; i++) {
    float x = cos( radians( i * angle ) ) * r;
    float y = sin( radians( i * angle ) ) * r;
    vertex(x, y, -halfHeight );
  }
  endShape(CLOSE);

  // draw bottom of the tube
  beginShape();
  for (int i = n/2; i < sides - n/2 + 1; i++) {
    float x = cos( radians( i * angle ) ) * r;
    float y = sin( radians( i * angle ) ) * r;
    vertex(x, y, halfHeight );
  }
  endShape(CLOSE);

  // draw sides
  for (int i = n/2; i < sides - n/2; i++) {
    fill(i * angle, 100, 100);
    beginShape();
    float x = cos( radians( i * angle ) ) * r;
    float y = sin( radians( i * angle ) ) * r;
    vertex(x, y, -halfHeight );    
    vertex(x, y, +halfHeight ); 

    x = cos( radians( (i+1) * angle ) ) * r;
    y = sin( radians( (i+1) * angle ) ) * r;
    vertex(x, y, +halfHeight );    
    vertex(x, y, -halfHeight ); 
    endShape(CLOSE);
  }

  //draw trunk plane
  beginShape();
  float x = cos( radians( n/2 * angle ) ) * r;
  float y = sin( radians( n/2 * angle ) ) * r;
  vertex(x, y, -halfHeight );    
  vertex(x, y, +halfHeight ); 
  x = cos( radians( (sides - n/2 ) * angle ) ) * r;
  y = sin( radians( (sides - n/2 ) * angle ) ) * r;
  vertex(x, y, +halfHeight );    
  vertex(x, y, -halfHeight );    
  endShape(CLOSE);

  colorMode(RGB);
  stroke(0);
  return x;
}
