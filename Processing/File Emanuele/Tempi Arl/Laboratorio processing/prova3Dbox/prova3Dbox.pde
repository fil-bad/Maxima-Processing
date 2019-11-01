/* Questo sketch permette di disegnare nello spazio 3D un 
parallelepipedo di dimensioni lato x lato x profondità e 
di spostarlo mediante il mouse e le frecce destra e sinistra. */

float xDes = 50; // coordinata x del parallelepipedo
float yDes = 100; // coordinata y del parallelepipedo
float lato = 100;
float profondita = 3*lato;
float zDes = 0;

void setup() 
{
  size(1000, 800, P3D);
  strokeWeight(2);
  stroke(0);
  fill(255);
}

void draw() 
{
  background(50);
  lights();
  
  if (mousePressed)
  {
    xDes = mouseX;
    yDes = mouseY;
  }
  if (keyPressed)
  {
    // movimento lungo z
    if (keyCode == LEFT)
    {
      zDes -= 10;
    }
    if (keyCode == RIGHT)
    {
      zDes += 10;
    }
  }
    
  textSize(25);
  fill(255,0,0);  
  text("zDes = ",20,50);
  text(zDes,120,50);
  fill(255);
  
  translate(xDes+lato/2,yDes+lato/2,zDes-profondita/2);
  
  box(lato,lato,profondita); 

}