package bg.project.proteinstructuresimilaritydetection.model;

import bg.project.proteinstructuresimilaritydetection.constants.Constants;

public class Files {

    private String firstFileName;
    private String firstMessage;
    private String secondFileName;
    private String secondMessage;
    private String scoringMessage;
    private String degreeOfSimilarity;
    private String firstFilePath;
    private String secondFilePath;
    private String positiveFactor;
    private String negativeFactor;

    public Files() {
    }

    public String getFirstFileName() {
        return firstFileName;
    }

    public void setFirstFileName(String firstFileName) {
        this.firstFileName = firstFileName;
    }

    public String getFirstMessage() {
        return firstMessage;
    }

    public void setFirstMessage(String firstMessage) {
        this.firstMessage = firstMessage;
    }

    public String getSecondFileName() {
        return secondFileName;
    }

    public void setSecondFileName(String secondFileName) {
        this.secondFileName = secondFileName;
    }

    public String getSecondMessage() {
        return secondMessage;
    }

    public void setSecondMessage(String secondMessage) {
        this.secondMessage = secondMessage;
    }

    public String getScoringMessage() {
        return scoringMessage;
    }

    public void setScoringMessage(String scoringMessage) {
        this.scoringMessage = scoringMessage;
    }

    public String getDegreeOfSimilarity() {
        return degreeOfSimilarity;
    }

    public void setDegreeOfSimilarity(String degreeOfSimilarity) {
        this.degreeOfSimilarity = degreeOfSimilarity;
    }

    public String getFirstFilePath() {
        return firstFilePath;
    }

    public void setFirstFilePath(String firstFilePath) {
        this.firstFilePath = firstFilePath;
    }

    public String getSecondFilePath() {
        return secondFilePath;
    }

    public void setSecondFilePath(String secondFilePath) {
        this.secondFilePath = secondFilePath;
    }

    public String getPositiveFactor() {
        return positiveFactor;
    }

    public void setPositiveFactor(String positiveFactor) {
        Constants.POSITIVE_TYPE_FACTOR = Integer.parseInt(positiveFactor);
        this.positiveFactor = positiveFactor;
    }

    public String getNegativeFactor() {
        return negativeFactor;
    }

    public void setNegativeFactor(String negativeFactor) {
        Constants.NEGATIVE_TYPE_FACTOR = Integer.parseInt(negativeFactor);
        this.negativeFactor = negativeFactor;
    }
}
