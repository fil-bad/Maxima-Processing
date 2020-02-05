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
gomito = 1; % 1 = Gomito alto, 0 = Gomito basso
% Calcoli notevoli
r = abs(L1-L2);
R = L1+L2;

global qOld_n
qOld_n = [(L1+L2)/2;0];

% Dati per simulazione
t = 0;  % Variabile curvilinea      
vel=0.005;
lambda = 1;
% figure(1)
figure('Name','2Dof Animation','Renderer', 'painters', 'Position', [10 10 1500 600])
clf

x = 0; y = 0;
t = -0.1;

subplot(1,2,1);
obj = animatedline('Color','r','LineStyle','none','Marker','o','MarkerSize',4,'MaximumNumPoints',10);
Inverse = animatedline('Color','b','Marker','.','MarkerSize',12);
Newton= animatedline('Color','g','Marker','.','MarkerSize',12);
axis([-R R -R R])
grid on
title('2Dof Animation')
legend('obj','Inverse','Newton')   


subplot(1,2,2);
q1InvLine = animatedline('Color','r');
q2InvLine = animatedline('Color','b');
q1NLine = animatedline('Color','r','LineStyle','--');
q2NLine = animatedline('Color','b','LineStyle','--');


xlim([0,1]);
legend('q1Inv','q2Inv','q1N','q2N')   
grid on


while t<=1
    tOld = t;
    t = t+vel; 
    xlabel(['iterazione = ',num2str(t)])
    xOld = x; 
    yOld = y;
    [x,y] = DofPath(t, r, R);
    [q1Inv,q2Inv] = invCin(x,y,L1,L2,gomito);
    [q1N,q2N] = algNewton(x,y,L1,L2,lambda);
    
%     Animation plot
    [p1,p2] = draw2Dof(q1Inv,q2Inv,L1,L2,Inverse);
    [p1,p2] = draw2Dof(q1N,q2N,L1,L2,Newton);
    addpoints(obj,x,y);
    
%     Value plot
    addpoints(q1InvLine,t,q1Inv);
    addpoints(q2InvLine,t,q2Inv);
    addpoints(q1NLine,t,q1N);
    addpoints(q2NLine,t,q2N);
    drawnow limitrate
%     pause(5)
end

function [q1,q2] = invCin(x,y,L1,L2,gom)
gom = mod(floor(gom),2);    % to be 0 or 1

a = (x^2+y^2-L1^2-L2^2)/(2*L1*L2);
q2 = atan2((1-gom*2) * real((1-a^2)^(1/2)),a); 

b1 = L1+cos(q2)*L2;
b2 = sin(q2)*L2;
q1=atan2(-b2*x+b1*y, b1*x+b2*y);
end

function [q1,q2] = algNewton(x,y,L1,L2,lambda)
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
q = qOld_n + lambda/2*inv(J)*(P - h);
q(1) = mod(q(1),2*pi);
q(2) = mod(q(2),2*pi);
q1 = q(1);
q2 = q(2);
qOld_n = q;
end

function [p1,p2] = cinDir2Dof(q1,q2,L1,L2)
p1 = [L1*cos(q1) ; L1*sin(q1)];
p2 = [L2*cos(q1+q2)+p1(1) ; L2*sin(q1+q2)+p1(2)];
end

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