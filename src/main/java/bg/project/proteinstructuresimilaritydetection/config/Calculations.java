package bg.project.proteinstructuresimilaritydetection.config;

import bg.project.proteinstructuresimilaritydetection.constants.Constants;

public class Calculations {

    public void fillMatrixCell(
            double[][] matrix,
            int i,
            int j,
            char firstType,
            char secondType,
            double firstMin,
            double firstMax,
            double secondMin,
            double secondMax
    ) {
        double diagonally = matrix[i - 1][j - 1] + calculateScoringFunction(
                firstType,
                secondType,
                firstMin,
                firstMax,
                secondMin,
                secondMax
        );
        double byColumn = matrix[i - 1][j - 1] + (Constants.NEGATIVE_TYPE_FACTOR);
        double byRow = matrix[i - 1][j - 1] + (Constants.NEGATIVE_TYPE_FACTOR);
        matrix[i][j] = Math.max(Math.max(diagonally, byColumn), byRow);
        //round to the second digit
    }

    public double calculateScoringFunction(
            char firstType,
            char secondType,
            double firstMin,
            double firstMax,
            double secondMin,
            double secondMax
    ) {
        int typeFactor = firstType == secondType ? Constants.POSITIVE_TYPE_FACTOR : Constants.NEGATIVE_TYPE_FACTOR;
        return typeFactor - calculateDistanceFactor(firstMin, firstMax, secondMin, secondMax);
    }

    public double calculateDistanceFactor(
            double firstMin,
            double firstMax,
            double secondMin,
            double secondMax
    ) {
        return Math.abs(((firstMin * firstMin) / firstMax) - ((secondMin * secondMin) / secondMax));
    }

     public double getDistanceBetweenTwoPoints(
             double x1,
             double y1,
             double z1,
             double x2,
             double y2,
             double z2
            ) {
        return Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)) + ((z2 - z1) * (z2 - z1)));
    }
}
