package dao;

import entities.Review;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 This class handles database operations related to customer reviews.
*/
public class ReviewDAO {

    /*
     Retrieves all reviews from the database.
    */
    public List<Review> getAllReviews() throws SQLException {

        List<Review> list = new ArrayList<>();

        String sql = "SELECT * FROM APP.REVIEWS ORDER BY REVIEW_DATE DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Review r = new Review();

                r.setReviewId(rs.getInt("REVIEW_ID"));
                r.setReviewerName(rs.getString("REVIEWER_NAME"));
                r.setComment(rs.getString("COMMENT"));
                r.setRating(rs.getInt("RATING"));
                r.setDate(rs.getTimestamp("REVIEW_DATE"));

                list.add(r);
            }
        }

        return list;
    }

    /*
     Inserts a new review into the database.
    */
    public boolean addReview(Review review) throws SQLException {

        String sql = "INSERT INTO APP.REVIEWS (REVIEWER_NAME, COMMENT, RATING, REVIEW_DATE) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, review.getReviewerName());
            stmt.setString(2, review.getComment());
            stmt.setInt(3, review.getRating());

            return stmt.executeUpdate() > 0;
        }
    }
}