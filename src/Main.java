
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// ---------------- INTERFACES ----------------
interface OrderOperations {
    void placeOrder(Order order);
    void cancelOrder(int orderId);
}

interface Payment {
    void makePayment(double amount, String method) throws PaymentException;
}

// ---------------- CUSTOM EXCEPTION ----------------
class PaymentException extends Exception {
    public PaymentException(String message) {
        super(message);
    }
}

// ---------------- ABSTRACT CLASS ----------------
abstract class Person {
    protected int id;
    protected String name;
    protected String phone;

    public Person(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }
}

// ---------------- CUSTOMER ----------------
class Customer extends Person implements Payment {

    public Customer(int id, String name, String phone) {
        super(id, name, phone);
    }

    public void viewMenu(ArrayList<FoodItem> menu) {
        System.out.println("\n----- RESTAURANT MENU -----");
        for (FoodItem f : menu) {
            System.out.println(f.getItemId() + ". " + f.getItemName()
                    + " (" + f.getCategory() + ") - Rs " + f.getPrice());
        }
    }

    @Override
    public void makePayment(double amount, String method) throws PaymentException {
        if (amount <= 0) {
            throw new PaymentException("Invalid payment amount!");
        }
        System.out.println("Payment successful: Rs " + amount + " via " + method);
    }
}

// ---------------- FOOD ITEM ----------------
class FoodItem {
    private int itemId;
    private String itemName;
    private String category;
    private double price;

    public FoodItem(int itemId, String itemName, String category, double price) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
    }

    public int getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
}

// ---------------- ORDER ----------------
class Order {
    private int orderId;
    private ArrayList<FoodItem> items = new ArrayList<>();

    public Order(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() { return orderId; }

    public void addItem(FoodItem item) {
        items.add(item);
    }

    public double calculateTotal() {
        double total = 0;
        for (FoodItem f : items) total += f.getPrice();
        if (total > 2000) total *= 0.90; // 10% discount
        return total;
    }

    public String toFileString() {
        String data = "Order ID: " + orderId + " | Items: ";
        for (FoodItem f : items) data += f.getItemName() + ", ";
        data += "| Final Total: Rs " + calculateTotal();
        return data;
    }
}

// ---------------- RESTAURANT ----------------
class Restaurant implements OrderOperations {
    private ArrayList<FoodItem> menu = new ArrayList<>();
    private ArrayList<Order> orders = new ArrayList<>();

    public void addFoodItem(FoodItem item) { menu.add(item); }
    public ArrayList<FoodItem> getMenu() { return menu; }

    @Override
    public void placeOrder(Order order) {
        orders.add(order);
        System.out.println("Order placed successfully.");
    }

    @Override
    public void cancelOrder(int orderId) {
        orders.removeIf(o -> o.getOrderId() == orderId);
        System.out.println("Order cancelled successfully.");
    }
}

// ---------------- FILE HANDLING ----------------
class FileManager {
    public static void saveOrder(Order order) {
        try (FileWriter fw = new FileWriter("restaurant_orders.txt", true)) {
            fw.write(order.toFileString() + "\n");
            System.out.println("Order saved to file.");
        } catch (IOException e) {
            System.out.println("File handling error.");
        }
    }
}

// ---------------- MULTITHREADING ----------------
class OrderProcessor extends Thread {
    private Order order;

    public OrderProcessor(Order order) {
        this.order = order;
    }

    @Override
    public void run() {
        System.out.println("Preparing your food...");
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
        System.out.println("Order ready! Total Bill: Rs " + order.calculateTotal());
    }
}

// ---------------- MAIN ----------------
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Restaurant restaurant = new Restaurant();

        // Add Food Items
        restaurant.addFoodItem(new FoodItem(1, "Chicken Biryani", "Main Course", 350));
        restaurant.addFoodItem(new FoodItem(2, "Veg Pizza", "Fast Food", 500));
        restaurant.addFoodItem(new FoodItem(3, "Burger", "Fast Food", 250));
        restaurant.addFoodItem(new FoodItem(4, "Pasta", "Italian", 400));
        restaurant.addFoodItem(new FoodItem(5, "Cold Drink", "Beverage", 100));
        restaurant.addFoodItem(new FoodItem(6, "Ice Cream", "Dessert", 150));

        Customer customer = new Customer(201, "Muskan", "98XXXXXXXX");
        customer.viewMenu(restaurant.getMenu());

        Order order = new Order(1);

        while (true) {
            System.out.print("\nEnter food item number (0 to finish): ");
            int choice = sc.nextInt();
            if (choice == 0) break;

            boolean found = false;
            for (FoodItem f : restaurant.getMenu()) {
                if (f.getItemId() == choice) {
                    order.addItem(f);
                    System.out.println(f.getItemName() + " added to cart.");
                    found = true;
                    break;
                }
            }
            if (!found) System.out.println("Invalid choice.");
        }

        restaurant.placeOrder(order);

        System.out.print("Enter payment method (Cash/Online): ");
        String paymentMethod = sc.next();

        try {
            customer.makePayment(order.calculateTotal(), paymentMethod);
        } catch (PaymentException e) {
            System.out.println(e.getMessage());
        }

        FileManager.saveOrder(order);

        OrderProcessor processor = new OrderProcessor(order);
        processor.start();
    }
}
