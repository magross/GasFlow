/*
 * StaticGraph.java
 * 
 */

package ds.graph;

/**
 * @TODO: adjNodes, inNodes, outNodes
 * @author Martin Gro� / Sebastian Schenker
 */
public class StaticGraph implements IdentifiableGraph {
    
    private int currentmaxnodeid;
    private int currentmaxedgeid;
    protected boolean directed;
    protected ArraySet<Edge> edges;
    protected HidingSetForThinFlow<Edge> visibleEdges;
    protected ArraySet<Node> nodes;
    
    protected transient HidingAdjacencySetForThinFlow[] adjacentEdges;
    protected transient HidingAdjacencySetForThinFlow[] incomingEdges;
    protected transient HidingAdjacencySetForThinFlow[] outgoingEdges;
    
    public StaticGraph(boolean directed) {
        this(directed,0,0);
        
    }
    
    public StaticGraph(boolean directed, int initialNodeCapacity, int initialEdgeCapacity) {
              
        this.directed = directed;
        edges = new ArraySet<Edge>(Edge.class, initialEdgeCapacity);
        visibleEdges = new HidingSetForThinFlow<Edge>(edges, initialEdgeCapacity);
        nodes = new ArraySet<Node>(Node.class, initialNodeCapacity);
        //visibleNodes = new HidingSet<Node>(nodes, initialNodeCapacity);
        
           
        adjacentEdges = new HidingAdjacencySetForThinFlow[initialNodeCapacity];
        if (directed) {
            incomingEdges = new HidingAdjacencySetForThinFlow[initialNodeCapacity];
            outgoingEdges = new HidingAdjacencySetForThinFlow[initialNodeCapacity];
        }
    }

    
    public Network getAsStaticNetwork() {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public IdentifiableCollection<Edge> getEdges(Node start, Node end) {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public boolean contains(Edge e) {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public boolean contains(Node n) {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public void printVisible() {
        visibleEdges.printVisible();
    }
    
    public void setCurrentMaxNodeId(int nodeid) {
        currentmaxnodeid = nodeid;
    }
    
    public void setCurrentMaxEdgeId(int edgeid) {
        currentmaxedgeid = edgeid;
    }
    
    /**
     * Gibt zur�ck, ob der Graph als gerichtet oder ungerichtet zu interpretieren ist. Laufzeit O(1).
     * @return <code>true</code>, wenn der Graph als gerichtet interpretiert werden soll, <code>false</code> anderenfalls.
     */ 
    public boolean isDirected() {
        return directed;
    }
    
    
    
    /**
     * Testet, ob der Graph der leere Graph ist. Enth�lt der Graph keine Knoten, wird <code>true</code> zur�ckgeben, 
     * anderenfalls <code>false</code>. Laufzeit O(1).
     * @return <code>true</code>, wenn der Graph keine Knoten (und damit auch keine Kanten) enth�lt, <code>false</code>
     * anderenfalls.
     */        
    public boolean empty() {
        return nodes.empty();
    }

    /**
     * Gibt alle Kanten des Graphen zur�ck (auch die versteckten). Laufzeit O(1).
     * @return eine nicht-�nderbare Menge aller Kanten des Graphen.
     */    
    public IdentifiableCollection<Edge> allEdges() {
        return edges;
    }

    /**
     * Gibt alle Knoten des Graphen zur�ck (auch die versteckten). Laufzeit O(1).
     * @return eine nicht-�nderbare Menge aller Knoten des Graphen.
     */        
    public IdentifiableCollection<Node> allNodes() {
        return nodes;
    }
    
    /**
     * Gibt alle sichtbaren Kanten des Graphen zur�ck. Laufzeit O(1).
     * @return eine nicht-�nderbare Menge aller sichtbaren Kanten des Graphen.
     */
    public IdentifiableCollection<Edge> edges() {
        return visibleEdges;
    }

    /**
     * Gibt alle sichtbaren Knoten des Graphen zur�ck. Laufzeit O(1).
     * @return eine nicht-�nderbare Menge aller sichtbaren Knoten des Graphen.
     */    
    public IdentifiableCollection<Node> nodes() {
        return nodes;
        //return visibleNodes;
    }
    
    /**
     * Gibt die Anzahl aller sichtbaren Kanten des Graphen zur�ck. Laufzeit O(1).
     * @return die Anzahl aller sichtbaren Kanten des Graphen.
     */    
    public int numberOfEdges() {
        return visibleEdges.numberOfVisibleElements();
    }

    /**
     * Gibt die Anzahl aller versteckten Kanten des Graphen zur�ck. Laufzeit O(1).
     * @return die Anzahl aller versteckten Kanten des Graphen.
     */    
    public int numberOfHiddenEdges() {
        return visibleEdges.size() - visibleEdges.numberOfVisibleElements();
    }
    
    /**
     * Gibt die Anzahl aller sichtbaren Knoten des Graphen zur�ck. Laufzeit O(1).
     * @return die Anzahl aller sichtbaren Knoten des Graphen.
     */    
    public int numberOfNodes() {
       // return visibleNodes.size()-visibleNodes.numberOfHiddenElements();
        return nodes.size();
        //return visibleNodes.numberOfVisibleElements();
    }    
    
    /**
     * Gibt die Anzahl aller versteckten Knoten des Graphen zur�ck. Laufzeit O(1).
     * @return die Anzahl aller versteckten Knoten des Graphen.
     */    
    /* public int numberOfHiddenNodes() {
        //return visibleNodes.numberOfHiddenElements();
        return visibleNodes.size() - visibleNodes.numberOfVisibleElements();
    } */
    
    /**
     * Gibt alle zum gegebenen Knoten sichtbaren adjazenten Kanten zur�ck. Im Falle von gerichteten Graphen werden sowohl
     * eingehende als auch ausgehende Kanten als adjazent betrachtet. Laufzeit O(1).
     * @param node der Knoten, zu dem die adjazenten Kanten ausgegeben werden sollen.
     * @return eine nicht-�nderbare Menge aller zu <code>node</code> sichtbaren adjazenten Kanten des Graphen.
     */ 
    public IdentifiableCollection<Edge> incidentEdges(Node node) {
        return adjacentEdges[node.id()];
    }

    /**
     * Gibt alle zum gegebenen Knoten sichtbaren eingehenden Kanten zur�ck. Im Falle von ungerichteten Graphen ist dies nicht
     * m�glich, daher wird in diesem Fall eine {@link GraphNotDirectedException} geworfen. Laufzeit O(1).
     * @param node der Knoten, zu dem die eingehenden Kanten ausgegeben werden sollen.
     * @return eine nicht-�nderbare Menge aller zu <code>node</code> sichtbaren eingehenden Kanten des Graphen.
     */     
    public IdentifiableCollection<Edge> incomingEdges(Node node) {
        if (!directed) throw new UnsupportedOperationException("GraphNotDirected");
        return incomingEdges[node.id()];
    }

    /**
     * Gibt alle zum gegebenen Knoten sichtbaren ausgehenden Kanten zur�ck. Im Falle von ungerichteten Graphen ist dies nicht
     * m�glich, daher wird in diesem Fall eine {@link GraphNotDirectedException} geworfen. Laufzeit O(1).
     * @param node der Knoten, zu dem die ausgehenden Kanten ausgegeben werden sollen.
     * @return eine nicht-�nderbare Menge aller zu <code>node</code> sichtbaren eingehenden Kanten des Graphen.
     */         
    public IdentifiableCollection<Edge> outgoingEdges(Node node) {
        if (!directed) throw new UnsupportedOperationException("GraphNotDirected");
        return outgoingEdges[node.id()];
    }
    //TODO
    public IdentifiableCollection<Node> adjacentNodes(Node node) {
        throw new UnsupportedOperationException();
    }
    //TODO
    public IdentifiableCollection<Node> predecessorNodes(Node node) {
        if (!directed) throw new UnsupportedOperationException("GraphNotDirected");
        throw new UnsupportedOperationException();
    }
    //TODO
    public IdentifiableCollection<Node> successorNodes(Node node) {
        if (!directed) throw new UnsupportedOperationException("GraphNotDirected");
        throw new UnsupportedOperationException();
    }

    /**
     * Gibt die Anzahl aller zum gegebenen Knoten sichtbaren adjazenten Kanten zur�ck. Im Falle von gerichteten Graphen 
     * werden sowohl eingehende als auch ausgehende Kanten als adjazent betrachtet. Laufzeit O(1).
     * @param node der Knoten, zu dem die Anzahl der adjazenten Kanten ausgegeben werden soll.
     * @return die Anzahl der zu <code>node</code> adjazenten Kanten, d.h. der Grad von <code>node</code>.
     */     
    public int degree(Node node) {
        return adjacentEdges[node.id()].size();
    }

    /**
     * Gibt die Anzahl aller zum gegebenen Knoten sichtbaren eingehenden Kanten zur�ck. Im Falle von ungerichteten Graphen 
     * ist dies nicht m�glich, daher wird in diesem Fall eine {@link GraphNotDirectedException} geworfen. Laufzeit O(1).
     * @param node der Knoten, zu dem die Anzahl der eingehenden Kanten ausgegeben werden soll.
     * @return die Anzahl der sichtbaren eingehenden Kanten.
     */         
    public int indegree(Node node) {
        if (!directed) throw new UnsupportedOperationException("GraphNotDirected");
        return incomingEdges[node.id()].size();
    }
    
    /**
     * Gibt die Anzahl aller zum gegebenen Knoten sichtbaren ausgehenden Kanten zur�ck. Im Falle von ungerichteten Graphen 
     * ist dies nicht m�glich, daher wird in diesem Fall eine {@link GraphNotDirectedException} geworfen. Laufzeit O(1).
     * @param node der Knoten, zu dem die Anzahl der ausgehenden Kanten ausgegeben werden sollen.
     * @return die Anzahl der sichtbaren ausgehenden Kanten.
     */             
    public int outdegree(Node node) {
        if (!directed) throw new UnsupportedOperationException("GraphNotDirected");
        return outgoingEdges[node.id()].size();
    }
    
    @Override
    public String deepToString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("({");
        for (Node node : nodes()) {
            buffer.append(node.id());
            buffer.append(", ");
        }
        buffer.delete(buffer.length()-2, buffer.length());
        buffer.append("}, {");
        for (Edge edge : edges()) {
            buffer.append(edge.nodesToString());
            buffer.append(", ");
        }
        buffer.delete(buffer.length()-2, buffer.length());        
        buffer.append("})");
        return buffer.toString();
    }
    
    /**
     * F�gt den angebenen Knoten dem Graphen hinzu, sofern dies m�glich ist. Da dies ein statischer Graph ist, h�ngt 
     * letzteres zum einen davon ab, dass noch Kapazit�t f�r einen neuen Knoten vorhanden ist 
     * ({@link getCapacity}, {@link setCapacity}). Zum anderen muss die ID des neuen Knotens um eins gr��er als die ID des
     * zuletzt eingef�gten Knoten sein (bzw. 0, wenn vorher noch kein Knoten eingef�gt wurde). Ist dies nicht der Fall, wird
     * eine {@link IllegalStateException} geworfen. Laufzeit O(1).
     * @param node der Knoten, der dem Graph hinzugef�gt werden soll.
     * @return den Knoten, der dem Graph hinzugef�gt wurde.
     */ 
    public void setNode(Node node) {
       /*if (node.id() != nodes.size()) {  //changed by Seba
            throw new IllegalStateException("");
        } else*/ if (nodes.size() == nodes.getCapacity()) {
            throw new IllegalStateException("");
        } else {
            //System.out.println("id = " + node.id());
            
            nodes.add(node);
            //System.out.println("NODES = " + nodes.toString());
            //visibleNodes.changeVisibility(node,true);
            
            adjacentEdges[node.id()] = new HidingAdjacencySetForThinFlow(visibleEdges);
            if (directed) {
                incomingEdges[node.id()] = new HidingAdjacencySetForThinFlow(visibleEdges);
                outgoingEdges[node.id()] = new HidingAdjacencySetForThinFlow(visibleEdges);
            }
           // return node;
        }
    }
    
    public void setNodes(Iterable<Node> nodes) {
        for (Node node : nodes) {
            //System.out.println("node = " + node.toString());
            setNode(node);
        }
    }
    
    public int getCurrentMaxNodeId() {
        return currentmaxnodeid;
    }
    
    private void incCurrentMaxNodeId() {
        ++currentmaxnodeid;
    }
    
    /**
     * Erzeugt einen neuen Knoten mit passender ID und f�gt ihm dem Graphen hinzu. Laufzeit O(1).
     * @return den erzeugten Knoten.
     */ 
    /*public Node createNode() {
        incCurrentMaxNodeId();
        return addNode(new Node(getCurrentMaxNodeId()));  //changed by Seba: new Node(nodes.size())
        
    }*/
    
    /**
     * Erzeugt die spezifizierte Anzahl Knoten. Laufzeit O(number).
     * @param number die Anzahl Knoten, die erzeugt werden soll.
     */ 
   /* public void createNodes(int number) {
        for (int i = 0; i < number; i++) {
            createNode();
        }
    }*/
    
    /*public void createAllNodes() {
        createNodes(getNodeCapacity()-numberOfNodes()-numberOfHiddenNodes());
    }*/
    
    /**
     * Gibt zur�ck, ob der angegebene Knoten versteckt ist. Laufzeit O(1).
     * @param node der Knoten, f�r den die Sichtbarkeit �berpr�ft werden soll.
     * @return <code>true</code>, wenn der Knoten <code>node</code> versteckt ist, <code>false</code> anderenfalls.
     */ 
   /* public boolean isVisible(Node node) {
        return visibleNodes.isVisible(node);
    } */

    /**
     * �ndert die Sichtbarkeit des angegebenen Knotens auf den angegebenen Zustand. Laufzeit O(1).
     * @param node der Knoten, f�r den die Sichtbarkeit ge�ndert werden soll.
     * @param hidden der neue Sichtbarkeits-Status: <code>true</code> versteckt den Knoten, <code>false</code> macht ihn 
     * sichtbar.
     */     
   /* public void changeVisibility(Node node, boolean hidden) {
        visibleNodes.changeVisibility(node, hidden);
    } */
    
    /**
     * F�gt die angebene Kante dem Graphen hinzu, sofern dies m�glich ist. Da dies ein statischer Graph ist, h�ngt 
     * letzteres zum einen davon ab, dass noch Kapazit�t f�r eine neuen Kante vorhanden ist 
     * ({@link getCapacity}, {@link setCapacity}). Zum anderen muss die ID der neuen Kante um eins gr��er als die ID der
     * zuletzt eingef�gten Kante sein (bzw. 0, wenn vorher noch keine Kante eingef�gt wurde). Ist dies nicht der Fall, wird
     * eine {@link IllegalStateException} geworfen. Laufzeit O(1).
     * @param edge die Kanten, die dem Graph hinzugef�gt werden soll.
     * @return die Kanten, die dem Graph hinzugef�gt wurde.
     */        
    public void setEdge(Edge edge) {
        /*if (edge.id() != edges.size()) {    //changed by Seba
            throw new IllegalStateException();
        } else*/ if (edges.size() == edges.getCapacity()) {
            throw new IllegalStateException();
        } else {
            
            edges.add(edge);
           /* System.out.println("edges:");
            for(Edge e : edges) 
                System.out.print(" " + e.toString());
            System.out.println();*/
            adjacentEdges[edge.start().id()].add(edge);
            adjacentEdges[edge.end().id()].add(edge);
            if (directed) {
                incomingEdges[edge.end().id()].add(edge);
                outgoingEdges[edge.start().id()].add(edge);
            }
           // return edge;
        }
    }
    
    public void setEdges(Iterable<Edge> edges) {
        for (Edge edge : edges) {
            
            setEdge(edge);
        }
    }
    
    private void incCurrentMaxEdgeId() {
        ++currentmaxedgeid;
    }
    
    public int getCurrentMaxEdgeId() {
        return currentmaxedgeid;
    }
    
    /**
     * Erzeugt eine neue Kante mit passender ID und f�gt sie dem Graphen hinzu. Die neue Kante verl�uft dabei zwischen den
     * angegeben Knoten. Im Falle eines ungerichteten Graphens ist es egal, welcher Knoten Startknoten und welcher Knoten
     * Endknoten der Kante ist. Laufzeit O(1).
     * @param start der Startknoten der Kante, die erzeugt werden soll.
     * @param end der Endknoten der Kante, die erzeugt werden soll.
     * @return die erzeugte Kante.
     */ 
    public void createEdge(Node start, Node end, int id) {
        //System.out.println("create edge in static graph (" + start.toString() + "," + end.toString() + ") id = " + id);
        //return addEdge(new Edge(id, start, end)); //changed by Seba: new Edge(edges.size(), start, end))
        setEdge(new Edge(id, start, end));
    }

    /**
     * Gibt zur�ck, ob die angegebene Kanten versteckt ist. Laufzeit O(1).
     * @param edge die Kanten, f�r die die Sichtbarkeit �berpr�ft werden soll.
     * @return <code>true</code>, wenn die Kanten <code>node</code> versteckt ist, <code>false</code> anderenfalls.
     */     
    public boolean isVisible(Edge edge) {
        return visibleEdges.isVisible(edge);
    }
    
    /**
     * �ndert die Sichtbarkeit der angegebenen Kante auf den angegebenen Zustand. Laufzeit O(1).
     * @param edge die Kante, f�r die die Sichtbarkeit ge�ndert werden soll.
     * @param hidden der neue Sichtbarkeits-Status: <code>true</code> versteckt die Kanten, <code>false</code> macht sie 
     * sichtbar.
     */       
    public void changeVisibility(Edge edge, boolean visible) {
        if (visible != isVisible(edge)) {
            visibleEdges.changeVisibility(edge, visible);
            if (!visible) {
                adjacentEdges[edge.start().id()].decreaseSize();
                adjacentEdges[edge.end().id()].decreaseSize();
                if (directed) {
                    incomingEdges[edge.end().id()].decreaseSize();
                    outgoingEdges[edge.start().id()].decreaseSize();
                }
            } else {
                adjacentEdges[edge.start().id()].increaseSize();
                adjacentEdges[edge.end().id()].increaseSize();                
                if (directed) {
                    incomingEdges[edge.end().id()].increaseSize();
                    outgoingEdges[edge.start().id()].increaseSize();
                }
            }
        }
    } 
    
    /**
     * Implementiert die <code>removeEdge</code> Methode des {@link Graph}-Interfaces. Da es sich um einen statischen Graphen
     * handelt, wird das entfernen der angegebenen Kante nur simuliert, d.h. es wird <code>setHidden(edge, true)</code>
     * aufgerufen.
     * @param edge die Kante, die entfernt werden soll.
     */
    public void removeEdge(Edge edge) {
        changeVisibility(edge, false);
    }
    
    public Edge getEdge(int id) {
        return edges.get(id);
    }
    
    public Edge getFirstEdge(Node start, Node end) {
        if (directed) {
            for (Edge edge : outgoingEdges(start)) {
                if (edge.end() == end) return edge;
            }
        } else {
            for (Edge edge : incidentEdges(start)) {
                if (edge.start() == end || edge.end() == end) return edge;
            }            
        }
        return null;
    }
    
    public Node getNode(int id) {
        return nodes.get(id);
    }
    
    public void clear() {
        
    }
    
    public Graph createDirectedGraph() {
        if (directed) {
            return this;
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    public Graph createUndirectedGraph() {
        if (directed) {
            throw new UnsupportedOperationException();
        } else {
            return this;
        }
    }
    
    public int getEdgeCapacity() {
        return edges.getCapacity();
    }
    
    public void setEdgeCapacity(int edgeCapacity) {
        edges.setCapacity(edgeCapacity);
        visibleEdges.setCapacity(edgeCapacity);
    }
    
    public int getNodeCapacity() {
        return nodes.getCapacity();
    }
    
    public void setNodeCapacity(int nodeCapacity) {
        nodes.setCapacity(nodeCapacity);
        //visibleNodes.setCapacity(nodeCapacity);
        adjacentEdges = new HidingAdjacencySetForThinFlow[nodeCapacity];
        if (directed) {
            incomingEdges = new HidingAdjacencySetForThinFlow[nodeCapacity];
            outgoingEdges = new HidingAdjacencySetForThinFlow[nodeCapacity];
        }        
    }
    
    public boolean existsPath(Node start, Node end) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean containsEdge(Edge edge) {
        return contains(edge);
    }

    @Override
    public boolean containsNode(Node node) {
        return contains(node);
    }

    @Override
    public void addNode(Node node) {
        setNode(node);
    }

    @Override
    public void addEdge(Edge edge) {
        setEdge(edge);
    }

}
