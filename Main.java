import java.util.*;

/**
 * Custom exception for grade validation.
 */
class InvalidGradeException extends Exception {
    public InvalidGradeException(String message) { super(message); }
}

/**
 * Abstract Student class.
 */
abstract class Student {
    private String studentName, studentNumber, course, section;
    private int yearLevel, semester;
    private Map<String, String> subjectProfessors;
    private Map<String, ArrayList<Double>> subjectGrades;
    private Map<String, Integer> subjectUnits;
    protected String[] subjects;

    public Student(String name, String sn, String course, int year, int semester, String section, String[] subjects) {
        this.studentName = name;
        this.studentNumber = sn;
        this.course = course;
        this.yearLevel = year;
        this.semester = semester;
        this.section = section;
        this.subjects = subjects;
        subjectProfessors = new LinkedHashMap<>();
        subjectGrades = new LinkedHashMap<>();
        subjectUnits = new LinkedHashMap<>();
        for (String s : subjects) {
            subjectProfessors.put(s, "TBA");
            subjectGrades.put(s, new ArrayList<>());
            subjectUnits.put(s, 0);
        }
    }

    public String getStudentName() { return studentName; }
    public String getStudentNumber() { return studentNumber; }
    public String getCourse() { return course; }
    public int getYearLevel() { return yearLevel; }
    public int getSemester() { return semester; }
    public String[] getSubjects() { return subjects; }
    public Map<String, ArrayList<Double>> getSubjectGrades() { return subjectGrades; }

    public void setProfessor(String s, String p) { if (subjectProfessors.containsKey(s)) subjectProfessors.put(s, p); }
    public String getProfessor(String s) { return subjectProfessors.getOrDefault(s, "N/A"); }
    public void setUnits(String s, int u) { if (subjectUnits.containsKey(s)) subjectUnits.put(s, u); }
    public int getUnits(String s) { return subjectUnits.getOrDefault(s, 0); }

    public void addGrade(String s, double g) throws InvalidGradeException {
        if (g < 0 || g > 100) throw new InvalidGradeException("Grade must be 0-100.");
        if (subjectGrades.containsKey(s)) subjectGrades.get(s).add(g);
    }

    public double computeAverage() {
        double totalWeightedScore = 0; int totalUnits = 0;
        for (String s : subjects) {
            ArrayList<Double> grades = subjectGrades.get(s);
            int units = subjectUnits.get(s);
            if (!grades.isEmpty()) {
                double sum = 0;
                for (double g : grades) sum += g;
                totalWeightedScore += (sum / grades.size() * units);
                totalUnits += units;
            }
        }
        return totalUnits == 0 ? 0 : totalWeightedScore / totalUnits;
    }

    public String getRemarks() { return computeAverage() >= 75 ? "PASSED" : "FAILED"; }

    public String getAcademicStatus() {
        if (this instanceof IrregularStudent) return "N/A";
        double avg = computeAverage();
        if (avg >= 95) return "PRESIDENT'S LISTER";
        if (avg >= 90) return "DEAN'S LISTER";
        if (avg >= 85) return "ACADEMIC SCHOLAR";
        return "REGULAR STATUS";
    }

    public abstract String getStudentType();
    public abstract void displayInfo();
}

class RegularStudent extends Student {
    public RegularStudent(String n, String sn, String c, int y, int sem, String s, String[] subjs) {
        super(n, sn, c, y, sem, s, subjs);
    }
    @Override public String getStudentType() { return "Regular BSCpE Student"; }
    @Override public void displayInfo() { Main.displayStudentRecord(this); }
}

class IrregularStudent extends Student {
    public IrregularStudent(String n, String sn, String c, int y, int sem, String s, String[] subjs) {
        super(n, sn, c, y, sem, s, subjs);
    }
    @Override public String getStudentType() { return "Irregular BSCpE Student"; }
    @Override public void displayInfo() { Main.displayStudentRecord(this); }
}

public class Main {
    static final Scanner scanner = new Scanner(System.in);

