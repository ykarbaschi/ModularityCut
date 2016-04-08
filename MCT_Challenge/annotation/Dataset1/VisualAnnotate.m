clear ;
clc;

ReadCam1Annotation;    

workingDir = pwd;
mkdir(workingDir,'images');
shuttleVideo = VideoReader('Cam1.avi');
% Video start from frame 0
ii = 0;

%for n=1:2000
while hasFrame(shuttleVideo)
   list = Cam1(ismember(Cam1(:,2),ii),:);
   img = readFrame(shuttleVideo);
   positions = list(1:end, 4:5);
   values = list(1:end, 3)';
   
   if(~isempty(list))
       % if have 10000 frame two next lin should be %05d 
        rendered = insertText(img, positions, values);
        filename = [sprintf('%06d',ii) '.jpg'];
        fullname = fullfile(workingDir,'images',filename);
        imwrite(rendered,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
   else
       filename = [sprintf('%06d',ii) '.jpg'];
        fullname = fullfile(workingDir,'images',filename);
        imwrite(img,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
   end
   ii = ii+1;
end

imageNames = dir(fullfile(workingDir,'images','*.jpg'));
imageNames = {imageNames.name}';

outputVideo = VideoWriter(fullfile(workingDir,'shuttle_out.avi'));
outputVideo.FrameRate = shuttleVideo.FrameRate;
open(outputVideo);

%for ii = 1:2000
for ii = 1:length(imageNames)
   img = imread(fullfile(workingDir,'images',imageNames{ii}));
   writeVideo(outputVideo,img)
end

close(outputVideo);