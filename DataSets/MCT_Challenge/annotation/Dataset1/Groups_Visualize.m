clear ;
clc;

ReadCam1Annotation;
ReadEdgeData;
ReadGroupingData;
workingDir = pwd;
shuttleVideo = VideoReader('Cam1.avi');

%delete edges with 0 weight
indices = Edges(:, 3) == 0;
Edges(indices, :) = [];

%delete edges with self
indices = find(Edges(:, 1) == Edges(:, 2));
Edges(indices, :) = [];

maxWeight = max(Edges(:, 3));

%for n=1:2000
ii = 0;
while hasFrame(shuttleVideo)
    personsInThisFrame = Cam1(ismember(Cam1(:,2),ii),:);
    img = readFrame(shuttleVideo);
    
    if(~isempty(personsInThisFrame))
        % if have 10000 frame two next lin should be %05d
        for i = 1 : size(personsInThisFrame, 1)
            edgeMemeberColumn1 = find(personsInThisFrame(i, 3) == Edges(:, 1));
            edgeMemeberColumn2 = find(personsInThisFrame(i, 3) == Edges(:, 2));
            
            edgeIndices = vertcat(edgeMemeberColumn1, edgeMemeberColumn2);
            
            edge = Edges(edgeIndices, :);
            
            for j = 1 : size(edge, 1)
                P1 = find(personsInThisFrame(:, 3) == edge(j , 1));
                X1 = personsInThisFrame(P1, 4);
                Y1 = personsInThisFrame(P1, 5);
                
                P2 = find(personsInThisFrame(:, 3) == edge(j , 2));
                X2 = personsInThisFrame(P2, 4);
                Y2 = personsInThisFrame(P2, 5);
                
                if (~isempty(P1) && ~isempty(P2))
                    weight = edge(j ,3);
                    bin = maxWeight / 5.0;
                    if(weight < bin)lineWidth = 1;
                    elseif ( bin < weight && weight < 2 * bin) lineWidth = 1; opacity = 0.1;
                    elseif ( 2 * bin < weight && weight < 3 * bin) lineWidth = 3;opacity = 0.5;
                    elseif ( 3 * bin < weight && weight < 4 * bin) lineWidth = 7;opacity = 1;
                    else lineWidth = 15;opacity = 1;
                    end
                    img = insertShape(img, 'Line',[X1 Y1 X2 Y2], 'LineWidth', lineWidth, 'Opacity', opacity);
                end
            end
            
            filename = [sprintf('%06d',ii) '.jpg'];
            fullname = fullfile(workingDir,'images',filename);
            imwrite(img,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
        end
        
        
    else
        filename = [sprintf('%06d',ii) '.jpg'];
        fullname = fullfile(workingDir,'images',filename);
        imwrite(img,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
    end
    
    ii = ii + 1;
    
end


imageNames = dir(fullfile(workingDir,'images','*.jpg'));
imageNames = {imageNames.name}';

outputVideo = VideoWriter(fullfile(workingDir,'groups.avi'));
outputVideo.FrameRate = shuttleVideo.FrameRate;
open(outputVideo);

%for ii = 1:2000
for ii = 1:length(imageNames)
   img = imread(fullfile(workingDir,'images',imageNames{ii}));
   writeVideo(outputVideo,img)
end

close(outputVideo);