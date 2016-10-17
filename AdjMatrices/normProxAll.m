function  accumWeight = normProxAll(data, windowSize, typeOfIndex)

accumWeight = zeros(size(data{1,1},1), size(data{1,1},2));
for i = 1 : size(data ,2)
    accumWeight = (data{1, i} / windowSize) + accumWeight;
end

figure;
hold on;
xlabels = [];
ylabels = []
for i =1 : size(accumWeight, 1)
    for j = i +1 : size(accumWeight, 1)
        if accumWeight(i, j) ~= 0
            ylabels = [ylabels ; accumWeight(i, j)];
            xlabels = [xlabels; {sprintf('%d-%d', i ,j)}];
            
        end
    end
end

bar(ylabels);
%set(gca, 'xticklabel', xlabels);

xt = [1:size(ylabels,1)]';
yt = ylabels(:,1);
text(xt, yt, xlabels, 'rotation', 90, 'Color', 'r');

% if (typeOfIndex == 0)
%     title(sprintf('Weight for ID = %d, Time Window = %d frames', (Id -1), windowSize));
% else
%     title(sprintf('Similarity for ID = %d, Time Window = %d frames', Id, windowSize));
% end
% xlabel('IDs');
% ylabel('Normalized Proxomity');
