clc
clear variables
close all
% Animazione della cinematica del 2Dof con i 3 Diversi metodi:
% 1) Cinematica inversa
% 2) Algoritmo di Newton
% 3) Algoritmo del Gradiente
% 4) Algoritmo Misto


% Parametri 2Dof
L1 = 3;     % Lunghezza 1° braccio
L2 = 2;     % Lunghezza 2° braccio
gomito = 0; % 1 = Gomito alto, 0 = Gomito basso
% Calcoli notevoli
r = abs(L1-L2);
R = L1+L2;
% ######################################################

% SetUp condizioni iniziali dell'algoritmo
global qOld_n
qOld_n = [(L1+L2)/2;0];

global qOld_g
qOld_g = [(L1+L2)/2;0];

global qOld_c;
qOld_c = [(L1+L2)/2;0];
% ######################################################


% Dati per simulazione
t = 0;  % Variabile curvilinea      
vel=0.001;
global lambdaN;
lambdaN = 1;
global lambdaG;
lambdaG = 0.05;



% ######################################################
%        Disegno della finestra e dei 2 SubPot
figure('Name','2Dof Animation','Renderer', 'painters', 'Position', [10 10 1500 600])
clf

x = 0; y = 0; t = 0;

%##############################################################
%################ Disegno dei vari robot 2DOF ################# 
%##############################################################
subplot(1,2,1); % Linee che voglio mettere dentro il 1° SubPlot
obj = animatedline('Color','r','LineStyle','none','Marker','o','MarkerSize',4,'MaximumNumPoints',50);
Inverse = animatedline('Color','b','Marker','.','MarkerSize',12);
Newton = animatedline('Color','g','Marker','.','MarkerSize',12);
Gradiente = animatedline('Color','m','Marker','.','MarkerSize',12);
Custom = animatedline('Color','k','Marker','.','MarkerSize',12);
axis([-R R -R R])
grid on
title('2Dof Animation')
legend('obj','Inverse','Newton','Gradiente','Custom')   



%##############################################################
%############# Vettori dello storico dei giunti ############### 
%##############################################################
subplot(1,2,2); % Linee che voglio mettere dentro il 2° SubPlot
% Inversa diretta
q1InvLine = animatedline('Color','b','LineStyle','-');
q2InvLine = animatedline('Color','b','LineStyle',':');
% Newton
q1NLine = animatedline('Color','g','LineStyle','-');
q2NLine = animatedline('Color','g','LineStyle',':');
% Gradiente
q1GLine = animatedline('Color','m','LineStyle','-');
q2GLine = animatedline('Color','m','LineStyle',':');
% Custom
q1CLine = animatedline('Color','k','LineStyle','-');
q2CLine = animatedline('Color','k','LineStyle',':');
xlim([0,1]);
legend('q1Inv','q2Inv','q1N','q2N','q1G','q2G','q1C','q2C')   
grid on

% Loop di calcolo per fare un giro completo lungo il percorso
while t<=1
%     Aggiornamento ciclo
    tOld = t;
    t = t+vel; 
    xlabel(['iterazione = ',num2str(t)])
    
%     Ottenimento nuovo punto obiettivo
    xOld = x; 
    yOld = y;
    [x,y] = DofPath(t, r, R);
    
%     Calcolo delle variabili di giunto sul nuovo obiettivo
    [q1Inv,q2Inv] = invCin(x,y,L1,L2,gomito);
    [q1N,q2N] = algNewton(x,y,L1,L2,lambdaN,gomito);
    [q1G,q2G] = algGrad(x,y,L1,L2,lambdaG,gomito);
    [q1C,q2C] = algCust(x,y,L1,L2,gomito);
    
%    2Dof Animation plot
    [p1,p2] = draw2Dof(q1Inv,q2Inv,L1,L2,Inverse);
    [p1,p2] = draw2Dof(q1N,q2N,L1,L2,Newton);
    [p1,p2] = draw2Dof(q1G,q2G,L1,L2,Gradiente);
    [p1,p2] = draw2Dof(q1C,q2C,L1,L2,Custom);
    addpoints(obj,x,y);
    
%   Link variable Value plot
    addpoints(q1InvLine,t,q1Inv);
    addpoints(q2InvLine,t,q2Inv);
    addpoints(q1NLine,t,q1N);
    addpoints(q2NLine,t,q2N);
    addpoints(q1GLine,t,q1G);
    addpoints(q2GLine,t,q2G);
    addpoints(q1CLine,t,q1C);
    addpoints(q2CLine,t,q2C);
    drawnow limitrate
