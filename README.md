# WhiteChapel

### Logic to mimic the game
1. Take the leaves Hashset and consider each leaf at a time
2. For each leaf we have to find the adjacent vertices in the gameboard.circleGraph() tree
3. Once we have all the vertices we need to create an edge from the leaf to each of the vertex.
4. Thus we update our move tree.
5. Then we call the setLeaves method to update the Leaves Hashset. We now add the vertices (leaf.add(v)) we just found as the new leaves in the leaves hashset. 

And we do the above for each of the leaves we have in the Leaves Set.

#### ProcessDetectiveMove method. 

Rules : 
1. If the answer to detectives question is yes then the  move tree remains the same

2. If the answer is no then we have to remove that vertex and node from the tree. 

So suppose detective asks about node 27. And Jack says no. Then we remove all the 27s and the edges connected to 27 from the tree. 

3. Same with arrest. If arrest result is yes then tree remains the same. Otherwise we remove that arrest node and its connected edges. 
