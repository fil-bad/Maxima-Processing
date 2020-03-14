clc
clear variables
close all
%% Open Image & set on greyscale & BW

imgData = imread('foto1.jpg');
figure(1)
imshow(imgData); % the same picture above



% scala di grigi pesata, tenendo conto della sensibilità maggiore al verde
% dell'occhio umano
img_gs = rgb2gray(imgData); 
figure(2)
imshow(img_gs)

% immagine b/w, con threshold di Otsu 
% (ne sceglie uno buono secondo sue metriche)
img_bw = imbinarize(img_gs);
figure(3)
imshow(img_bw)

%numero di pixel dell'immagine
img_size = size(img_bw);
img_size = img_size(1)*img_size(2);

%% Fill internal holes below a certain scale

% remove all the connected objects smaller than a tenth of the image;
bw2 = bwareaopen(img_bw, floor(0.1*img_size));
figure(4)
imshow(bw2)

% fill all the holes smaller than 35 pixel
bw3 = ~bwareaopen(~bw2, 35);
figure(5)
imshow(bw3)

%% Area & Perimeter calc

area_np = bwarea(bw3);

perim_np = bwperim(bw3); % ritorna un'immagine del perimetro

perim_val = bwarea(perim_np); % calcolo i pixel bianchi del perimetro

figure(6)
imshow(perim_np)

%% Dilation-erosion approach

r = 4; % radius
n = 10; % approximating circle with polygon of n edges

SE = strel('disk',r); % maschera di punti circolari

eroded = imerode(bw3, SE); % erodiamo l'immagine

figure(7)
imshow(eroded)

dilated = imdilate(bw3, SE); % dilatiamola

figure(8)
imshow(dilated)

perimeter = dilated-eroded; % facciamo la sottrazione
figure(9)
imshow(perimeter)

figure(10) % differenze nel calcolo del perimetro
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

%% Valore della trasformata a diverse angolazioni

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
    [vals(i), maxes(i)] = max(R(:,i)); % prendo una colonna alla volta
end

% ampl valore massimo per l'angolo, locs angolo del picco
[ampl, locs] = findpeaks(vals,'MinPeakProminence',0.01*max(R(:)));

% per fare in modo, successivamente, di avere sempre la bisettrice scelta 
% con lo stesso criterio (in ordine crescente di valore)
[b, ind] = sort(ampl);
for i = 1:length(locs)
b(i) = locs(ind(i));
end
locs = b;

pix = zeros(1,length(locs));

for i = 1:length(locs) % x' pixel corrispondente al massimo
    pix(i) = maxes(locs(i));
end

figure(13)
title("Max value for each angle")
plot(0:179,vals, 'r', locs, ampl, 'b.', 'MarkerSize', 15)

%% Inverse of Radon

figure(20)
clf

iR = 0;

for i = 1:length(locs) 
    pause(1);
    iR = iR + iradon([R(:,locs(i)) R(:, locs(i))], [locs(i) locs(i)])/2;
    imshow(iR)
end

%% Calcolo di baricentro e orientamento

tmp = imgData;
p_s  = zeros(2,4);
%centro dell'immagine
img_c = [floor(length(tmp(1,:,1))/2), ... %x
    floor(length(tmp(:,1))/2)]; % y

% inseriamo il centro dell'immagine
tmp = insertShape(tmp,'FilledCircle',[img_c(1) img_c(2) 5], 'Color','green');

for i = 1:2
    th = locs(i);
    x1 = pix(i);
    
    xMax = x1-length(R(:,locs(i)))/2; % coordinata in R1 relativa al centro
    % dell'immagine (attorno alla quale ruoteremo)
    
    % punto di massimo dell'asse di proiezione riportato nel SdR
    % dell'immagine R0.
    
    Rot = [cosd(-th) -sind(-th);
        sind(-th) cosd(-th)];
 
    p = Rot*[xMax;  0]; % riporto la posizione in R0
    % la sposto realitvamente al centro
    p(1) = p(1)+img_c(1);
    p(2) = p(2)+img_c(2);
    % print della p0 trovata
    tmp = insertShape(tmp,'FilledCircle',[p(1) p(2) 5], 'Color','red');
    
    % retta passante per la p0, linea lungo il massimo dell'asse di proiezione
    [p1, p2] = rettaPassante(-th+90, [p(1); p(2)] , 0, length(bw3(1,:)));
    tmp = insertShape(tmp,'Line',[p1(1) p1(2)... %x0 y0
        p2(1) p2(2)],... %x1 y1
        'LineWidth',2,'Color','blue');
    
    % asse di proiezione di Radon
    [p_r1, p_r2] = rettaPassante(-th, [p(1); p(2)] , 0, length(bw3(1,:)));
    tmp = insertShape(tmp,'Line',[p_r1(1) p_r1(2)... %x0 y0
        p_r2(1) p_r2(2)],... %x1 y1
        'LineWidth',1,'Color','black');
      
    % inserisco entrambi i punti per calcolo della bisettrice e dell'angolo
    % di orientamento
    p_s(:,i*2-1) = p1;
    p_s(:,i*2) = p2;    
end

% calcolo l'intersezione delle due rette
[int_px, int_py] = intersect(p_s(:,1),p_s(:,2),p_s(:,3),p_s(:,4));
% disegno punto d'intersezione
tmp = insertShape(tmp,'FilledCircle',[int_px int_py 5], 'Color','yellow');

th_b = locs(1)+(locs(2)-locs(1))/2; % angolo di orientamento
% traccio la bisettrice
[p_b1, p_b2] = rettaPassante(-th_b+90, [int_px; int_py], 0, length(bw3(1,:)));

tmp = insertShape(tmp,'Line',[p_b1(1) p_b1(2)... %x0 y0
        p_b2(1) p_b2(2)],... %x1 y1
        'LineWidth',2,'Color','magenta');

%disegno la figura completa
figure(22)
clf
imshow(tmp);

%debug su command window
fprintf("Centro in [X,Y] = [%.2f, %.2f]\nOrientamento %.2f°\n", int_px, int_py, th_b)

%% Funzioni d'appoggio

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


