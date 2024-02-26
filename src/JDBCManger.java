import java.sql.*;
import java.util.List;

public class JDBCManger {
    public Connection connectToDB() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost/gradesystem", "Ruwaid", "Pirate65");
    }

    public boolean authenticateUser(String userName, String password) {
        String query = "SELECT * FROM login WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connectToDB().prepareStatement(query)) {
            statement.setString(1, userName);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getRole(String username, String password) throws SQLException {
        String query = "SELECT role FROM login WHERE username = ? AND password = ?";
        ResultSet resultSet = logInResult(username, password, query);
        resultSet.next();
        return resultSet.getString("role");
    }

    public Integer getID(String username, String password) throws SQLException {
        String query = "SELECT id FROM login WHERE username = ? AND password = ?";
        ResultSet resultSet = logInResult(username, password, query);
        resultSet.next();
        return resultSet.getInt("id");
    }

    public ResultSet logInResult(String username, String password, String query) throws SQLException {
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);
        return statement.executeQuery();
    }

    public void insertIntoLogin(String userName, String password, String role) throws SQLException {
        String query = "INSERT INTO login (userName,password,role) VALUES(? , ? , ?)";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setString(1, userName);
        statement.setString(2, password);
        statement.setString(3, role);
        statement.executeUpdate();
    }

    public void insertIntoStudent(String name, int loginID) throws SQLException {
        String query = "INSERT INTO user (name,loginID) VALUES(? , ?)";
        insertINTO(query,name,loginID);
    }

    public Integer getUserID(String name ) throws SQLException {
        String query = "SELECT userID FROM user WHERE name = ?";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("userID");
    }
    public Integer getInstructorID(String name ) throws SQLException {
        String query = "SELECT instructorID FROM instructor WHERE name = ?";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("instructorID");
    }

    public Integer getUserID(int loginID ) throws SQLException {
        String query = "SELECT userID FROM user WHERE loginID = ?";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setInt(1, loginID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("userID");
    }

    public Integer getID(String courseName) throws SQLException {
        String query = "SELECT courseID FROM course WHERE courseName = ? ";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setString(1, courseName);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("courseID");
    }

    public void insertIntoInstructor(String name, int loginID) throws SQLException {
        String query = "INSERT INTO instructor (name,loginID) VALUES(? , ?)";
        insertINTO(query,name,loginID);
    }

    public void insertIntoCourse(String courseName) throws SQLException {
        String query = "INSERT INTO course (courseName) VALUES(?)";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setString(1, courseName);
        statement.executeUpdate();
    }

    public void printClasses() {
        String sqlQuery = "SELECT courseName, instructor.name, GROUP_CONCAT(user.name) AS studentNames " +
                "FROM course, instructor, user, student_course " +
                "WHERE instructor.instructorID = course.instructorID " +
                "AND student_course.stID = user.userID " +
                "AND student_course.courseID = course.courseID " +
                "GROUP BY courseName, instructor.name";

        try (Statement statement = connectToDB().createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-20s", metaData.getColumnName(i));
            }
            System.out.println();

            // Print rows
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.printf("%-20s", resultSet.getString(i));
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showCourses(int id) throws SQLException {
        String query = "select courseName from course where instructorID=?";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        int i = 1;
        while (resultSet.next()) {
            System.out.println(i + "-" + resultSet.getString("courseName"));
            i++;
        }
    }
    public void showStudentInSpecificCourse(int id) throws SQLException {
            String query = "select name from user,student_course where student_course.courseID=? and user.userID=student_course.stID;";
            PreparedStatement statement = connectToDB().prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            int i=1;
            while (resultSet.next()) {
                System.out.println(i+"-"+resultSet.getString("name"));
                i++;
            }

    }

    public Integer getID(int loginID) throws SQLException {
        String query = "SELECT instructorID FROM instructor WHERE loginID = ?";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setInt(1, loginID);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt("instructorID");

    }
    public void insertIntoGrade(int grade,int stID,int courseID) throws SQLException {
        String query = "INSERT INTO grades (grade,stID,courseID) VALUES(?,?,?)";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setInt(1, grade);
        statement.setInt(2, stID);
        statement.setInt(3, courseID);
        statement.executeUpdate();
    }
    public void showGrades(int userID) throws SQLException {
        String query = "select name, courseName,grade from user,course,grades where user.userID=grades.stID and course.courseID=grades.courseID and grades.stID=?";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setInt(1, userID);
        ResultSet resultSet = statement.executeQuery();
        int i = 1;
        while (resultSet.next()) {
            System.out.println(i + "-" + resultSet.getString("courseName") +" : "+resultSet.getInt("grade"));
            i++;
        }
    }
    public void showCoursesForStudent(int id) throws SQLException {
        String query = "SELECT course.courseName FROM course\n" +
                "INNER JOIN student_course ON course.courseID = student_course.courseID\n" +
                "INNER JOIN user ON student_course.stID = user.userID\n" +
                "WHERE user.userID = ?";
        selectSomeCourses(query,id);
    }
    public void showCoursesAvailableForStudent(int id) throws SQLException {
        String query = "SELECT DISTINCT course.courseName \n" +
                "FROM course\n" +
                "LEFT JOIN student_course ON course.courseID = student_course.courseID AND student_course.stID = ?\n" +
                "WHERE student_course.courseID IS NULL\n";
        selectSomeCourses(query,id);
    }
    public void insertINTO(String query,String name,int loginID) throws SQLException {
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setString(1, name);
        statement.setInt(2, loginID);
        statement.executeUpdate();
    }
    public void showAllStudent() throws SQLException {
        String query="select name from user";
        PreparedStatement statement=connectToDB().prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        int i = 1;
        while (resultSet.next()) {
            System.out.println(i + "-" + resultSet.getString("name"));
            i++;
        }
    }
    public void showAllInstructor() throws SQLException {
        String query="select name from instructor";
        PreparedStatement statement=connectToDB().prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        int i = 1;
        while (resultSet.next()) {
            System.out.println(i + "-" + resultSet.getString("name"));
            i++;
        }
    }
    public void selectSomeCourses(String query,int id) throws SQLException {
        PreparedStatement statement = connectToDB().prepareStatement(query);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        int i = 1;
        while (resultSet.next()) {
            System.out.println(i + "-" + resultSet.getString("courseName"));
            i++;
        }
    }

    public void insertINTOStudentCourse(int stID,int courseID) throws SQLException {
        String query="INSERT INTO student_course (stID,courseID) VALUES(?,?)";
        PreparedStatement statement= connectToDB().prepareStatement(query);
        statement.setInt(1,stID);
        statement.setInt(2,courseID);
        statement.executeUpdate();
    }
    public void showCoursesAvailableForInstructor(int id) throws SQLException {
        String query = "select courseName from course where instructorID is null";
        PreparedStatement statement = connectToDB().prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        int i = 1;
        while (resultSet.next()) {
            System.out.println(i + "-" + resultSet.getString("courseName"));
            i++;
        }
    }
    public void assignCourseToInstructor(int courseID,int instructorID) throws SQLException {
        String query="update course set instructorID=? where courseID=?";
        PreparedStatement statement= connectToDB().prepareStatement(query);
        statement.setInt(1,instructorID);
        statement.setInt(2,courseID);
        statement.executeUpdate();
    }

}
