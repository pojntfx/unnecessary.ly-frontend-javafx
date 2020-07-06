package ly.unnecessary.frontend;

import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class SignInComponent {
    private Function<SignInInfo, Integer> onSignIn;
    private Function<SignInInfo, Integer> onSignUp;
    private Function<SignInInfo, Integer> onPasswordReset;
    private Runnable handleSignInActivate;
    private Runnable handleSignUpActivate;
    private Runnable handlePasswordResetActivate;

    public void setOnSignIn(Function<SignInInfo, Integer> onSignIn) {
        this.onSignIn = onSignIn;
    }

    public void setOnSignUp(Function<SignInInfo, Integer> onSignUp) {
        this.onSignUp = onSignUp;
    }

    public void setOnPasswordReset(Function<SignInInfo, Integer> onPasswordReset) {
        this.onPasswordReset = onPasswordReset;
    }

    public class SignInInfo {
        private String apiUrl;

        private String displayName;

        private String email;

        private String password;

        private Boolean isSignUp = false;

        private Boolean isPasswordReset = false;

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean getIsSignUp() {
            return isSignUp;
        }

        public void setIsSignUp(Boolean isSignUp) {
            this.isSignUp = isSignUp;
        }

        public Boolean getIsPasswordReset() {
            return isPasswordReset;
        }

        public void setIsPasswordReset(Boolean isPasswordReset) {
            this.isPasswordReset = isPasswordReset;
        }
    }

    public Node render() {
        var wrapper = new VBox();

        var header = new Label("unnecessary.ly");
        header.setStyle("-fx-font-size: 42; -fx-font-weight: bold");
        header.setPadding(new Insets(0, 0, 24, 0));

        var apiUrlField = new TextField("localhost:1999");
        apiUrlField.setPromptText("API URL");
        apiUrlField.setMaxWidth(300);
        apiUrlField.setStyle("-fx-background-radius: 16");

        var displayNameField = new TextField();
        displayNameField.setPromptText("Display name");
        displayNameField.setMaxWidth(300);
        displayNameField.setStyle("-fx-background-radius: 16");
        displayNameField.setVisible(false);
        displayNameField.setManaged(false);

        var emailField = new TextField("felix@pojtinger.com");
        emailField.setPromptText("Email");
        emailField.setMaxWidth(300);
        emailField.setStyle("-fx-background-radius: 16");

        var passwordField = new PasswordField();
        passwordField.setText("pass1234");
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-background-radius: 16");

        var actionWrapper = new HBox();

        var signInButton = new Button("Sign in");
        signInButton.setStyle("-fx-base: royalblue; -fx-background-radius: 16");

        var signUpButton = new Button("Sign up");
        signUpButton.setStyle("-fx-background-radius: 16");

        var passwordResetButton = new Button("Reset password");
        passwordResetButton.setStyle("-fx-background-radius: 16");

        actionWrapper.getChildren().addAll(passwordResetButton, signUpButton, signInButton);
        actionWrapper.setAlignment(Pos.CENTER_RIGHT);
        actionWrapper.setMaxWidth(300);
        actionWrapper.setSpacing(8);
        actionWrapper.setPadding(new Insets(16, 0, 0, 0));

        var signInInfo = new SignInInfo();

        Runnable submitHandler = () -> {
            var invalidSignInInfoAlert = new Alert(AlertType.ERROR);
            invalidSignInInfoAlert.setHeaderText("Invalid credentials");
            invalidSignInInfoAlert.setContentText("Invalid API URL, email or password.");

            signInInfo.setApiUrl(apiUrlField.getText());
            signInInfo.setDisplayName(displayNameField.getText());
            signInInfo.setEmail(emailField.getText());
            signInInfo.setPassword(passwordField.getText());

            final int rv;
            if (signInInfo.getIsSignUp()) {
                rv = this.onSignUp.apply(signInInfo);
            } else if (!signInInfo.getIsPasswordReset()) {
                rv = this.onSignIn.apply(signInInfo);
            } else {
                rv = this.onPasswordReset.apply(signInInfo);
            }

            switch (rv) {
                case 0:
                    apiUrlField.clear();
                    displayNameField.clear();
                    emailField.clear();
                    passwordField.clear();

                    return;
                case 1:
                    return;
                case 2:
                    invalidSignInInfoAlert.show();

                    return;
            }
        };

        this.handleSignInActivate = () -> {
            signInInfo.setIsSignUp(false);
            signInInfo.setIsPasswordReset(false);

            displayNameField.setVisible(false);
            displayNameField.setManaged(false);

            passwordField.setVisible(true);
            passwordField.setManaged(true);

            signInButton.setOnAction((e) -> submitHandler.run());
            signUpButton.setOnAction((e) -> this.handleSignUpActivate.run());
            passwordResetButton.setOnAction((e) -> this.handlePasswordResetActivate.run());
        };

        this.handleSignUpActivate = () -> {
            signInInfo.setIsSignUp(true);
            signInInfo.setIsPasswordReset(false);

            displayNameField.setVisible(true);
            displayNameField.setManaged(true);

            passwordField.setVisible(true);
            passwordField.setManaged(true);

            signInButton.setOnAction((e) -> this.handleSignInActivate.run());
            signUpButton.setOnAction((e) -> submitHandler.run());
            passwordResetButton.setOnAction((e) -> this.handlePasswordResetActivate.run());
        };

        this.handlePasswordResetActivate = () -> {
            signInInfo.setIsSignUp(false);
            signInInfo.setIsPasswordReset(true);

            displayNameField.setVisible(false);
            displayNameField.setManaged(false);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            signInButton.setOnAction((e) -> this.handleSignInActivate.run());
            signUpButton.setOnAction((e) -> this.handleSignUpActivate.run());
            passwordResetButton.setOnAction((e) -> submitHandler.run());
        };

        apiUrlField.setOnAction((e) -> submitHandler.run());
        emailField.setOnAction((e) -> submitHandler.run());
        displayNameField.setOnAction((e) -> submitHandler.run());
        passwordField.setOnAction((e) -> submitHandler.run());
        signUpButton.setOnAction((e) -> this.handleSignUpActivate.run());
        signInButton.setOnAction((e) -> submitHandler.run());
        passwordResetButton.setOnAction((e) -> this.handlePasswordResetActivate.run());

        wrapper.getChildren().addAll(header, apiUrlField, displayNameField, emailField, passwordField, actionWrapper);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(8));
        wrapper.setSpacing(8);
        wrapper.setStyle("-fx-font-family: 'Arial';");

        return wrapper;
    }
}