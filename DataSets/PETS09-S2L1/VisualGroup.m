clear ;
clc;

ReadS2L1; 
ReadEdgeData;
ReadGroupingData;
workingDir = pwd;

mkdir(workingDir,'images');

imagefiles = dir('view_001\*.jpg');
nfiles = length(imagefiles);    % Number of files found

%delete edges with 0 weight
indices = Edges(:, 3) == 0;
Edges(indices, :) = [];

%delete edges with self
indices = find(Edges(:, 1) == Edges(:, 2));
Edges(indices, :) = [];

maxWeight = max(Edges(:, 3));

for ii=1:795
    
    currentfilename = imagefiles(ii).name;
    currentimage = imread(fullfile(workingDir,'view_001',currentfilename));
    %while hasFrame(shuttleVideo)
    personsInThisFrame = S2L1(ismember(S2L1(:,1),ii),:);
    
    if(~isempty(personsInThisFrame))
        % if have 10000 frame two next lin should be %05d
        for i = 1 : size(personsInThisFrame, 1)
            edgeMemeberColumn1 = find(personsInThisFrame(i, 2) - 1 == Edges(:, 1));
            edgeMemeberColumn2 = find(personsInThisFrame(i, 2) - 1 == Edges(:, 2));
            
            edgeIndices = vertcat(edgeMemeberColumn1, edgeMemeberColumn2);
            
            edge = Edges(edgeIndices, :);
            
            for j = 1 : size(edge, 1)
                P1 = find(personsInThisFrame(:, 2) == edge(j , 1) + 1);
                X1 = personsInThisFrame(P1, 3);
                Y1 = personsInThisFrame(P1, 4);
                
                P2 = find(personsInThisFrame(:, 2) == edge(j , 2) + 1);
                X2 = personsInThisFrame(P2, 3);
                Y2 = personsInThisFrame(P2, 4);
                
                if (~isempty(P1) && ~isempty(P2))
                    weight = edge(j ,3);
                    bin = maxWeight / 5.0;
                    if(weight < bin)lineWidth = 1;
                    elseif ( bin < weight && weight < 2 * bin) lineWidth = 1; opacity = 0.1;
                    elseif ( 2 * bin < weight && weight < 3 * bin) lineWidth = 3;opacity = 0.5;
                    elseif ( 3 * bin < weight && weight < 4 * bin) lineWidth = 7;opacity = 1;
                    else lineWidth = 15;opacity = 1;
                    end
                    currentimage = insertShape(currentimage, 'Line',[X1 Y1 X2 Y2], 'LineWidth', lineWidth, 'Opacity', opacity);
                end
            end
            
            filename = [sprintf('%06d',ii) '.jpg'];
            fullname = fullfile(workingDir,'images',filename);
            imwrite(currentimage,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
        end
        
        
    else
        filename = [sprintf('%06d',ii) '.jpg'];
        fullname = fullfile(workingDir,'images',filename);
        imwrite(img,fullname)  ;  % Write out to a JPEG file (img1.jpg, img2.jpg, etc.)
    end
end


imageNames = dir(fullfile(workingDir,'images','*.jpg'));
imageNames = {imageNames.name}';

outputVideo = VideoWriter(fullfile(workingDir,'Groups_PETS.avi'));
outputVideo.FrameRate = 15;
open(outputVideo);

%for ii = 1:2000
for ii = 1:length(imageNames)
   img = imread(fullfile(workingDir,'images',imageNames{ii}));
   writeVideo(outputVideo,img)
end

close(outputVideo);