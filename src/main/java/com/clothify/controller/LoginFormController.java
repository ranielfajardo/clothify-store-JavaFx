package com.clothify.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable {

    @FXML
    private JFXButton btnSignIn;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private Label heading;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    void btnSignInOnAction(ActionEvent event) {
        openAdminDashboard();
    }

    @FXML
    void txtPasswordOnAction(ActionEvent event) {
        openAdminDashboard();
    }

    private void openAdminDashboard() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/view/admin/dashboard/admin_dashboard_base_form.fxml")
            );

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.setResizable(false);
            stage.getIcons().add(new Image("img/logo-round.png"));
            stage.show();

            btnSignIn.getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void txtForgotPasswordOnAction(MouseEvent event) {
        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/view/auth/forgot_password.fxml"))));
            stage.setTitle("Forgot Password");
            stage.setResizable(false);
            stage.getIcons().add(new Image("img/logo-round.png"));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Font customFont = Font.loadFont(getClass().getResource("/fonts/Poppins-Medium.ttf").toExternalForm(), 40);
        // heading.setFont(customFont);
    }
}