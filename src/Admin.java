import java.sql.*;
import java.util.InputMismatchException;

public class Admin extends User {
    private String name;
    private int admin_id;

    public Admin() {
        this.userType = UserType.ADMIN;
        this.isLoggedOut = false;
    }

    @Override
    public boolean ValidateUser() {
        String passWord;
        System.out.println("Login Details: ");
        System.out.println("Enter your UserName: ");
        this.userName = Main.sc.next();
        System.out.println("Enter your Password: ");
        passWord = Main.sc.next();
        ResultSet rs = DataBase.getUser(this.userName, "Administrator");
        String dbPass = "";
        try {
            while (rs.next()) {
                this.name = rs.getString("name");
                dbPass = rs.getString("password");
                this.admin_id = rs.getInt("admin_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            return dbPass.equals(passWord);
        }
    }

    @Override
    public void DisplayOptions() {
        //List of all the options of the admin
        System.out.println("Admin Options:");
        System.out.println("1. Manage Course Catalog");
        System.out.println("2. Manage Student Records");
        System.out.println("3. Assign Professors to Courses");
        System.out.println("4. Handle Complaints");
        System.out.println("5. Logout");


        int choice = 0;
        try{
            choice=Main.sc.nextInt();
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
        }
        switch (choice) {
            case 1:
                manageCourseCatalog();
                break;
            case 2:
                manageStudentRecords();
                break;
            case 3:
                assignProfessorsToCourses();
                break;
            case 4:
                handleComplaints();
                break;
            case 5:
                this.isLoggedOut = true; // Log out the admin
                System.out.println("Logging out...");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }

    }

    private void manageCourseCatalog() {
        System.out.println("Manage Course Catalog:");
        //List of all options to manage course catalog
        System.out.println("1. Add a Course");
        System.out.println("2. Delete a Course");
        System.out.println("3. View Course Catalog");
        System.out.print("Enter your choice: ");
        int choice = 0;
        try{
            choice=Main.sc.nextInt();
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
        }
        switch (choice) {
            case 1:
                addCourse();
                break;
            case 2:
                deleteCourse();
                break;
            case 3:
                viewCourseCatalog();
                break;
            default:
                System.out.println("Invalid choice");
        }
    }

    // Implementation of  course management logic here
    private void addCourse() {
        System.out.print("Enter Course Name: ");
        String courseName = Main.sc.next();
        System.out.print("Enter Credits: ");
        int credits = Main.sc.nextInt();
        System.out.print("Enter Professor ID: ");
        int professorId = Main.sc.nextInt();
        System.out.println("Enter the Timings of the Course");
        String timings = Main.sc.next();
        System.out.println("Enter the Number of Prerequisite Courses");
        int noOfPre=Main.sc.nextInt();
        StringBuffer st=new StringBuffer("");
        System.out.println("Enter all the Prerequisite Course ID's");
        for(int i=0;i<noOfPre;i++){
            int j=Main.sc.nextInt();
            st.append(j);
            if(i!=noOfPre-1){
                st.append(",");
            }
        }

        String checkSql = String.format("SELECT COUNT(*) as count FROM Course WHERE   title = '%s' AND professor_id = %d",courseName,professorId);
        int c=0;
        try
        {
            ResultSet rs=DataBase.statement.executeQuery(checkSql);
            while(rs.next())
            {
                c=rs.getInt("count");
            }

        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
            return;
        }
        if(c==1)
        {
            System.out.println("Course Already Exits ");
            return;
        }
        try
        {
            String Query=String.format("INSERT INTO Course (title,credits,professor_id,prerequisites,timing) VALUES('%s',%d,%d,'%s','%s')",courseName,credits,professorId,st,timings);
            int rs=DataBase.statement.executeUpdate(Query);
            if(rs>0)
            {
                System.out.println("Course Is Added");
            }
            else{
                System.out.println("Error While Adding the Course.Please Try Again");
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }

    }

    private void deleteCourse() {
        System.out.print("Enter Course ID or Course Code to be removed/deleted: ");
        String courseIdentifier = Main.sc.next();
        try
        {
            String query=String.format("DELETE FROM Course WHERE course_id = %s", courseIdentifier);
            int rowsAffected=DataBase.statement.executeUpdate(query);
            if(rowsAffected>0)
            {
                System.out.println("Course Deleted");
                viewCourseCatalog();
            }
            else{
                System.out.println("No Such Course Exits");
            }
        } catch (SQLException e) {
            System.out.println("Error while removing course: " + e.getMessage());
        }
    }


    private void viewCourseCatalog() {
        String sql = "SELECT * FROM Course"; // Adjust based on your table

        try (ResultSet rs=DataBase.statement.executeQuery(sql)) {

            // Check if there are any courses
            if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
                System.out.println("No courses found in the catalog.");
                return;
            }

            // Print the header
            System.out.printf("%-10s %-30s  %-15s%n", "Course ID", "Course Name", "Credits");
            System.out.println("-------------------------------------------------------------");

            // Iterate through the ResultSet and print course details
            while (rs.next()) {
                int courseId = rs.getInt("course_id"); // Adjust based on your column names
                String courseName = rs.getString("title");
                int credits = rs.getInt("credits");

                System.out.printf("%-10d %-30s  %-15d%n", courseId, courseName, credits);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving course catalog: " + e.getMessage());
        }
    }

    private void manageStudentRecords() {
        System.out.println("Manage Student Records:");
        System.out.println("1. View Student Records");
        System.out.println("2. Update Student Record");
        System.out.println("3. Add New Student Record");
        int choice=0 ;
               try{
                   choice=Main.sc.nextInt();
               }catch(InputMismatchException ex)
               {
                   System.out.println(ex.getMessage());
                   return;
               }
        Main.sc.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.println("Displaying all student records...");
                viewStudentRecords();
                break;
            case 2:
                updateStudentRecord();
                break;
            default:
                System.out.println("Invalid choice.");
        }

    }

    // Logic to retrieve and display student records from the database
    private void viewStudentRecords() {
        // change attribute names if necessary
        String sql = "SELECT * FROM Student"; // Adjust based on your table structure

        try (ResultSet rs = DataBase.statement.executeQuery(sql)) {

            // Check if there are any student records
            if (!rs.isBeforeFirst()) { //no student records
                System.out.println("No student records found.");
                return;
            }

            //To print the header
            System.out.printf("%-10s %-30s %-15s %-15s%n", "Student ID", "Student Name", "Email", "Major");
            System.out.println("-------------------------------------------------------------");

            // Iterating through the ResultSet and printing student details
            while (rs.next()) {
                int studentId = rs.getInt("student_id"); // Adjust based on your column names
                String studentName = rs.getString("name");
                String email = rs.getString("email");
                String major = rs.getString("semester");

                System.out.printf("%-10d %-30s %-15s %-15s%n", studentId, studentName, email, major);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving student records: " + e.getMessage());
        }
    }

    // changes are required
    private void updateStudentRecord() {
        System.out.print("Enter Student ID to update: ");
        int studentId = 0;
        try{studentId=Main.sc.nextInt();}
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
            return;
        }
        Main.sc.nextLine(); // Consume newline

        System.out.print("Enter new Email: ");
        String newEmail = Main.sc.nextLine();
        // change attribute names
        String sql = "UPDATE Student SET  email = ? WHERE student_id = ?";

        try (// Use the existing connection from DataBase
             PreparedStatement pstmt = DataBase.connection.prepareStatement(sql)) {

            // To set the parameters for the SQL query
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, studentId); // Assuming student_id is an int

            // Execution to update operation
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student record updated successfully!");
            } else {
                System.out.println("No student found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating student record: " + e.getMessage());
        }
    }

    private void assignProfessorsToCourses() {

        System.out.print("Enter Course ID: ");
        int courseId = 0;
        try{
            courseId=Main.sc.nextInt();
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
        }
        Main.sc.nextLine();
        System.out.print("Enter Professor ID: ");
        int professorId = 0;
        try{
            professorId=Main.sc.nextInt();
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
        }
        String sql = "UPDATE  Course11 SET professor_id=? WHERE  Course_id=?";

        try (PreparedStatement pstmt = DataBase.connection.prepareStatement(sql)) {

            // parameters for the SQL query
            pstmt.setInt(2, courseId);
            pstmt.setInt(1, professorId);

            //insert operation
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Professor assigned to course successfully!");
            } else {
                System.out.println("Error: Professor could not be assigned to the course.");
            }
        } catch (SQLException e) {
            System.out.println("Error while assigning professor to course: " + e.getMessage());
        }
    }

