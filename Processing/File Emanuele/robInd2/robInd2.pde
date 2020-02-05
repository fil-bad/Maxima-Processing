
void setup() {
  size(450, 450);
}
int L=100;   //lunghezza link
int R=30;    //raggio link
float q1, q2;
float q1r, q2r;
float increase=0.5;
float step=0.05;
float gomito=1;
float x, y;

color cRed=color(255, 0, 0, 125);
color cGreen=color(0, 255, 0, 125);

void draw() {
  background(#B9FDFF);
  translate(width/2, height/2);
  SR(50);
  fill(255, 125);
  ellipse(0, 0, 4*L, 4*L);    //Spazio di lavoro


  float a =(pow(x, 2)+pow(y, 2)-L*L-L*L)/(2*L*L);
  q2r=atan2(gomito*sqrt(abs(1-a*a)), a);
  float b1= L+cos(q2r)*L;
  float b2=sin(q2r)*L;
  q1r=atan2(-b2*x+b1*y, b1*x+b2*y);
  q1=q1-step*(q1-q1r);
  q2=q2-step*(q2-q2r);

  if (1-a*a<0)
    fill(cRed);
  else
    fill(cGreen);
  ellipse(x, y, 2*R, 2*R);

  robot(q1-PI/2, q2, cRed);
  robot(q1r-PI/2, q2r, cGreen);
}

void SR(int unita) {
  strokeWeight(4);
  stroke(#FF0000);
  line(0, 0, unita, 0);
  stroke(#00FF00);
  line(0, 0, 0, unita);
  stroke(0);
  strokeWeight(10);
  point(0, 0);
  strokeWeight(1);
}

void robot (float q1, float q2, color c) {
  fill(c);
  pushMatrix();
  rotate(q1);
  link(R, L);
  SR(50);
  rotate(q2);
  link(R, L);
  SR(50);
  popMatrix();
}

void link(float r, float l) {
  arc(0, 0, r, r, PI, 2*PI, OPEN);
  translate(-r/2, 0);
  line(0, 0, 0, l);
  line(r, 0, r, l);
  noStroke();
  rect(0, 0, r, l);
  translate(+r/2, l);
  stroke(0);
  arc(0, 0, r, r, 0, PI, OPEN);
  strokeWeight(10);
  point(0, 0);
  strokeWeight(1);
}

void mousePressed() {
  x=mouseX-width/2;
  y=mouseY-height/2;
}


void keyPressed() {
  if (key=='g')
    gomito*=-1;
  //if (key=='q')
  //  q1r+=increase;
  //if (key=='w')
  //  q1r-=increase;
  //if (key=='a')
  //  q2r+=increase;
  //if (key=='s')
  //  q2r-=increase;

  if (keyCode==UP)
    y-=increase;
  if (keyCode==DOWN)
    y+=increase;
  if (keyCode==LEFT)
    x-=increase;
  if (keyCode==RIGHT)
    x+=increase;
  if (key=='+')
    increase += 0.1;
  if (key=='-')
    increase -= 0.1; 
  if (increase<0.1)
    increase=0.1;
}

void mouseWheel(MouseEvent event) {
  increase += -0.1*event.getCount();
  if (increase<0.1)
    increase=0.1;
  println(increase);
}
