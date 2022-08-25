package bg.project.proteinstructuresimilaritydetection.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class SimilarityDetectionService {

    public void openFile() {
        try {
            File file = new File("src/main/resources/testFile/4y2n.pdb");
            if (checkFileExtension(file)) {
                readFile(file);
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

    private void readFile(File file) {
        try {
            Scanner reader = new Scanner(file);
            // These map is needed if we want to know if the values come from HELIX or SHEET,
            // instead we use the map below (values)
            //Map<String, Map<Integer, Integer>> cardMap = new HashMap<>();
            Map<Integer, Integer> values = new TreeMap<>();
            while (reader.hasNextLine()) {
                String row = reader.nextLine();
                String[] data = row.split("\\s+");
                String card = data[0];
                if (card.equals("HELIX")) {
                    int startNumber = Integer.parseInt(data[5]);
                    int endNumber = Integer.parseInt(data[8]);
                    //cardMap.putIfAbsent(card, new TreeMap<>());
                    //cardMap.get(card).put(startNumber, endNumber);
                    values.put(startNumber, endNumber);
                } else if (card.equals("SHEET")) {
                    String acid = data[5];
                    if (acid.equals("A")) {
                        int startNumber = Integer.parseInt(data[6]);
                        int endNumber = Integer.parseInt(data[9]);
                        //cardMap.putIfAbsent(card, new TreeMap<>());
                        //cardMap.get(card).put(startNumber, endNumber);
                        values.put(startNumber, endNumber);
                    }
                } else if (card.equals("ATOM")) {
                    String atomType = data[2];
                    if (atomType.equals("CA")) {
                        int residueNumber = Integer.parseInt(data[5]);
                        //for (Map.Entry<String, Map<Integer, Integer>> c : cardMap.entrySet()) {
                        //    Map<Integer, Integer> values = c.getValue();
                              Iterator<Map.Entry<Integer, Integer>> it = values.entrySet().iterator();
                              if (it.hasNext()) {
                                  Map.Entry<Integer, Integer> entry = it.next();
                                  if (residueNumber >= entry.getKey() && residueNumber <= entry.getValue()) {
                                      float X = Float.parseFloat(data[6]);
                                      float Y = Float.parseFloat(data[7]);
                                      float Z = Float.parseFloat(data[8]);
                                      System.out.printf("%.3f   %.3f  %.3f%n", X, Y, Z);
                                      if (residueNumber == entry.getValue()) {
                                          it.remove();
                                    }
                                  }
                              }
                        //}
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