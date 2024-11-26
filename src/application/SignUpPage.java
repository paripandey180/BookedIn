package application;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import javafx.util.Duration;

public class SignUpPage {

    public void showSignUpPage(Stage stage) {
        // Top Panel (Logo and Slogan)
        VBox topPanel = new VBox();
        topPanel.setStyle("-fx-background-color: #FF9900;");
        topPanel.setPadding(new Insets(20, 20, 10, 20)); 
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setSpacing(5);

        // Logo
        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/images/BookedInLogo.png")));
        logoView.setFitWidth(120);
        logoView.setPreserveRatio(true);
        Circle clip = new Circle(60);
        clip.setCenterX(60);
        clip.setCenterY(60);
        logoView.setClip(clip);

        // Slogan
        Label slogan = new Label("Fuel Your Fire, One Story at a Time!");
        slogan.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        
        // Add the logo and slogan to the top panel
        topPanel.getChildren().addAll(logoView, slogan);

        // Sign-Up Form Panel
        VBox formPanel = new VBox();
        formPanel.setPadding(new Insets(20, 20, 15, 20)); 
        formPanel.setAlignment(Pos.CENTER);
        formPanel.setSpacing(10); 
        formPanel.setStyle("-fx-background-color: #FFFFFF; -fx-border-radius: 15; -fx-background-radius: 15;");
        formPanel.setPrefSize(400, 400);
        formPanel.setMinSize(400, 400);
        formPanel.setMaxSize(400, 400);

        // Sign-Up Label
        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        
        // Input Fields
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField asuIdField = new TextField();
        asuIdField.setPromptText("ASU ID");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        firstNameField.setMaxWidth(280);
        lastNameField.setMaxWidth(280);
        asuIdField.setMaxWidth(280);
        passwordField.setMaxWidth(280);

        // Role Selection (CheckBoxes)
        CheckBox buyerCheckBox = new CheckBox("Buyer");
        CheckBox sellerCheckBox = new CheckBox("Seller");

        HBox roleSelection = new HBox(10, buyerCheckBox, sellerCheckBox);
        roleSelection.setAlignment(Pos.CENTER);

        // Sign-Up Button
        Button signUpButton = new Button("Join Now →");
        signUpButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-padding: 8 16;");
        signUpButton.setMaxWidth(200);

        // Message label for feedback
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Sign-up button action
        signUpButton.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String asuId = asuIdField.getText().trim();
            String password = passwordField.getText().trim();
            boolean isBuyer = buyerCheckBox.isSelected();
            boolean isSeller = sellerCheckBox.isSelected();

            // Basic validation
            if (isAsuIdTaken(asuId)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Account Found");
                alert.setHeaderText(null);
                alert.setContentText("An account with this ASU ID already exists.");
                alert.showAndWait();
            } else if (firstName.isEmpty() || lastName.isEmpty()) {
                messageLabel.setText("Please enter your first and last name.");
            } else if (!firstName.matches("[A-Za-z]+") || !lastName.matches("[A-Za-z]+")){
                messageLabel.setText("Only letters allowed for first and last name.");
            } else if (asuId.isEmpty() || password.isEmpty()) {
                messageLabel.setText("ASU ID and Password are required.");
            } else if (!asuId.matches("\\d{10}")) {
                messageLabel.setText("ASU ID must be 10 digits.");
            } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Password must contain:\n1. At least one uppercase letter\n2. One lowercase letter\n3. One digit\n4. One special character\n5. And be at least 8 characters long");
                alert.show();
                return;
            } else if (!isBuyer && !isSeller) {
                messageLabel.setText("Please select at least one role.");
            } else if (isAsuIdTaken(asuId)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Account Found");
                alert.setHeaderText(null);
                alert.setContentText("An account with this ASU ID already exists.");
                alert.showAndWait();
            } 
            else {
                // Save user details to file
                saveUserToFile(asuId, password, isBuyer, isSeller);
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Registration successful! Please proceed to login.");

                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> {
                    new LoginPage().start(stage);
                });
                pause.play();
            }
        });

        // Hyperlink for login page
        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        loginLink.setStyle("-fx-text-fill: #8B0000;");
        loginLink.setPadding(new Insets(3, 0, 0, 0)); 
        loginLink.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(stage); 
        });

        // Add elements to the form panel
        formPanel.getChildren().addAll(
            signUpLabel, firstNameField, lastNameField, asuIdField, passwordField,
            new Label("Select Role(s):"), roleSelection, signUpButton, messageLabel, loginLink
        );

        // Main layout (combining the top panel and form panel)
        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(topPanel, formPanel);
        mainLayout.setSpacing(5);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-background-color: #FF9900;");
        mainLayout.setPadding(new Insets(10, 20, 10, 20)); 

        Scene signUpScene = new Scene(mainLayout, 400, 650); 
        stage.setScene(signUpScene);
        stage.setTitle("BookedIn - Sign Up");
        stage.show();
    }

    // This is where the saving for users.txt takes place
    private void saveUserToFile(String asuId, String password, boolean isBuyer, boolean isSeller) {
        String buyerRole = isBuyer ? "true" : "false";
        String sellerRole = isSeller ? "true" : "false";
        String adminRole = "false"; // Default admin role to false since we are not trying to include that functionality

        String userDetails = String.format("%s,%s,%s,%s,%s", asuId, password, buyerRole, sellerRole, adminRole);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("txtfiles/users.txt", true))) {
            writer.write(userDetails);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to users.txt: " + e.getMessage());
        }
    }

    private boolean isAsuIdTaken(String asuId) {
    	try (BufferedReader reader = new BufferedReader(new FileReader("txtfiles/users.txt"))) {
    		String line;
    		while ((line = reader.readLine()) != null) {
    			String[] userDetails = line.split(",");
    			if (userDetails.length > 0 && userDetails[0].equals(asuId)) {
    				return true; // ASU ID already exists
    			}
    		}
    	} catch (IOException e) {
        System.out.println("Error reading from users.txt: " + e.getMessage());
    	}
    return false;
	}

}
