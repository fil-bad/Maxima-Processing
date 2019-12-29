function [xDot] = sys2Dif(t,x)
%SYS2DIF Summary of this function goes here
%     |q1   |        |q1Dot   |
% x = |q2Dot| xDot = |q2DotDot|
%     |q2   |        |q2Dot   |

D=1;
M=1;
K=1;
u=sin(t);
if(t>0)
    u=0;
else
    u=100;
end

xDot = zeros(3,1);

xDot(1) = -K/M*x(1)+x(2);
xDot(2) = -K/M*x(1) + u/M;
xDot(3) = x(2);
end

