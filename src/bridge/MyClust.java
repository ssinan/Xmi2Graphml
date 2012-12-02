package bridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.Set;

public class MyClust {

    private int nodes, edges;
    private int[][] graph;
    private int[][] hamDist;
    private int[][] halfHamDist;
    private int[] degrees;
    private int cutRate, hamDistThr, halfHamDistThr;
    private Set<Integer> centers, singles, cycles, authorities, hubs;
    private ArrayList<Integer[]> hammingPairs, halfHammingPairs;
    private Map<Integer[], Integer[]> bridges;
    private Set<int[]> uniEdges;
    private final double HARate = 0.85;

    public MyClust(String fname, String rate, String hdThr, String hhdThr) {
        readSolution(fname);
        uniEdges = new TreeSet<int[]>(new EdgeComparator());
        cutRate = (int) Math.ceil(nodes * Double.parseDouble(rate));
        hamDistThr = (int) Integer.parseInt(hdThr);
        halfHamDistThr = (int) Integer.parseInt(hhdThr);
        degrees = new int[nodes];
        hamDist = new int[nodes][nodes];
        halfHamDist = new int[nodes][nodes];
        centers = new TreeSet<Integer>();
        authorities = new TreeSet<Integer>();
        hubs = new TreeSet<Integer>();
        singles = new TreeSet<Integer>();
        cycles = new TreeSet<Integer>();
        hammingPairs = new ArrayList<Integer[]>();
        halfHammingPairs = new ArrayList<Integer[]>();
        bridges = new TreeMap<Integer[], Integer[]>(new PairComparator());

    }

    public Map<Integer[], Integer[]> getBridgeNodes() {
        ArrayList<int[]> jointRows = new ArrayList<int[]>();
        ArrayList<Integer> tempSet = new ArrayList<Integer>();
        int t;
        Integer[] temPair;

        getHammingPairs();

        ArrayList<Integer[]> temp = new ArrayList<Integer[]>(hammingPairs);
        boolean[] dirty = new boolean[hammingPairs.size()];

        for (Integer[] i : hammingPairs) {
            jointRows.add(andRows(graph[i[0] - 1], graph[i[1] - 1]));
            if (sum(jointRows.get(jointRows.size() - 1)) <= 1) {
                jointRows.remove(jointRows.size() - 1);
                temp.remove(i);
            }
        }

        while (joinRows(temp, jointRows) > 0);

        for (Integer[] i : temp) {
            temPair = getBridgeSources(jointRows.get(temp.indexOf(i)));
            if (!Arrays.asList(i).contains(temPair[0]) && !Arrays.asList(i).contains(temPair[1])) {
                bridges.put(i, temPair.clone());
            }
        }

        return bridges;
    }

    private Integer[] getBridgeSources(int[] sourceList) {
        ArrayList<Integer> sources = new ArrayList<Integer>();

        for (int i = 0; i < sourceList.length; i++) {
            if (sourceList[i] == 1) {
                sources.add(i + 1);
            }
        }

        return sources.toArray(new Integer[sources.size()]);
    }

    private int joinRows(ArrayList<Integer[]> pairs, ArrayList<int[]> rows) {
        int joins = 0;
        Map<Integer[], int[]> pairTab = new TreeMap<Integer[], int[]>(new PairComparator());
        Map<Integer[], int[]> tempPairTab = new TreeMap<Integer[], int[]>(new PairComparator());
        Map<Integer[], int[]> newPairTab = new TreeMap<Integer[], int[]>(new PairComparator());
        Integer[] tempJPair;
        int[] tempANDRow;

        for (Integer[] i : pairs) {
            pairTab.put(i, rows.get(pairs.indexOf(i)));
        }

        newPairTab.clear();
        newPairTab.putAll(pairTab);

        for (Map.Entry<Integer[], int[]> e : pairTab.entrySet()) {
            tempPairTab.clear();
            tempPairTab.putAll(pairTab);
            tempPairTab.remove(e.getKey());
            tempJPair = e.getKey();
            tempANDRow = e.getValue();
            for (Map.Entry<Integer[], int[]> s : tempPairTab.entrySet()) {
                if (hasCommon(e.getValue(), s.getValue())) {
                    newPairTab.remove(e.getKey());
                    newPairTab.remove(s.getKey());
                    tempJPair = joinPairs(tempJPair, s.getKey());
                    tempANDRow = andRows(tempANDRow, e.getValue());
                    joins++;
                }
            }
            newPairTab.put(tempJPair, tempANDRow);
        }


        pairs.clear();
        rows.clear();

        for (Map.Entry<Integer[], int[]> e : newPairTab.entrySet()) {
            pairs.add(e.getKey());
            rows.add(e.getValue());
        }

        pairTab = null;
        tempPairTab = null;
        newPairTab = null;
        return joins;
    }

