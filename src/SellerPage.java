

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SellerPage extends Application {

    private final List<Book> bookList = new ArrayList<>();
    private static final String FILE_PATH = "books_new.txt";
    private static final String IMAGE_FOLDER = "bookimages";
    private VBox bookDisplayArea;

    @Override
    public void start(Stage primaryStage) {
        // Ensure book images folder exists
        File imageFolder = new File(IMAGE_FOLDER);
        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }

        loadBooksFromFile(); // Load books from file

        // Header Bar
        HBox headerBar = createHeaderBar(primaryStage);

        // Input Layout for adding books
        HBox inputLayout = createInputLayout(primaryStage);

        // Book display area with scrollable panel
        bookDisplayArea = createBookDisplayArea();

        ScrollPane scrollPane = new ScrollPane(bookDisplayArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(headerBar, inputLayout, scrollPane);
        mainLayout.setStyle("-fx-background-color: orange;"); // Set the background color to orange
        mainLayout.setPadding(new Insets(10)); // Add padding for the main layout

        Scene scene = new Scene(mainLayout, 900, 600);

        primaryStage.setTitle("Seller Page");
        configureStage(primaryStage, scene);
    }

    private HBox createHeaderBar(Stage primaryStage) {
        HBox headerBar = new HBox();
        headerBar.setPrefHeight(50);
        headerBar.setStyle("-fx-background-color: maroon;");

        Image logoImage = new Image("file:logobook.png", 40, 40, true, true);
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(40);
        logoView.setFitWidth(40);
        logoView.setPreserveRatio(true);

        Circle clip = new Circle(20);
        clip.setCenterX(20);
        clip.setCenterY(20);
        logoView.setClip(clip);
        DropShadow shadow = new DropShadow();
        shadow.setRadius(3);
        shadow.setColor(Color.BLACK);
        logoView.setEffect(shadow);

        Label title = new Label("BookedIn");
        title.setStyle("-fx-text-fill: gold; -fx-font-size: 20px; -fx-font-weight: bold;");

        Button logoutButton = new Button("Log Out");
        logoutButton.setStyle("-fx-background-color: gold; -fx-text-fill: #8B0000;");
        logoutButton.setFont(Font.font("Arial", 14));
        logoutButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        });

        HBox leftBox = new HBox(10, logoView, title);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        HBox rightBox = new HBox(10, logoutButton);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(leftBox, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        headerBar.getChildren().addAll(leftBox, rightBox);

        return headerBar;
    }

    private HBox createInputLayout(Stage primaryStage) {
        TextField nameField = new TextField();
        nameField.setPromptText("Book Name");

        TextField authorField = new TextField();
        authorField.setPromptText("Author's Name");

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Fiction", "Non-Fiction", "Science", "History");
        categoryComboBox.setPromptText("Category");

        ComboBox<String> conditionComboBox = new ComboBox<>();
        conditionComboBox.getItems().addAll("Used Like New", "Moderately Used", "Heavily Used");
        conditionComboBox.setPromptText("Condition");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        Button uploadButton = new Button("Upload Cover");
        uploadButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: gold;");

        Button addButton = new Button("Add to Library");
        addButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: gold; -fx-font-weight: bold;");

        final File[] selectedImage = {null};
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedImage[0] = file;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Image selected: " + file.getName());
                alert.show();
            }
        });

        addButton.setOnAction(event -> {
            String name = nameField.getText();
            String author = authorField.getText();
            String category = categoryComboBox.getValue();
            String condition = conditionComboBox.getValue();
            String priceText = priceField.getText();

            if (name.isEmpty() || author.isEmpty() || category == null || condition == null || priceText.isEmpty() || selectedImage[0] == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill out all fields and upload a book cover.");
                alert.show();
                return;
            }

            if (!priceText.matches("\\d+(\\.\\d{1,2})?")) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Price must be a valid number (e.g., 10 or 10.99).");
                alert.show();
                return;
            }

            double originalPrice = Double.parseDouble(priceText);
            double discountedPrice = calculateDiscountPrice(originalPrice, condition);

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Listing");
            confirmationAlert.setHeaderText("New Price Calculated");
            confirmationAlert.setContentText("Original Price: $" + String.format("%.2f", originalPrice) +
                    "\nDiscounted Price: $" + String.format("%.2f", discountedPrice) +
                    "\nDo you want to list this book?");
            
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String imageName = selectedImage[0].getName();
                    File destination = new File(IMAGE_FOLDER, imageName);
                    try {
                        Files.copy(selectedImage[0].toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to upload cover image.");
                        alert.show();
                        return;
                    }

                    // Create the book object and add it to the list
                    Book book = new Book(name, author, category, condition, String.format("%.2f", discountedPrice), imageName);
                    bookList.add(book);
                    saveBookToFile(book);

                    // Refresh the book display immediately
                    bookDisplayArea.getChildren().clear(); // Clear current display
                    for (Book b : bookList) {
                        bookDisplayArea.getChildren().add(createBookEntry(b)); // Reload all books
                    }

                    // Show a confirmation that the book is listed
                    Alert addedConfirmation = new Alert(Alert.AlertType.INFORMATION, "Your book has been listed!");
                    addedConfirmation.show();
                }
            });
        });

        HBox inputLayout = new HBox(10, nameField, authorField, categoryComboBox, conditionComboBox, priceField, uploadButton, addButton);
        inputLayout.setPadding(new Insets(10));
        inputLayout.setAlignment(Pos.CENTER);
        return inputLayout;
    }

    private VBox createBookDisplayArea() {
        VBox bookDisplayArea = new VBox(10);
        bookDisplayArea.setPadding(new Insets(10));
        bookDisplayArea.setAlignment(Pos.TOP_CENTER);
        bookDisplayArea.setStyle("-fx-background-color: white; -fx-border-color: gray;"); // White background for book display

        Set<String> seenBooks = new HashSet<>();
        for (Book book : bookList) {
            if (seenBooks.add(book.getName())) { // Prevent duplicates
                bookDisplayArea.getChildren().add(createBookEntry(book));
            }
        }

        return bookDisplayArea;
    }

    private HBox createBookEntry(Book book) {
        HBox bookEntry = new HBox(10);
        bookEntry.setPadding(new Insets(10));
        bookEntry.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: white;");

        ImageView bookImageView = new ImageView(new Image("file:" + IMAGE_FOLDER + "/" + book.getImageName()));
        bookImageView.setFitHeight(100);
        bookImageView.setFitWidth(80);

        VBox details = new VBox(5);
        HBox nameRow = new HBox();
        Label nameLabel = new Label("Name: ");
        Label bookTitleLabel = new Label(book.getName());
        bookTitleLabel.setStyle("-fx-font-weight: bold;");
        nameRow.getChildren().addAll(nameLabel, bookTitleLabel);

        details.getChildren().addAll(
                nameRow,
                new Label("Author: " + book.getAuthor()),
                new Label("Category: " + book.getCategory()),
                new Label("Condition: " + book.getCondition()),
                new Label("Price: $" + book.getPrice())
        );

        bookEntry.getChildren().addAll(bookImageView, details);
        return bookEntry;
    }

    private double calculateDiscountPrice(double originalPrice, String condition) {
        switch (condition) {
            case "Used Like New":
                return originalPrice * 0.90; // 10% discount
            case "Moderately Used":
                return originalPrice * 0.70; // 30% discount
            case "Heavily Used":
                return originalPrice * 0.50; // 50% discount
            default:
                return originalPrice;
        }
    }

    private void saveBookToFile(Book book) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(book.toFileFormat());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBooksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            bookList.clear(); // Clear before loading to avoid duplicates
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

    public void refreshPage(Stage primaryStage) {
        bookList.clear();
        loadBooksFromFile();
        start(primaryStage);
    }

    public void configureStage(Stage stage, Scene scene) {
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
