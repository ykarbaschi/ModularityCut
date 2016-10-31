function  accumWeight = normProxAll(data, windowSize, typeOfIndex)

accumWeight = zeros(size(data{1,1},1), size(data{1,1},2));
for i = 1 : size(data ,2)
    accumWeight = (data{1, i} / windowSize) + accumWeight;
end

figure;
hold on;
xlabels = [];
ylabels = [];
for i =1 : size(accumWeight, 1)
    for j = i +1 : size(accumWeight, 1)
        if accumWeight(i, j) ~= 0
            ylabels = [ylabels ; accumWeight(i, j)];
            
            if(typeOfIndex == 0)
                xlabels = [xlabels; {sprintf('%d-%d', i-1 ,j-1)}];
            else
                xlabels = [xlabels; {sprintf('%d-%d', i ,j)}];
            end
        end
    end
end

Min = min(min(accumWeight));
Max = max(max(accumWeight));

ylabelsNorm = [];
for i =1 : size(accumWeight, 1)
    for j = i +1 : size(accumWeight, 1)
        if accumWeight(i, j) ~= 0
            ylabelsNorm = [ylabelsNorm ; (accumWeight(i, j) - Min)/(Max - Min)];
        end
    end
end

%h =bar(horzcat(ylabels, ylabelsNorm));
%h = bar(ylabels);
h = bar(ylabelsNorm);

% colormap(summer(2));
% grid on
% l = cell(1,2);
% l{1}='NON'; l{2}='Normalized';
% legend(h,l);

xt = [1:size(ylabels,1)]';
yt = ylabelsNorm(:,1);
text(xt, yt, xlabels, 'rotation', 45, 'Color', 'r');

%title(sprintf('Edge Weights, Window Size = %d', windowSize));
title(sprintf('Normalized Edge Weights, Window Size = %d', windowSize));
xlabel('Number Of Edges (labels show participating nodes)');
ylabel('Normalized Proxomity');