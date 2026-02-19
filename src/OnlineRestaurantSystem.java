import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;

// ================= ABSTRACT CLASS =================
abstract class FoodProduct implements Serializable {
    private String id;
    private String name;
    private String category;
    private double price;

    public FoodProduct(String id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }

    public void setPrice(double price) { this.price = price; }
}

// ================= FOOD TYPES =================
class VegItem extends FoodProduct { public VegItem(String id, String name, double price) { super(id, name, price, "Veg"); } }
class NonVegItem extends FoodProduct { public NonVegItem(String id, String name, double price) { super(id, name, price, "NonVeg"); } }
class JuiceItem extends FoodProduct { public JuiceItem(String id, String name, double price) { super(id, name, price, "Juice"); } }
class DessertItem extends FoodProduct { public DessertItem(String id, String name, double price) { super(id, name, price, "Dessert"); } }

// ================= CUSTOMER =================
class Customer implements Serializable {
    private String name;
    private String phone;
    public Customer(String name, String phone) { this.name = name; this.phone = phone; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}

// ================= CART =================
class Cart implements Serializable {
    private Map<FoodProduct, Integer> items = new LinkedHashMap<>();

    public void addItem(FoodProduct p, int qty) { items.put(p, items.getOrDefault(p,0)+qty); }

    public void removeItem(String name) {
        for(FoodProduct p : new ArrayList<>(items.keySet())) {
            if(p.getName().equalsIgnoreCase(name)) { items.remove(p); JOptionPane.showMessageDialog(null,name+" removed!"); return; }
        }
        JOptionPane.showMessageDialog(null,"Item not found!");
    }

    public void updateQuantity(String name, int qty) {
        for(FoodProduct p : items.keySet()) {
            if(p.getName().equalsIgnoreCase(name)) {
                if(qty<=0) items.remove(p); else items.put(p, qty);
                JOptionPane.showMessageDialog(null,"Quantity updated!");
                return;
            }
        }
        JOptionPane.showMessageDialog(null,"Item not found!");
    }

    public Map<FoodProduct,Integer> getItems() { return items; }
    public void clearCart() { items.clear(); }
    public boolean isEmpty() { return items.isEmpty(); }

