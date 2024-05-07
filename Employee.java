// Employee class

/**
 * Represents a employee in the company.
 * 
 * @author Yusuf Anil Yazici
 */

public class Employee implements HashTable.Hashable {
    // Class variables
    private String name;
    private String position;
    private int monthlyScore;
    private int promotionPoints;
    private boolean dismiss;
    private boolean promotion;

    // Constructor
    public Employee(String name, String position) {
        this.name = name;
        this.position = position;
        this.monthlyScore = 0;
        this.promotionPoints = 0;
        this.dismiss = false;
        this.promotion = false;

    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getMonthlyScore() {
        return monthlyScore;
    }

    public void setDismiss(boolean dismiss) {
        this.dismiss = dismiss;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    public int getPromotionPoints() {
        return promotionPoints;
    }

    public boolean getDismiss() {
        return dismiss;
    }

    public boolean getPromotion() {
        return promotion;
    }

    @Override
    public String getHashKey() {
        return this.getName();
    }

    // Updates the promotion points then makes montly score zero again.
    public void updateMonthlyScore(int score) {
        this.monthlyScore = score;
        updatePromotionPoints();
        this.monthlyScore = 0;
    }

    // Reduces promotion points after a promotion.
    public void reducePromotionPoints(int x) {
        this.promotionPoints = promotionPoints - x;

        if (position.equals("COOK") && promotionPoints < 10) {
            promotion = false;
        }

    }

    // Calculate promotion points based on the monthly score
    private void updatePromotionPoints() {

        if (monthlyScore >= 0) {
            promotionPoints += monthlyScore / 200;

        } else {
            promotionPoints += monthlyScore / 200;
        }

        // After calculating promotion points, assign values to promote or dismiss
        // booleans.

        if (promotionPoints <= -5 && position != "COURIER") {
            dismiss = true;
        } else {
            dismiss = false;
        }

        if (position.equals("CASHIER") && (promotionPoints >= 3)) {
            promotion = true;
        } else if (position.equals("COOK") && (promotionPoints >= 10)) {
            promotion = true;
        } else {
            promotion = false;
        }

    }

}
