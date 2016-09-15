clear ;
clc;

ReadMOT1604;    

workingDir = pwd;
mkdir(workingDir,'images');
% Video start from frame 0

imagefiles = dir('img1\*.jpg');
nfiles = length(imagefiles);    % Number of files found

for ii=1:1050
   currentfilename = imagefiles(ii).name;
   currentimage = imread(fullfile(workingDir,'img1',currentfilename));
   
   list = MOT1604(ismember(MOT1604(:,1),ii),:);
   positions = list(1:end, 3:4);
   values = list(1:end, 2)';
   
   if(~isempty(list))
       % if have 10000 frame two next lin should be %05d 
        rendered = insertText(currentimage, positions, values);
        filename = [sprintf('%06d',ii) '.jpg'];
        fullname = fullfile(workingDir,'images',filename);
        imwrite(rendered,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
   else
       filename = [sprintf('%06d',ii) '.jpg'];
       fullname = fullfile(workingDir,'images',filename);
       imwrite(currentimage,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
   end
end

imageNames = dir(fullfile(workingDir,'images','*.jpg'));
imageNames = {imageNames.name}';

outputVideo = VideoWriter(fullfile(workingDir,'MOT1604.avi'));
outputVideo.FrameRate = 15;
open(outputVideo);

%for ii = 1:2000
for ii = 1:length(imageNames)
   img = imread(fullfile(workingDir,'images',imageNames{ii}));
   writeVideo(outputVideo,img)
end

close(outputVideo);