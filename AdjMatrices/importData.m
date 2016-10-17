function data = importData(endColumn)
files = getAllFiles(uigetdir);
data=[];
for i = 1 : size(files, 1)
    data{i} = importWindowInfo(files{i, 1}, endColumn);
end

end
    