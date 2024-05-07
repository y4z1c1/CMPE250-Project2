// Branch class

/**
 * Represents a branch in the company.
 * @author Yusuf Anil Yazici
 */

import java.util.ArrayList;

public class Branch implements HashTable.Hashable {
    // Class variables
    private String city;
    private String district;
    private HashTable<Employee> employees; // Stores the employees of the branch.
    private Employee manager;
    private int numberOfCashiers;
    private int numberOfCooks;
    private int numberOfCouriers;
    private int monthlyBonuses;
    private int overallBonuses;
    private ArrayList<Employee> willBePromoted; // Stores cook candidates.
    private ArrayList<Employee> willbeDismissed; // Stores employees that will be dissmised asap.
    private ArrayList<Employee> managerCandidates; // Stores manager candidates.

    // Constructor
    public Branch(String city, String district) {
        this.city = city;
        this.district = district;
        this.numberOfCashiers = 0;
        this.numberOfCooks = 0;
        this.monthlyBonuses = 0;
        this.overallBonuses = 0;
        this.manager = null;
        this.willBePromoted = new ArrayList<>();
        this.willbeDismissed = new ArrayList<>();
        this.managerCandidates = new ArrayList<>();
        this.employees = new HashTable<Employee>();
    }

    // Getter and Setter methods
    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public HashTable<Employee> getEmployees() {
        return employees;
    }

    public Employee getManager() {
        return manager;
    }

    public int getMonthlyBonuses() {
        return monthlyBonuses;
    }

    public int getOverallBonuses() {
        return overallBonuses;
    }

    @Override
    public String getHashKey() {
        return this.getCity() + "-" + this.getDistrict();
    }

    public void setMonthlyBonuses(int monthlyBonuses) {
        this.monthlyBonuses = monthlyBonuses;
    }

    // Add an employee to the branch
    public void addEmployee(Employee employee) {

        if (employee.getPosition().equals("CASHIER")) {
            numberOfCashiers += 1;
        } else if (employee.getPosition().equals("COOK")) {
            numberOfCooks += 1;
        } else if (employee.getPosition().equals("MANAGER")) {
            this.manager = employee;
        } else if (employee.getPosition().equals("COURIER")) {
            numberOfCouriers += 1;
        }
        employees.add(employee);
        checkHierarchy(); // Triggers checkHierarchy method to check if there is a suitable employee to
                          // promote.
    }

    // Adds the specified COOK to managerCandidates arraylist.
    public void addManagerCandidate(Employee employee) {

        if (employee.getPosition().equals("COOK") && !managerCandidates.contains(employee)) {

            this.managerCandidates.add(employee);
        }

    }

    // Removes the specified COOK from managerCandidates arraylist.
    public void removeManagerCandidate(Employee employee) {

        this.managerCandidates.remove(employee);
    }

    // Checks if the specified employee is a manager candidate or not.
    public boolean isCandidate(Employee employee) {
        return managerCandidates.contains(employee);
    }

    // Records the added bonuses.
    public void addBonuses(int x) {

        monthlyBonuses += x;
        overallBonuses += x;

    }

    // Checks the arraylists of promotion and dismissals. If there are appropriate
    // employees, promotes or dismisses them.
    public void checkHierarchy() {

        for (Employee employee : new ArrayList<>(willBePromoted)) { // Iterates over a copy of willBePromoted arraylist.
            if (employee.getPromotion() && employee.getPosition().equals("CASHIER")) {
                promoteEnd(employee); // Promotes the specified member if he/she meets the conditions.
            }
        }

        for (Employee employee : new ArrayList<>(managerCandidates)) { // Iterates over a copy of managerCandidates
                                                                       // arraylist.
            if (employee.getPromotion()) {
                promoteEnd(employee); // Promotes the specified member if he/she meets the conditions.
            }
        }

        for (Employee employee : new ArrayList<>(willbeDismissed)) { // Iterates over a copy of willBeDismissed
                                                                     // arraylist.
            if (employee.getDismiss()) {
                dismissEnd(employee); // Dismisses the specified member if he/she meets the conditions.
            }
        }

    }

    // Promotes an employee
    public void promote(Employee employee) {

        switch (employee.getPosition()) {
            // If the employee is a Cook, add him/her to the managerCandidates arraylist.
            case "COOK":

                addManagerCandidate(employee);
                break;

            // If the employee is a Cashier, check if there is another Cashiers, if not send
            // the employee to the willBePromoted arraylist
            case "CASHIER":
                if (numberOfCashiers == 1) {

                    if (!willBePromoted.contains(employee)) {
                        willBePromoted.add(employee);
                    }
                    break;

                } else {
                    // Promote the cashier successfully.
                    FileWrite.writeToFile(employee.getName() + " is promoted from Cashier to Cook.");
                    employee.setPosition("COOK");
                    employee.reducePromotionPoints(3);
                    if (employee.getPromotionPoints() >= 10) { // If the employee still has at least 10 promotion
                                                               // points, send him/her directly to the
                                                               // managerCandidates.
                        addManagerCandidate(employee);
                    }

                    employee.setPromotion(false);

                    if (willBePromoted.contains(employee)) {
                        willBePromoted.remove(employee);
                    }

                    numberOfCashiers--;
                    numberOfCooks++;
                    break;
                }

            default:
                break;
        }

        checkHierarchy(); // Check hierarchy in case of a possible promotion after this employee is
                          // promoted.
    }

