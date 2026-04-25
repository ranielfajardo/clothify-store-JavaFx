package com.clothify.controller.common.order;

import com.clothify.dto.*;
import com.clothify.service.ServiceFactory;
import com.clothify.service.custom.CustomerService;
import com.clothify.service.custom.OrderService;
import com.clothify.service.custom.ProductService;
import com.clothify.util.CustomAlert;
import com.clothify.util.PDFGenerator;
import com.clothify.util.ProductType;
import com.clothify.util.ServiceType;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PlaceOrderFormController implements Initializable {

    @FXML
    private JFXButton btnGents;

    @FXML
    private JFXButton btnKids;

    @FXML
    private JFXButton btnLadies;

    @FXML
    private JFXButton btnPlaceOrder;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblDiscount;

    @FXML
    private Label lblOrderID;

    @FXML
    private Label lblSubTotal;

    @FXML
    private Label lblTotal;

    @FXML
    private GridPane productGrid;

    @FXML
    private ScrollPane productScrollPane;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TableColumn<CartTM, JFXButton> colAction;

    @FXML
    private TableColumn<CartTM, Double> colPrice;

    @FXML
    private TableColumn<CartTM, String> colProductName;

    @FXML
    private TableColumn<CartTM, Integer> colQty;

    @FXML
    private TableView<CartTM> tblCart;

    @FXML
    private TextField txtSearch;

    @Getter
    private static PlaceOrderFormController placeOrderFormController;

    private final OrderService service = ServiceFactory.getInstance().getServiceType(ServiceType.ORDER);
    private final ProductService productService = ServiceFactory.getInstance().getServiceType(ServiceType.PRODUCT);
    private final CustomerService customerService = ServiceFactory.getInstance().getServiceType(ServiceType.CUSTOMER);

    private List<Product> productList = new ArrayList<>();
    private List<JFXButton> buttonList = new ArrayList<>();
    private final ObservableList<CartTM> cartList = FXCollections.observableArrayList();

    private Customer searchedCustomer;
    private double total = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        placeOrderFormController = this;

        buttonList = Arrays.asList(btnGents, btnLadies, btnKids);

        setupCartTable();
        setupDefaultValues();

        try {
            List<Product> products = productService.getAllProducts();
            productList = products == null ? new ArrayList<>() : products;
        } catch (Exception e) {
            e.printStackTrace();
            productList = new ArrayList<>();
        }

        loadProducts(ProductType.GENTS);
        changeTheButtonStyle(btnGents);
        loadDateTime();
        generateNextID();

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                searchProducts(newValue == null ? "" : newValue);
            });
        }
    }

    private void setupCartTable() {
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btnDelete"));
        tblCart.setItems(cartList);
    }

    private void setupDefaultValues() {
        txtSearch.setText("");
        txtPhoneNumber.setText("");
        txtCustomerName.setText("");

        lblSubTotal.setText("0.00");
        lblDiscount.setText("0.00");
        lblTotal.setText("LKR 0.00");

        btnPlaceOrder.setDisable(true);
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private void searchProducts(String searchQuery) {
        List<Product> filteredProducts = new ArrayList<>();

        if (productList == null) {
            productList = new ArrayList<>();
        }

        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadProducts(ProductType.GENTS);
            return;
        }

        for (Product product : productList) {
            if (product == null || product.getName() == null) {
                continue;
            }

            if (product.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredProducts.add(product);
            }
        }

        loadProductToGridPane(filteredProducts);
    }

    @FXML
    void btnGentsOnAction(ActionEvent event) {
        loadProducts(ProductType.GENTS);
        changeTheButtonStyle(btnGents);
    }

    @FXML
    void btnKidsOnAction(ActionEvent event) {
        loadProducts(ProductType.KIDS);
        changeTheButtonStyle(btnKids);
    }

    @FXML
    void btnLadiesOnAction(ActionEvent event) {
        loadProducts(ProductType.LADIES);
        changeTheButtonStyle(btnLadies);
    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) {
        if (searchedCustomer == null) {
            CustomAlert.showAlert(
                    Alert.AlertType.WARNING,
                    "Clothify Store",
                    "Please search/select a customer first.",
                    "/img/icon/warning-48.png"
            );
            return;
        }

        if (cartList.isEmpty()) {
            CustomAlert.showAlert(
                    Alert.AlertType.WARNING,
                    "Clothify Store",
                    "Please add at least one product to the cart.",
                    "/img/icon/warning-48.png"
            );
            return;
        }

        Order order = new Order(
                lblOrderID.getText(),
                loadDateTime(),
                searchedCustomer.getId(),
                total
        );

        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartTM obj : cartList) {
            orderDetails.add(
                    new OrderDetail(
                            lblOrderID.getText(),
                            obj.getProductId(),
                            obj.getQuantity(),
                            0.0
                    )
            );
        }

        try {
            if (service.placeOrder(order, orderDetails)) {
                CustomAlert.showAlert(
                        Alert.AlertType.WARNING,
                        "Clothify Store",
                        "Order Placed Successfully",
                        "/img/icon/success-48.png"
                );

                ObservableList<OrderDetail> orderDetailObservableList = FXCollections.observableArrayList();
                orderDetailObservableList.addAll(orderDetails);

                PDFGenerator pdfGenerator = new PDFGenerator();
                pdfGenerator.downloadPdf((Stage) btnPlaceOrder.getScene().getWindow(), orderDetailObservableList);

                clearAll();
                generateNextID();

            } else {
                CustomAlert.showAlert(
                        Alert.AlertType.WARNING,
                        "Clothify Store",
                        "Order Placed Failed",
                        "/img/icon/error-48.png"
                );
            }
        } catch (SQLException e) {
            CustomAlert.errorAlert("Clothify Store", e);
        } catch (Exception e) {
            e.printStackTrace();
            CustomAlert.errorAlert("Clothify Store", e);
        }
    }

    @FXML
    void iconAddCustomerOnClick(MouseEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/view/common/customer/add_customer_form.fxml"))));
            stage.setTitle("Add Customer");
            stage.setResizable(false);
            stage.getIcons().add(new Image("img/logo-round.png"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlert.errorAlert("Clothify Store", e);
        }
    }

    @FXML
    void iconFilterOnClick(MouseEvent event) {
        System.out.println("Filter clicked");
    }

    @FXML
    void txtPhoneNumberOnAction(ActionEvent event) {
        String phone = txtPhoneNumber.getText();

        if (phone == null || phone.trim().isEmpty()) {
            CustomAlert.showAlert(
                    Alert.AlertType.WARNING,
                    "Clothify Store",
                    "Please enter customer phone number.",
                    "/img/icon/warning-48.png"
            );
            return;
        }

        searchedCustomer = customerService.searchCustomer(phone.trim());

        if (searchedCustomer != null) {
            txtCustomerName.setText(safeText(searchedCustomer.getName()));
            btnPlaceOrder.setDisable(cartList.isEmpty());
        } else {
            txtCustomerName.setText("");
            btnPlaceOrder.setDisable(true);

            CustomAlert.showAlert(
                    Alert.AlertType.WARNING,
                    "Clothify Store",
                    "Customer Not Found!",
                    "/img/icon/warning-48.png"
            );
        }
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        searchProducts(txtSearch.getText());
    }

    private void loadProducts(ProductType productType) {
        List<Product> categorizedProductList = new ArrayList<>();

        if (productList == null) {
            productList = new ArrayList<>();
        }

        for (Product product : productList) {
            if (product == null || product.getCategory() == null) {
                continue;
            }

            String category = product.getCategory();

            switch (productType) {
                case GENTS:
                    if ("GENTS".equalsIgnoreCase(category)) {
                        categorizedProductList.add(product);
                    }
                    break;

                case LADIES:
                    if ("LADIES".equalsIgnoreCase(category)) {
                        categorizedProductList.add(product);
                    }
                    break;

                case KIDS:
                    if ("KIDS".equalsIgnoreCase(category)) {
                        categorizedProductList.add(product);
                    }
                    break;
            }
        }

        loadProductToGridPane(categorizedProductList);
    }

    private void loadProductToGridPane(List<Product> categorizedProductList) {
        productGrid.getChildren().clear();

        if (categorizedProductList == null || categorizedProductList.isEmpty()) {
            System.out.println("No products found for this category.");
            return;
        }

        int column = 0;
        int row = 1;

        try {
            for (Product product : categorizedProductList) {
                if (product == null) {
                    continue;
                }

                URL cardUrl = getClass().getResource("/view/common/order/order_product_card.fxml");

                if (cardUrl == null) {
                    System.out.println("FXML not found: /view/common/order/order_product_card.fxml");
                    return;
                }

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(cardUrl);

                AnchorPane anchorPane = fxmlLoader.load();

                OrderProductCardController orderProductCardController = fxmlLoader.getController();
                orderProductCardController.setProduct(product);

                if (column == 4) {
                    column = 0;
                    row++;
                }

                productGrid.add(anchorPane, column++, row);

                productGrid.setMinWidth(Region.USE_COMPUTED_SIZE);
                productGrid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                productGrid.setMaxWidth(Region.USE_PREF_SIZE);

                productGrid.setMinHeight(Region.USE_COMPUTED_SIZE);
                productGrid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                productGrid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
            CustomAlert.errorAlert("Clothify Store", e);
        }
    }

    public void addToCart(Product product, Integer quantity) {
        if (product == null) {
            return;
        }

        if (quantity == null || quantity <= 0) {
            CustomAlert.showAlert(
                    Alert.AlertType.ERROR,
                    "Clothify Store",
                    "Invalid quantity.",
                    "/img/icon/error-48.png"
            );
            return;
        }

        if (quantity > product.getQuantity()) {
            CustomAlert.showAlert(
                    Alert.AlertType.ERROR,
                    "Clothify Store",
                    "Insufficient Stock",
                    "/img/icon/error-48.png"
            );
            return;
        }

        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/img/icon/delete.png")));
        JFXButton btnDelete = new JFXButton();
        btnDelete.setGraphic(deleteIcon);
        btnDelete.setCursor(Cursor.HAND);

        cartList.removeIf(oldProduct -> oldProduct.getProductId().equals(product.getId()));

        CartTM cartItem = new CartTM(
                product.getId(),
                safeText(product.getName()),
                quantity,
                product.getUnitPrice(),
                btnDelete
        );

        btnDelete.setOnAction(event -> {
            cartList.remove(cartItem);
            tblCart.setItems(cartList);
            setTotal();
            btnPlaceOrder.setDisable(searchedCustomer == null || cartList.isEmpty());
        });

        cartList.add(cartItem);
        tblCart.setItems(cartList);
        setTotal();

        btnPlaceOrder.setDisable(searchedCustomer == null || cartList.isEmpty());
    }

    private void setTotal() {
        total = 0.0;

        for (CartTM cartTM : cartList) {
            total += cartTM.getUnitPrice() * cartTM.getQuantity();
        }

        lblSubTotal.setText(String.format("%.2f", total));
        lblTotal.setText(String.format("LKR %.2f", total));
    }

    private void changeTheButtonStyle(JFXButton selectedButton) {
        if (buttonList == null) {
            return;
        }

        for (JFXButton button : buttonList) {
            if (button != null) {
                button.setStyle("-fx-background-color: #C4E3FF");
            }
        }

        if (selectedButton != null) {
            selectedButton.setStyle("-fx-background-color: #308EDF");
        }
    }

    private LocalDateTime loadDateTime() {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateNow = f.format(date);

        lblDate.setText(dateNow.substring(0, 10));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateNow, formatter);
    }

    private void generateNextID() {
        String lastOrderId = null;

        try {
            lastOrderId = service.getLastOrderID();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int idNumber = 0;

        if (lastOrderId != null && !lastOrderId.trim().isEmpty()) {
            String digitsOnly = lastOrderId.replaceAll("[^0-9]", "");

            if (!digitsOnly.isEmpty()) {
                try {
                    idNumber = Integer.parseInt(digitsOnly);
                } catch (NumberFormatException e) {
                    idNumber = 0;
                }
            }
        }

        int nextId = idNumber + 1;
        lblOrderID.setText(String.format("D%03d", nextId));
    }

    private void clearAll() {
        cartList.clear();
        tblCart.setItems(cartList);

        txtSearch.setText("");
        txtPhoneNumber.setText("");
        txtCustomerName.setText("");

        searchedCustomer = null;
        total = 0.0;

        lblSubTotal.setText("0.00");
        lblDiscount.setText("0.00");
        lblTotal.setText("LKR 0.00");

        btnPlaceOrder.setDisable(true);
    }
}