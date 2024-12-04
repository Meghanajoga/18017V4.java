import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

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

    @Override
    public String toString() {
        return "Size: " + size + ", Toppings: " + String.join(", ", toppings);
    }
}

class PizzaShop {
    private Vector<Vector<Pizza>> orders;

    public PizzaShop() {
        orders = new Vector<>();
    }

    public void addOrder(Vector<Pizza> order) {
        if (orders.size() < 100) {
            orders.add(order);
            JOptionPane.showMessageDialog(null, "Order placed successfully.");
        } else {
            JOptionPane.showMessageDialog(null, "Maximum order limit reached!");
        }
    }

    public void addPizzaToOrder(int orderIndex, Pizza pizza) {
        if (orderIndex >= 0 && orderIndex < orders.size()) {
            orders.get(orderIndex).add(pizza);
            JOptionPane.showMessageDialog(null, "Pizza added to Order #" + (orderIndex + 1));
        } else {
            JOptionPane.showMessageDialog(null, "Invalid order number.");
        }
    }

    public void deletePizzaFromOrder(int orderIndex, int pizzaIndex) {
        if (orderIndex >= 0 && orderIndex < orders.size()) {
            Vector<Pizza> order = orders.get(orderIndex);
            if (pizzaIndex >= 0 && pizzaIndex < order.size()) {
                order.remove(pizzaIndex);
                JOptionPane.showMessageDialog(null, "Pizza #" + (pizzaIndex + 1) + " has been deleted from Order #" + (orderIndex + 1) + ".");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid pizza number.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid order number.");
        }
    }

    public void deleteOrder(int orderIndex) {
        if (orderIndex >= 0 && orderIndex < orders.size()) {
            orders.remove(orderIndex);
            JOptionPane.showMessageDialog(null, "Order #" + (orderIndex + 1) + " has been deleted.");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid order number.");
        }
    }

    public void displayOrders() {
        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No orders placed.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < orders.size(); i++) {
            sb.append("Order #").append(i + 1).append(":\n");
            Vector<Pizza> order = orders.get(i);
            for (int j = 0; j < order.size(); j++) {
                sb.append("  Pizza #").append(j + 1).append(": ").append(order.get(j)).append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }
}

public class PizzaShopAppGUI extends JFrame {
    private PizzaShop pizzaShop;

    public PizzaShopAppGUI() {
        pizzaShop = new PizzaShop();

        setTitle("Pizza Shop");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        JButton placeOrderButton = new JButton("Place a new order");
        JButton addPizzaButton = new JButton("Add a pizza to an existing order");
        JButton deletePizzaButton = new JButton("Delete a pizza from an existing order");
        JButton deleteOrderButton = new JButton("Delete an existing order");
        JButton displayOrdersButton = new JButton("Display all orders");
        JButton exitButton = new JButton("Exit");

        placeOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });

        addPizzaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPizzaToOrder();
            }
        });

        deletePizzaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deletePizzaFromOrder();
            }
        });

        deleteOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });

        displayOrdersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pizzaShop.displayOrders();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(placeOrderButton);
        add(addPizzaButton);
        add(deletePizzaButton);
        add(deleteOrderButton);
        add(displayOrdersButton);
        add(exitButton);
    }

    private void placeOrder() {
        Vector<Pizza> currentOrder = new Vector<>();
        while (true) {
            String size = JOptionPane.showInputDialog(this, "Enter pizza size (small, medium, large) or 'done' to finish this order:");
            if (size == null || size.equalsIgnoreCase("done")) {
                break;
            }

            Pizza pizza = new Pizza(size);
            while (true) {
                String topping = JOptionPane.showInputDialog(this, "Enter topping to add (Onions, Black Olives, Mushrooms, Extra Cheese, Pepperoni, Sausage, Bell Pepper) or 'done' to finish adding toppings:");
                if (topping == null || topping.equalsIgnoreCase("done")) {
                    break;
                }
                pizza.addTopping(topping);
            }

            currentOrder.add(pizza);
        }
        pizzaShop.addOrder(currentOrder);
    }

    private void addPizzaToOrder() {
        String orderNumberStr = JOptionPane.showInputDialog(this, "Enter the order number to which you want to add a pizza:");
        int orderNumber = Integer.parseInt(orderNumberStr) - 1; // Adjust for zero indexing

        String size = JOptionPane.showInputDialog(this, "Enter pizza size (small, medium, large):");
        Pizza pizza = new Pizza(size);

        while (true) {
            String topping = JOptionPane.showInputDialog(this, "Enter topping to add (or 'done' to finish adding toppings):");
            if (topping == null || topping.equalsIgnoreCase("done")) {
                break;
            }
            pizza.addTopping(topping);
        }

        pizzaShop.addPizzaToOrder(orderNumber, pizza);
    }

    private void deletePizzaFromOrder() {
        String orderNumberStr = JOptionPane.showInputDialog(this, "Enter the order number to delete a pizza from:");
        int orderNumber = Integer.parseInt(orderNumberStr) - 1; // Adjust for zero indexing

        pizzaShop.displayOrders();
        String pizzaNumberStr = JOptionPane.showInputDialog(this, "Enter the pizza number to delete:");
        int pizzaNumber = Integer.parseInt(pizzaNumberStr) - 1; // Adjust for zero indexing

        pizzaShop.deletePizzaFromOrder(orderNumber, pizzaNumber);
    }

    private void deleteOrder() {
        String orderNumberStr = JOptionPane.showInputDialog(this, "Enter the order number to delete:");
        int orderNumber = Integer.parseInt(orderNumberStr) - 1; // Adjust for zero indexing

        pizzaShop.deleteOrder(orderNumber);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PizzaShopAppGUI().setVisible(true);
            }
        });
    }
}