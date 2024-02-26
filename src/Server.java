import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Server {
    private static DataOutputStream sendData;
    private static DataInputStream receiveData;
    private static final JDBCManger jdbcManger = new JDBCManger();
    private static String userName;
    private static String password;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(8000);
        Socket socket = serverSocket.accept();
        boolean isAuthenticated = false;
        while (!isAuthenticated) {
            receiveData = new DataInputStream(socket.getInputStream());
             userName = receiveData.readUTF();
             password = receiveData.readUTF();

            isAuthenticated=jdbcManger.authenticateUser(userName,password);

            sendData=new DataOutputStream(socket.getOutputStream());
            sendData.writeBoolean(isAuthenticated);
        }

        String role=jdbcManger.getRole(userName,password);
        sendData.writeUTF(role);
        int loginID= jdbcManger.getID(userName,password);
        sendData.writeInt(loginID);

        int choice=-1;
        if (role.equals("admin")){
            while (choice!=0){
                choice=receiveData.readInt();
                switch (choice) {
                    case 0:
                        break;
                    case 1:
                        String userName1, pass;
                        userName1 = receiveData.readUTF();
                        pass = receiveData.readUTF();
                        jdbcManger.insertIntoLogin(userName1, pass, "student");

                        int loginid = jdbcManger.getID(userName1, pass);
                        String studentName =  receiveData.readUTF();

                        jdbcManger.insertIntoStudent(studentName, loginid);

                        break;
                    case 2:
                        String userName2, pass2;
                        userName2 = receiveData.readUTF();
                        pass2 = receiveData.readUTF();
                        jdbcManger.insertIntoLogin(userName2, pass2, "Instructor");

                        int loginid2 = jdbcManger.getID(userName2, pass2);
                        String instructorName = receiveData.readUTF();

                        jdbcManger.insertIntoInstructor(userName2, loginid2);

                        break;
                    case 3:
                        String courseName = receiveData.readUTF();
                        jdbcManger.insertIntoCourse(courseName);

                    case 4:
                        jdbcManger.printClasses();
                        break;
                    case 5:
                        String stName=receiveData.readUTF();
                        int stID=jdbcManger.getUserID(stName);
                        sendData.writeInt(stID);

                        courseName=receiveData.readUTF();
                        int courseID=jdbcManger.getID(courseName);
                        jdbcManger.insertINTOStudentCourse(stID,courseID);
                        break;
                    case 6:
                        String instName=receiveData.readUTF();
                        int instID= jdbcManger.getInstructorID(instName);
                        sendData.writeInt(instID);
                        courseName=receiveData.readUTF();
                        courseID= jdbcManger.getID(courseName);
                        jdbcManger.assignCourseToInstructor(courseID,instID);
                        break;
                }
            }
        }
        else if (role.equals("instructor")) {
            while (choice !=0){
                choice=receiveData.readInt();

                switch (choice){
                    case 0:
                        break;
                    case 1:
                        jdbcManger.showCourses(jdbcManger.getID(loginID));
                        String course=receiveData.readUTF();
                        int courseID=jdbcManger.getID(course);
                        sendData.writeInt(courseID);
                        String studentName=receiveData.readUTF();
                        int stID= jdbcManger.getUserID(studentName);
                        while (!studentName.equals("0")){
                            int grade=receiveData.readInt();
                            jdbcManger.insertIntoGrade(grade,stID,courseID);
                            studentName=receiveData.readUTF();
                        }
                        break;
                    case 2:
                        int instructorID=jdbcManger.getID(loginID);
                        sendData.writeInt(instructorID);
                        break;

                }
            }
        }

        else if (role.equals("student")) {
            while (choice != 0) {
                choice = receiveData.readInt();

                switch (choice) {
                    case 0:
                        break;
                    case 1:
                        int userID= jdbcManger.getUserID(loginID);
                        sendData.writeInt(userID);
                        break;
                    case 2:
                        userID= jdbcManger.getUserID(loginID);
                        sendData.writeInt(userID);
                }
            }
        }
    }

}
