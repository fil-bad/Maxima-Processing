import grafica.*;
public GPlot plot2;

int xOff, yOff;
int nPointPlot=500;
GPointsArray q1p = new GPointsArray(nPointPlot);
GPointsArray q2p = new GPointsArray(nPointPlot);
GPointsArray qr1p = new GPointsArray(nPointPlot);
GPointsArray qr2p = new GPointsArray(nPointPlot);
GPointsArray qn1p = new GPointsArray(nPointPlot);
GPointsArray qn2p = new GPointsArray(nPointPlot);
GPointsArray qg1p = new GPointsArray(nPointPlot);
GPointsArray qg2p = new GPointsArray(nPointPlot);

void setup() {
  size(800, 600);
  xOff=width/4;
  yOff=2*height/3;
  plot2 = new GPlot(this);
  plot2.setPos(width/2, height/3);
  plot2.setDim(250, 250);
  plot2.getTitle().setText("Link state");
  plot2.getXAxis().getAxisLabel().setText("t");
  plot2.getYAxis().getAxisLabel().setText("Rad");

  plot2.addLayer("objQ1", q1p);
  plot2.getLayer("objQ1").setPointColor(cRfin);
  plot2.addLayer("objQ2", q2p);
  plot2.getLayer("objQ2").setPointColor(cRfin);

  plot2.addLayer("inverseQ1", qr1p);
  plot2.getLayer("inverseQ1").setPointColor(cRinv);
  plot2.addLayer("inverseQ2", qr2p); 
  plot2.getLayer("inverseQ2").setPointColor(cRinv);

  plot2.addLayer("newtonQ1", qn1p);
  plot2.getLayer("newtonQ1").setPointColor(cRnewton);
  plot2.addLayer("newtonQ2", qn2p);
  plot2.getLayer("newtonQ2").setPointColor(cRnewton);

  plot2.addLayer("gradientQ1", qg1p); 
  plot2.getLayer("gradientQ1").setPointColor(cRGradiente);
  plot2.addLayer("gradientQ2", qg1p);
  plot2.getLayer("gradientQ2").setPointColor(cRGradiente);

  //plot2.activateZooming(1.5);
}

//Geometric variable
int L1=100;  //lunghezza link1
int L2=75;  //lunghezza link2
int R=30;    //raggio link

//State variable
float q1, q2;
float q1r, q2r;  //obiettivo da cinematica inversa
float q1n=1, q2n=1;  //obiettivo da algoritmo di newton
float q1G=1, q2G=1;  //obiettivo da algoritmo del Gradiente
float increase=0.5;
float lamda=0.05;
float kp=0.05; //k controllo proporzionale
float gomito=1;
float x=50, y=50;

//Color reconition
color cRed=color(255, 0, 0, 125);
color cGreen=color(0, 255, 0, 125);
color cRfin=color(60, 125);
color cRinv=color(0, 255, 0, 125);
color cRnewton=color(50, 75, 230, 125);
color cRGradiente=color(150, 75, 230, 125);

