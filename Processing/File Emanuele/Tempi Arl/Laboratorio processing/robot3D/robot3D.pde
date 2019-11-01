/* Questo sketch permette di disegnare un robot con un solo
giunto, di spostarlo col mouse e di attuare il giunto con 
le frecce destra e sinistra */

float L1w = 30; // lato quadrato sezione link 1
float L1 = 200; // lunghezza link 1
float L2w = 30; // lato quadrato sezione link 2
float L2 = 200; // lunghezza link 2

float xBase;
float yBase;

float theta = 0; // angolo giunto 

void setup() 
{
  size(1000, 800, P3D);
  strokeWeight(2);
  stroke(0);
  xBase = width/2;
  yBase = height/2;  
}

void draw() 
{
  background(50);
  lights();
 
  if (mousePressed)
  {
    xBase = mouseX;
    yBase = mouseY;
  }
  
  if (keyPressed)
  {
    if (keyCode == LEFT)
    {
      theta -= .01;
    }
    if (keyCode == RIGHT)
    {
      theta += .01;
    }
  }
  
  textSize(25);
  fill(255,0,0);
  text("theta = ",10,20); 
  text(theta*180/PI,100,20);
  
  robot(theta);
  
} 
 
void robot(float theta)
{
  fill(255);
  
  // Primo link (parallelepipedo)
  translate(xBase,yBase-L1/2);
  box(L1w,L1,L1w);
  
  // Secondo link (parallelepipedo)
  rotateY(theta);
  translate(L2/2-L1w/2,-L1/2-L2w/2,0);
  box(L2,L2w,L2w);
  
}