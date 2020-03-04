clc
clear variables

% Reduntat system
% P1 = q1 + q2
% P2 = q1 - q2
% P3 = 3q1 + q2
% 

% Punto obiettivo
P = zeros(3,1);
P(1) = 1;
P(2) = 2;
% P(3) = 3/2 *(P(1)+P(2))+1/2*(P(1)-P(2));    %Giusta e raggiungibile
P(3) = 4.5;    %Fuori raggiungibilità
P

% Variabile stimante
qN = zeros(2,1);
qD = qN;
qNewN = qN;
qNewD = qN;


% In questo caso J costante, essendo esempio lineare
J = [1 1
     1 -1
     3 1]
 
 itr = 100;
 qStepN =  zeros(2,itr+1);
 qStepD =  zeros(2,itr+1);
 qStepN(:,1) = qN;
 qStepD(:,1) = qN;
 eN =  zeros(0,itr);
 eD =  zeros(0,itr);

 for i=1:itr
     j = J*J'*J;    % j di appoggio per l'adattamento di J
     qNewN = qN + (3)*(pinv(j)*(P-h(qN)));     % "Newtown"
     qNewD = qD + (1/100)*(j'*(P-h(qD)));      % "Gradiente"

     qN = qNewN;
     qD = qNewD;
     qStepN(:,i+1) = qN;
     qStepD(:,i+1) = qD;
     eN(i) = norm(P-h(qN));
     eD(i) = norm(P-h(qD));
 end

 
figure (1)
clf
subplot(3,2,1)
plot(qStepN(1,:),qStepN(2,:))
grid on
hold on
plot(qStepN(1,1),qStepN(2,1),'.','MarkerSize',20)
legend("Evoluzioni q", "Start point")
title(strcat("Newton in q space iteration = ",num2str(itr)))

subplot(3,2,2)
pStepN =  zeros(3,itr+1);
for i = 1:itr+1
pStepN(:,i) = h(qStepN(:,i));    
end

plot3(pStepN(1,:),pStepN(2,:),pStepN(3,:),'.-','MarkerSize',10)
grid on
hold on
plot3(P(1),P(2),P(3),'.','MarkerSize',20)
plot3(pStepN(1,1),pStepN(2,1),pStepN(3,1),'d','MarkerSize',20)
legend("Stime", "Obj", "Start Point")
title(strcat("Newton in p space iteration = ",num2str(itr)))



%  Same with gradient
subplot(3,2,3)
plot(qStepD(1,:),qStepD(2,:))
grid on
hold on
plot(qStepD(1,1),qStepD(2,1),'.','MarkerSize',20)
legend("Evoluzioni q", "Start point")
title(strcat("Gradiente in q space iteration = ",num2str(itr)))

subplot(3,2,4)
pStepD =  zeros(3,itr+1);
for i = 1:itr+1
pStepD(:,i) = h(qStepD(:,i));    
end

plot3(pStepD(1,:),pStepD(2,:),pStepD(3,:),'.-','MarkerSize',10)
grid on
hold on
plot3(P(1),P(2),P(3),'.','MarkerSize',20)
plot3(pStepD(1,1),pStepD(2,1),pStepD(3,1),'d','MarkerSize',20)
legend("Stime", "Obj", "Start Point")
title(strcat("Gradiente in p space iteration = ",num2str(itr)))


% Error comparison
subplot(3,2,[5;6])
stem(eN(:))
grid on
hold on
stem(eD(:))
legend("e-Newton","e-Gradiente")
title("Errore da Obj, step per step")
xlabel('# Iteration')



 function [qDir] = h(q)
    qDir = zeros(3,1);
    qDir(1) = q(1)+q(2);
    qDir(2) = q(1)-q(2);
    qDir(3) = 3*q(1)+q(2); 
 end