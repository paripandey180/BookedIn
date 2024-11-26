package application;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CheckoutPage extends Application {
    private List<Book> cartBooks;          // Central cart book list
    private List<Book> buyerBookList;     // Reference to the buyer's book list
    private Label billDetails;            // Bill details label
    private VBox bookListContainer;       // Container for books in the cart
    private static final String FILE_PATH = "txtfiles/books.txt";

    // Constructor to pass the buyer's book list
    public CheckoutPage(List<Book> buyerBookList) {
        this.buyerBookList = buyerBookList;
        this.cartBooks = loadBooksFromCartFile(); // Load cart from file
    }

    @Override
    public void start(Stage primaryStage) {
        // Header
        HBox headerBar = createHeaderBar();

        // Labels for "Order Summary" and "Bill"
        HBox labelRow = createLabelRow();

        // Book list container with scrolling
        bookListContainer = new VBox(10);
        bookListContainer.setPadding(new Insets(10));
        bookListContainer.setStyle("-fx-background-color: white; -fx-border-color: gray;"); // Keep book panel white
        bookListContainer.setAlignment(Pos.TOP_CENTER);

        // Check if the cart is empty
        if (cartBooks.isEmpty()) {
            // Display message if the cart is empty
            Label emptyCartMessage = new Label("Nothing to buy here, please add a book!");
            emptyCartMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            bookListContainer.getChildren().add(emptyCartMessage);
        } else {
            // Populate the container with books from the cart
            for (Book book : cartBooks) {
                bookListContainer.getChildren().add(createBookEntry(book));
            }
        }

        ScrollPane bookScrollPane = new ScrollPane(bookListContainer);
        bookScrollPane.setFitToWidth(true);
        bookScrollPane.setPrefWidth(850);

        // Set the scroll pane style to ensure it fits seamlessly into the layout
        bookScrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");

        // Bill container
        billDetails = new Label();
        updateBilling();
        VBox billContainer = new VBox(billDetails);
        billContainer.setPadding(new Insets(10));
        billContainer.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1px;");
        billContainer.setPrefWidth(350);

        // Buttons (Back, Cancel, and Place Order)
        VBox buttonBox = createButtonBox(primaryStage);

        // Main layout
        HBox mainLayout = new HBox(20, bookScrollPane, billContainer, buttonBox);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: orange;");

        // Combine Top Header and Main Layout
        VBox main = new VBox(headerBar, labelRow, mainLayout);
        main.setStyle("-fx-background-color: orange;");

        // Scene configuration and display
        Scene scene = new Scene(main);
        configureStage(primaryStage, scene);
    }

    private HBox createHeaderBar() {
        HBox headerBar = new HBox();
        headerBar.setPrefHeight(50);
        headerBar.setStyle("-fx-background-color: maroon;");

        Image logoImage = new Image("file:logobook.png", 40, 40, true, true);
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(40);
        logoView.setFitWidth(40);
        Circle clip = new Circle(20, 20, 20);
        logoView.setClip(clip);
        DropShadow shadow = new DropShadow();
        shadow.setRadius(3);
        shadow.setColor(Color.BLACK);
        logoView.setEffect(shadow);

        Label title = new Label("BookedIn");
        title.setStyle("-fx-text-fill: gold; -fx-font-size: 20px; -fx-font-weight: bold;");

        headerBar.getChildren().addAll(logoView, title);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(5, 10, 5, 10));
        headerBar.setSpacing(10);

        return headerBar;
    }

    private HBox createLabelRow() {
        Label orderSumLabel = new Label("Order Summary");
        orderSumLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        orderSumLabel.setTextFill(Color.BLACK);

        Label billTextLabel = new Label("Bill");
        billTextLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        billTextLabel.setTextFill(Color.BLACK);

        HBox labelRow = new HBox(600, orderSumLabel, billTextLabel);
        labelRow.setAlignment(Pos.CENTER_LEFT);
        labelRow.setPadding(new Insets(10, 20, 5, 20));
        return labelRow;
    }

    private VBox createButtonBox(Stage primaryStage) {
        Button backButton = new Button("BACK ←");
        backButton.setStyle("-fx-background-color: #4D220C; -fx-text-fill: yellow; -fx-font-size: 16px; -fx-padding: 10 20;");
        backButton.setOnAction(e -> {
            BuyerPage buyerPage = new BuyerPage();
            buyerPage.start(primaryStage); // Redirect to BuyerPage
        });

        Button cancelButton = new Button("CANCEL ORDER");
        cancelButton.setStyle("-fx-background-color: #4D220C; -fx-text-fill: yellow; -fx-font-size: 16px; -fx-padding: 10 20;");
        cancelButton.setOnAction(e -> {
            // Show confirmation dialog
            Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to cancel the order?", ButtonType.YES, ButtonType.NO);
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setTitle("Confirm Cancel");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    cartBooks.clear();
                    saveCartBooksToFile();
                    bookListContainer.getChildren().clear();
                    Label emptyCartMessage = new Label("Nothing to buy here, please add a book!");
                    emptyCartMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
                    bookListContainer.getChildren().add(emptyCartMessage);
                    updateBilling();
                }
            });
        });

        Button placeOrderButton = new Button("PLACE ORDER ➔");
        placeOrderButton.setStyle("-fx-background-color: #4D220C; -fx-text-fill: yellow; -fx-font-size: 16px; -fx-padding: 10 20;");
        placeOrderButton.setOnAction(e -> {
            if (cartBooks.isEmpty()) {
                Alert emptyCartAlert = new Alert(AlertType.WARNING, "Your cart is empty. Please add items before placing an order.");
                emptyCartAlert.show();
                return;
            }

            // Confirm order placement
            Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to place this order?", ButtonType.YES, ButtonType.NO);
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setTitle("Confirm Order");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // Add purchased books to the purchasedBooks list in BuyerPage
                    for (Book purchasedBook : cartBooks) {
                        buyerBookList.removeIf(book -> book.getName().equals(purchasedBook.getName()));
                        removeFromBooksFile(purchasedBook); // Remove from books.txt
                    }

                    cartBooks.clear(); // Clear the cart
                    saveCartBooksToFile(); // Update cart file
                    updateBilling(); // Refresh billing details
                    bookListContainer.getChildren().clear(); // Clear the book display area
                    Label emptyCartMessage = new Label("Nothing to buy here, please add a book!");
                    emptyCartMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
                    bookListContainer.getChildren().add(emptyCartMessage);

                    // Generate random delivery date
                    LocalDate today = LocalDate.now();
                    Random random = new Random();
                    int randomDays = random.nextInt(7); // Random delivery within 7 days
                    LocalDate randomDate = today.plusDays(randomDays);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    String deliveryDate = randomDate.format(formatter);

                    // Generate random 10-digit order number
                    long orderNumber = 1000000000L + (long) (random.nextDouble() * 9000000000L);

                    Alert successAlert = new Alert(AlertType.INFORMATION, 
                        "Order placed successfully!\n" +
                        "Order Number: " + orderNumber + "\n" +
                        "Estimated Delivery Date: " + deliveryDate);
                    successAlert.setHeaderText("Order Confirmation");
                    successAlert.showAndWait();
                }
            });
        });

        VBox buttonBox = new VBox(20, backButton, cancelButton, placeOrderButton);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    private HBox createBookEntry(Book book) {
        HBox bookEntry = new HBox(10);
        bookEntry.setPadding(new Insets(10));
        bookEntry.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-background-color: white;");

        // Book image
        ImageView bookImageView = new ImageView(new Image("file:bookimages/" + book.getImageName()));
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

        // Add a remove button to the book entry
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: gold;");
        removeButton.setOnAction(e -> {
            bookListContainer.getChildren().remove(bookEntry); // Remove from UI
            cartBooks.remove(book); // Remove from cart list
            saveCartBooksToFile(); // Update cart file
            updateBilling(); // Recalculate bill
        });

        details.getChildren().add(removeButton);

        bookEntry.getChildren().addAll(bookImageView, details);
        return bookEntry;
    }

    private void updateBilling() {
        StringBuilder billText = new StringBuilder("Your Order:\n");
        double totalBeforeTax = 0;

        for (Book book : cartBooks) {
            try {
                double price = Double.parseDouble(book.getPrice());
                billText.append(book.getName()).append(" - $").append(price).append("\n");
                totalBeforeTax += price;
            } catch (NumberFormatException e) {
                billText.append(book.getName()).append(" - Invalid price\n");
            }
        }

        double tax = totalBeforeTax * 0.07;
        double totalAfterTax = totalBeforeTax + tax;

        billText.append("\nTotal (before tax): $").append(String.format("%.2f", totalBeforeTax))
                .append("\nTax (7%): $").append(String.format("%.2f", tax))
                .append("\nTotal (after tax): $").append(String.format("%.2f", totalAfterTax));

        if (cartBooks.isEmpty()) {
            billDetails.setText("Your cart is empty.");
        } else {
            billDetails.setText(billText.toString());
        }
    }

    private List<Book> loadBooksFromCartFile() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("txtfiles/shoppingcart.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    books.add(new Book(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    private void saveCartBooksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("txtfiles/shoppingcart.txt"))) {
            for (Book book : cartBooks) {
                writer.write(book.toFileFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromBooksFile(Book book) {
        try {
            List<String> books = Files.lines(new File(FILE_PATH).toPath())
                    .filter(line -> !line.contains(book.getName()))
                    .collect(Collectors.toList());
            Files.write(new File(FILE_PATH).toPath(), books);
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
