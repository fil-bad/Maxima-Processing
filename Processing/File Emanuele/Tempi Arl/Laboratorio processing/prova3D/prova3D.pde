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
  
  translate(0,0,zDes);
  
  // Faccia anteriore
  beginShape();
    vertex(xDes, yDes,0);
    vertex(xDes+lato, yDes,0);    
    vertex(xDes+lato, yDes+lato,0);
    vertex(xDes, yDes+lato,0);    
  endShape(CLOSE);    

  // Faccia laterale sinistra
  beginShape();
    vertex(xDes, yDes,0);
    vertex(xDes, yDes,-profondita);    
    vertex(xDes, yDes+lato,-profondita);    
    vertex(xDes, yDes+lato,0);
  endShape(CLOSE);    

  // Faccia superiore
  beginShape();
    vertex(xDes, yDes,0);
    vertex(xDes, yDes,-profondita);    
    vertex(xDes+lato, yDes,-profondita);    
    vertex(xDes+lato, yDes,0);
  endShape(CLOSE);    

  // Faccia laterale destra
  beginShape();
    vertex(xDes+lato, yDes,0);
    vertex(xDes+lato, yDes,-profondita);    
    vertex(xDes+lato, yDes+lato,-profondita);    
    vertex(xDes+lato, yDes+lato,0);
  endShape(CLOSE);    

  // Faccia inferiore
  beginShape();
    vertex(xDes, yDes+lato,0);
    vertex(xDes, yDes+lato,-profondita);    
    vertex(xDes+lato, yDes+lato,-profondita);    
    vertex(xDes+lato, yDes+lato,0);
  endShape(CLOSE);

  // Osservazione: la faccia posteriore non viene disegnata  

}