function  [accumWeight, normAccumWeight] = normProxByDivision(data, windowSize, typeOfIndex)
% for each edge we haev multiple windows. we sum all positive values of
% each edge and sum absolute value of negative vales seperately. then divide the positive
% summed over negative summed +1 .... positivesummed / (AbsOfNegativeSum + 1)
sumOfPositiveEdges = zeros(size(data{1,1},1), size(data{1,1},2));
sumOfNegativeEdges = zeros(size(data{1,1},1), size(data{1,1},2));
sizeOfAdjMatrix = size(data{1,1},1);
for i = 1 : size(data ,2)
    for j = 1: sizeOfAdjMatrix
        for k = 1: sizeOfAdjMatrix
            if (data{1, i}(j, k) > 0)
                sumOfPositiveEdges(j, k) = data{1, i}(j, k) + sumOfPositiveEdges(j, k);
            else
                sumOfNegativeEdges(j, k) =+ data{1, i}(j, k) + sumOfNegativeEdges(j, k);
            end
        end
    end
end

for j = 1: sizeOfAdjMatrix
    for k = 1: sizeOfAdjMatrix
        if (sumOfNegativeEdges(j, k) ~= 0)
            sumOfNegativeEdges(j, k) = 1 + (-1 * sumOfNegativeEdges(j, k));
        end
    end
end

accumWeight = zeros(size(data{1,1},1), size(data{1,1},2));
for j = 1: sizeOfAdjMatrix
    for k = 1: sizeOfAdjMatrix
        if (sumOfNegativeEdges(j, k) ~= 0)
            accumWeight(j, k) = sumOfPositiveEdges(j ,k)/ sumOfNegativeEdges(j ,k);
        end
    end
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

normAccumWeight = (accumWeight - Min) / (Max - Min);

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