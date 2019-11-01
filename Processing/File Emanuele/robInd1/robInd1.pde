PVector v;
PVector vEr;
PVector vObj;
void setup()
{
  size(500, 600);
  v = new PVector(0, 0);
  vObj = new PVector(0, 0);
  vEr = new PVector(0, 0);
}

float kErr=0.1;

void draw() {
  translate(width/2, height/2);
  scale(1,-1);
  background(150);
  stroke(0);
  line(0, 0, v.x, v.y);
  stroke(#FF0000);
  line(0, 0, vObj.x, vObj.y);
  stroke(#00FFFF);
  vEr.set(PVector.sub(vObj, v));
  line(v.x, v.y, v.x+vEr.x, v.y+vEr.y);
  stroke(0);

  fill(#ff0000, 150);
  ellipse(v.x, v.y, 50, 50);
  vEr.set(PVector.sub(vObj, v));
  vEr.setMag(PVector.sub(v, vObj).mag()*kErr);
  v.add(vEr);

  vectPrint(v);
}

void mousePressed() {
  vObj.set(mouseX-width/2, -(mouseY- height/2));
  vectPrint(vEr);
}

void vectPrint(PVector v){
    println("vector mag= "+v.mag()+" xVect="+v.x+" yVect="+v.y);   
}
