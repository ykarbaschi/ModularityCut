function summedWeights = plotAccWeitghs(data)

summedWeights = sum(data, 2);

figure;
bar(summedWeights);

xlabels = [];
for j = 1 : size(summedWeights, 1)
    xlabels = [xlabels; {sprintf('%d', j)}];
end

xt = [1:size(summedWeights,1)]';
yt = summedWeights(:,1);
text(xt, yt, xlabels, 'rotation', 45, 'Color', 'r');

title(sprintf('MCT Dataset, Window = 20 frames'));
xlabel('Node ID');
ylabel('Sum of All Edge Weight of Each Node');