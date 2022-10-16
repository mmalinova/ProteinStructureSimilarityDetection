package bg.project.proteinstructuresimilaritydetection.service;

import bg.project.proteinstructuresimilaritydetection.config.Calculations;
import bg.project.proteinstructuresimilaritydetection.config.MyResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class SimilarityDetectionService {

    private final Calculations calculate = new Calculations();

    public void openFile(String firstFilePath, String secondFilePath) {
        try {
            File firstFile = new File(firstFilePath);
            File secondFile = new File(secondFilePath);
            if (checkFileExtension(firstFile) && checkFileExtension(secondFile)) {
                LinkedList<Character> firstSequence = new LinkedList<>();
                LinkedList<float[]> firstCoordinates = new LinkedList<>();
                LinkedList<Character> secondSequence = new LinkedList<>();
                LinkedList<float[]> secondCoordinates = new LinkedList<>();
                readFile(firstFile, firstSequence, firstCoordinates);
                readFile(secondFile, secondSequence, secondCoordinates);

                double[][] matrix = new double[firstSequence.size() + 1][secondSequence.size() + 1];

                MyResult firstResults = calculateMinAndMaxDistances(firstCoordinates);
                System.out.println(firstResults.getMinDistance());
                System.out.println(firstResults.getMaxDistance());
                MyResult secondResults = calculateMinAndMaxDistances(secondCoordinates);
                System.out.println(secondResults.getMinDistance());
                System.out.println(secondResults.getMaxDistance());

                for (int i = 1; i < matrix.length; i++) {
                    for (int j = 1; j < matrix[i].length; j++) {
                        calculate.fillMatrixCell(
                                matrix,
                                i,
                                j,
                                firstSequence.getFirst(),
                                secondSequence.getFirst(),
                                firstResults.getMinDistance(),
                                firstResults.getMaxDistance(),
                                secondResults.getMinDistance(),
                                secondResults.getMaxDistance()
                        );
                    }
                    System.out.println();
                }

                double scoringFunction = calculate.calculateScoringFunction(
                        firstSequence.getFirst(), secondSequence.getFirst(),
                        firstResults.getMinDistance(), firstResults.getMaxDistance(),
                        secondResults.getMinDistance(), secondResults.getMaxDistance()
                );
                System.out.println(scoringFunction);
                firstSequence.removeFirst();
                secondSequence.removeFirst();
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MyResult calculateMinAndMaxDistances(LinkedList<float[]> coordinates) {
        double minDistance = Double.MAX_VALUE;
        double maxDistance = Double.MIN_VALUE;
        int firstIndex = 0;
        int secondIndex = 0;

        for (int i = 0; i < coordinates.size(); i++) {
            if (firstIndex == 0 && coordinates.get(i).length == 0) {
                firstIndex = i;
            }
            else if (coordinates.get(i).length == 0) {
                secondIndex = i;
                break;
            }
        }

        for (int i = 0; i < firstIndex; i++) {
            for (int j = firstIndex + 1; j < secondIndex; j++) {
                double distanceBetweenTwoPoints = calculate.getDistanceBetweenTwoPoints(
                        coordinates.get(i)[0],
                        coordinates.get(i)[1],
                        coordinates.get(i)[2],
                        coordinates.get(j)[0],
                        coordinates.get(j)[1],
                        coordinates.get(j)[2]
                );
                if (distanceBetweenTwoPoints <= minDistance) {
                    minDistance = distanceBetweenTwoPoints;
                }
                if (distanceBetweenTwoPoints >= maxDistance) {
                    maxDistance = distanceBetweenTwoPoints;
                }
            }
        }

        coordinates.subList(0, firstIndex).clear();
        return new MyResult(minDistance, maxDistance);
    }

    private boolean checkFileExtension(File file) {
        // convert the file name to string
        String fileName = file.toString();

        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            String extension = fileName.substring(index + 1);
            if (!extension.equals("pdb")) {
                System.out.println("Please upload .pdb file!");
                return false;
            }
        } else {
            System.out.println("The given file is without extension. Please check the file extension first!");
            return false;
        }
        return true;
    }

    private void readFile(
            File file,
            LinkedList<Character> sequence,
            LinkedList<float[]> coordinates
    ) {
        Map<String, Map<Integer, Integer>> cardMap = new HashMap<>();

        try {
            Scanner reader = new Scanner(file);
            // The map is needed if we want to know if the values come from HELIX or SHEET,
            // instead we use the map below (values)
            //Map<Integer, Integer> values = new TreeMap<>();
            while (reader.hasNextLine()) {
                String row = reader.nextLine();
                String[] data = row.split("\\s+");
                String card = data[0];
                switch (card) {
                    case "HELIX" -> {
                        int startNumber = Integer.parseInt(data[5]);
                        int endNumber = Integer.parseInt(data[8]);
                        cardMap.putIfAbsent(card, new TreeMap<>());
                        cardMap.get(card).put(startNumber, endNumber);
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
                                        float[] currentCoordinates = {Float.parseFloat(data[6]),
                                                Float.parseFloat(data[7]),
                                                Float.parseFloat(data[8])
                                        };
                                        coordinates.add(currentCoordinates);
                                        if (residueNumber == entry.getValue()) {
                                            coordinates.add(new float[0]);
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
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}