    public double calculateSubtotal() {
        double sum=0;
        for(Map.Entry<FoodProduct,Integer> e: items.entrySet()) sum+=e.getKey().getPrice()*e.getValue();
        return sum;
    }
}

// ================= ORDER PROCESSOR =================
class OrderProcessor extends Thread {
    private int orderId;
    public OrderProcessor(int orderId) { this.orderId=orderId; }
    public void run() {
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        JOptionPane.showMessageDialog(null,"Order #"+orderId+" Delivered Successfully!");
    }
}

// ================= MAIN SYSTEM =================
public class OnlineRestaurantSystem {
    private Cart cart = new Cart();
    private java.util.List<FoodProduct> products = new ArrayList<>();
    private java.util.List<String> orderHistory = new ArrayList<>();
    private int orderCounter = 1;

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new OnlineRestaurantSystem().createGUI()); }

    public OnlineRestaurantSystem() { initializeProducts(); }

    private void createGUI() {
        JFrame frame = new JFrame("Online Restaurant System");
        frame.setSize(700,700);
        frame.setLayout(new GridLayout(10,1,10,10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton vegBtn = new JButton("Veg Items");
        JButton nonVegBtn = new JButton("Non-Veg Items");
        JButton juiceBtn = new JButton("Juice Items");
        JButton dessertBtn = new JButton("Dessert Items");
        JButton viewCartBtn = new JButton("View Cart");
        JButton removeBtn = new JButton("Remove Item");
        JButton updateQtyBtn = new JButton("Update Quantity");
        JButton checkoutBtn = new JButton("Checkout");
        JButton searchBtn = new JButton("Search Item");
        JButton adminBtn = new JButton("Admin Panel");

        vegBtn.addActionListener(e -> displayProducts("Veg"));
        nonVegBtn.addActionListener(e -> displayProducts("NonVeg"));
        juiceBtn.addActionListener(e -> displayProducts("Juice"));
        dessertBtn.addActionListener(e -> displayProducts("Dessert"));
        viewCartBtn.addActionListener(e -> displayCart());
        removeBtn.addActionListener(e -> removeFromCart());
        updateQtyBtn.addActionListener(e -> updateQuantity());
        checkoutBtn.addActionListener(e -> checkout());
        searchBtn.addActionListener(e -> searchItem());
        adminBtn.addActionListener(e -> adminPanel());

        frame.add(vegBtn); frame.add(nonVegBtn); frame.add(juiceBtn); frame.add(dessertBtn);
        frame.add(viewCartBtn); frame.add(removeBtn); frame.add(updateQtyBtn); frame.add(checkoutBtn);
        frame.add(searchBtn); frame.add(adminBtn);

        frame.setVisible(true);
    }

    private void initializeProducts() {
        // ================= VEG =================
        products.add(new VegItem("V1","Paneer Curry",350));
        products.add(new VegItem("V2","Veg Biryani",300));
        products.add(new VegItem("V3","Dal Fry",250));
        products.add(new VegItem("V4","Mix Veg",280));
        products.add(new VegItem("V5","Mushroom Curry",320));
        products.add(new VegItem("V6","Aloo Paratha",150));
        products.add(new VegItem("V7","Veg Chowmein",200));
        products.add(new VegItem("V8","Chana Masala",220));
        products.add(new VegItem("V9","Paneer Tikka",400));
        products.add(new VegItem("V10","Veg Momos",180));

        // ================= NON VEG =================
        products.add(new NonVegItem("N1","Chicken Curry",500));
        products.add(new NonVegItem("N2","Chicken Biryani",450));
        products.add(new NonVegItem("N3","Mutton Curry",700));
        products.add(new NonVegItem("N4","Fish Fry",600));
        products.add(new NonVegItem("N5","Chicken Chowmein",350));
        products.add(new NonVegItem("N6","Buff Sekuwa",550));
        products.add(new NonVegItem("N7","Prawn Curry",650));
        products.add(new NonVegItem("N8","Egg Curry",300));
        products.add(new NonVegItem("N9","Chicken Lollipop",400));
        products.add(new NonVegItem("N10","Grilled Fish",550));

        // ================= JUICE =================
        products.add(new JuiceItem("J1","Orange Juice",200));
        products.add(new JuiceItem("J2","Mango Juice",220));
        products.add(new JuiceItem("J3","Apple Juice",210));
        products.add(new JuiceItem("J4","Pineapple Juice",230));
        products.add(new JuiceItem("J5","Strawberry Shake",250));
        products.add(new JuiceItem("J6","Banana Shake",240));
        products.add(new JuiceItem("J7","Watermelon Juice",220));
        products.add(new JuiceItem("J8","Lassi",200));
        products.add(new JuiceItem("J9","Papaya Juice",210));
        products.add(new JuiceItem("J10","Mixed Fruit Juice",250));

        // ================= DESSERT =================
        products.add(new DessertItem("D1","Ice Cream",150));
        products.add(new DessertItem("D2","Chocolate Cake",250));
        products.add(new DessertItem("D3","Brownie",180));
        products.add(new DessertItem("D4","Gulab Jamun",120));
        products.add(new DessertItem("D5","Cheese Cake",300));
        products.add(new DessertItem("D6","Jalebi",100));
        products.add(new DessertItem("D7","Rasmalai",200));
        products.add(new DessertItem("D8","Cupcake",150));
        products.add(new DessertItem("D9","Muffin",180));
        products.add(new DessertItem("D10","Fruit Custard",200));
    }

    private void displayProducts(String category) {
        StringBuilder sb = new StringBuilder(category + " Items:\n");
        for(FoodProduct p: products) if(p.getCategory().equalsIgnoreCase(category))
            sb.append(p.getId()).append(" | ").append(p.getName()).append(" | Rs ").append(p.getPrice()).append("\n");

        String id = JOptionPane.showInputDialog(sb + "\nEnter Product ID (leave empty to cancel):");
        if(id==null || id.trim().isEmpty()) return;

        FoodProduct selected = null;
        for(FoodProduct p: products) if(p.getId().equalsIgnoreCase(id)) { selected=p; break; }
        if(selected==null) { JOptionPane.showMessageDialog(null,"Invalid Product ID!"); return; }

        try {
            int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter Quantity:"));
            if(qty<=0) throw new Exception();
            cart.addItem(selected, qty);
            JOptionPane.showMessageDialog(null,"Added to cart!");
        } catch(Exception e) { JOptionPane.showMessageDialog(null,"Invalid quantity!"); }
    }

    private void displayCart() {
        if(cart.isEmpty()) { JOptionPane.showMessageDialog(null,"Cart Empty!"); return; }
        String[] cols = {"Item","Qty","Unit Price","Total"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        for(Map.Entry<FoodProduct,Integer> e: cart.getItems().entrySet())
            model.addRow(new Object[]{e.getKey().getName(),e.getValue(),e.getKey().getPrice(),e.getValue()*e.getKey().getPrice()});

        JTable table = new JTable(model);
        table.setEnabled(false); table.setRowHeight(25);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(right);
        table.getColumnModel().getColumn(3).setCellRenderer(right);

        JOptionPane.showMessageDialog(null,new JScrollPane(table),"Cart",JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeFromCart() {
        if(cart.isEmpty()) { JOptionPane.showMessageDialog(null,"Cart Empty!"); return; }
        String item = JOptionPane.showInputDialog("Enter item name to remove:");
        if(item!=null) cart.removeItem(item.trim());
    }

    private void updateQuantity() {
        if(cart.isEmpty()) { JOptionPane.showMessageDialog(null,"Cart Empty!"); return; }
        String item = JOptionPane.showInputDialog("Enter item name to update:");
        if(item!=null) {
            try {
                int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter new quantity:"));
                cart.updateQuantity(item.trim(),qty);
            } catch(Exception e) { JOptionPane.showMessageDialog(null,"Invalid quantity!"); }
        }
    }

    private void searchItem() {
        String keyword = JOptionPane.showInputDialog("Enter item name to search:");
        if(keyword==null || keyword.trim().isEmpty()) return;
        StringBuilder sb = new StringBuilder("Search Results:\n");
        boolean found=false;
        for(FoodProduct p: products)
            if(p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                sb.append(p.getId()).append(" | ").append(p.getName()).append(" | Rs ").append(p.getPrice()).append("\n");
                found=true;
            }
        if(!found) sb.append("No items found!");
        JOptionPane.showMessageDialog(null,sb.toString());
    }

    private void checkout() {
        if(cart.isEmpty()) { JOptionPane.showMessageDialog(null,"Cart Empty!"); return; }

        String name;
        while(true) { name = JOptionPane.showInputDialog("Full Name:"); if(name!=null && !name.trim().isEmpty()) break; JOptionPane.showMessageDialog(null,"Name required!"); }
        String phone;
        while(true) { phone = JOptionPane.showInputDialog("Enter 10-digit phone (98/97):"); if(phone!=null && phone.matches("^(98|97)\\d{8}$")) break; JOptionPane.showMessageDialog(null,"Invalid phone!"); }

        Customer customer = new Customer(name,phone);
        double subtotal = cart.calculateSubtotal();
        double delivery = subtotal>1500?0:100;
        double total = subtotal+delivery;

        // ==== Bill Table ====
        String[] cols = {"Item","Qty","Unit Price","Total"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        for(Map.Entry<FoodProduct,Integer> e: cart.getItems().entrySet())
            model.addRow(new Object[]{e.getKey().getName(),e.getValue(),e.getKey().getPrice(),e.getValue()*e.getKey().getPrice()});
        model.addRow(new Object[]{"","","Subtotal",subtotal});
        model.addRow(new Object[]{"","","Delivery",delivery});
        model.addRow(new Object[]{"","","Total",total});

        JTable table = new JTable(model);
        table.setEnabled(false); table.setRowHeight(25);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(right);
        table.getColumnModel().getColumn(3).setCellRenderer(right);

        JOptionPane.showMessageDialog(null,new JScrollPane(table),"Final Bill",JOptionPane.INFORMATION_MESSAGE);

        // Save to file
        try(PrintWriter pw = new PrintWriter(new FileWriter("receipt.txt",true))){
            pw.println("Customer: "+customer.getName()+" | Phone: "+customer.getPhone());
            for(Map.Entry<FoodProduct,Integer> e: cart.getItems().entrySet())
                pw.println(e.getKey().getName()+" x"+e.getValue()+" = Rs "+(e.getKey().getPrice()*e.getValue()));
            pw.println("Subtotal: Rs "+subtotal);
            pw.println("Delivery: Rs "+delivery);
            pw.println("Total: Rs "+total);
            pw.println("--------------------------------------");
        } catch(Exception ex){ ex.printStackTrace(); }

        orderHistory.add("Order#"+orderCounter+": Rs "+total);
        new OrderProcessor(orderCounter++).start();
        cart.clearCart();
    }

    private void adminPanel() {
        String pass = JOptionPane.showInputDialog("Enter admin password:");
        if(!"admin123".equals(pass)) { JOptionPane.showMessageDialog(null,"Access Denied!"); return; }

        String[] options = {"Add Item","Remove Item","Change Price","View Orders","Cancel"};
        int choice = JOptionPane.showOptionDialog(null,"Admin Panel","Admin",0,JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
        if(choice==0) addNewItem();
        else if(choice==1) removeItemAdmin();
        else if(choice==2) changePriceAdmin();
        else if(choice==3) viewOrders();
    }

    private void addNewItem() {
        String cat = JOptionPane.showInputDialog("Enter Category (Veg/NonVeg/Juice/Dessert):");
        String id = JOptionPane.showInputDialog("Enter ID:");
        String name = JOptionPane.showInputDialog("Enter Name:");
        double price = Double.parseDouble(JOptionPane.showInputDialog("Enter Price:"));

        FoodProduct p=null;
        switch(cat.toLowerCase()) {
            case "veg": p=new VegItem(id,name,price); break;
            case "nonveg": p=new NonVegItem(id,name,price); break;
            case "juice": p=new JuiceItem(id,name,price); break;
            case "dessert": p=new DessertItem(id,name,price); break;
            default: JOptionPane.showMessageDialog(null,"Invalid Category!"); return;
        }
        products.add(p);
        JOptionPane.showMessageDialog(null,"Item Added!");
    }

    private void removeItemAdmin() {
        String id = JOptionPane.showInputDialog("Enter Product ID to remove:");
        for(FoodProduct p: new ArrayList<>(products)) {
            if(p.getId().equalsIgnoreCase(id)) { products.remove(p); JOptionPane.showMessageDialog(null,"Item Removed!"); return; }
        }
        JOptionPane.showMessageDialog(null,"Item not found!");
    }

    private void changePriceAdmin() {
        String id = JOptionPane.showInputDialog("Enter Product ID to change price:");
        for(FoodProduct p: products) {
            if(p.getId().equalsIgnoreCase(id)) {
                double price = Double.parseDouble(JOptionPane.showInputDialog("Enter new price:"));
                p.setPrice(price);
                JOptionPane.showMessageDialog(null,"Price Updated!");
                return;
            }
        }
        JOptionPane.showMessageDialog(null,"Item not found!");
    }

    private void viewOrders() {
        if(orderHistory.isEmpty()) { JOptionPane.showMessageDialog(null,"No orders yet."); return; }
        StringBuilder sb = new StringBuilder("Order History:\n");
        for(String o: orderHistory) sb.append(o).append("\n");
        JOptionPane.showMessageDialog(null,sb.toString());
    }
}