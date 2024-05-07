// project2 class

/**
 * Creates a company
 * Reads the input files and executes the required methods
 * 
 * @author Yusuf Anil Yazici
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class project2 {

    public static void main(String[] args) {

        String inputFile1 = "src/initial9.txt"; // Parameters thats specify the input and output files.
        String inputFile2 = "src/input9.txt";
        String outputFile = "output.txt";

        // Create a writer and specify the output path.
        FileWrite.initWriter(outputFile);

        HashTable<Branch> company = new HashTable<>(29); // Create a company hashtable that stores branches in it.

        // Process the initial state input file.
        processInitialState(inputFile1, company);

        // Process monthly updates.
        processMonthlyUpdates(inputFile2, company);

        // Close the writer.
        FileWrite.closeWriter();

    }

    private static void processInitialState(String fileName, HashTable<Branch> company) {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Parse the line to extract city, district, employee name, and position
                String[] parts = line.split(", ");
                if (parts.length == 4) {
                    String city = parts[0];
                    String district = parts[1];
                    String employeeName = parts[2];
                    String position = parts[3];

                    // Check if the branch exists, if not, create the branch
                    Branch branch = (Branch) company.get(getKey(city, district));
                    if (branch == null) {
                        branch = new Branch(city, district);
                        company.add(branch);
                    }

                    // Add the employee to the branch
                    Employee employee = new Employee(employeeName, position);
                    branch.addEmployee(employee);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    private static void processMonthlyUpdates(String fileName, HashTable<Branch> company) {
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Parse the line to command and details.
                String[] parts = line.split(": ");

                if (parts.length == 2) {
                    String command = parts[0].trim();
                    String details = parts[1].trim();
                    String[] detailsParts = details.split(", ");

                    // Call the linked method according to the command.
                    switch (command) {
                        case "ADD":
                            handleAdd(detailsParts, company);
                            break;
                        case "PERFORMANCE_UPDATE":
                            handlePerformanceUpdate(detailsParts, company);
                            break;
                        case "LEAVE":
                            handleLeave(detailsParts, company);
                            break;
                        case "PRINT_MANAGER":
                            handlePrintManager(detailsParts, company);
                            break;
                        case "PRINT_MONTHLY_BONUSES":
                            handlePrintMonthlyBonuses(detailsParts, company);
                            break;
                        case "PRINT_OVERALL_BONUSES":
                            handlePrintOverallBonuses(detailsParts, company);
                            break;

                    }
                } else {
                    // Call end of the month when the month is changed.
                    if (parts[0] != "") {
                        String[] month = parts[0].split(":");
                        month[0].trim();
                        endOfTheMonth(company);
                    }

                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }

    // getKey methods to get keys of branches and employees.
    public static String getKey(String city, String district) {
        return city + "-" + district;
    }

    public static String getKey(String name) {
        return name;
    }

    // At the end of the month, set monthly bonus of the branches 0.
    private static void endOfTheMonth(HashTable<Branch> company) {

        for (Object hashable : company) {
            Branch branch = (Branch) hashable;
            branch.setMonthlyBonuses(0);
        }
    }

    // Parse detailsparts and add the specified employee to the branch.
    public static void handleAdd(String[] detailsParts, HashTable<Branch> company) {
        String city = detailsParts[0].strip();
        String district = detailsParts[1].strip();
        String employeeName = detailsParts[2].strip();
        String position = detailsParts[3].strip();
        Branch branch = (Branch) company.get(getKey(city, district));

        if (!branch.getEmployees().contains(employeeName)) {
            Employee employee = new Employee(employeeName, position);
            branch.addEmployee(employee);

        } else {
            // Employee already exists.
            FileWrite.writeToFile("Existing employee cannot be added again.");
        }

    }

    // Parse detailsparts and leave the specified employee from the branch.
    public static void handleLeave(String[] detailsParts, HashTable<Branch> company) {
        String city = detailsParts[0].strip();
        String district = detailsParts[1].strip();
        String employeeName = detailsParts[2].strip();
        Branch branch = (Branch) company.get(getKey(city, district));

        HashTable<Employee> employees = branch.getEmployees();
        Employee employee = (Employee) employees.get(getKey(employeeName));

        if (employee == null) { // Employee doesn't exist.
            FileWrite.writeToFile("There is no such employee.");

        } else {

            branch.leave(employee);
        }

    }

    // Parse detailsparts and update the specified employee's prommotion points.
    public static void handlePerformanceUpdate(String[] detailsParts, HashTable<Branch> company) {
        String city = detailsParts[0].strip();
        String district = detailsParts[1].strip();
        String employeeName = detailsParts[2].strip();
        String score = detailsParts[3].strip();
        Integer monthlyScore = Integer.parseInt(score);
        Branch branch = (Branch) company.get(getKey(city, district));
        HashTable<Employee> employees = branch.getEmployees();
        Employee employee = (Employee) employees.get(getKey(employeeName));

        if (employee == null) { // Employee doesn't exist.
            FileWrite.writeToFile("There is no such employee.");

        } else {
            if (employees.contains(employeeName)) {

                // Update monthly score of the employee.
                employee.updateMonthlyScore(monthlyScore);
                if (monthlyScore > 0) {
                    branch.addBonuses(monthlyScore % 200);
                }

                // If employee gets -5 or lower promotion points, dismiss the employee.
                if (employee.getDismiss()) {
                    branch.dismiss(employee);
                }

                // If employee gets the required promotion points, promote the employee.
                else if (employee.getPromotion()) {

                    branch.promote(employee);

                }

                // If employee loses 10 points, remove the employee from the manager candidate
                // arraylist.
                else if (!employee.getPromotion() && branch.isCandidate(employee)) {
                    branch.removeManagerCandidate(employee);
                }
            }

        }

    }


    // Parse detailsparts and print the manager of the specified branch.
    public static void handlePrintManager(String[] detailsParts, HashTable<Branch> company) {
        String city = detailsParts[0].strip();
        String district = detailsParts[1].strip();
        Branch branch = (Branch) company.get(getKey(city, district));
        Employee manager = branch.getManager();
        FileWrite.writeToFile("Manager of the " + district + " branch is " + manager.getName() + ".");

    }

    // Parse detailsparts and print the total monthly bonuses of the specified branch.
    public static void handlePrintMonthlyBonuses(String[] detailsParts, HashTable<Branch> company) {
        String city = detailsParts[0].strip();
        String district = detailsParts[1].strip();
        Branch branch = (Branch) company.get(getKey(city, district));
        FileWrite.writeToFile(
                "Total bonuses for the " + district + " branch this month are: " + branch.getMonthlyBonuses());

    }

    // Parse detailsparts and print the total overall bonuses of the specified branch.
    public static void handlePrintOverallBonuses(String[] detailsParts, HashTable<Branch> company) {
        String city = detailsParts[0].strip();
        String district = detailsParts[1].strip();
        Branch branch = (Branch) company.get(getKey(city, district));
        FileWrite.writeToFile("Total bonuses for the " + district + " branch are: " + branch.getOverallBonuses());

    }

}