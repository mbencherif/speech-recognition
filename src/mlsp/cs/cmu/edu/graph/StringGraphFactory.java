package mlsp.cs.cmu.edu.graph;

public class StringGraphFactory implements GraphFactory<Character, String> {

  private String[] processList;

  public StringGraphFactory(String... processList) {
    this.processList = processList;
  }

  @Override
  public Graph<Character, String> buildGraph() {
    Node<Character> head = new CharNode(null);
    Graph<Character, String> G = new StringGraph(head); 
    Node<Character> currNode = head;
    for (String stringGraph : processList) {
      for(Character c : stringGraph.toCharArray()) {
        Node<Character> newNode = new CharNode(c);
        Edge<String> newEdge = new Edge<String>(currNode, newNode);
      }
    }
    return G;
  }
}
