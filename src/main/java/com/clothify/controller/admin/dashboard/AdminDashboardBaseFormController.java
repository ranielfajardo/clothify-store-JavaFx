package com.clothify.controller.admin.dashboard;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminDashboardBaseFormController implements Initializable {

    @FXML
    private JFXButton btnCustomer;

    @FXML
    private JFXButton btnDashboard;

    @FXML
    private JFXButton btnEmployee;

    @FXML
    private JFXButton btnReturn;

    @FXML
    private JFXButton btnLogout;

    @FXML
    private JFXButton btnOrders;

    @FXML
    private JFXButton btnPlaceOrder;

    @FXML
    private JFXButton btnProduct;

    @FXML
    private JFXButton btnSettings;

    @FXML
    private JFXButton btnSupplier;

    @FXML
    private BorderPane mainBorderPane;

    private List<JFXButton> buttonList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonList = Arrays.asList(
                btnDashboard,
                btnPlaceOrder,
                btnOrders,
                btnProduct,
                btnSupplier,
                btnEmployee,
                btnReturn,
                btnCustomer,
                btnSettings
        );

        changeTheButtonStyle(btnDashboard);
        loadContent("/view/admin/dashboard/admin_dashboard_form.fxml");
    }

    @FXML
    void btnDashboardOnAction(ActionEvent event) {
        System.out.println("Dashboard clicked");
        changeTheButtonStyle(btnDashboard);
        loadContent("/view/admin/dashboard/admin_dashboard_form.fxml");
    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) {
        System.out.println("Place Order clicked");
        changeTheButtonStyle(btnPlaceOrder);
        loadContent("/view/common/order/place_order_form.fxml");
    }

    @FXML
    void btnCustomerOnAction(ActionEvent event) {
        System.out.println("Customer clicked");
        changeTheButtonStyle(btnCustomer);
        loadContent("/view/common/customer/customer_form.fxml");
    }

    @FXML
    void btnEmployeeOnAction(ActionEvent event) {
        System.out.println("Employee clicked");
        changeTheButtonStyle(btnEmployee);
        loadContent("/view/admin/employee/employee_form.fxml");
    }

    @FXML
    void btnReturnOnAction(ActionEvent event) {
        System.out.println("Return clicked");
        changeTheButtonStyle(btnReturn);
        loadContent("/view/common/order/return_order_form.fxml");
    }

    @FXML
    void btnOrdersOnAction(ActionEvent event) {
        System.out.println("Orders clicked");
        changeTheButtonStyle(btnOrders);
        loadContent("/view/common/order/orders_form.fxml");
    }

    @FXML
    void btnProductOnAction(ActionEvent event) {
        System.out.println("Product clicked");
        changeTheButtonStyle(btnProduct);
        loadContent("/view/common/product/product_form.fxml");
    }

    @FXML
    void btnSupplierOnAction(ActionEvent event) {
        System.out.println("Supplier clicked");
        changeTheButtonStyle(btnSupplier);
        loadContent("/view/admin/supplier/supplier_form.fxml");
    }

    @FXML
    void btnSettingsOnAction(ActionEvent event) {
        System.out.println("Settings clicked");
        changeTheButtonStyle(btnSettings);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Settings Page");
        alert.setContentText("Settings page is not yet available in this project.");
        alert.showAndWait();
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) {
        logout();
    }

    @FXML
    void menubarCloseOnAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void menubarLogoutOnAction(ActionEvent event) {
        logout();
    }

    private void changeTheButtonStyle(JFXButton selectedButton) {
        if (buttonList == null) {
            return;
        }

        for (JFXButton button : buttonList) {
            if (button != null) {
                button.setStyle(
                        "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 5;"
                );
            }
        }

        if (selectedButton != null) {
            selectedButton.setStyle(
                    "-fx-background-color: #C4E3FF;" +
                    "-fx-background-radius: 5;"
            );
        }
    }

    private void loadContent(String fxmlPath) {
    try {
        URL resource = getClass().getResource(fxmlPath);

        if (resource == null) {
            System.out.println("FXML not found: " + fxmlPath);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("FXML Error");
            alert.setHeaderText("Page not found");
            alert.setContentText("Cannot find: " + fxmlPath);
            alert.showAndWait();
            return;
        }

        AnchorPane content = FXMLLoader.load(resource);
        mainBorderPane.setCenter(content);

    } catch (Exception e) {
        e.printStackTrace();

        Throwable root = e;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load Error");
        alert.setHeaderText("Cannot load page");
        alert.setContentText(root.getMessage());
        alert.showAndWait();
    }
}

    private void logout() {
        Alert logoutAlert = new Alert(Alert.AlertType.CONFIRMATION);
        logoutAlert.setTitle("Clothify Store");
        logoutAlert.setHeaderText("Logout Confirmation");
        logoutAlert.setContentText("Do you want to logout?");

        Optional<ButtonType> buttonType = logoutAlert.showAndWait();

        if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
            try {
                Stage stage = new Stage();
                stage.setScene(new Scene(
                        FXMLLoader.load(getClass().getResource("/view/login_form.fxml"))
                ));
                stage.setTitle("Login");
                stage.setResizable(false);
                stage.getIcons().add(new Image("img/logo-round.png"));
                stage.show();

                btnDashboard.getScene().getWindow().hide();

            } catch (IOException e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Logout Error");
                alert.setHeaderText("Cannot return to login page");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
}