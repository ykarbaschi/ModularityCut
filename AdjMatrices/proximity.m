function proximity(Id, data, windowSize, typeOfIndex)

weight = zeros(2, size(data{1,1},2));
for i = 1 : size(data ,2)
    edge = data{1, i}(Id,:);
    for j = 1 : size(edge ,2)
        if (edge(1, j) ~= 0)
            weight(1, j) = weight(1, j) + edge(1,j);
            weight(2, j) = weight(2, j) + windowSize;
        end
    end
end

figure;
hold on;
for i =1 : size(weight, 2)
    if weight(2, i) ~= 0
        plot(weight(2,i), weight(1,i), 'Ob');
        if (typeOfIndex == 0)
            text(weight(2,i), weight(1,i), int2str(i-1));
        else
            text(weight(2,i), weight(1,i), int2str(i));
        end
    end
end
xL = xlim;
line(xL, [0 0],'Color', 'k');

if (typeOfIndex == 0)
    title(sprintf('Weight for ID = %d, Time Window = %d frames', (Id -1), windowSize));
else
    title(sprintf('Similarity for ID = %d, Time Window = %d frames', Id, windowSize));
end
xlabel('Common Frames in Total');
ylabel('Accumulated Proxomity');