    private boolean hasCommon(int[] arr1, int[] arr2) {
        int common = 0;

        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] == arr2[i] && arr1[i] == 1) {
                common++;
            }
        }

        if (common >= 2) {
            return true;
        } else {
            return false;
        }
    }

    private Integer[] joinPairs(Integer[] arr1, Integer[] arr2) {
        int c = 0;
        Integer[] temp = new Integer[arr1.length + arr2.length];
        SortedSet<Integer> set = new TreeSet<Integer>();

        for (Integer i : arr1) {
            set.add(i);
        }

        for (Integer j : arr2) {
            set.add(j);
        }

        temp = null;
        return (Integer[]) set.toArray(new Integer[set.size()]);
    }

    private int sum(int[] arr) {
        int sum = 0;

        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }

        return sum;
    }

    private void getHammingPairs() {
        for (int i = 0; i < hamDist.length; i++) {
            if (!(centers.contains(i + 1) || singles.contains(i + 1))) {
                for (int j = i + 1; j < hamDist[i].length; j++) {
                    if (hamDist[i][j] < hamDistThr && !(centers.contains(j + 1) || singles.contains(j + 1))) {
                        hammingPairs.add(new Integer[]{new Integer(i + 1), new Integer(j + 1)});
                    }
                }
            }
        }


    }

    private int[] andRows(int[] row1, int[] row2) {
        int[] joint = new int[row1.length];

        for (int i = 0; i < row2.length; i++) {
            if (row1[i] == 1 && row2[i] == 1) {
                joint[i] = 1;
            } else {
                joint[i] = 0;
            }
        }

        return joint;
    }

    private void getHalfHammingPairs() {

        for (int i = 0; i < halfHamDist.length; i++) {
            if (!(centers.contains(i + 1) || singles.contains(i + 1))) {
                for (int j = 0; j < halfHamDist[i].length; j++) {
                    if (halfHamDist[i][j] < halfHamDistThr && i != j && !(centers.contains(j + 1) || singles.contains(j + 1))) {
                        halfHammingPairs.add(new Integer[]{new Integer(i + 1), new Integer(j + 1)});
                    }
                }
            }
        }


        removeSingleHammingPairs();
        removeDuplicateHammingPairs();

        /*
         * for(Integer[] i:halfHammingPairs) System.out.print(i[0]+"-"+i[1]+"
         * "); System.out.println();
         */
    }

    private void removeDuplicateHammingPairs() {
        ArrayList<Integer[]> temp = new ArrayList<Integer[]>(halfHammingPairs);
        boolean found = false;
        Integer[] j;

        for (Integer[] i : temp) {
            found = false;

            for (int k = temp.indexOf(i) + 1; k < temp.size(); k++) {
                j = temp.get(k);
                if (i[0].compareTo(j[1]) == 0 && i[1].compareTo(j[0]) == 0) {
                    found = true;
                }
            }

            if (found) {
                halfHammingPairs.remove(i);
            }
        }
    }

    private void removeSingleHammingPairs() {
        ArrayList<Integer[]> temp = new ArrayList<Integer[]>(halfHammingPairs);
        boolean found = false;

        for (Integer[] i : temp) {
            found = false;
            for (Integer[] j : temp) {
                if (i[0].compareTo(j[1]) == 0 && i[1].compareTo(j[0]) == 0) {
                    found = true;
                }
            }

            if (!found) {
                halfHammingPairs.remove(i);
            }
        }
    }

    public Set<Integer> getCenterNodes() {

        double degree;

        for (int i = 0; i < degrees.length; i++) {
            if (degrees[i] > cutRate) {
                centers.add(new Integer(i + 1));
            }
        }

        for (Integer j : centers) {
            degree = getDegree(j - 1);
            if (degree > HARate) {
                hubs.add(j);
            } else if (degree < -HARate) {
                authorities.add(j);
            }

        }

        return centers;

    }

    public double getDegree(int node) {
        int j = 0;

        for (int[] i : uniEdges) {
            if (i[0] == node) {
                j++;
            }
            if (i[1] == node) {
                j--;
            }
        }

        return j / degrees[node];
    }

    public Set<Integer> getAuthorityNodes() {
        return authorities;
    }

    public Set<Integer> getHubNodes() {
        return hubs;
    }

    public Set<Integer> getSingleNodes() {
        for (int i = 0; i < degrees.length; i++) {
            if (degrees[i] <= 1) {
                singles.add(new Integer(i + 1));
            }
        }


        return singles;

    }

    public Set<Integer> getCycleNodes() {
        for (int i = 0; i < graph.length; i++) {
            if (graph[i][i] == 1) {
                cycles.add(new Integer(i + 1));
            }
        }

        return cycles;

    }

    public void calculateDegrees() {

        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                if (i == j) {
                    degrees[i] = degrees[i] + graph[i][j] + graph[i][j];
                } else {
                    degrees[i] = degrees[i] + graph[i][j];
                }
            }
        }

    }

    public void calculateDistances() {

        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < nodes; j++) {
                hamDist[i][j] = hamming(graph[i], graph[j]);
                halfHamDist[i][j] = halfHamming(graph[i], graph[j]);
            }
        }
    }

    private int hamming(int[] nodesA, int[] nodesB) {
        int dist = 0;

        for (int i = 0; i < nodesA.length; i++) {
            dist += Math.abs(nodesA[i] - nodesB[i]);
        }

        return dist;
    }

    private int halfHamming(int[] nodesA, int[] nodesB) {
        int dist = 0;

        for (int i = 0; i < nodesA.length; i++) {
            if (nodesA[i] == 1) {
                dist += nodesA[i] - nodesB[i];
            }
        }

        return dist;
    }

    private void readSolution(String file) {
        if (file != null) {
            try {
                FileReader fReader = new FileReader(new File(file));
                BufferedReader breader = new BufferedReader(fReader);
                String s[];

                s = (breader.readLine()).split(" ");
                nodes = Integer.parseInt(s[0]);
                edges = Integer.parseInt(s[1]);

                graph = new int[nodes][nodes];

                for (int i = 0; i < nodes; i++) {
                    s = (breader.readLine()).split(" ");
                    for (int j = 0; j < nodes; j++) {
                        graph[i][j] = Integer.parseInt(s[j]);
                    }
                }

                breader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void readBidGraph(String fname) throws IOException {
        String buffer = new String();
        String edgeA = new String();
        String edgeB = new String();

        List<String[]> edges = new ArrayList<String[]>();
        List<String> nodes = new ArrayList<String>();

        FileReader graphFile = new FileReader(fname);

        BufferedReader source = new BufferedReader(graphFile);

        try {
            while ((buffer = source.readLine()) != null) {
                if (buffer.contains("->")) {
                    edgeA = (buffer.substring(0, buffer.indexOf("->"))).trim();
                    edgeB = (buffer.substring(buffer.indexOf("->") + 2)).trim();
                    edges.add(new String[]{edgeA, edgeB});
                    if (!nodes.contains(edgeA)) {
                        nodes.add(edgeA);
                    }
                    if (!nodes.contains(edgeB)) {
                        nodes.add(edgeB);
                    }
                }
            }

            for (String[] s : edges) {
                uniEdges.add(new int[]{nodes.indexOf(s[0]), nodes.indexOf(s[1])});
            }


        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                }
            }
        }
    }

    class EdgeComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            int[] arr1 = (int[]) o1;
            int[] arr2 = (int[]) o2;

            for (int i = 0; i < arr1.length; i++) {
                if (arr1[i] > arr2[i]) {
                    return 1;
                } else if (arr1[i] < arr2[i]) {
                    return -1;
                }
            }
            return 0;
        }
    }
}

class PairComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Integer[] arr1 = (Integer[]) o1;
        Integer[] arr2 = (Integer[]) o2;
        int size = (arr1.length <= arr2.length ? arr1.length : arr2.length);

        for (int i = 0; i < size; i++) {
            if (arr1[i].compareTo(arr2[i]) > 0) {
                return 1;
            } else if (arr1[i].compareTo(arr2[i]) < 0) {
                return -1;
            }
        }

        if (arr1.length < arr2.length) {
            return -1;
        } else if (arr1.length > arr2.length) {
            return 1;
        } else {
            return 0;
        }
    }
}

class RowComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        int[] arr1 = (int[]) o1;
        int[] arr2 = (int[]) o2;

        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] > arr2[i]) {
                return 1;
            } else if (arr1[i] < arr2[i]) {
                return -1;
            }
        }
        return 0;
    }
}