    static final Map<String, Integer> MASTER_CATALOG = new LinkedHashMap<>() {{
        // 1st Year
        put("Calculus 1", 3); put("Chemistry for Engineers", 4); put("CpE as a Discipline", 3);
        put("Programming Logic and Design", 3); put("Math in the Modern World", 3); put("STS", 3);
        put("Understanding the Self", 3); put("PATHFIT 1", 2); put("NSTP 1", 3);
        put("Calculus 2", 3); put("Physics for Engineers", 4); put("Object Oriented Programming", 3);
        put("Engineering Data Analysis", 3); put("Discrete Mathematics", 3); put("Readings in Phil History", 3);
        put("PATHFIT 2", 2); put("Intro to Crop Science", 3); put("NSTP 2", 3);
        // 2nd Year
        put("Differential Equations", 3); put("Art Appreciation", 3); put("Data Structures and Algorithms", 3);
        put("Engineering Economics", 3); put("Fundamentals of Electrical Circuits", 4); put("Environmental Science", 3);
        put("Computer-Aided Drafting", 2); put("PATHFIT 3", 2); put("Values Education", 3);
        put("Numerical Methods", 3); put("Software Design", 4); put("Purposive Communication", 3);
        put("Fundamentals of Electronic Circuits", 4); put("Life and Works of Rizal", 3); put("Intro to Animal Science", 3);
        put("PATHFIT 4", 2); put("The Contemporary World", 3);
        // 3rd Year
        put("Logic Circuits and Design", 4); put("Operating Systems", 3); put("Data and Digital Communications", 3);
        put("Introduction to HDL", 3); put("Feedback and Control Systems", 3); put("Fundamentals of Mixed Signals and Sensors", 3);
        put("Computer Engineering Drafting and Design", 2); put("Cognate Course 1", 3); put("IT Integration for Ag Dev", 3);
        put("Basic Occupational Health and Safety", 3); put("Computer Networks and Security", 4); put("Microprocessors", 4);
        put("Methods of Research", 3); put("Technopreneurship", 3); put("Ethics", 3);
        put("CpE Laws and Professional Practice", 2); put("Cognate Course 2", 3);
        // 4th Year
        put("Embedded Systems", 4); put("Computer Architecture and Organization", 3); put("Emerging Technologies in CpE", 3);
        put("CpE Practice and Design 1", 2); put("Digital Signal Processing", 3); put("Indigenous Creative Crafts", 3);
        put("Cognate Course 3", 3); put("CpE Practice and Design 2", 2); put("Seminars and Fieldtrips", 1);
        put("On the Job Training", 6); put("Gender and Society", 3);
    }};

