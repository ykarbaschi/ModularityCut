function exportWeights(weights, nameOfFile)

%fileName = sprintf('%s_Edges.txt', nameOfFile);
fileName = sprintf('%s_Edges.csv', nameOfFile);
fileID = fopen(fileName,'w');
%fprintf(fileID, 'Source;Target;Type;Weight\n');
fprintf(fileID, '"from","to","weight"\n');
for i = 1 : size(weights, 1)
    %for j = i + 1 : size(weights, 1)
    for j = 1 : size(weights, 1)
        if(weights(i ,j) ~= 0)
        %line = sprintf('%d;%d;Undirected;%f\n', i, j, weights(i ,j));
        line = sprintf('"Node%02d","Node%02d",%f\n', i, j, weights(i ,j));
        fprintf(fileID,line);
        end
    end
end
fclose(fileID);

%fileName = sprintf('%s_Nodes.txt', nameOfFile);
fileName = sprintf('%s_Nodes.csv', nameOfFile);
fileID = fopen(fileName,'w');
%fprintf(fileID, 'ID;Label\n');
fprintf(fileID, '"name"\n');
for i = 1 : size(weights, 1)
        %line = sprintf('%d;%d\n', i, i);
        line = sprintf('"Node%02d"\n', i);
        fprintf(fileID,line);
end
fclose(fileID);