//Plotting variable
int k=0;  //numero di iterazioni
void draw() {
  // Graphic command
  background(#B9FDFF);

  // Input data
  if (mousePressed) {
    x=mouseX-xOff;
    y=-(mouseY-yOff);
  }

  //Inverse Cinematic
  float a =(sq(x)+sq(y)-sq(L1)-sq(L2))/(2*L1*L2);
  q2r=atan2(gomito*sqrt(abs(1-a*a)), a);
  float b1= L1+cos(q2r)*L2;
  float b2=sin(q2r)*L2;
  q1r=atan2(-b2*x+b1*y, b1*x+b2*y);

  // Dinamic proportional Controll
  q1=q1-kp*(q1-q1r);
  q2=q2-kp*(q2-q2r);

  //Numerical algoritm
  //Newton Algoritm
  float q1nNew, q2nNew;
  float[][] jInv = new float[2][2];
  float[] errN = new float[2]; //P-h(q)


  //variabili di calcolo di appoggio:
  float denTrig = (cos(q1n)*sin(q2n+q1n)-sin(q2n)*cos(q2n+q1n));
  float numY = (y-L2*sin(q2n+q1n)-L1*sin(q1n));
  float numX = (x-L2*cos(q2n+q1n)-L1*cos(q1n));


  jInv[0][0]=cos(q2n+q1n)/(L1*denTrig);
  jInv[0][1]=sin(q2n+q1n)/(L1*denTrig);
  println("jInv[0][0]="+jInv[0][0] + " jInv[0][1]=" + jInv[0][1]);

  float numY2 = (L2*sin(q2n+q1n)+L1*sin(q1n));
  float numX2 = (L2*cos(q2n+q1n)+L1*cos(q1n));

  jInv[1][0]=-numX2/(L1*L2*denTrig);
  jInv[1][1]=-numY2/(L1*L2*denTrig);
  println("jInv[1][0]="+jInv[1][0] + " jInv[1][1]=" + jInv[1][1]);


  errN[0]= x-L2*cos(q2n+q1n)-L1*cos(q1n);
  errN[1]= y-L2*sin(q2n+q1n)-L1*sin(q1n);
  println("errN[0]="+errN[0] + " errN[1]=" + errN[1]);

  q1nNew = (lamda/2) * (jInv[0][0] * errN[0] + jInv[0][1] * errN[1]);
  q2nNew = (lamda/2) * (jInv[1][0] * errN[0] + jInv[1][1] * errN[1]);
  println("q1nNew="+q1nNew + " q2nNew=" + q2nNew);
  q1n+=q1nNew;
  q2n+=q2nNew;

  //Gradient algoritm
  /*
  Si incarta quando ci si mette (apposta) esattamente dall'altro lato di una singolarità
   In questa condizione rallenta fino a fermarsi, ma NON DIVERGE
   */
  float q1GNew, q2GNew;
  float[][] jt = new float[2][2];
  float[] errG = new float[2]; //P-h(q)

  jt[0][0]=-L2*sin(q2G+q1G)-L1*sin(q1G);
  jt[0][1]= L2*cos(q2G+q1G)+L1*cos(q1G);
  println("jt[0][0]="+jt[0][0] + " jt[0][1]=" + jt[0][1]);

  jt[1][0]=-L2*sin(q2G+q1G);
  jt[1][1]= L2*cos(q2G+q1G);
  println("jt[1][0]="+jt[1][0] + " jt[1][1]=" + jt[1][1]);

  errG[0]= x-L2*cos(q2G+q1G)-L1*cos(q1G);
  errG[1]= y-L2*sin(q2G+q1G)-L1*sin(q1G);
  println("errG[0]="+errG[0] + " errG[1]=" + errG[1]);

  q1GNew = (lamda/2) * (jt[0][0] * errG[0] + jt[0][1] * errG[1]);
  q2GNew = (lamda/2) * (jt[1][0] * errG[0] + jt[1][1] * errG[1]);
  println("q1GNew="+q1GNew + " q1GNew=" + q1GNew);
  q1G+=q1GNew/5000; //incremento troppo grande
  q2G+=q2GNew/5000; //incremento troppo grandes

  //data plot
  // Add a new point to the second plot if the mouse moves significantly
  q1p.add(k, q1);
  q2p.add(k, q2);
  qr1p.add(k, q1r);
  qr2p.add(k, q2r);
  qn1p.add(k, q1n);
  qn2p.add(k, q2n);
  qg1p.add(k, q1G);
  qg2p.add(k, q2G);
  if (k>300) {
    q1p.remove(0);
    q2p.remove(0);
    qr1p.remove(0);
    qr2p.remove(0);
    qn1p.remove(0);
    qn2p.remove(0);
    qg1p.remove(0);
    qg2p.remove(0);
  }

  plot2.setPoints(q1p);
  plot2.getLayer("objQ1").setPoints(q1p);
  plot2.getLayer("objQ2").setPoints(q2p);

  plot2.getLayer("inverseQ1").setPoints(qr1p);
  plot2.getLayer("inverseQ2").setPoints(qr2p);

  plot2.getLayer("newtonQ1").setPoints(qn1p);
  plot2.getLayer("newtonQ2").setPoints(qn2p);

  plot2.getLayer("gradientQ1").setPoints(qg1p);
  plot2.getLayer("gradientQ2").setPoints(qg2p);



  // Draw the plot  
  plot2.beginDraw();
  plot2.drawBackground();
  plot2.drawBox();
  plot2.drawXAxis();
  plot2.drawYAxis();
  plot2.drawTitle();
  plot2.drawGridLines(GPlot.BOTH);
  plot2.drawLines();
  //plot2.drawPoints(star);
  plot2.endDraw();
  plot2.activatePanning(); // Activate the panning (only for the first plot)

  k++;    //iterazione finita

  // Robot draw
  pushMatrix();
  translate(xOff, yOff);
  println(xOff);
  scale(1, -1);

  SR(50);
  fill(255, 125);
  ellipse(0, 0, 2*(L1+L2), 2*(L1+L2));    //Spazio di lavoro Esterno
  fill(255, 0, 0, 125);
  ellipse(0, 0, 2*abs(L1-L2), 2*abs(L1-L2));    //Spazio di lavoro Interno

  if (1-a*a<0)
    fill(cRed);
  else
    fill(cGreen);
  ellipse(x, y, 2*R, 2*R);

  robot(q1r, q2r, cRfin);
  robot(q1, q2, cRinv);
  robot(q1n, q2n, cRnewton);
  robot(q1G, q2G, cRGradiente);

  popMatrix();

  // Statics draw
  float rd=100.0; //use tu controll number of digit after 0.
  String stats = "Robot parameters:\n";
  stats+= "L1="+L1+"\n";
  stats+= "L2="+L2+"\n";
  stats+= "x="+round(x*rd)/rd+"   y="+round(y*rd)/rd+"\n";
  stats+= "q1="+round(q1*rd)/rd+"rad   q1r="+round(q1r*rd)/rd+"rad     q1n="+round(q1n*rd)/rd+"rad     q1G="+round(q1G*rd)/rd+"rad"+"\n";
  stats+= "q2="+round(q2*rd)/rd+"rad   q2r="+round(q2r*rd)/rd+"rad     q2n="+round(q2n*rd)/rd+"rad     q2G="+round(q2G*rd)/rd+"rad"+"\n";
  stats+= "inc="+round(increase*rd)/rd+"     kp="+round(kp*rd)/rd+"     lambda="+round(lamda*rd)/rd+"\n";
  pushStyle();
  fill(50);
  textSize(15);
  text(stats, 10, 15);
  popStyle();


  String legend = "Posizione finale cin inv:\n";
  legend+="Movimento legge proporzionale:\n";
  legend+="Algoritmo di newton discreto:\n";
  legend+="Algoritmo del Gradiente discreto:\n";
  pushStyle();
  textAlign(RIGHT);
  textSize(15);
  textLeading(16);  // Set leading to 10
  fill(50);
  text(legend, width-50, 20);
  fill(cRfin);
  rect(width-40, 20-textAscent()-5, textAscent(), textAscent());
  fill(cRinv);
  rect(width-40, 20, textAscent(), textAscent());
  fill(cRnewton);
  rect(width-40, 20+textAscent()+5, textAscent(), textAscent());
  fill(cRGradiente);
  rect(width-40, 20+2*textAscent()+10, textAscent(), textAscent());
  popStyle();

  //pushStyle();


  //popStyle();
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
  link(R, L1);
  SR(50);
  rotate(q2);
  link(R, L2);
  SR(50);
  popMatrix();
}

void link(float r, float l) {
  arc(0, 0, r, r, PI/2, 3*PI/2, OPEN);
  translate(0, -r/2);
  line(0, 0, l, 0);
  line(0, r, l, r);
  noStroke();
  rect(0, 0, l, r);
  translate(l, +r/2);
  stroke(0);
  arc(0, 0, r, r, -PI/2, PI/2, OPEN);
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
  if (key=='r') {  //state reset
    y=x=60;
    q1n=q2n=1;
    q1G=q2G=1;
    increase=0.5;
    kp=0.05;
    gomito=1;
  }
  if (key=='l')
    lamda-=0.01;
  if (key=='L')
    lamda+=0.01;

  if (keyCode==UP)
    y+=increase;
  if (keyCode==DOWN)
    y-=increase;
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
  kp += -0.01*event.getCount();
  //if (increase<0.1)
  //  increase=0.1;
  println(kp);
}
