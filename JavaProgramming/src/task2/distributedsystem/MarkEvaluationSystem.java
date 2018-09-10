package task2.distributedsystem;

import java.io.*;
import java.util.*;

public class MarkEvaluationSystem {
    private final static Scanner scanner = new Scanner(System.in);

    private Map<String, ArrayList<String>> Data; //Roll no. -> [name, email, marks, last_updated_by]

    private ArrayList<ArrayList<String>> InputBuffer; // Teachers name, roll num, update marks by

    private MarkEvaluationSystem() {
        Data = new HashMap<>();
        InputBuffer = new ArrayList<>();
    }

    private void AddInputBuffer() {
        // get the type of teacher
        String teacher = GetTeacherName();

        // get the roll number
        String rollNumber = GetRollNumber();

        // get the marks change
        String marksUpdate = GetUpdateMarks();

        ArrayList<String> newInput = new ArrayList<>();
        newInput.add(teacher);
        newInput.add(rollNumber);
        newInput.add(marksUpdate);
        InputBuffer.add(newInput);
    }

    private String GetUpdateMarks() {
        System.out.println("Choose one of the following\n" +
                "   1) Increase marks\n" +
                "   2) Decrease marks");

        int option = scanner.nextInt();
        if (option == 1) {
            System.out.println("Increase marks by : ");
            int marks = scanner.nextInt();
            return String.valueOf(marks);
        } else if (option == 2) {
            System.out.println("Decrease marks by : ");
            int marks = scanner.nextInt();
            return String.valueOf(-marks);
        } else {
            System.out.println("Invalid Option.");
            return GetUpdateMarks();
        }
    }

    private String GetRollNumber() {
        System.out.println("Enter Roll Number :- ");
        return scanner.next();
    }

    private String GetTeacherName() {
        System.out.println("Enter teacher's name:- ");
        String teacher = scanner.next();
        if (teacher.equals("CC") || teacher.equals("TA1") || teacher.equals("TA2")) {
            return teacher;
        } else {
            System.out.println("Invalid teacher name.");
            return GetTeacherName();
        }
    }

    private void UpdateMarks() {
        // ask if updating the files synchronously or asynchronously for hte previous inputs.
        System.out.println("Choose one:\n" +
                "   1. Without Synchronization\n" +
                "   2. With Synchronization");
        int option = scanner.nextInt();

        Teacher courseCoordinator = new Teacher(this, Constants.CC, Thread.MAX_PRIORITY);
        Teacher teachingAssistant1 = new Teacher(this, Constants.TA1, Thread.NORM_PRIORITY);
        Teacher teachingAssistant2 = new Teacher(this, Constants.TA2, Thread.NORM_PRIORITY);

        // Update the files accordingly
        if (option == 1) {    // without synchronisation
            teachingAssistant1.setSynchronize(false);
            teachingAssistant2.setSynchronize(false);
            courseCoordinator.setSynchronize(false);
        } else if (option == 2) {   // with synchronization
            teachingAssistant1.setSynchronize(true);
            teachingAssistant2.setSynchronize(true);
            courseCoordinator.setSynchronize(true);
        } else {
            System.out.println("Invalid Option");
            return;
        }
        for (ArrayList<String> entry : InputBuffer) {
            String teacher = entry.get(0);
            String rollNumber = entry.get(1);
            String updateMarks = entry.get(2);
            if (Constants.TA1.equals(teacher)) {
                teachingAssistant1.addInputToBuffer(rollNumber, updateMarks);
            } else if (Constants.TA2.equals(teacher)) {
                teachingAssistant2.addInputToBuffer(rollNumber, updateMarks);
            } else if (Constants.CC.equals(teacher)) {
                courseCoordinator.addInputToBuffer(rollNumber, updateMarks);
            }
        }
        InputBuffer.clear();

        courseCoordinator.start();
        teachingAssistant1.start();
        teachingAssistant2.start();

        try {
            courseCoordinator.join();
            teachingAssistant1.join();
            teachingAssistant2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // write the final results back to the file
        writeFinalData();
    }

    void updateWithSynchronisation(String rollNumber, int marksToUpdate, String updatedBy) {
        if (Data.get(rollNumber) != null) {
            synchronized (Data.get(rollNumber)) {
                updateData(Data.get(rollNumber), marksToUpdate, updatedBy);
            }
        }
    }

    void updateWithoutSynchronisation(String rollNumber, int marksToUpdate, String updatedBy) {
        if (Data.get(rollNumber) != null) {
            updateData(Data.get(rollNumber), marksToUpdate, updatedBy);
        }
    }

    private void updateData(ArrayList<String> data, int marksToUpdate, String updatedBy) {
        if (data.get(3).equals(Constants.CC) && !updatedBy.equals(Constants.CC)) {
            return;
        }
        int marks = Integer.parseInt(data.get(2).trim());
        marks = marks + marksToUpdate;
        data.set(2, String.valueOf(marks));
        data.set(3, updatedBy);
    }

    private void readInitialData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(Constants.STUD_INFO));
        String line;
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] data = line.split(",");
            ArrayList<String> newEntry = new ArrayList<>();
            newEntry.add(data[1]);
            newEntry.add(data[2]);
            newEntry.add(data[3]);
            newEntry.add(data[4]);
            Data.put(data[0], newEntry);
        }
    }

    private void writeFinalData() {
        /* Write back to the original file. */
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(Constants.STUD_INFO));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert writer != null;
        for (Map.Entry<String, ArrayList<String>> entry : Data.entrySet()) {
            try {
                writer.append(entry.getKey());
                for (String value : entry.getValue()) {
                    writer.append(',');
                    writer.append(value);
                }
                writer.append('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Write to a roll number sorted file. */
        try {
            writer = new BufferedWriter(new FileWriter(Constants.STUD_INFO_SORTED_BY_ROLL_NUMBER));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> sortedKeys = new ArrayList<>(Data.keySet());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            try {
                writer.append(key);
                for (String value : Data.get(key)) {
                    writer.append(',');
                    writer.append(value);
                }
                writer.append('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        /*Give the following choices to the user :-
         * 1) Update student marks
         * 2) Generate roll number wise sorted list
         * 3) Generate name wise sorted list
         * */

        MarkEvaluationSystem markEvaluationSystem = new MarkEvaluationSystem();

        // Read the current data from the file.
        markEvaluationSystem.readInitialData();

        while (true) {
            int choice;
            System.out.println("Choose one option\n" +
                    "       1) Update student marks\n" +
                    "       2) Execute\n" +
                    "       0) Exit the system.");
            choice = scanner.nextInt();
            switch (choice) {
                case 0:
                    return;
                case 1:
                    markEvaluationSystem.AddInputBuffer();
                    break;
                case 2:
                    markEvaluationSystem.UpdateMarks();
                    break;
                default:
                    System.out.println("Invalid Option!");
                    break;
            }
        }
    }
}

