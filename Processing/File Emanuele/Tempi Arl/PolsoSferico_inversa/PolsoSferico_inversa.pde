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

/* Dimensionamento Braccio*/
float[] theta = {0, 0, 0}; // parametri giunto (theta4, theta5, theta6)
float[] RPY = {0, 0, 0}; // Parametrizzazione Angolo (Roll, Pitch, Yaw)
float d4=200;
float d6=200;
Float R3_6[][] = new Float[4][4];
String R3_6String[][] = new String[4][4];
float apPinza = 1;

/* Parametri di controllo*/
boolean solDuale=true;
float kp=0.01;
float[] thetaKp = {0, 0, 0}; // parametri giunto (theta4, theta5, theta6)


void setup() 
{
  size(1000, 800, P3D);
  stroke(255);
  strokeWeight(2);
  xBase = width/2;
  yBase = height-50;
  smooth();
  RPY_Calc(solDuale);
}

void draw() 
{

  background(0);
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
    if (keyCode == DOWN) eyeY -= 5;
    if (keyCode == UP) eyeY += 5;
    if (key == '1') giunto = 0;
    if (key == '2') giunto = 1;
    if (key == '3') giunto = 2;
    if (key == 'o' || key == 'O') apPinza = max(min(apPinza+0.05, 1), 0);
    if (key == 'p' || key == 'P') apPinza = max(min(apPinza-0.05, 1), 0);
    if (key == '+') kp = max(min(kp+0.005, 0.5), 0.005);
    if (key == '-') kp = max(min(kp-0.005, 0.5), 0.005);
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
  muoviKp();


  textSize(25);  //distanzio ogni riga di 30 dalla precedente
  fill(255, 0, 0);
  text("giunto = " + str(giunto+1) + " INVERSA", 10, 20); 
  text("theta4 = " + str(round(100*((theta[0]*180/PI + 360.0)%360.0))/100.0) + "°", 10, 50); 
  text("theta5 = " + str(round(100*((theta[1]*180/PI + 360.0)%360.0))/100.0) + "°", 10, 80); 
  text("theta6 = " + str(round(100*((theta[2]*180/PI + 360.0)%360.0))/100.0) + "°", 10, 110); 
  int yfMatrix=r3_6Text(25, 5, 10, 150, 3);
  fill(255, 0, 0);
  text("Sol +/- : " + str(solDuale), 10, yfMatrix+30); 
  text("Roll = " + str(round(100*((RPY[0]*180/PI + 360.0)%360.0))/100.0) + "°", 10, yfMatrix+60); 
  text("Pitch= " + str(round(100*((RPY[1]*180/PI + 360.0)%360.0))/100.0) + "°", 10, yfMatrix+90); 
  text("Yaw  = " + str(round(100*((RPY[2]*180/PI + 360.0)%360.0))/100.0) + "°", 10, yfMatrix+120); 
  text("kp  = " + str(round(1000*kp)/1000.0), 10, yfMatrix+150); 


  fill(0, 255, 0);  
  text("coordinata y vista = " + str(round(1000*eyeY)/1000.0), 500, 30); 
  text("percentuale apertura pinza = "+str(round(100*apPinza))+"%", 500, 60); 


  axisReset();
  pushMatrix();
  polsoDraw(theta);
  popMatrix();
  pushMatrix();
  polsoDraw(thetaKp);
  popMatrix();
  pushMatrix();
  rotateZ(RPY[0]);
  rotateY(RPY[1]);
  rotateX(RPY[2]);
  assi(150);
  popMatrix();
}

void muoviKp()
{
  float er=0;
  for (int i =0; i<3; i++)
  {
    er=theta[i]-thetaKp[i];
    if (abs(er)>kp) thetaKp[i]+=er*kp;
    else thetaKp[i] = theta[i];
  }
}

void muovi()
{
  RPY[giunto] += segno*.02;
  RPY_Calc(solDuale);
}

