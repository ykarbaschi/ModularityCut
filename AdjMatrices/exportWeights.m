function exportWeights(weights, nameOfFile)

fileName = sprintf('%s_Edges.txt', nameOfFile);
fileID = fopen(fileName,'w');
fprintf(fileID, 'Source;Target;Type;Weight\n');
for i = 1 : size(weights, 1)
    for j = i + 1 : size(weights, 1)
        if(weights(i ,j) ~= 0)
        line = sprintf('%d;%d;Undirected;%f\n', i, j, weights(i ,j));
        fprintf(fileID,line);
        end
    end
end
fclose(fileID);

fileName = sprintf('%s_Nodes.txt', nameOfFile);
fileID = fopen(fileName,'w');
fprintf(fileID, 'ID;Label\n');
for i = 1 : size(weights, 1)
        line = sprintf('%d;%d\n', i, i);
        fprintf(fileID,line);
end
fclose(fileID);