clc;
clear variables;
% Calcolo simulazione del sistema a 2 Dof tramite ode45
%     |q1   |        |q1Dot   |
% x = |q2Dot| xDot = |q2DotDot|
%     |q2   |        |q2Dot   |

x=zeros(3,1);
xDot = x;

% Initial State
x0=x;
x0(1)=0;
x0(2)=0;
x0(3)=0;

t = (0:0.01:30);
u = ones(1,length(t));
u(1) = 0;

[TOUT,X] = ode45(@(t, x) sys2Dif(t, x),t,x0);

figure(1)
clf;
plot(TOUT(:),X(:,1));
grid on
hold on
plot(TOUT(:),X(:,2));
plot(TOUT(:),X(:,3));
legend("q1","q2Vel","q2");
