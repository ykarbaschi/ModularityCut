clear ;
clc;

ReadCam1Annotation;

startingID = 0;
numOfIDs = 76;
figure;
AllPosition = [];
IDs = [];
for ii = startingID : numOfIDs
   
   list = Cam1(ismember(Cam1(:,3),ii),:);
   positions = list(1:end, 4:5);
   id = ii * ones(size(positions, 1), 1);
   
   AllPosition = vertcat(AllPosition, positions);
   IDs = vertcat(IDs, id);
%    if(~isempty(list))
%        % if have 10000 frame two next lin should be %05d 
%         rendered = insertText(currentimage, positions, values);
%         filename = [sprintf('%06d',ii) '.jpg'];
%         fullname = fullfile(workingDir,'images',filename);
%         imwrite(rendered,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
%    else
%        filename = [sprintf('%06d',ii) '.jpg'];
%        fullname = fullfile(workingDir,'images',filename);
%        imwrite(currentimage,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
%    end
end

gscatter(AllPosition(:,1), AllPosition(:,2), IDs);
set(gca,'XAxisLocation','top','YAxisLocation','left','ydir','reverse');
set(gca,'Color',[0 0 0]);
lg = gca;
lg.Legend.ItemHitFcn = @hitcallback_ex1;