clc 
clear variables

%% Open Image & set on greyscale & BW

imgData = imread('foto1.jpg');

figure(1)
imshow(imgData); % the same picture above

img_gs = rgb2gray(imgData);

figure(2)
imshow(img_gs)

img_bw = imbinarize(img_gs, 0.2);

figure(3)
imshow(img_bw)

%% not processed Area & Perimeter 

area_np = bwarea(img_bw);

perim_np = bwperim(img_bw);



figure(4)
imshow(perim_np)

%%
r = 4; % radius
n = 10; % approximating circle with polygon of n edges

SE = strel('disk',r);

eroded = imerode(img_bw, SE);

figure(5)
imshow(eroded)

dilated = imdilate(img_bw, SE);

figure(6)
imshow(dilated)

perimeter = dilated-eroded;
figure(7)
imshow(perimeter)

%% Comparison after dilation-erosion mask

figure(8)
imshowpair(perim_np, perimeter, 'montage')

perim_np_value = bwarea(perim_np)
perimeter_val = bwarea(perimeter)


%%

bw2 = ~bwareaopen(~img_bw, 300);
figure(9)
imshow(bw2)

%%

bw3 = bwareaopen(bw2, 300);
figure(10)
imshow(bw3)

%% 

area_new = bwarea(bw3);

perim_new = bwperim(bw3);



figure(11)
imshow(perim_new)

figure(12)
imshowpair(perim_np, perim_new, 'montage')

perim_np_value = bwarea(perim_np)
perim_new_val = bwarea(perim_new)















