function normProx(Id, data, windowSize, typeOfIndex)

weight = zeros(2, size(data{1,1},2));
for i = 1 : size(data ,2)
    edge = data{1, i}(Id,:);
    for j = 1 : size(edge ,2)
        if (edge(1, j) ~= 0)
            %weight(1, j) = weight(1, j) + edge(1,j));
            weight(1, j) = weight(1, j) + (edge(1,j) / windowSize);
            weight(2, j) = weight(2, j) + windowSize;
        end
    end
end

figure;
hold on;
for i =1 : size(weight, 2)
    if weight(2, i) ~= 0
        if (typeOfIndex == 0)
            %             plot( i-1 , (weight(1, i) / weight(2, i)), 'Ob');
            %             text( i-1, (weight(1, i) / weight(2, i)), int2str(i-1));
            plot( i-1 , weight(1, i), 'Ob');
            text( i-1, weight(1, i), int2str(i-1));
        else
            %             plot( i , (weight(1, i) / weight(2, i)), 'Ob');
            %             text( i , (weight(1, i) / weight(2, i)), int2str(i));
            plot( i , weight(1, i) , 'Ob');
            text( i, weight(1, i), int2str(i));
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
xlabel('IDs');
ylabel('Normalized Proxomity');
