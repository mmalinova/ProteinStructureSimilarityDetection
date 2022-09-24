package bg.project.proteinstructuresimilaritydetection.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class SimilarityDetectionService {

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
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void readFile(File file, LinkedList<Character> sequence, LinkedList<float[]> coordinates) {
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
                                        sequence.add(c.getKey().equals("HELIX") ? 'H' : 'S');
                                        float[] currentCoordinates = {Float.parseFloat(data[6]),
                                                Float.parseFloat(data[7]),
                                                Float.parseFloat(data[8])
                                        };
                                        coordinates.add(currentCoordinates);
                                        if (residueNumber == entry.getValue()) {
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