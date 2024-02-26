import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

public class Client {
    private static final JDBCManger jdbcManger=new JDBCManger();
    private static DataOutputStream sendData;
    private static DataInputStream receiveData;
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        System.out.println("Welcome to the Student Grading System !");
        Socket socket = new Socket("localhost", 8000);
        boolean isAuthenticated = false;

        while (!isAuthenticated) {
            System.out.println("Enter your username :");
            String userName = in.nextLine();
            System.out.println("Enter your password :");
            String password = in.nextLine();
            if(!userName.equalsIgnoreCase("admin1")){
                password=hashPassword(password);
            }
            sendData = new DataOutputStream(socket.getOutputStream());
            receiveData = new DataInputStream(socket.getInputStream());

            sendData.writeUTF(userName);
            sendData.writeUTF(password);

            isAuthenticated = receiveData.readBoolean();
            if (!isAuthenticated)
                System.out.println("Invalid username or password. Please try again.");
        }
        String role = receiveData.readUTF();
        int logInID = receiveData.readInt();
        System.out.println(logInID);

        int choice = -1;
        if(role.equals("admin")){
            while(choice!=0){
                menuForAdmin();
                choice = in.nextInt();

                sendData.writeInt(choice);
                in.nextLine();
                switch (choice){
                    case 0:
                        break;
                    case 1:
                        handleLogin();
                        System.out.println("Enter the student Name :");
                        String studentName=in.nextLine();
                        sendData.writeUTF(studentName);

                        System.out.println("Student added Successfully");
                        break;
                    case 2:
                        handleLogin();
                        System.out.println("Enter the instructor Name :");
                        String instructorName=in.nextLine();
                        sendData.writeUTF(instructorName);

                        System.out.println("Instructor added Successfully");
                        break;
                    case 3:
                        System.out.println("Enter Course name : ");
                        String courseName=in.nextLine();
                        sendData.writeUTF(courseName);
                        System.out.println("Course added successfully");
                        break;
                    case 4:
                        jdbcManger.printClasses();
                        break;
                    case 5:
                        System.out.println("Choose a student :");
                        jdbcManger.showAllStudent();
                        String stName=in.nextLine();
                        sendData.writeUTF(stName);
                        int stID=receiveData.readInt();
                        System.out.println("Courses "+stName+" Assigned in :");
                        jdbcManger.showCoursesForStudent(stID);
                        System.out.println("Courses Available to enroll \n choose one to assign it to "+stName+" :");
                        jdbcManger.showCoursesAvailableForStudent(stID);
                        courseName=in.nextLine();
                        sendData.writeUTF(courseName);
                        System.out.println("Course added successfully");
                        System.out.println();
                        break;
                    case 6:
                        System.out.println("Choose an instructor :");
                        jdbcManger.showAllInstructor();
                        String instName=in.nextLine();
                        sendData.writeUTF(instName);
                        int instID=receiveData.readInt();
                        System.out.println("Courses "+instName+" Assigned in :");
                        jdbcManger.showCourses(instID);
                        System.out.println();
                        System.out.println("Courses Available to enroll \n choose one to assign it to "+instName+" :");
                        jdbcManger.showCoursesAvailableForInstructor(instID);
                        courseName=in.nextLine();
                        sendData.writeUTF(courseName);
                        System.out.println("Course added Successfully");
                        System.out.println();
                        break;
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
        }
        else if (role.equals("instructor")) {
            while (choice !=0){
                menuForInstructor();
                choice=in.nextInt();

                sendData.writeInt(choice);
                in.nextLine();

                switch (choice){
                    case 0:
                        break;
                    case 1:
                        System.out.println("Enter the course name :");
                        jdbcManger.showCourses(jdbcManger.getID(logInID));
                        String course=in.nextLine();
                        sendData.writeUTF(course);
                        int courseID=receiveData.readInt();
                        System.out.println("Enter the student name :");
                        jdbcManger.showStudentInSpecificCourse(courseID);
                        String studentName=in.nextLine();
                        sendData.writeUTF(studentName);
                        while(!studentName.equals("0")){
                            System.out.println("Insert the grade :");
                            int grade=in.nextInt();
                            in.nextLine();
                            sendData.writeInt(grade);
                            System.out.println("Grade Added successfully");
                            System.out.println();
                            System.out.println("Enter the student name :");
                            System.out.println("If you want to exit press (0)");
                            studentName=in.nextLine();

                            sendData.writeUTF(studentName);
                        }
                        break;
                    case 2:
                        int instructorID=receiveData.readInt();
                        System.out.println("Your Courses : ");
                        jdbcManger.showCourses(instructorID);
                        System.out.println();
                        break;
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
        }
        else if (role.equals("student")) {
            while (choice !=0){
                menuForStudent();
                choice=in.nextInt();

                sendData.writeInt(choice);
                in.nextLine();

                switch (choice){
                    case 0:
                        break;
                    case 1:
                        System.out.println("Your Courses :");
                        int userID=receiveData.readInt();
                        jdbcManger.showCoursesForStudent(userID);
                        System.out.println();
                        break;
                    case 2:
                        System.out.println("Your Grades :");
                        userID=receiveData.readInt();
                        jdbcManger.showGrades(userID);
                        System.out.println();
                        break;
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
        }

    }

    public static void menuForAdmin() {
        System.out.println("1-Add a student");
        System.out.println("2-Add an instructor");
        System.out.println("3-Add a course");
        System.out.println("4-Show All Classes");
        System.out.println("5-Assign Course to student");
        System.out.println("6-Assign Course to instructor");
        System.out.println("Press 0 to Exit");
    }

    public static void menuForStudent() {
        System.out.println("1-Show courses");
        System.out.println("2-Show grades");
        System.out.println("Press 0 to Exit");
    }

    public static void menuForInstructor() {
        System.out.println("1-Input grades");
        System.out.println("2-Show courses for you");
        System.out.println("Press 0 to Exit");
    }

    public static void handleLogin() throws IOException, NoSuchAlgorithmException {
        System.out.println("Create a user name :");
        String userName1=in.nextLine();

        System.out.println("Create a password :");
        String pass=in.nextLine();

        String hashedPassword = hashPassword(pass);

        sendData.writeUTF(userName1);
        sendData.writeUTF(hashedPassword);
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());

        // Convert byte array to hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
