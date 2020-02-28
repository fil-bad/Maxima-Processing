package processingElement;

public interface Subject {
    void attachScene(Observer s);

    void notifyChange();
}
