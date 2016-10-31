clear ;
clc;

ReadS2L1;

startingID = 1;
numOfIDs = 19;
figure;
AllPosition = [];
IDs = [];
for ii = startingID : numOfIDs
   
   list = S2L1(ismember(S2L1(:,2),ii),:);
   positions = list(1:end, 3:4);
   id = ii * ones(size(positions, 1), 1);
   
   AllPosition = vertcat(AllPosition, positions);
   IDs = vertcat(IDs, id);
end

gscatter(AllPosition(:,1), AllPosition(:,2), IDs);
set(gca,'XAxisLocation','top','YAxisLocation','left','ydir','reverse');
set(gca,'Color',[0 0 0]);
lg = gca;
lg.Legend.ItemHitFcn = @hitcallback_ex1;