    private void handleComplaints() {
        System.out.println("Handling Complaints...");

        // Display all complaints
        String sql = "SELECT * FROM Complaint";
        try (ResultSet rs = DataBase.statement.executeQuery(sql)) {
            if (!rs.isBeforeFirst()) {
                System.out.println("No complaints found.");
                return;
            }


            System.out.printf("%-10s %-30s %-15s %-15s%n", "Complaint ID", "Complaint Description", "Status", "Date");
            System.out.println("-------------------------------------------------------------");

            // Iterate through the ResultSet and print complaint details
            while (rs.next()) {
                int complaintId = rs.getInt("complaint_id");
                String description = rs.getString("description");
                String status = rs.getString("status");
                Date date = rs.getDate("date");

                System.out.printf("%-10d %-30s %-15s %-15s%n", complaintId, description, status, date);
            }
            updateComplaintStatus();
        } catch (SQLException e) {
            System.out.println("Error while retrieving complaints: " + e.getMessage());
        }
    }


    private void updateComplaintStatus() {
        System.out.print("Enter Complaint ID to update: ");
        int complaintId = 0;
        try{
            complaintId=Main.sc.nextInt();
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
        }
        Main.sc.nextLine(); // Consume newline

        System.out.print("Enter new status (Pending/Resolved): ");
        String newStatus = Main.sc.nextLine();
        System.out.println("Enter the Resolution Details");
        String resolutionDetails=Main.sc.nextLine();
        String sql = "UPDATE Complaint SET status = ?,resolution_details=? WHERE complaint_id = ?";

        try (
             PreparedStatement pstmt = DataBase.connection.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, resolutionDetails);
            pstmt.setInt(3, complaintId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Complaint status updated successfully!");
            } else {
                System.out.println("No complaint found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error while updating a complaint: " + e.getMessage());
        }
    }
}


