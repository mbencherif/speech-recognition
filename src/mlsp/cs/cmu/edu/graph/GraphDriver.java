package mlsp.cs.cmu.edu.graph;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GraphDriver {

  public static void main(String[] args) throws FileNotFoundException {

    List<String> dictionary = new ArrayList<String>();
//    Scanner scn = new Scanner(new File("./dict/dict_5k.txt"));
//    while (scn.hasNextLine()) {
//      dictionary.add(scn.nextLine());
//    }
//    scn.close();
   dictionary.add("abc");

    List<String> input = new ArrayList<String>();
    
//    scn = new Scanner(new File("./text/typos.txt"));
//    StringBuilder sb = new StringBuilder();
//    while (scn.hasNextLine()) {
//      sb.append(scn.nextLine());
////      String[] arr = scn.nextLine().split(" ");
////      input.addAll(Arrays.asList(arr));
//    }
//    scn.close();
//    input.add(sb.toString());
    input.add("abb");
 

    GraphFactory<Character, String> factory = new StringGraphFactory(dictionary.toArray(new String[dictionary.size()]));
//    GraphFactory<Character, String> factory = new DummyStringGraphFactory("abc");
    Graph<Character, String> G1 = factory.buildGraph();
    System.out.println("Dictionary Graph: Done building graph!");
    printGraph(G1);

    List<Graph<Character, String>> words = new ArrayList<Graph<Character, String>>();
    int i = 0;
    for (String s : input) {
      factory = new StringGraphFactory(s);
//      factory = new DummyStringGraphFactory(s);
      Graph<Character, String> G2 = factory.buildGraph();
      words.add(G2);
      System.out.println("String Graph "+i+++": Done building graph!");
      printGraph(G2);
    }
    i = 0;
    for (Graph<Character, String> wordGraph : words) {
      CartesianGraph<Character, String> product = new StringCartesianGraph(G1, wordGraph);
      System.out.println("Cartesian Graph "+i+++": Done building graph!");
      Node<?> node = product.getViterbiBestPath();
      printGraph(product);
      System.out.println("last node: " + node.toString());
    }
    
    
  }

  public static void printGraph(Graph<?, ?> graph) {
    System.out.println(graph.toString());
  }

}