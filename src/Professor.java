import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;

public class Professor extends User {
    private int professor_id;
    private String name;
    public Professor()
    {
        this.userType=UserType.PROFESSOR;
        this.isLoggedOut=false;

    }
    @Override
    public boolean ValidateUser()
    {
        String password;
        System.out.println("Login Details: ");
        System.out.println("Enter your UserName:  ");
        this.userName=Main.sc.next();
        System.out.println("Enter your Password:  ");
        password=Main.sc.next();
        ResultSet rs=DataBase.getUser(this.userName,"Professor");
        String dbPass="";
        try
        {
            while(rs.next())
            {
                this.name=rs.getString("name");
                dbPass=rs.getString("password");
                this.professor_id=rs.getInt("professor_id");
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            return dbPass.equals(password);
        }
    }
    @Override
    public void DisplayOptions()
    {
        System.out.println("Choose from the Below");
        System.out.println("1. Manage Courses");
        System.out.println("2. View Enrolled Students");
        System.out.println("3. Assign Grades");
        System.out.println("4. Logout");
        int choice=0;
        try{
            choice=Main.sc.nextInt();
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
            return;
        }
        if(choice==1)
        {
            manageCourses();
        }
        else if(choice==2)
        {
            viewStudents();
        }
        else if(choice==3)
        {
            giveGrades();
        }
        else if(choice==4)
        {
            isLoggedOut=true;
        }
        else{
            System.out.println("Enter a valid Choice");
        }
    }
    public void giveGrades()
    {
        viewStudents();
        System.out.println("Select the Student ID To Give Grade");
        int choice;
        try{
            choice=Main.sc.nextInt();
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
            return;
        }
        ResultSet rs=null;
        try{
            String Query=String.format("Select * FROM Course c JOIN Student_Course sc ON c.course_id=sc.course_id JOIN Student s ON s.student_id=sc.student_id WHERE c.professor_id=%d AND s.student_id=%d",this.professor_id,choice);
            rs=DataBase.statement.executeQuery(Query);
            System.out.printf("+------------+\n");
            System.out.printf("| Course ID  |\n");
            System.out.printf("+------------+\n");
            while(rs.next())
            {
                int course_id=rs.getInt("course_id");
                System.out.printf("|    %d    |\n", course_id);
            }
            System.out.printf("+------------+\n");
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Select the Course To Give Grade");
        int choice2;
        try {
            choice2=Main.sc.nextInt();
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
            return;
        }
        System.out.println("Enter the Grade");
        int grade;
        try{
            grade=Main.sc.nextInt();
        }catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
            return;
        }
        try{
            String Query=String.format("INSERT INTO Grade (course_id,student_id,grade) VALUES(%d,%d,%d)",choice2,choice,grade);
            int rowsAffected=DataBase.statement.executeUpdate(Query);
            if(rowsAffected>0)
            {
                System.out.println("Grade Added");
            }else {
                System.out.println("Grade Not Added");
            }
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            return;
        }
    }
    public int dispCourse()
    {
        //Show the course of the Professor
        ResultSet rs=null;
        ArrayList<Integer>courses=new ArrayList<Integer>();
        String format="%-10s%-30s%-15s%n";
        try
        {
            String query=String.format("SELECT * FROM Course WHERE professor_id =%d",this.professor_id);
            rs= DataBase.statement.executeQuery(query);
            System.out.println("-------------------------------------------------------------");
            System.out.printf(format,"CourseID","CoureName","Timings");
            while(rs.next())
            {
                String courseId=rs.getString("course_id");
                String name=rs.getString("title");
                String timing=rs.getString("timing");
                System.out.printf(format,courseId,name,timing);
                courses.add(Integer.parseInt(courseId));
            }
            System.out.println("-------------------------------------------------------------");
            System.out.println("Enter the Course ID of the Course to Update");
            int choice=Main.sc.nextInt();
            boolean check=false;
            for(int i=0;i<courses.size();i++)
            {
                if(courses.get(i)==choice)
                {
                    check=true;
                    break;
                }
            }
            if(check) {
                return choice;
            }
            else{
                System.out.println("No course Found");
                return -100;
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return -100;
    }
    public void updateSyllabus(int choice)
    {
        String newSyllabus;
        System.out.println("Enter the new Syllabus");
        Main.sc.nextLine();
        newSyllabus=Main.sc.nextLine();
        try
        {
            String Query=String.format("UPDATE Course SET syllabus='%s' WHERE course_id=%d",newSyllabus,choice);
            int rs=DataBase.statement.executeUpdate(Query);
            if(rs>0)
            {
                System.out.println("The Syllabus has been updated");
            }else{
                System.out.println("Error while Updating the Syllabus");
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public void manageCourses() {
        int choice = dispCourse();
        if (choice == -100) {
            return;
        }
        System.out.println("Enter the Field To Edit");
        System.out.println("1. Syllabus");
        System.out.println("2.Timings");
        System.out.println("3. Enrollment Limit");
        System.out.println("4. Credits");
        System.out.println("5. Prerequisites");
        try {
            int fChoice = Main.sc.nextInt();
            if(fChoice==1)
            {
                updateSyllabus(choice);
            }else if(fChoice==2)
            {
                updateTimings(choice);
            }
            else if(fChoice==3)
            {
                updateEnrollmentLimit(choice);
            }
            else if(fChoice==4)
            {
                updateCredits(choice);
            }
            else if(fChoice==5)
            {
                updatePrereq(choice);
            }
            else {
                System.out.println("Enter A valid Choice");
            }
        } catch (InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    private void updatePrereq(int choice) {
        System.out.println("Enter the Number of Prerequisites");
        int noOfpreq=Main.sc.nextInt();
        if(noOfpreq<0)
        {
            System.out.println("Enter a valid number ");
            return;
        }
        StringBuffer pre=new StringBuffer("");
        System.out.println("Enter the Prerequisite ID's");
        while(noOfpreq>0)
        {
            int prereqId=Main.sc.nextInt();
            pre.append(prereqId);
            noOfpreq--;
            if(noOfpreq!=0)
            {
                pre.append(",");
            }
        }
        try{
            String query=String.format("UPDATE Course SET prerequisites = '%s' WHERE course_id=%d",pre,choice);
            int rs=DataBase.statement.executeUpdate(query);
            if(rs>0)
            {
                System.out.println("The Prerequisites Have Been Updated");
            }
            else {
                System.out.println("Error while Updating the Prerequisites");
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void updateCredits(int choice) {
        System.out.println("Enter the New Credits");
        try
        {
            int credits=Main.sc.nextInt();
            if(credits!=2 &&  credits!=4){
                System.out.println("Invalid Credits");
                return;
            }
            String Query=String.format("UPDATE Course SET credits = %d WHERE course_id=%d",credits,choice);
            try{
                int rs= DataBase.statement.executeUpdate(Query);
                if(rs>0) {
                    System.out.println("Credits updated");
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void updateEnrollmentLimit(int choice)
    {
        int newEr;
        System.out.println("Enter the New Enrollment Limit");
        try{
            newEr=Main.sc.nextInt();
            String Query=String.format("UPDATE Course SET enrollment_limit = %d WHERE course_id=%d",newEr,choice);
            try{
                int rs=DataBase.statement.executeUpdate(Query);
                if(rs>0){
                    System.out.println("Enrollment limit updated");
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }catch(InputMismatchException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void updateTimings(int choice) {
        String new_timings;
        System.out.println("Enter the New Timings");
        Main.sc.nextLine();
        new_timings=Main.sc.nextLine();
        try
        {
            String query=String.format("UPDATE Course SET timing ='%s' WHERE course_id=%d",new_timings,choice);
            int rs=DataBase.statement.executeUpdate(query);
            if(rs>0)
            {
                System.out.println("The Timings has been updated");
            }
            else{
                System.out.println("Error while Updating the Timings");
            }
        }catch(SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void viewStudents()
    {
        try{
            String query = String.format("SELECT c.course_id, s.student_id, s.name AS student_name, s.email, s.sgpa,s.cgpa FROM Student s JOIN Student_Course sc ON s.student_id = sc.student_id JOIN Course c ON sc.course_id = c.course_id WHERE c.professor_id=%d ORDER BY c.course_id",this.professor_id);
            ResultSet resultset= DataBase.statement.executeQuery(query);

            System.out.printf("%-10s %-12s %-20s %-30s %-10s %-10s%n", "Course ID", "Student ID", "Student Name", "Email", "SGPA", "CGPA");
            System.out.println("---------------------------------------------------------------------------------------------");

            while (resultset.next()){
                int cid=resultset.getInt("course_id");
                int sid=resultset.getInt("student_id");
                String name = resultset.getString("student_name");
                String Email = resultset.getString("email");
                float Cgpa = resultset.getFloat("Cgpa");
                float Sgpa = resultset.getFloat("Sgpa");

                System.out.printf("%-10d %-12d %-20s %-30s %-10.2f %-10.2f%n", cid, sid, name, Email, Sgpa, Cgpa);

            }
            System.out.println("---------------------------------------------------------------------------------------------");

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
}