end


%##############################################################
%##################### Cinematica inversa ##################### 
%##############################################################
function [q1,q2] = invCin(x,y,L1,L2,gom)
gom = mod(floor(gom),2);    % to be 0 or 1

a = (x^2+y^2-L1^2-L2^2)/(2*L1*L2);
q2 = atan2((1-gom*2) * real((1-a^2)^(1/2)),a); 

b1 = L1+cos(q2)*L2;
b2 = sin(q2)*L2;
q1=atan2(-b2*x+b1*y, b1*x+b2*y);
end

%##############################################################
%##################### Cinematica Newton ##################### 
%##############################################################

function [q1,q2] = algNewton(x,y,L1,L2,lambda,gom)
global qOld_n;
P = [x;y];
if(~isfinite(qOld_n(1)))
    qOld_n(1) = L1+L2/2;
end

if(~isfinite(qOld_n(2)))
    qOld_n(2) = L1+L2/2;
end

J=[-L2*sin(qOld_n(2)+qOld_n(1))-L1*sin(qOld_n(1)) ,	-L2*sin(qOld_n(2)+qOld_n(1))
    L2*cos(qOld_n(2)+qOld_n(1))+L1*cos(qOld_n(1)),	L2*cos(qOld_n(2)+qOld_n(1))];

[p1,h] = cinDir2Dof(qOld_n(1),qOld_n(2),L1,L2);

% Se |arcos(a)| < 1 Esiste una soluzione alla cinematica inversa.
% a = (x^2+y^2-L1^2-L2^2)/(2*L1*L2);
% Se arcos(a)<0 gomito basso
% Se arcos(a)>0 gomito alto
a = (x^2+y^2-L1^2-L2^2)/(2*L1*L2);
theta2 = atan2((1-gom*2) * real((1-a^2)^(1/2)),a);
theta2 = mod(theta2,2*pi);
% fprintf('theta2-pi = %f3\t',theta2-pi);
% fprintf('qOld_g(2)-pi = %f3\n',qOld_n(2)-pi);

if sign(theta2-pi) ~= sign(qOld_n(2)-pi) && abs(theta2-pi) >= 0.1 && abs(theta2-pi) <= pi-0.1
    qOld_n = qOld_n+[pi/4;0];    % Sposto la stima finchè non mi va bene
    qOld_n(2) = theta2;
end

q = qOld_n + lambda/2*inv(J)*(P - h);
q(1) = mod(q(1),2*pi);
q(2) = mod(q(2),2*pi);
q1 = q(1);
q2 = q(2);
qOld_n = q;
end


%##############################################################
%##################### Cinematica Gradiente ################### 
%##############################################################

function [q1,q2] = algGrad(x,y,L1,L2,lambda, gom)
global qOld_g;
P = [x;y];
if(~isfinite(qOld_g(1)))
    qOld_g(1) = L1+L2/2;
end

if(~isfinite(qOld_g(2)))
    qOld_g(2) = L1+L2/2;
end

J=[-L2*sin(qOld_g(2)+qOld_g(1))-L1*sin(qOld_g(1)) ,	-L2*sin(qOld_g(2)+qOld_g(1))
    L2*cos(qOld_g(2)+qOld_g(1))+L1*cos(qOld_g(1)),	L2*cos(qOld_g(2)+qOld_g(1))];

[p1,h] = cinDir2Dof(qOld_g(1),qOld_g(2),L1,L2);

% Se |arcos(a)| < 1 Esiste una soluzione alla cinematica inversa.
% a = (x^2+y^2-L1^2-L2^2)/(2*L1*L2);
% Se arcos(a)<0 gomito basso
% Se arcos(a)>0 gomito alto
a = (x^2+y^2-L1^2-L2^2)/(2*L1*L2);
theta2 = atan2((1-gom*2) * real((1-a^2)^(1/2)),a);
theta2 = mod(theta2,2*pi);
% fprintf('theta2-pi = %f3\t',theta2-pi);
% fprintf('qOld_g(2)-pi = %f3\n',qOld_g(2)-pi);

if sign(theta2-pi) ~= sign(qOld_g(2)-pi) && abs(theta2-pi) >= 0.1 && abs(theta2-pi) <= pi-0.1
    qOld_g = qOld_g+[pi/4;0];    % Sposto la stima finchè non mi va bene
    qOld_g(2) = theta2;
end


q = qOld_g + lambda/2*J'*(P - h);
q(1) = mod(q(1),2*pi);
q(2) = mod(q(2),2*pi);
q1 = q(1);
q2 = q(2);
qOld_g = q;
end

