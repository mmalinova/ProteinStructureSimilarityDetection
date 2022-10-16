package bg.project.proteinstructuresimilaritydetection.config;

public final class MyResult {
    private final double minDistance;
    private final double maxDistance;

    public MyResult(double minDistance, double maxDistance) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }
}
