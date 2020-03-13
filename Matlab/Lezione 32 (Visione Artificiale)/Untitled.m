clc
clear variables
close all
%% Open Image & set on greyscale & BW

imgData = imread('foto5-rot2.jpg');
figure(1)
imshow(imgData); % the same picture above

img_gs = rgb2gray(imgData);

figure(2)
imshow(img_gs)

img_bw = imbinarize(img_gs); %~

figure(3)
imshow(img_bw)

img_size = size(img_bw);
img_size = img_size(1)*img_size(2);

%% Fill internal holes below a certain scale

bw2 = bwareaopen(img_bw, floor(0.1*img_size)); % all the connected objects
% bigger than a tenth of the image;
figure(4)
imshow(bw2)

bw3 = ~bwareaopen(~bw2, 35);
figure(5)
imshow(bw3)

%% Area & Perimeter calc

area_np = bwarea(bw3);

perim_np = bwperim(bw3);

perim_val = bwarea(perim_np);

figure(6)
imshow(perim_np)

%% Dilation-erosion approach

r = 4; % radius
n = 10; % approximating circle with polygon of n edges

SE = strel('disk',r);

eroded = imerode(bw3, SE);

figure(7)
imshow(eroded)

dilated = imdilate(bw3, SE);

figure(8)
imshow(dilated)

perimeter = dilated-eroded;
figure(9)
imshow(perimeter)

figure(10)
imshowpair(perim_np, perimeter, 'montage')

%% Radon Transform
theta = 0:180-1;

[R,xp] = radon(bw3, theta);
% plot of radon transform
figure(11)
imshow(R,[],'Xdata',theta,'Ydata',xp,'InitialMagnification','fit')
xlabel('\theta (degrees)')
ylabel('x''')
colormap(gca,hot), colorbar

%%

n_pixel = 0:length(R(:,1))-1;

figure(12)
clf
maxR = max(R(:));

for i = 1:180
    pause(0.1)
    plot(n_pixel, R(:,i));
    str = "Radon transform at " + i + "°";
    title(str)
    ylim([0 maxR+10]);
    
end
%% Find diagonals

maxes = zeros(1,180);
vals = zeros(1,180);
for i = 1:180
    [vals(i), maxes(i)] = max(R(:,i));
end

[ampl, locs] = findpeaks(vals,'MinPeakProminence',0.01*max(R(:)));

% per fare in modo, successivamente, di avere sempre la bisettrice scelta 
% con lo stesso criterio (NON FUNZIONAAA)
[b, ind] = sort(ampl);
for i = 1:length(locs)
b(i) = locs(ind(i));
end
locs = b;

pix = zeros(1,length(locs));

for i = 1:length(locs)
    pix(i) = maxes(locs(i));
end

figure(13)
title("Max value for each angle")
plot(0:179,vals, 'r', locs, ampl, 'b.', 'MarkerSize', 15)

%%
figure(20)
clf

iR = 0;

for i = 1:length(locs)
    pause(1);
    iR = iR + iradon([R(:,locs(i)) R(:, locs(i))], [locs(i) locs(i)])/2;
    imshow(iR)
end

%%
tmp = imgData;
p_s  = zeros(2,4);

img_c = [floor(length(tmp(1,:,1))/2), ... %x
    floor(length(tmp(:,1))/2)]; % y

for i = 1:2
    th = locs(i);
    x1 = pix(i);
    
    xMax = x1-length(R(:,locs(i)))/2;
    
    % punto di massimo dell'asse di proiezione riportato nel SdR
    % dell'immagine.
    
    Rot = [cosd(-th) -sind(-th);
        sind(-th) cosd(-th)];
    
    
    p = Rot*[xMax;  0]; 
    
    p(1) = p(1)+img_c(1);
    p(2) = p(2)+img_c(2);
    
    [p1, p2] = rettaPassante(-th+90, [p(1); p(2)] , 0, length(bw3(1,:)));
    
    [p_r1, p_r2] = rettaPassante(-th, [p(1); p(2)] , 0, length(bw3(1,:)));
    
    p_s(:,i*2-1) = p1;
    p_s(:,i*2) = p2;
    
    tmp = insertShape(tmp,'FilledCircle',[img_c(1) img_c(2) 5], 'Color','green');
    tmp = insertShape(tmp,'FilledCircle',[p(1) p(2) 5], 'Color','red');
    % todo: spostare disegni vicino calcolo
    % linea lungo il massimo dell'asse di proiezione
    tmp = insertShape(tmp,'Line',[p1(1) p1(2)... %x0 y0
        p2(1) p2(2)],... %x1 y1
        'LineWidth',2,'Color','blue');
    % asse di proiezione di Radon
    tmp = insertShape(tmp,'Line',[p_r1(1) p_r1(2)... %x0 y0
        p_r2(1) p_r2(2)],... %x1 y1
        'LineWidth',1,'Color','black');
end

[int_px, int_py] = intersect(p_s(:,1),p_s(:,2),p_s(:,3),p_s(:,4));

tmp = insertShape(tmp,'FilledCircle',[int_px int_py 5], 'Color','yellow');

th_b = locs(1)+(locs(2)-locs(1))/2;

[p_b1, p_b2] = rettaPassante(-th_b+90, [int_px; int_py], 0, length(bw3(1,:)));

tmp = insertShape(tmp,'Line',[p_b1(1) p_b1(2)... %x0 y0
        p_b2(1) p_b2(2)],... %x1 y1
        'LineWidth',2,'Color','magenta');

figure(22)
clf
imshow(tmp);

fprintf("Centro in [X,Y] = [%.2f, %.2f]\nOrientamento %.2f°\n", int_px, int_py, th_b)

% una retta si definisce come y-yp = m*(x-xp), m = tan(th)
%%

function [x, y] = intersect(p1r1, p2r1, p1r2, p2r2)

% " Coefficente angolare"
% y2-y1 / x2-x1

m1 = (p2r1(2)-p1r1(2))/(p2r1(1)-p1r1(1));
m2 = (p2r2(2)-p1r2(2))/(p2r2(1)-p1r2(1));

% "Calcolo della X di intersezione"
%x = (m2*x2-y2-x1+y1)/(m2-m1)
x = (m2*p1r2(1)-p1r2(2)-p1r1(1)+p1r1(2))/(m2-m1);

y = m1*(x-p1r1(1))+p1r1(2);

end

function [p1, p2] = rettaPassante(th, p, x1 ,x2)

% una retta si definisce come y-yp = m*(x-xp), m = tan(th)

p1 = zeros(2,1);
p2 = zeros(2,1);

m = tand(th);

p1(1) = x1;
p1(2) = m*(x1-p(1))+p(2);


p2(1) = x2;
p2(2) = m*(x2-p(1))+p(2);



end





