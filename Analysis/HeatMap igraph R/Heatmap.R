library(igraph)
library(dplyr)
library(ggplot2)

#close active windows
graphics.off()

# clear screen and remove varialbes
cat("\014")
rm(list=ls(all=TRUE))

setwd("C:\\Users\\Yaser\\Documents\\IdeaProjects\\ModularityCut\\Analysis\\Heatmap igraph R")

# Read in CSV files with edge and node attributes
#original_edgelist <- read.csv("goltzius_edges.csv", stringsAsFactors = FALSE)
#original_nodelist <- read.csv("goltzius_nodes.csv", stringsAsFactors = FALSE)

original_edgelist <- read.csv("PETS20_Edges.csv", stringsAsFactors = FALSE)
original_nodelist <- read.csv("PETS20_Nodes.csv", stringsAsFactors = FALSE)

# Create iGraph object
graph <- graph.data.frame(original_edgelist, directed = TRUE, vertices = original_nodelist)

# Calculate various network properties, adding them as attributes
# to each node/vertex
# Community with Max Modularity
V(graph)$comm <- membership(optimal.community(graph))


# Diff Comm Detection Algorithm
graph <- simplify(graph, remove.multiple = TRUE, remove.loops = TRUE,
         edge.attr.comb = "ignore")
V(graph)$comm <- membership(cluster_fast_greedy(graph, merges = TRUE, modularity = TRUE,
                    membership = TRUE, weights = E(graph)$weight))

V(graph)$comm <- membership(cluster_walktrap(graph, weights = E(graph)$weight, steps = 4,
                 merges = TRUE, modularity = FALSE, membership = TRUE))

V(graph)$comm <- membership(cluster_louvain(graph))

V(graph)$comm <- membership(cluster_label_prop(graph, weights = E(graph)$weight, initial = NULL, fixed = NULL))

V(graph)$comm <- membership(cluster_infomap(graph, e.weights = E(graph)$weight, v.weights = NULL, nb.trials = 10,
                modularity = TRUE))

V(graph)$comm <- membership(cluster_edge_betweenness(graph, weights = E(graph)$weight, directed = TRUE,
                         edge.betweenness = TRUE, merges = TRUE, bridges = TRUE,
                         modularity = TRUE, membership = TRUE))
#Other Graph Metrics
V(graph)$degree <- degree(graph)
V(graph)$closeness <- centralization.closeness(graph)$res
V(graph)$betweenness <- centralization.betweenness(graph)$res
V(graph)$eigen <- centralization.evcent(graph)$vector

# Re-generate dataframes for both nodes and edges, now containing
# calculated network attributes
node_list <- get.data.frame(graph, what = "vertices")
edge_list <- get.data.frame(graph, what = "edges")
# Determine a community for each edge. If two nodes belong to the
# same community, label the edge with that community. If not,
# the edge community value is 'NA'
edge_list <- get.data.frame(graph, what = "edges") %>%
  inner_join(node_list %>% select(name, comm), by = c("from" = "name")) %>%
  inner_join(node_list %>% select(name, comm), by = c("to" = "name")) %>%
  mutate(group = ifelse(comm.x == comm.y, comm.x, NA) %>% factor())

# Create a character vector containing every node name
all_nodes <- sort(node_list$name)

# Adjust the 'to' and 'from' factor levels so they are equal
# to this complete list of node names
plot_data <- edge_list %>% mutate(
  to = factor(to, levels = all_nodes),
  from = factor(from, levels = all_nodes))


name_order <- (node_list %>% arrange(comm))$name
# Reorder edge_list "from" and "to" factor levels based on
# this new name_order
plot_data <- edge_list %>% mutate(
  to = factor(to, levels = name_order),
  from = factor(from, levels = name_order))

# Order based on eigen value
eigen_order <- (node_list %>% arrange(eigen))$name
plot_data <- edge_list %>% mutate(
  to = factor(to, levels = eigen_order),
  from = factor(from, levels = eigen_order))

closeness_order <- (node_list %>% arrange(closeness))$name
plot_data <- edge_list %>% mutate(
  to = factor(to, levels = closeness_order),
  from = factor(from, levels = closeness_order))

degree_order <- (node_list %>% arrange(degree))$name
plot_data <- edge_list %>% mutate(
  to = factor(to, levels = degree_order),
  from = factor(from, levels = degree_order))

betweenness_order <- (node_list %>% arrange(betweenness))$name
plot_data <- edge_list %>% mutate(
  to = factor(to, levels = betweenness_order),
  from = factor(from, levels = betweenness_order))

# Create the adjacency matrix plot
#ggplot(plot_data, aes(x = from, y = to, fill = weight))+
ggplot(plot_data, aes(x = from, y = to, fill = group))+
#scale_fill_gradient(low = "grey", high = "red")+
  geom_raster()+
  theme_bw() +
  #   # Because we need the x and y axis to display every node,
  #   # not just the nodes that have connections to each other,
  #   # make sure that ggplot does not drop unused factor levels
  scale_x_discrete(drop = FALSE) +
  scale_y_discrete(drop = FALSE) +
  theme(
    # Rotate the x-axis lables so they are legible
    axis.text.x = element_text(angle = 270, hjust = 0),
    # Force the plot into a square aspect ratio
    aspect.ratio = 1,
    # Hide the legend (optional)
    legend.position = "right")+
  ggtitle("From Highest to Lowest Betweenness Value")