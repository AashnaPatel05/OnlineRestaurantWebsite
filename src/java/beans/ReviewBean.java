package beans;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.List;

/**
 * ReviewBean
 * -----------------------------------
 * Handles:
 * - Submitting reviews
 * - Storing reviews (temporary list)
 * - Displaying reviews
 *
 * NOTE:
 * This version uses in-memory storage (List)
 * → good enough for assignment/demo
 */
@Named("reviewBean")
@RequestScoped
public class ReviewBean {

    // ===================== STORAGE =====================

    /**
     * Static list so reviews persist while app runs
     */
    private static List<Review> allReviews = new ArrayList<>();


    // FORM FIELDS

    /**
     * Name entered by user in form
     */
    private String reviewerName;

    /**
     * Rating selected (1–5)
     */
    private int rating;

    /**
     * Review comment text
     */
    private String comment;


    // CONSTRUCTOR

    /**
     * Loads sample reviews only once (first time app runs)
     */
    public ReviewBean() {
        if (allReviews.isEmpty()) {
            loadSampleReviews();
        }
    }

    /**
     * Adds demo reviews for UI display
     */
    private void loadSampleReviews() {
        allReviews.add(new Review("John Doe", 5, "Excellent food!"));
        allReviews.add(new Review("Jane Smith", 4, "Very tasty and fresh."));
        allReviews.add(new Review("Mike Johnson", 5, "Amazing experience!"));
    }


    // SUBMIT REVIEW

    /**
     * Called when user clicks "Submit Review"
     */
    public String submitReview() {

        // Validate form input
        if (!validateReview()) {
            return null; // Stay on same page
        }

        // Create new review object
        Review review = new Review(reviewerName, rating, comment);

        // Add to list
        allReviews.add(review);

        // Show success message
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Review submitted successfully!", null));

        // Clear form fields after submission
        reviewerName = null;
        rating = 0;
        comment = null;

        return null;
    }


    // VALIDATION

    /**
     * Validates user input before saving
     */
    private boolean validateReview() {

        if (reviewerName == null || reviewerName.trim().isEmpty()) {
            addError("Name is required.");
            return false;
        }

        if (rating < 1 || rating > 5) {
            addError("Rating must be between 1 and 5.");
            return false;
        }

        if (comment == null || comment.trim().length() < 5) {
            addError("Comment must be at least 5 characters.");
            return false;
        }

        return true;
    }

    /**
     * Helper method to show error messages
     */
    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }


    // DISPLAY METHODS

    /**
     * Returns all reviews for UI display
     */
    public List<Review> getAllReviews() {
        return allReviews;
    }

    /**
     * Converts rating number → stars (★★★★★)
     */
    public String getStars(int rating) {
        StringBuilder stars = new StringBuilder();

        for (int i = 1; i <= 5; i++) {
            stars.append(i <= rating ? "★" : "☆");
        }

        return stars.toString();
    }


    // GETTERS & SETTERS

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    // INNER CLASS

    /**
     * Review model class
     * Represents one review entry
     */
    public static class Review {

        private String reviewerName;
        private int rating;
        private String comment;
        private String date;

        public Review(String reviewerName, int rating, String comment) {
            this.reviewerName = reviewerName;
            this.rating = rating;
            this.comment = comment;

            // Auto-generate date
            this.date = new java.text.SimpleDateFormat("dd/MM/yyyy")
                    .format(new java.util.Date());
        }

        public String getReviewerName() {
            return reviewerName;
        }

        public int getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }

        public String getDate() {
            return date;
        }
    }
}