    // In the right circumstances, promotes the waiting employee.
    public void promoteEnd(Employee employee) {

        switch (employee.getPosition()) {
            case "COOK":

                addManagerCandidate(employee);
                break;

            case "CASHIER":
                if (numberOfCashiers == 1) {

                    break;

                } else {
                    // Promote the cashier succesfully.
                    FileWrite.writeToFile(employee.getName() + " is promoted from Cashier to Cook.");
                    employee.setPosition("COOK");
                    employee.reducePromotionPoints(3);
                    // Since promotion is successfull, remove employee from willBePromoted list.
                    if (willBePromoted.contains(employee)) {
                        willBePromoted.remove(employee);
                    }
                    if (employee.getPromotionPoints() >= 10) {
                        addManagerCandidate(employee);
                    }

                    numberOfCashiers--;
                    numberOfCooks++;
                    break;
                }

            default:
                break;
        }

    }

    // Dismisses the given employee
    public void dismiss(Employee employee) {

        switch (employee.getPosition()) {
            case "COOK":
                if (numberOfCooks == 1) { // Dismissal is not possible since there is no other Cook.
                    if (!willbeDismissed.contains(employee)) {
                        willbeDismissed.add(employee);
                    }

                    break;

                } else {
                    // Successfully dismiss the employee.
                    FileWrite.writeToFile(employee.getName() + " is dismissed from branch: " + district + ".");
                    employees.remove(employee.getHashKey());
                    employee.setDismiss(false);
                    if (willbeDismissed.contains(employee)) {
                        willbeDismissed.remove(employee);
                    }
                    numberOfCooks--;
                    break;

                }

            case "COURIER":
                if (numberOfCouriers == 1) { // Dismissial is not possible since there is no other courier.
                    if (!willbeDismissed.contains(employee)) {
                        willbeDismissed.add(employee);
                    }
                    break;

                } else {
                    // Successfully dismiss the courier.
                    FileWrite.writeToFile(employee.getName() + " is dismissed from branch: " + district + ".");
                    employees.remove(employee.getHashKey());
                    employee.setDismiss(false);
                    if (willbeDismissed.contains(employee)) {
                        willbeDismissed.remove(employee);
                    }

                    numberOfCouriers--;
                    break;

                }

            case "CASHIER":
                if (numberOfCashiers == 1) { // Dismissal is not possible since there is no other cashier
                    if (!willbeDismissed.contains(employee)) {
                        willbeDismissed.add(employee);
                    }
                    break;

                } else {
                    // Successfully dismiss the cashier.
                    FileWrite.writeToFile(employee.getName() + " is dismissed from branch: " + district + ".");
                    employees.remove(employee.getHashKey());
                    numberOfCashiers--;
                    if (willbeDismissed.contains(employee)) {
                        willbeDismissed.remove(employee);
                    }
                    break;
                }

            case "MANAGER":
                if (managerCandidates.size() == 0) {// Dismissal is not possible since there is no manager candidate.
                    if (!willbeDismissed.contains(employee)) {
                        willbeDismissed.add(employee);
                    }
                    break;

                } else {
                    // Successfully dismiss the manager and promote the first manager candidate.
                    if (numberOfCooks != 1 && managerCandidates.get(0) != null) {
                        FileWrite.writeToFile(employee.getName() + " is dismissed from branch: " + district + ".");
                        employee.setDismiss(false);
                        employees.remove(employee.getHashKey());
                        this.manager = managerCandidates.get(0);
                        if (willbeDismissed.contains(employee)) {
                            willbeDismissed.remove(employee);
                        }
                        FileWrite.writeToFile(manager.getName() + " is promoted from Cook to Manager.");
                        manager.setPosition("MANAGER");
                        manager.reducePromotionPoints(10);
                        managerCandidates.remove(0);
                        numberOfCooks--;
                        break;
                    } else {
                        if (!willbeDismissed.contains(employee)) {
                            willbeDismissed.add(employee);
                        }
                        break;
                    }
                }

            default:
                break;
        }

    }

