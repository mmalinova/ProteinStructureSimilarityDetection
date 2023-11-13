package bg.project.proteinstructuresimilaritydetection.service;

import bg.project.proteinstructuresimilaritydetection.config.Calculations;
import bg.project.proteinstructuresimilaritydetection.config.MyResult;
import org.springframework.stereotype.Service;

import java.util.*;

import static bg.project.proteinstructuresimilaritydetection.constants.Constants.SIMILARITY_DEGREE;

@Service
public class SimilarityDetectionService {

    private final Calculations calculate = new Calculations();
    private int numberOfElementsFromTheSmallestProtein = Integer.MIN_VALUE;
    private double maxValueFromTheMatrix = Double.MIN_VALUE;

    public void openFile(String firstFileContent, String secondFileContent) {
        try {
            LinkedList<Character> firstSequence = new LinkedList<>();
            LinkedList<LinkedList<float[]>> firstCoordinates = new LinkedList<>();
            LinkedList<Character> secondSequence = new LinkedList<>();
            LinkedList<LinkedList<float[]>> secondCoordinates = new LinkedList<>();
            readFile(firstFileContent, firstSequence, firstCoordinates);
            readFile(secondFileContent, secondSequence, secondCoordinates);

            double[][] matrix = new double[firstSequence.size() + 1][secondSequence.size() + 1];
            numberOfElementsFromTheSmallestProtein = Math.min(firstSequence.size(), secondSequence.size());

            for (int i = 1; i < matrix.length; i++) {
                for (int j = 1; j < matrix[i].length; j++) {
                    MyResult firstResults = calculateMinAndMaxDistances(firstCoordinates, i - 2, i - 1);
                    MyResult secondResults = calculateMinAndMaxDistances(secondCoordinates, j - 2, j - 1);
                    matrix = calculate.fillMatrixCell(
                            matrix,
                            i,
                            j,
                            firstSequence.get(i - 1),
                            secondSequence.get(j - 1),
                            firstResults.getMinDistance(),
                            firstResults.getMaxDistance(),
                            secondResults.getMinDistance(),
                            secondResults.getMaxDistance()
                    );
                }
            }
            // Get the largest number in the last row
            double maxInLastRow = getMaxInLastRow(matrix);
            System.out.println("Largest number in the last row: " + maxInLastRow);

            // Get the largest number in the last column
            double maxInLastColumn = getMaxInLastColumn(matrix);
            System.out.println("Largest number in the last column: " + maxInLastColumn);

            maxValueFromTheMatrix = Math.max(maxInLastRow, maxInLastColumn);

            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MyResult calculateMinAndMaxDistances(
            LinkedList<LinkedList<float[]>> coordinates,
            int firstIndex,
            int secondIndex
    ) {
        double minDistance = Double.MAX_VALUE;
        double maxDistance = Double.MIN_VALUE;

        if (firstIndex >= 0 && secondIndex >= 0) {
            for (int i = 0; i < coordinates.get(firstIndex).size(); i++) {
                for (int j = 0; j < coordinates.get(secondIndex).size(); j++) {
                    double distanceBetweenTwoPoints = calculate.getDistanceBetweenTwoPoints(
                            coordinates.get(firstIndex).get(i)[0],
                            coordinates.get(firstIndex).get(i)[1],
                            coordinates.get(firstIndex).get(i)[2],
                            coordinates.get(secondIndex).get(j)[0],
                            coordinates.get(secondIndex).get(j)[1],
                            coordinates.get(secondIndex).get(j)[2]
                    );
                    if (distanceBetweenTwoPoints <= minDistance) {
                        minDistance = distanceBetweenTwoPoints;
                    }
                    if (distanceBetweenTwoPoints >= maxDistance) {
                        maxDistance = distanceBetweenTwoPoints;
                    }
                }
            }
            return new MyResult(minDistance, maxDistance);
        } else {
            return new MyResult(0, 0);
        }
    }

    private void readFile(
            String content,
            LinkedList<Character> sequence,
            LinkedList<LinkedList<float[]>> coordinates
    ) {
        Map<String, Map<Integer, Integer>> cardMap = new HashMap<>();
        LinkedList<float[]> currentCoordinates = new LinkedList<>();

        Scanner reader = new Scanner(content);
        // The map is needed if we want to know if the values come from HELIX or SHEET,
        // instead we use the map below (values)
        //Map<Integer, Integer> values = new TreeMap<>();
        while (reader.hasNextLine()) {
            String row = reader.nextLine();
            String[] data = row.split("\\s+");
            String card = data[0];
            switch (card) {
                case "HELIX" -> {
                    String acid = data[4];
                    if (acid.equals("A")) {
                        int startNumber = Integer.parseInt(data[5]);
                        int endNumber = Integer.parseInt(data[8]);
                        cardMap.putIfAbsent(card, new TreeMap<>());
                        cardMap.get(card).put(startNumber, endNumber);
                    }
                }
                //values.put(startNumber, endNumber);
                case "SHEET" -> {
                    String acid = data[5];
                    if (acid.equals("A")) {
                        int startNumber = Integer.parseInt(data[6]);
                        int endNumber = Integer.parseInt(data[9]);
                        cardMap.putIfAbsent(card, new TreeMap<>());
                        cardMap.get(card).put(startNumber, endNumber);
                        //values.put(startNumber, endNumber);
                    }
                }
                case "ATOM" -> {
                    String atomType = data[2];
                    if (atomType.equals("CA")) {
                        int residueNumber = Integer.parseInt(data[5]);

                        for (Map.Entry<String, Map<Integer, Integer>> c : cardMap.entrySet()) {
                            Map<Integer, Integer> values = c.getValue();
                            Iterator<Map.Entry<Integer, Integer>> it = values.entrySet().iterator();
                            if (it.hasNext()) {
                                Map.Entry<Integer, Integer> entry = it.next();
                                if (residueNumber >= entry.getKey() && residueNumber <= entry.getValue()) {
                                    if (residueNumber == entry.getValue()) {
                                        sequence.add(c.getKey().equals("HELIX") ? 'H' : 'S');
                                    }
                                    currentCoordinates.add(new float[]{Float.parseFloat(data[6]),
                                            Float.parseFloat(data[7]),
                                            Float.parseFloat(data[8])
                                    });

                                    if (residueNumber == entry.getValue()) {
                                        coordinates.add(currentCoordinates);
                                        currentCoordinates = new LinkedList<>();
                                        it.remove();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        reader.close();
    }

    // Method to get the largest number in the last row
    private static double getMaxInLastRow(double[][] matrix) {
        int lastRow = matrix.length - 1; // Index of the last row
        double maxInLastRow = matrix[lastRow][0]; // Initialize with the first element in the last row

        for (int i = 1; i < matrix[lastRow].length; i++) {
            if (matrix[lastRow][i] > maxInLastRow)
                maxInLastRow = matrix[lastRow][i];
        }

        return maxInLastRow;
    }

    // Method to get the largest number in the last column
    private static double getMaxInLastColumn(double[][] matrix) {
        int lastColumn = matrix[0].length - 1; // Index of the last column
        double maxInLastColumn = matrix[0][lastColumn]; // Initialize with the first element in the last column

        // Iterate through the last column to find the maximum
        for (int i = 1; i < matrix.length; i++) {
            if (matrix[i][lastColumn] > maxInLastColumn) {
                maxInLastColumn = matrix[i][lastColumn];
            }
        }

        return maxInLastColumn;
    }

    public String getTheDegreeOfSimilarity() {
        return SIMILARITY_DEGREE + calculate.calculateTheDegreeOfSimilarity(
                maxValueFromTheMatrix,
                numberOfElementsFromTheSmallestProtein
        ) + "%";
    }
}