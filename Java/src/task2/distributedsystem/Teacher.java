package task2.distributedsystem;

import java.util.ArrayList;

/*
 * Teacher class (Threads)
 * */
public class Teacher extends Thread {
    private MarkEvaluationSystem markEvaluationSystem;  // The main parent running class
    private ArrayList<ArrayList<String>> InputBuffer; // roll num, update marks by
    private boolean Synchronize;    // indicates if the marks need to be updated synchronously or nor

    /*
     * Constructor
     * */
    Teacher(MarkEvaluationSystem markEvaluationSystem, String Name, int priority) {
        this.markEvaluationSystem = markEvaluationSystem;
        setName(Name);
        setPriority(priority);
        InputBuffer = new ArrayList<>();
        Synchronize = false;
    }

    /*
     * While the teacher has input in it's buffer,
     * keep on updating the marks
     * */
    @Override
    public void run() {
        while (InputBuffer.size() > 0) {
            if (Synchronize) {
                markEvaluationSystem.updateWithSynchronisation(InputBuffer.get(0).get(0), Integer.parseInt(InputBuffer.get(0).get(1)), getName());
            } else {
                markEvaluationSystem.updateWithoutSynchronisation(InputBuffer.get(0).get(0), Integer.parseInt(InputBuffer.get(0).get(1)), getName());
            }
            InputBuffer.remove(0);
        }
    }

    /*
     * Add a new input to the buffer
     * input contains the details about updating the marks
     * */
    void addInputToBuffer(String rollNumber, String updateMarks) {
        ArrayList<String> newInput = new ArrayList<>();
        newInput.add(rollNumber);
        newInput.add(updateMarks);
        InputBuffer.add(newInput);
    }

    void setSynchronize(boolean synchronize) {
        Synchronize = synchronize;
    }
}
