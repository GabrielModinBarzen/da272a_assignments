package pacman.entries.pacman;
import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.*;

public class Mahesh extends Controller<Constants.MOVE> {
    public static class Node {
        //String attribute;
        Map<String, Node> children;
        String label;
        String outcome;

        public Node() {
            children = new HashMap<>();
        }
        public Node(String outcome) {
            this.outcome = outcome;
        }

        public Node(String label, String attribute) {
            this.label = label;
            //this.attribute = attribute;
            children = new HashMap<>();
        }

        public void printNode() {
            //System.out.println("Label: " + label + " | Outcome: " + outcome);
            if (children != null) {
                if (!children.keySet().isEmpty()) {
                    for (String s : children.keySet()) {
                        System.out.println("Parent : " + label +", Branch : " + s + ", outcome : " + children.get(s).outcome);
                        children.get(s).printNode();
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        new Mahesh();
    }

    public Mahesh(){
        ID3DataTuple[] ID3gameData = DataSaverLoader.ID3LoadPacManData();
        Arrays.stream(ID3gameData).forEach(ID3DataTuple::discretizeAll);

        List<List<String>> examplesList = new ArrayList<>(ID3gameData[0].getAttributes().size());
        List<String> labels = ID3gameData[0].getLabels();


        for (int i = 0; i < ID3gameData[0].getAttributes().size(); i++) {
            examplesList.add(new ArrayList<>());
        }
        for (ID3DataTuple id3gameDatum : ID3gameData) {
            for (int i = 0; i < id3gameDatum.getAttributes().size(); i++) {
                examplesList.get(i).add(id3gameDatum.getAttributes().get(i));
            }
        }

        System.out.println(labels);
        System.out.println(examplesList);

        Node root = id3(examplesList, labels, new Node());
        System.out.println("=====================");
        root.printNode();
        tree = root;
    }

    Node tree = null;

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        Constants.MOVE move = null;
        if (tree == null) System.exit(5);

        DataTuple dt = new DataTuple(game, Constants.MOVE.NEUTRAL );
        ID3DataTuple id3dt = new ID3DataTuple(dt.getSaveString());

        id3dt.discretizeAll();
        List<String> attributes = id3dt.getAttributes();
        List<String> labels = id3dt.getLabels();
        Node node = tree;

        while (move == null) {

            //checka vilken label noden har
            //checka vilket värde vår tupel har
            //hämta child ur hashmap
            //kolla ifall child har move
            //sätt node till child
            if(node.outcome != null) {
                move = Constants.MOVE.valueOf(node.outcome);
            } else {
                for (int i = 0; i < labels.size(); i++) {
                    if (!(node.label == null)) {
                        if (labels.get(i).equals(node.label)) {
                            if (node.children.get(attributes.get(i)) != null) {
                                node = node.children.get(attributes.get(i));
                            } else break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return move;
    }

    public Node id3(List<List<String>> examples, List<String> labels, Node root) {

        if (labels.size() == 2) {
            root.label = labels.get(0);
            root.outcome = examples.get(examples.size()-1).get(0);
            return root;
        }
        if (examples.size() == 0 ) {
            root.label = labels.get(0);
            root.outcome = "UP";
            return root;
        }

        double setEntropy = entropy(examples.get(examples.size()-1));
        List<Double> informationGainList = informationGain(examples,labels,setEntropy);

        int biggest = 0;
        for (int i = 1; i < informationGainList.size(); i++) {
            if (informationGainList.get(biggest) < informationGainList.get(i)) {
                biggest = i;

            }
        }
        if (informationGainList.size() == 0) return root;
        root.label = labels.get(biggest);

        HashMap<String,List<List<String>>> exampleList = split(biggest, examples);

        for (String s : exampleList.keySet()) {
            List<List<String>> newExample = exampleList.get(s);

            boolean pure = isPureClass(newExample);

            HashMap<String,Node> children = (HashMap<String, Node>) root.children;

            System.out.println("Label : " + labels.get(biggest) + ", Pure check " + newExample.get(biggest).get(0) + ", is : " + pure);
            if (pure) {

                String branch = (newExample.get(biggest).get(0));


                children.put(branch, new Node(newExample.get(newExample.size()-1).get(0)));
                root.children = children;
            } else {


                String branch = (newExample.get(biggest).get(0));
                List<List<String>> branchExamples = newExample;


                branchExamples.remove(biggest);
                ArrayList<String> newLabels = new ArrayList<>(labels);

                newLabels.remove(biggest);



                children.put(branch, id3(branchExamples,newLabels,new Node()));
                root.children = children;
            }
        }
        return root;
    }

    private boolean isPureClass(List<List<String>> newExample) {
        boolean pure = true;
        for (String i : newExample.get(newExample.size() - 1)) {
            for (String j : newExample.get(newExample.size() - 1)) {
                if (!i.equals(j)) {
                    pure = false;
                    break;
                }
            }
        }
        return pure;
    }

    private HashMap<String,List<List<String>>> split(int i, List<List<String>> examples) {
        HashMap<String,List<List<String>>> splitList = new HashMap<>();
        for (int row = 0; row < examples.get(0).size(); row++) {
            List<List<String>> tempRow = new ArrayList<>();
            for (List<String> example : examples) {
                List<String> tempRowCol = new ArrayList<>();
                tempRowCol.add(example.get(row));
                tempRow.add(tempRowCol);
            }

            if (!splitList.containsKey(examples.get(i).get(row))) {
                splitList.put(examples.get(i).get(row),tempRow);
            } else {
                List<List<String>> tempTable = splitList.get(examples.get(i).get(row));
                for (List<String> list : tempTable) {
                    list.add(tempRow.remove(0).remove(0));
                }
                splitList.put(examples.get(i).get(row), tempTable);
            }
        }
        return splitList;
    }

    private List<Double> informationGain(List<List<String>> examples, List<String> labels, double setEntropy) {
        List<Double> informationGainList = new ArrayList<>();
        for (int i = 0; i < labels.size() - 1; i++) {
            HashMap<String,Double> entropyMap = entropy(examples.get(i), examples.get(labels.size()-1));
            double labelAverageEntropy = 0.0;
            for (String attributeValue : entropyMap.keySet()) {
                Double divisor = (double) examples.get(examples.size()-1).size();
                Double dividend = 0.0;

                for (String attributeCompareValue : examples.get(i)) {
                    if (attributeValue.equals(attributeCompareValue)) {
                        dividend++;
                    }
                }

                labelAverageEntropy += (dividend/divisor) * entropyMap.get(attributeValue);
            }
            informationGainList.add(setEntropy - labelAverageEntropy);
        }


        return informationGainList;

    }

    private HashMap<String,Double> entropy(List<String> attribute, List<String> classAttribute) {
        HashMap<String,HashMap<String,Integer>> outcomeProportion = new HashMap<>();

        for (int i = 0; i < attribute.size(); i++) {
            //If outcome proportion contains an attribute, ex rain.
            if (outcomeProportion.containsKey(attribute.get(i))) {
                //Get the hashmap for rain.
                HashMap<String,Integer> proportionFrequencyMap = outcomeProportion.get(attribute.get(i));
                //If the proportion contains the class attribute
                if (proportionFrequencyMap.containsKey(classAttribute.get(i))) {
                    //add one to the frequency;
                    proportionFrequencyMap.put(classAttribute.get(i),proportionFrequencyMap.get(classAttribute.get(i)) + 1);
                } else {
                    //Set one as the frequency
                    proportionFrequencyMap.put(classAttribute.get(i), 1);
                }
            } else {
                //If it does not contain an attribute
                HashMap<String,Integer> proportionFrequencyMap = new HashMap<>();
                proportionFrequencyMap.put(classAttribute.get(i),1);
                outcomeProportion.put(attribute.get(i),proportionFrequencyMap);

            }

        }

        HashMap<String, Double> entropyMap = new HashMap<>();
        for (String s : outcomeProportion.keySet()) {
            entropyMap.put(s,entropy(outcomeProportion.get(s)));
        }


        return entropyMap;
    }

    public double entropy(List<String> values) {
        return entropy(values,null, 0);
    }
    public double entropy(HashMap<String,Integer> premadeFrequencyMap) {
        Integer total = 0;
        for (Integer value : premadeFrequencyMap.values()) {
            total += value;
        }
        return entropy(null,premadeFrequencyMap,total);
    }

    public double entropy(List<String> values, HashMap<String,Integer> premadeFrequencyMap, Integer total) {
        HashMap<String, Integer> frequencyMap = premadeFrequencyMap;

        if (premadeFrequencyMap == null) {
            frequencyMap = new HashMap<>();
            for (String value : values) {
                if (frequencyMap.containsKey(value)) {
                    frequencyMap.put(value, frequencyMap.get(value) + 1);
                } else {
                    frequencyMap.put(value, 1);
                }
            }
            total = values.size();
        }

        double entropy = 0.0;
        for (String value : frequencyMap.keySet()) {
            double proportion = (double) frequencyMap.get(value) / total;
            if (proportion > 0) {
                entropy -= proportion * (Math.log(proportion) / Math.log(2));
            }
        }
        return entropy;
    }
}
