package application;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuyerPage extends Application {
    private final List<Book> bookList = new ArrayList<>();
    private static final String FILE_PATH = "textfiles/books.txt";

    private VBox bookDisplayArea;

    @Override
    public void start(Stage primaryStage) {
        // Load books from file
        loadBooksFromFile();

        // Create header bar
        HBox headerBar = createHeaderBar(primaryStage);

        // Create search section
        HBox searchSection = createSearchSection();

        // Create display area for books
        bookDisplayArea = new VBox(10);
        bookDisplayArea.setPadding(new Insets(10));
        bookDisplayArea.setAlignment(Pos.TOP_CENTER);
        bookDisplayArea.setStyle("-fx-background-color: white; -fx-border-color: gray;");

        // Populate book display
        updateBookDisplay(bookList);

        // ScrollPane for book display
        ScrollPane scrollPane = new ScrollPane(bookDisplayArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;"); // Make ScrollPane transparent
        scrollPane.setPadding(new Insets(10));

        // Main layout
        VBox mainLayout = new VBox(headerBar, searchSection, scrollPane);
        mainLayout.setStyle("-fx-background-color: orange;"); // Set orange background for the main layout
        mainLayout.setPadding(new Insets(10)); // Add padding to main layout

        Scene scene = new Scene(mainLayout, 900, 600);

        primaryStage.setTitle("Buyer Page");
        configureStage(primaryStage, scene);
    }

    private HBox createHeaderBar(Stage primaryStage) {
        HBox headerBar = new HBox();
        headerBar.setPrefHeight(50);
        headerBar.setStyle("-fx-background-color: maroon;");
        headerBar.setPadding(new Insets(5, 10, 5, 10));

        // Logo and title on the left
        ImageView logoView = new ImageView(new Image("file:logobook.png", 40, 40, true, true));
        Label title = new Label("BookedIn");
        title.setStyle("-fx-text-fill: gold; -fx-font-size: 20px; -fx-font-weight: bold;");

        HBox leftBox = new HBox(10, logoView, title);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // Buttons on the right
        Button viewCart = new Button("View Cart");
        viewCart.setStyle("-fx-background-color: gold; -fx-text-fill: #8B0000;");
        viewCart.setFont(Font.font("Arial", 14));
        viewCart.setOnAction(event -> {
            CheckoutPage checkoutPage = new CheckoutPage(bookList); // Pass the book list
            checkoutPage.start(primaryStage);
        });

        Button logoutButton = new Button("Log Out");
        logoutButton.setStyle("-fx-background-color: gold; -fx-text-fill: #8B0000;");
        logoutButton.setFont(Font.font("Arial", 14));
        logoutButton.setOnAction(event -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage); // Redirect to login page
        });

        HBox rightBox = new HBox(10, viewCart, logoutButton);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        // Add spacing and distribute alignment between left and right
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        headerBar.getChildren().addAll(leftBox, rightBox);

        return headerBar;
    }

    private HBox createSearchSection() {
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Fiction", "Non-Fiction", "Science", "History");
        categoryComboBox.setPromptText("Category");

        ComboBox<String> conditionComboBox = new ComboBox<>();
        conditionComboBox.getItems().addAll("Used Like New", "Moderately Used", "Heavily Used");
        conditionComboBox.setPromptText("Condition");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: gold;");
        searchButton.setFont(Font.font("Arial", 14));

        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: gold;");
        resetButton.setFont(Font.font("Arial", 14));

        searchButton.setOnAction(event -> {
            String selectedCategory = categoryComboBox.getValue();
            String selectedCondition = conditionComboBox.getValue();

            // Filter books based on category and condition
            List<Book> filteredBooks = bookList.stream()
                    .filter(book -> (selectedCategory == null || book.getCategory().equals(selectedCategory)) &&
                                    (selectedCondition == null || book.getCondition().equals(selectedCondition)))
                    .collect(Collectors.toList());

            updateBookDisplay(filteredBooks); // Update display with filtered books
        });

        resetButton.setOnAction(event -> {
            categoryComboBox.setValue(null); // Clear category filter
            conditionComboBox.setValue(null); // Clear condition filter
            updateBookDisplay(bookList); // Reset display to all books
        });

        HBox searchSection = new HBox(10, categoryComboBox, conditionComboBox, searchButton, resetButton);
        searchSection.setPadding(new Insets(10));
        searchSection.setAlignment(Pos.CENTER);

        return searchSection;
    }

    private void updateBookDisplay(List<Book> books) {
        bookDisplayArea.getChildren().clear();
        if (books.isEmpty()) {
            Label noBooksLabel = new Label("No books match the selected filters.");
            noBooksLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 16px;");
            bookDisplayArea.getChildren().add(noBooksLabel);
        } else {
            for (Book book : books) {
                bookDisplayArea.getChildren().add(createBookEntry(book));
            }
        }
    }

    private HBox createBookEntry(Book book) {
        HBox bookEntry = new HBox(10);
        bookEntry.setPadding(new Insets(10));
        bookEntry.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: white;");

        // Book image
        ImageView bookImageView = new ImageView(new Image("file:txtfiles/bookimages/" + book.getImageName()));
        bookImageView.setFitHeight(100);
        bookImageView.setFitWidth(80);

        // Book details
        VBox details = new VBox(5);

        // Create separate labels for "Name:" and the book title
        HBox nameRow = new HBox();
        Label nameLabel = new Label("Name: ");
        Label bookTitleLabel = new Label(book.getName());
        bookTitleLabel.setStyle("-fx-font-weight: bold;"); // Bold only the book title
        nameRow.getChildren().addAll(nameLabel, bookTitleLabel);

        details.getChildren().addAll(
            nameRow,
            new Label("Author: " + book.getAuthor()),
            new Label("Category: " + book.getCategory()),
            new Label("Condition: " + book.getCondition()),
            new Label("Price: $" + book.getPrice())
        );

        // Add to Cart button
        Button addToCart = new Button("Add to Cart");
        addToCart.setStyle("-fx-background-color: #8B0000; -fx-text-fill: gold;");
        addToCart.setOnAction(event -> addToCart(book));
        details.getChildren().add(addToCart);

        bookEntry.getChildren().addAll(bookImageView, details);
        return bookEntry;
    }

    private void addToCart(Book book) {
        // Check if the book is already in the shopping cart
        try (BufferedReader reader = new BufferedReader(new FileReader("textfiles/shoppingcart.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6 && parts[0].equals(book.getName()) && parts[1].equals(book.getAuthor())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "This book is already in the cart!");
                    alert.show();
                    return; // Exit the method since the book is already in the cart
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error checking cart contents.");
            errorAlert.show();
            return; // Exit on error
        }

        // Add book to shopping cart if it's not already there
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("txtfiles/shoppingcart.txt", true))) {
            writer.write(book.toFileFormat());
            writer.newLine();
            Alert addedConfirmation = new Alert(Alert.AlertType.INFORMATION, "Book has been added to cart!");
            addedConfirmation.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to add book to cart.");
            errorAlert.show();
        }
    }

    private void loadBooksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    bookList.add(new Book(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureStage(Stage stage, Scene scene) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
