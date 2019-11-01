/* Questo sketch disegna un robot SCARA con una pinza che si può aprire e
chiudere usando i tasti o (open) e c (chiudi). Le frecce SU e GIÙ permettono
di spostare la visuale in alto o in basso. Con il mouse si può traslare il 
robot. Le frecce DESTRA e SINISTRA permettono di ruotare il giunto selezionato
(il giunto si seleziona digitando il tasto numerico corrispondente, da 1 a 4) */

// parametro della funzione camera() che viene modificato con le 
// frecce SU e GIU e determina l'altezza della vista rispetto al robot
float eyeY = 0;

// Coordinate del centro del link 1 del robot che viene spostato
// col mouse
float xBase;
float yBase;

// variabile per compattare le condizioni di fine corsa
int segno = 1;

// Permette di selezionare il giunto da muovere
int giunto = 0;

// dimensioni link 0:
float d0x = 30; // lungo x
float d0y = 200; // lungo y
float d0z = 30; // lungo z

// dimensioni link 1
float d1x = 200; // lungo x
float d1y = 30; // lungo y
float d1z = 30; // lungo z

// dimensioni link 2
float d2x = 200; // lungo x
float d2y = 30; // lungo y
float d2z = 30; // lungo z

// dimensioni link 3
float d3x = 30; // lungo x
float d3y = 200; // lungo y
float d3z = 30; // lungo z

// dimensioni link 4
float d4x = 80; // lungo x
float d4y = 20; // lungo y
float d4z = 20; // lungo z

// dimensioni link 5
float d5x = 10; // lungo x
float d5y = 40; // lungo y
float d5z = 20; // lungo z

// parametri giunto (theta1, theta2, d3)
float[] theta = {0,0,0,0}; 

float posPinza = 0;

void setup() 
{
  size(1000, 800, P3D);
  stroke(255);
  strokeWeight(2);
  xBase = width/2;
  yBase = height/2;
  smooth();
}

void draw() 
{
  
  background(0);
//  directionalLight(128, 128, 128, -1, 0, 1);   
  lights();
// Permette di ruotare la vista:
  camera((width/2.0), height/2 - eyeY, (height/2.0) / tan(PI*60.0 / 360.0), width/2.0, height/2.0, 0, 0, 1, 0);  

  if (mousePressed)
  {
    xBase = mouseX;
    yBase = mouseY;
  }
  if (keyPressed)
  {
    // movimento camera
    if (keyCode == DOWN)
    {
      eyeY -= 5;
    }
    if (keyCode == UP)
    {
      eyeY += 5;
    }

    if (key == '1')
    {
      giunto = 0;
    }
    if (key == '2')
    {
      giunto = 1;
    }
    if (key == '3')
    {
      giunto = 2;
    }
    if (key == '4')
    {
      giunto = 3;
    }
    if (key == 'o' || key == 'O')
    {
      if (posPinza<d4x/2-d5x)
      {
        posPinza += 1;
      }
    }
    if (key == 'c' || key == 'C')
    {
      if (posPinza>0)
      {
        posPinza -= 1;
      }
    }
    if (keyCode == LEFT)
    {
      segno = -1;
      muovi();
    }
    if (keyCode == RIGHT)
    {
      segno = 1;
      muovi();      
    }
  }    

  textSize(25);
  fill(255,0,0);
  text("giunto = ",10,20); 
  text(giunto+1,120,20);
  text("theta1 = ",10,70); 
  text(theta[0]*180/PI,120,70);
  text("theta2 = ",10,120); 
  text(theta[1]*180/PI,120,120);
  text("theta3 = ",10,170); 
  text(theta[2],120,170);
  text("theta4 = ",10,220); 
  text(theta[3],120,220);
  fill(0,255,0);  
  text("coordinata y vista = ",500,30); 
  text(eyeY,750,30);
  text("percentuale apertura pinza = ",500,80); 
  text(round(100*posPinza/(d4x/2-d5x)),900,80);
  text("%",950,80);
  
  fill(200,0,200); // Colore del robot
  
  // Link 0 (base)
  translate(xBase,yBase);
  box(d0x,d0y,d0z);
  
  // Link 1 (si muove con theta1 = theta[0])
  rotateY(theta[0]); 
  translate((d1x-d0x)/2,-(d0y+d1y)/2,0);  
  box(d1x,d1y,d1z);
  
  // Link 2 (si muove con theta2 = theta[1])
  translate((d1x-d2z)/2,0,0);
  rotateY(theta[1]);
  translate((d2x-d2z)/2,0,0);
  box(d2x,d2y,d2z);  
  
  // Link 3 (si muove con d3 = theta[2])
  translate((d2x-d3x)/2,theta[2],0);  
  box(d3x,d3y,d3z);


  // Link 4 (si muove con theta4 = theta[3])
  fill(255,0,0); // Colore della pinza
  rotateY(theta[3]);
  translate(0,(d3y+d4y)/2,0);  
  box(d4x,d4y,d4z);
  
  // Pinza
  pushMatrix(); // Memorizzo il sistema attuale
  translate(-d5x/2-posPinza,d4y/2+d5y/2,0);  
  box(d5x,d5y,d5z); // Disegno il primo elemento della pinza
  popMatrix();  // Ritorno al sistema di riferimento memorizzato
  translate(d5x/2+posPinza,d4y/2+d5y/2,0);  
  box(d5x,d5y,d5z); // Disegno il secondo elemento della pinza



} 

void muovi()
{
  if (giunto == 0)
  {
    theta[giunto] += segno*.02;
  }
  if (giunto == 1)
  {
    if (segno*theta[giunto]-145*PI/180<0)
    {
      theta[giunto] += segno*.02;
    }
  }
  if (giunto == 2)
  {
    if (segno*theta[giunto]-d3y/2+d2y/2<0)
    {
      theta[giunto] += segno*1;
    }
  }
  if (giunto == 3)
  {
    theta[giunto] += segno*.1;
  }
  
}