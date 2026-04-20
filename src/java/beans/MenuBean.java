package beans;

import dao.FoodDAO;
import entities.Food;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuBean - Manages food menu display and search
 * SessionScoped so menu stays loaded across pages
 */
@Named("menuBean")
@SessionScoped
public class MenuBean implements Serializable {

    private List<Food> allFoodItems;
    private List<Food> starters;
    private List<Food> mainCourses;
    private List<Food> desserts;
    private List<Food> drinks;
    private String selectedCategory;
    private String searchKeyword;
    private List<Food> searchResults;

    @PostConstruct
    public void init() {
        loadMenu();
        // IMPORTANT: start with empty search results
        // so allFoodItems shows by default
        searchResults = new ArrayList<>();
    }

    // Load all food items from database
    public void loadMenu() {
        try {
            FoodDAO foodDAO = new FoodDAO();
            allFoodItems = foodDAO.getAllFood();

            if (allFoodItems == null) {
                allFoodItems = new ArrayList<>();
            }

            // Split into categories
            starters    = new ArrayList<>();
            mainCourses = new ArrayList<>();
            desserts    = new ArrayList<>();
            drinks      = new ArrayList<>();

            for (Food item : allFoodItems) {
                String cat = item.getCategory();
                if (cat == null) continue;

                if ("Starters".equalsIgnoreCase(cat)) {
                    starters.add(item);
                } else if ("Main Courses".equalsIgnoreCase(cat) ||
                           "Main Course".equalsIgnoreCase(cat)) {
                    mainCourses.add(item);
                } else if ("Desserts".equalsIgnoreCase(cat) ||
                           "Dessert".equalsIgnoreCase(cat)) {
                    desserts.add(item);
                } else if ("Drinks".equalsIgnoreCase(cat) ||
                           "Drink".equalsIgnoreCase(cat)) {
                    drinks.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            allFoodItems = new ArrayList<>();
            starters     = new ArrayList<>();
            mainCourses  = new ArrayList<>();
            desserts     = new ArrayList<>();
            drinks       = new ArrayList<>();
        }
    }

    // Search food by keyword
    public String search() {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            try {
                searchResults = new FoodDAO().searchFood(searchKeyword.trim());
                if (searchResults == null) {
                    searchResults = new ArrayList<>();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                searchResults = new ArrayList<>();
            }
        } else {
            // If empty search, clear results so allFoodItems shows
            searchResults = new ArrayList<>();
        }
        return null;
    }

    // Clear search and show all items again
    public String clearSearch() {
        searchKeyword = null;
        searchResults = new ArrayList<>();
        return null;
    }

    // ===================== GETTERS & SETTERS =====================

    public List<Food> getAllFoodItems() {
        return allFoodItems;
    }

    public List<Food> getStarters() {
        return starters;
    }

    public List<Food> getMainCourses() {
        return mainCourses;
    }

    public List<Food> getDesserts() {
        return desserts;
    }

    public List<Food> getDrinks() {
        return drinks;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public List<Food> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Food> searchResults) {
        this.searchResults = searchResults;
    }
}