    // Dismiss employees in the willBeDismissed arraylist.
    public void dismissEnd(Employee employee) {

        switch (employee.getPosition()) {
            case "COOK":
                if (numberOfCooks == 1) {
                    break;

                } else {
                    FileWrite.writeToFile(employee.getName() + " is dismissed from branch: " + district + ".");
                    employee.setDismiss(false);
                    if (willbeDismissed.contains(employee)) {
                        willbeDismissed.remove(employee);
                    }
                    employees.remove(employee.getHashKey());
                    numberOfCooks--;
                    break;

                }

            case "COURIER":
                if (numberOfCouriers == 1) {
                    break;

                } else {
                    FileWrite.writeToFile(employee.getName() + " is dismissed from branch: " + district + ".");
                    employee.setDismiss(false);
                    if (willbeDismissed.contains(employee)) {
                        willbeDismissed.remove(employee);
                    }
                    employees.remove(employee.getHashKey());
                    numberOfCouriers--;
                    break;

                }

            case "CASHIER":
                if (numberOfCashiers == 1) {
                    break;

                } else {

                    FileWrite.writeToFile(employee.getName() + " is dismissed from branch: " + district + ".");
                    employee.setDismiss(false);
                    employees.remove(employee.getHashKey());
                    if (willbeDismissed.contains(employee)) {
                        willbeDismissed.remove(employee);
                    }
                    numberOfCashiers--;
                    break;
                }

            case "MANAGER":
                if (managerCandidates.size() == 0) {
                    break;

                } else {
                    if (numberOfCooks != 1 && managerCandidates.get(0) != null) {
                        FileWrite.writeToFile(employee.getName() + " is dismissed from branch: " + district + ".");
                        employee.setDismiss(false);
                        employees.remove(employee.getHashKey());
                        this.manager = managerCandidates.get(0);
                        if (willbeDismissed.contains(employee)) {
                            willbeDismissed.remove(employee);
                        }

                        FileWrite.writeToFile(manager.getName() + " is promoted from Cook to Manager.");
                        manager.setPosition("MANAGER");
                        manager.reducePromotionPoints(10);
                        managerCandidates.remove(0);
                        numberOfCooks--;
                        break;
                    } else {
                        break;
                    }
                }

            default:
                break;
        }

    }

    // Removes the employee who wants to leave if there is suitable conditions.
    public void leave(Employee employee) {

        String position = employee.getPosition();
        switch (position) {
            case "COOK":

                if (numberOfCooks == 1) { // Since there is only one cook, leaving is not possible. Hence the cook gets
                                          // 200$ bonus and continues.
                    if (!willbeDismissed.contains(employee)) {
                        addBonuses(200);
                    }
                    break;

                } else {
                    // The cook leaves.
                    FileWrite.writeToFile(employee.getName() + " is leaving from branch: " + district + ".");
                    employees.remove(employee.getHashKey());
                    // If the cook is a manager candidate, remove it from the managerCandidates
                    // arraylist.
                    if (managerCandidates.contains(employee)) {
                        managerCandidates.remove(employee);
                    }

                    numberOfCooks--;
                    break;

                }

            case "COURIER":
                if (numberOfCouriers == 1) {// Since there is only one courier, leaving is not possible. Hence the
                                            // courier
                                            // gets 200$ bonus and continues.
                    if (!employee.getDismiss()) {
                        addBonuses(200);
                    }
                    break;

                } else {
                    // The courier leaves.
                    FileWrite.writeToFile(employee.getName() + " is leaving from branch: " + district + ".");
                    employees.remove(employee.getHashKey());
                    numberOfCouriers--;
                    break;

                }

            case "CASHIER":
                if (numberOfCashiers == 1) {// Since there is only one cashier, leaving is not possible. Hence the
                                            // cashier gets 200$ bonus and continues.

                    if (!employee.getDismiss()) {
                        addBonuses(200);
                    }
                    break;

                } else {
                    // The cashier leaves.
                    FileWrite.writeToFile(employee.getName() + " is leaving from branch: " + district + ".");
                    employees.remove(employee.getHashKey());
                    numberOfCashiers--;

                    break;
                }

            case "MANAGER":
                if (managerCandidates.size() == 0) { // Since there is no managerCandidate, leaving is not possible.
                    // Manager gets 200$ bonus and continues.
                    if (!employee.getDismiss()) {
                        addBonuses(200);
                    }
                    break;

                } else {
                    if (numberOfCooks != 1 && managerCandidates.get(0) != null) {

                        // The manager leaves and the first manager candidate promotes to manager.

                        FileWrite.writeToFile(employee.getName() + " is leaving from branch: " + district + ".");
                        employees.remove(employee.getHashKey());
                        while (!managerCandidates.get(0).getPromotion()) {
                            int x = 0;
                            managerCandidates.remove(x);
                        }
                        this.manager = managerCandidates.get(0);
                        FileWrite.writeToFile(manager.getName() + " is promoted from Cook to Manager.");
                        manager.setPosition("MANAGER");
                        manager.reducePromotionPoints(10);
                        numberOfCooks--;
                        manager.setPosition("MANAGER");
                        managerCandidates.remove(0);
                        break;
                    } else {
                        if (!employee.getDismiss()) {
                            addBonuses(200);
                        }

                        break;
                    }
                }

            default:
                break;
        }

    }

}