    static final List<String> ALL_SUBJECT_NAMES = new ArrayList<>(MASTER_CATALOG.keySet());

    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        int choice;
        do {
            System.out.println("\n===== PSAU-BSCpE INTEGRATED ACADEMIC MANAGEMENT SYSTEM =====");
            System.out.println("1. Register Student\n2. Add Grades\n3. View Records\n4. Remove Student\n5. Exit");
            choice = getValidInt("Enter choice: ");
            switch (choice) {
                case 1 -> registerStudents(students);
                case 2 -> addGrades(students);
                case 3 -> { for (Student s : students) s.displayInfo(); }
                case 4 -> removeStudent(students);
            }
        } while (choice != 5);
    }

    static void registerStudents(ArrayList<Student> list) {
        System.out.print("\nName: "); String name = scanner.nextLine();
        System.out.print("Student Number: "); String sn = scanner.nextLine();
        int year = getValidInt("Year Level (1-4): ");
        int sem = getValidInt("Semester (1 or 2): ");
        System.out.print("Section: "); String section = scanner.nextLine();

        System.out.println("\nRegistration Type:\n1. Regular\n2. Irregular");
        int type = getValidInt("Choice: ");
        Student student = null;

        if (type == 1) {

            List<String> regList = new ArrayList<>();
            if (year == 1 && sem == 1) Collections.addAll(regList, "Calculus 1", "Chemistry for Engineers", "CpE as a Discipline", "Programming Logic and Design", "Math in the Modern World", "STS", "Understanding the Self", "PATHFIT 1", "NSTP 1");
            else if (year == 1 && sem == 2) Collections.addAll(regList, "Calculus 2", "Physics for Engineers", "Object Oriented Programming", "Engineering Data Analysis", "Discrete Mathematics", "Readings in Phil History", "PATHFIT 2", "Intro to Crop Science", "NSTP 2");
            else if (year == 2 && sem == 1) Collections.addAll(regList, "Differential Equations", "Art Appreciation", "Data Structures and Algorithms", "Engineering Economics", "Fundamentals of Electrical Circuits", "Environmental Science", "Computer-Aided Drafting", "PATHFIT 3", "Values Education");
            else if (year == 2 && sem == 2) Collections.addAll(regList, "Numerical Methods", "Software Design", "Purposive Communication", "Fundamentals of Electronic Circuits", "Life and Works of Rizal", "Intro to Animal Science", "PATHFIT 4", "The Contemporary World");
            else if (year == 3 && sem == 1) Collections.addAll(regList, "Logic Circuits and Design", "Operating Systems", "Data and Digital Communications", "Introduction to HDL", "Feedback and Control Systems", "Fundamentals of Mixed Signals and Sensors", "Computer Engineering Drafting and Design", "Cognate Course 1", "IT Integration for Ag Dev");
            else if (year == 3 && sem == 2) Collections.addAll(regList, "Basic Occupational Health and Safety", "Computer Networks and Security", "Microprocessors", "Methods of Research", "Technopreneurship", "Ethics", "CpE Laws and Professional Practice", "Cognate Course 2");
            else if (year == 4 && sem == 1) Collections.addAll(regList, "Embedded Systems", "Computer Architecture and Organization", "Emerging Technologies in CpE", "CpE Practice and Design 1", "Digital Signal Processing", "Indigenous Creative Crafts", "Cognate Course 3");
            else if (year == 4 && sem == 2) Collections.addAll(regList, "CpE Practice and Design 2", "Seminars and Fieldtrips", "On the Job Training", "Gender and Society");

            student = new RegularStudent(name, sn, "BSCpE", year, sem, section, regList.toArray(new String[0]));
        } else {
            System.out.println("\n--- FULL BSCpE SUBJECT CATALOG (1st - 4th Year) ---");
            for (int j = 0; j < ALL_SUBJECT_NAMES.size(); j++) {
                System.out.printf("%2d. %-45s [%d Units]%n", (j + 1), ALL_SUBJECT_NAMES.get(j), MASTER_CATALOG.get(ALL_SUBJECT_NAMES.get(j)));
            }
            int subCount = getValidInt("How many subjects to enroll? ");
            String[] selectedSubjs = new String[subCount];
            for (int j = 0; j < subCount; j++) {
                int sc = getValidInt("Select subject #" + (j + 1) + ": ");
                if (sc >= 1 && sc <= ALL_SUBJECT_NAMES.size()) {
                    selectedSubjs[j] = ALL_SUBJECT_NAMES.get(sc - 1);
                } else {
                    System.out.print("Enter custom subject name: ");
                    selectedSubjs[j] = scanner.nextLine();
                }
            }
            student = new IrregularStudent(name, sn, "BSCpE", year, sem, section, selectedSubjs);
        }

        if (student != null) {
            for (String sub : student.getSubjects()) {
                Integer autoUnits = MASTER_CATALOG.get(sub);
                student.setUnits(sub, (autoUnits != null) ? autoUnits : getValidInt("Units for " + sub + ": "));
                System.out.print("  Professor for [" + sub + "]: "); student.setProfessor(sub, scanner.nextLine());
            }
            list.add(student);
            System.out.println("\n>>> Registration Successful!");
        }
    }

    static void displayStudentRecord(Student s) {
        System.out.println("\n================================================================================");
        System.out.println("NAME           : " + s.getStudentName());
        System.out.println("STUDENT NUMBER : " + s.getStudentNumber());
        System.out.println("COURSE         : " + s.getCourse());
        System.out.println("YEAR & SEM     : Year " + s.getYearLevel() + " - Semester " + s.getSemester());
        System.out.println("STATUS         : " + s.getStudentType());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf(" %-60s | %-30s | %-10s%n", "Subject Title", "Units", "Professor", "Grades");
        System.out.println("--------------------------------------------------------------------------------");
        for (String sub : s.getSubjects()) {
            System.out.printf("%-60s | %-30d | %-10s | %s%n", sub, s.getUnits(sub), s.getProfessor(sub), s.getSubjectGrades().get(sub));
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("WEIGHTED GWA   : %.2f%n", s.computeAverage());
        System.out.println("REMARKS        : " + s.getRemarks());
        if (!(s instanceof IrregularStudent)) System.out.println("ACADEMIC       : " + s.getAcademicStatus());
        System.out.println("================================================================================");
    }

    static void addGrades(ArrayList<Student> list) {
        if (list.isEmpty()) return;
        for (int i = 0; i < list.size(); i++) System.out.println((i + 1) + ". " + list.get(i).getStudentName());
        int idx = getValidInt("Select student: ") - 1;
        if (idx < 0 || idx >= list.size()) return;
        Student s = list.get(idx);
        for (String sub : s.getSubjects()) {
            while (true) {
                try {
                    System.out.print("Grade for " + sub + ": ");
                    s.addGrade(sub, Double.parseDouble(scanner.nextLine()));
                    break;
                } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
            }
        }
    }

    static void removeStudent(ArrayList<Student> list) {
        if (list.isEmpty()) return;
        for (int i = 0; i < list.size(); i++) System.out.println((i + 1) + ". " + list.get(i).getStudentName());
        int idx = getValidInt("Remove #: ") - 1;
        if (idx >= 0 && idx < list.size()) list.remove(idx);
    }

    static int getValidInt(String p) {
        while (true) {
            try {
                System.out.print(p);
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a numerical value.");
            }
        }
    }
}