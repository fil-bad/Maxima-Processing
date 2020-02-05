close
X = [0:0.01:1];
k=0;
while true
    k = mod((k+1),90);
    plot(X,1-abs(10*(X-k*0.01)));
    axis([0 1 0 1])
    xlabel(['iterazione = ',num2str(k)])
    title('In viaggio col triangolo')
    pause(0.01);
end
hold on
plot(X,exp(-3*X),'-r');
legend('triangle','exponential','Location','SouthWest')
hold off