void RPY_Calc(boolean sol)
{
  /* Calc Normale */
  R3_6[0][0]=  cos(RPY[0])*cos(RPY[1]);
  R3_6[0][1]=  sin(RPY[0])*cos(RPY[1]);
  R3_6[0][2]= -sin(RPY[1]);
  R3_6[0][3]=0.0;
  /* Calc Scorrimento */
  R3_6[1][0]=  cos(RPY[0])*sin(RPY[1])*sin(RPY[2]) - sin(RPY[0])*cos(RPY[2]);
  R3_6[1][1]=  sin(RPY[0])*sin(RPY[1])*sin(RPY[2]) + cos(RPY[0])*cos(RPY[2]);
  R3_6[1][2]=  cos(RPY[1])*sin(RPY[2]);
  R3_6[1][3]=0.0;
  /* Calc Avanzamento */
  R3_6[2][0]=  cos(RPY[0])*sin(RPY[1])*cos(RPY[2]) + sin(RPY[0])*sin(RPY[2]);
  R3_6[2][1]=  sin(RPY[0])*sin(RPY[1])*cos(RPY[2]) - cos(RPY[0])*sin(RPY[2]);
  R3_6[2][2]=  cos(RPY[1])*cos(RPY[2]);
  R3_6[2][3]=0.0;

  /*Calcolo dei theta*/
  if (sol)  //soluzione positiva
  {
    theta[1]=atan2(+sqrt(sq(R3_6[2][0])+sq(R3_6[2][1])), R3_6[2][2]);  //theta5
    if (theta[1]==0)
    {
      theta[2]=0;  //theta6 arbitrario
      theta[0]=atan2(R3_6[0][1], R3_6[0][0]);   //theta 4
    } else if (theta[1]==PI)
    {
      theta[2]=0;                               //theta6 arbitrario
      theta[0]=atan2(-R3_6[0][1], -R3_6[0][0]); //theta 4
    } else {
      theta[2]=atan2(R3_6[1][2], -R3_6[0][2]);  //theta6 
      theta[0]=atan2(R3_6[2][1], R3_6[2][0]);   //theta 4
    }
  } else { //sol negativa
    theta[1]=atan2(-sqrt(sq(R3_6[2][0])+sq(R3_6[2][1])), R3_6[2][2]);  //theta5
    if (theta[1]==0)
    {
      theta[2]=0;  //theta6 arbitrario
      theta[0]=atan2(R3_6[0][1], R3_6[0][0]);   //theta 4
    } else if (theta[1]==PI)
    {
      theta[2]=0;                               //theta6 arbitrario
      theta[0]=atan2(-R3_6[0][1], -R3_6[0][0]); //theta 4
    } else {
      theta[2]=atan2(-R3_6[1][2], R3_6[0][2]);  //theta6 
      theta[0]=atan2(-R3_6[2][1], -R3_6[2][0]);   //theta 4
    }
  }

  /* Calc Pos */
  R3_6[3][0]=d6*cos(theta[0])*sin(theta[1]);
  R3_6[3][1]=d6*sin(theta[0])*sin(theta[1]);
  R3_6[3][2]=d6*cos(theta[1])+d4;
  R3_6[3][3]=1.0;
}

int r3_6Text(int sizeT, int spaceT, int x, int y, int digit)
{
  //return yfin
  textSize(sizeT);

  float fdigit=pow(10, digit);


  float sChar[]=new float[10];
  for (int i = 0; i<4; i++) sChar[i] = textWidth(str(i));
  float lCel=max(sChar)*(digit+3) + 10;    //le caselle interne variano tra 0:1, le esterne hanno spazio
  int yf= y+(sizeT+spaceT)*3;
  int xf= x+ceil(lCel*4);

  fill(0, 255, 0);
  for (int i = 0; i<4; i++)
  {
    for (int j = 0; j<4; j++)
    {
      R3_6String[i][j]=str(round(fdigit*R3_6[i][j])/fdigit);
      if (i==0 && j<3) fill(255, 0, 0);  //Parametri asse X
      else if (i==1 && j<3) fill(0, 255, 0);  //Parametri asse Y
      else if (i==2 && j<3) fill(0, 0, 255);  //Parametri asse Z
      else if (i==3 && j<3)
      {
        if (j==0) fill(255, 0, 0);  //Parametri asse X
        else if (j==1) fill(0, 255, 0);  //Parametri asse Y
        else if (j==2) fill(0, 0, 255);  //Parametri asse Z
      } else fill(0, 255, 255);

      text(R3_6String[i][j], x+5+lCel*i, y-spaceT/2+(sizeT+spaceT)*j);
      //println("i="+i+" j="+j+"  \t"+R3_6String[i][j]+"\t"+str(R3_6[i][j]));
    }
  }

  strokeWeight(2);
  stroke(255, 0, 0);
  for (int i = 1; i<4; i++)
  {
    line(x+lCel*i, y-(sizeT+spaceT), x+lCel*i, yf);
    line(x, y+(sizeT+spaceT)*(i-1), xf, y+(sizeT+spaceT)*(i-1));
  }

  return yf;
}


void keyPressed()
{
  if (key == 'd' || key == 'D') 
  {
    solDuale = !solDuale;
    RPY_Calc(solDuale);
  }
}
