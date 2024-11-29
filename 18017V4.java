import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Pizza class definition
class Pizza {
    private String size;
    private Vector<String> toppings;

    public Pizza(String size) {
        this.size = size;
        this.toppings = new Vector<>();
    }

    public void addTopping(String topping) {
        toppings.add(topping);
    }

    public String getSize() {
        return size;
    }

    public Vector<String> getToppings() {
        return toppings;
    }

    @Override
    public String toString() {
        return "Size: " + size + ", Toppings: " + String.join(", ", toppings);
    }
}

// PizzaShop class for handling database operations
class PizzaShop {
    private Connection conn;

    public PizzaShop() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/PizzaShopDB", "root", "");
            System.out.println("Connected to the database!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    public void addOrder(Vector<Pizza> order) {
        try {
            String orderQuery = "INSERT INTO Orders (order_number) VALUES (NULL)";
            PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderStmt.executeUpdate();

            ResultSet generatedKeys = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            }

            String pizzaQuery = "INSERT INTO Pizzas (order_id, size, toppings) VALUES (?, ?, ?)";
            for (Pizza pizza : order) {
                PreparedStatement pizzaStmt = conn.prepareStatement(pizzaQuery);
                pizzaStmt.setInt(1, orderId);
                pizzaStmt.setString(2, pizza.getSize());
                pizzaStmt.setString(3, String.join(", ", pizza.getToppings()));
                pizzaStmt.executeUpdate();
            }
            System.out.println("Order placed successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public Vector<Vector<Object>> getOrders() {
        Vector<Vector<Object>> data = new Vector<>();
        String query = "SELECT o.id, p.size, p.toppings FROM Orders o JOIN Pizzas p ON o.id = p.order_id";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("size"));
                row.add(rs.getString("toppings"));
                data.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return data;
    }

    public void cancelOrder(int orderId) {
        try {
            String deletePizzasQuery = "DELETE FROM Pizzas WHERE order_id = ?";
            PreparedStatement deletePizzasStmt = conn.prepareStatement(deletePizzasQuery);
            deletePizzasStmt.setInt(1, orderId);
            deletePizzasStmt.executeUpdate();

            String deleteOrderQuery = "DELETE FROM Orders WHERE id = ?";
            PreparedStatement deleteOrderStmt = conn.prepareStatement(deleteOrderQuery);
            deleteOrderStmt.setInt(1, orderId);
            int affectedRows = deleteOrderStmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Order #" + orderId + " has been canceled.");
            } else {
                System.out.println("Order not found!");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

// Main class for the GUI
public class PizzaShopGUI extends JFrame {
    private PizzaShop pizzaShop;
    private JTextField sizeField;
    private JTextField toppingsField;
    private JTextField cancelOrderField;
    private JTable table;
    private DefaultTableModel tableModel;

    public PizzaShopGUI() {
        pizzaShop = new PizzaShop();
        initComponents();
    }

    private void initComponents() {
        setTitle("Pizza Shop Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Pizza Size:"));
        sizeField = new JTextField();
        panel.add(sizeField);
        panel.add(new JLabel("Toppings (comma separated):"));
        toppingsField = new JTextField();
        panel.add(toppingsField);

        JButton addButton = new JButton("Add Order");
        JButton displayButton = new JButton("Display Orders");

        panel.add(addButton);
        panel.add(displayButton);

        panel.add(new JLabel("Order ID to Cancel:"));
        cancelOrderField = new JTextField();
        panel.add(cancelOrderField);
        JButton cancelButton = new JButton("Cancel Order");
        panel.add(cancelButton);

        add(panel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Order ID", "Pizza Size", "Toppings"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addButton.addActionListener(e -> addOrder());
        displayButton.addActionListener(e -> displayOrders());
        cancelButton.addActionListener(e -> cancelOrder());

        setVisible(true);
    }

    private void addOrder() {
        String size = sizeField.getText();
        String[] toppingsArray = toppingsField.getText().split(",");
        Vector<Pizza> order = new Vector<>();
        Pizza pizza = new Pizza(size);
        for (String topping : toppingsArray) {
            pizza.addTopping(topping.trim());
        }
        order.add(pizza);
        pizzaShop.addOrder(order);
        JOptionPane.showMessageDialog(this, "Order added successfully.");
    }

    private void displayOrders() {
        Vector<Vector<Object>> data = pizzaShop.getOrders();
        tableModel.setRowCount(0); // Clear existing data
        for (Vector<Object> row : data) {
            tableModel.addRow(row);
        }
    }

    private void cancelOrder() {
        try {
            int orderId = Integer.parseInt(cancelOrderField.getText());
            pizzaShop.cancelOrder(orderId);
            JOptionPane.showMessageDialog(this, "Order canceled successfully.");
            displayOrders();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid order ID.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PizzaShopGUI::new);
    }
}