%##############################################################
%##################### Cinematica Custom ###################### 
%##############################################################
function [q1,q2] = algCust(x,y,L1,L2,gom)
global qOld_c;
P = [x;y];
if(~isfinite(qOld_c(1)))
    qOld_c(1) = L1+L2/2;
end

if(~isfinite(qOld_c(2)))
    qOld_c(2) = L1+L2/2;
end

J=[-L2*sin(qOld_c(2)+qOld_c(1))-L1*sin(qOld_c(1)) ,	-L2*sin(qOld_c(2)+qOld_c(1))
    L2*cos(qOld_c(2)+qOld_c(1))+L1*cos(qOld_c(1)),	L2*cos(qOld_c(2)+qOld_c(1))];

[p1,h] = cinDir2Dof(qOld_c(1),qOld_c(2),L1,L2);

% Se |arcos(a)| < 1 Esiste una soluzione alla cinematica inversa.
% a = (x^2+y^2-L1^2-L2^2)/(2*L1*L2);
% Se arcos(a)<0 gomito basso
% Se arcos(a)>0 gomito alto
a = (x^2+y^2-L1^2-L2^2)/(2*L1*L2);
theta2 = atan2((1-gom*2) * real((1-a^2)^(1/2)),a);
theta2 = mod(theta2,2*pi);
% fprintf('theta2-pi = %f3\t',theta2-pi);
% fprintf('qOld_g(2)-pi = %f3\n',qOld_c(2)-pi);

if sign(theta2-pi) ~= sign(qOld_c(2)-pi) && abs(theta2-pi) >= 0.1 && abs(theta2-pi) <= pi-0.1
    qOld_c = qOld_c+[pi/4;0];    % Sposto la stima finchè non mi va bene
    qOld_c(2) = theta2;
end

global lambdaG;
global lambdaN;

if(abs(det(J)) > 0.01)
%     Newton
    q = qOld_c + lambdaN/2*inv(J)*(P - h);
else
%     Gradiente
    q = qOld_c + lambdaG/2*J'*(P - h);
end
q(1) = mod(q(1),2*pi);
q(2) = mod(q(2),2*pi);
q1 = q(1);
q2 = q(2);
qOld_c = q;
end


%##############################################################
%##################### Cinematica Diretta ##################### 

function [p1,p2] = cinDir2Dof(q1,q2,L1,L2)
p1 = [L1*cos(q1) ; L1*sin(q1)];
p2 = [L2*cos(q1+q2)+p1(1) ; L2*sin(q1+q2)+p1(2)];
end

%##################### Disegno e obiettivo #################### 

function [p1,p2] = draw2Dof(q1,q2,L1,L2,anLin)

[p1,p2] = cinDir2Dof(q1,q2,L1,L2);
clearpoints(anLin);
addpoints(anLin,0,0)
addpoints(anLin,p1(1),p1(2));
addpoints(anLin,p2(1),p2(2));
end

function [x,y] = DofPath(t, r, R)
% t = 0 --> 100 % (float)
t = t - floor(t);
% r = Raggio minore
% R = Raggio maggiore
% Il percorso ha 8 segmenti => ogni 0.125 cambio tipo
tratto = floor(t*8); % ottenco indici da 0 a 7

tRemap = (t-tratto/8)*8; % Rimappo t affinchè ogni tratto vada da 0 a 1
    switch (tratto)
        case 0     %1° quarto di cerchio Esterno
            x = R * cos(tRemap*pi/2);
            y = R * sin(tRemap*pi/2);
        case 1     %Verticale Alto
            x = 0;
            y = R - tRemap*abs(R-r);
        case 2     %1° quarto di cerchio Interno
            x = r * cos(tRemap*pi/2 + pi/2);
            y = r * sin(tRemap*pi/2 + pi/2);
        case 3    %Orizontale Dx
            x = -(r + tRemap*abs(R-r));
            y = 0;
        case 4     %2° quarto di cerchio Esterno
            x = R * cos(tRemap*pi/2 + pi);
            y = R * sin(tRemap*pi/2 + pi);
        case 5     %Verticale Basso
            x = 0;
            y = -(R - tRemap*abs(R-r));
        case 6     %2° quarto di cerchio Interno
            x = r * cos(tRemap*pi/2 + pi*3/2);
            y = r * sin(tRemap*pi/2 + pi*3/2);
        case 7     %Orizontale Sx
            x = r + tRemap*abs(R-r);
            y = 0;     
    end
end