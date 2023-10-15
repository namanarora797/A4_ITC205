package library.entities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Library implements Serializable {
	
	private static final String LIBRARY_FILE = "library.obj";
	private static final int ORDER_LIMIT = 100;
	private static final int ORDER_PERIOD = 14;
	private static final double OVERDUE_PAYMENT_PER_DAY = 1.0;
	private static final double MAX_PAYMENTS_OWED = 1.0;
	private static final double DAMAGE_FEE = 2.0;
	
	private static Library self;
	private long nextItemId;
	private long nextPublisherId;
	private long nextOrderId;
	private Date currentDate;
	
	private Map<Long, Item> catalog;
	private Map<Long, Publisher> publishers;
	private Map<Long, Order> orders;
	private Map<Long, Order> currentOrders;
	private Map<Long, Item> damagedItems;
	

	private Library() {
		catalog = new HashMap<>();
		publishers = new HashMap<>();
		orders = new HashMap<>();
		currentOrders = new HashMap<>();
		damagedItems = new HashMap<>();
		nextItemId = 1;
		nextPublisherId = 1;		
		nextOrderId = 1;		
	}

	
	public static synchronized Library getInstance() {		
		if (self == null) {
			Path path = Paths.get(LIBRARY_FILE);			
			if (Files.exists(path)) {	
				try (ObjectInputStream libraryFile = new ObjectInputStream(new FileInputStream(LIBRARY_FILE));) {
			    
					self = (Library) libraryFile.readObject();
					Calendar.getInstance().setDate(self.currentDate);
					libraryFile.close();
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else {
			    self = new Library();
			}
		}
		return self;
	}

	
	public static synchronized void save() {
		if (self != null) {
			self.currentDate = Calendar.getInstance().getDate();
			try (ObjectOutputStream libraryFile = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE));) {
				libraryFile.writeObject(self);
				libraryFile.flush();
				libraryFile.close();	
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	private long getNextItemId() {
		return nextItemId++;
	}

	
	private long getNextPublisherId() {
		return nextPublisherId++;
	}

	
	private long getNextOrderId() {
		return nextOrderId++;
	}

	
	public List<Publisher> listPublishers() {		
		return new ArrayList<Publisher>(publishers.values()); 
	}


	public List<Item> listItems() {		
		return new ArrayList<Item>(catalog.values()); 
	}


	public List<Order> listCurrentOrders() {
		return new ArrayList<Order>(currentOrders.values());
	}


	public Publisher addPublisher(String firstName, String lastName, String email, long phoneNo) {		
		Publisher publisher = new Publisher(firstName, lastName, email, phoneNo, getNextPublisherId());
		publishers.put(publisher.getId(), publisher);		
		return publisher;
	}

	
	public Item addItem(String author, String title, String callNo, ItemType itemType, double price) {		
		Item item = new Item(author, title, callNo, itemType, getNextItemId(), price);
		catalog.put(item.getId(), item);		
		return item;
	}

	
	public Publisher getPublisher(long publisherId) {
		if (publishers.containsKey(publisherId)) {
			return publishers.get(publisherId);
		}
		return null;
	}

	
	public Item getItem(long itemId) {
		if (catalog.containsKey(itemId)) {
			return catalog.get(itemId);
		}
		return null;
	}

	
	public int getOrderLimit() {
		return ORDER_LIMIT;
	}

	
	public boolean canPublisherOrder(Publisher publisher) {		
		if (publisher.getNumberOfCurrentOrders() == ORDER_LIMIT ) {
			return false;
		}
		if (publisher.paymentsOwed() >= MAX_PAYMENTS_OWED) {
			return false;
		}
		for (Order order : publisher.getOrders()) {
			if (order.isOverDue()) {
				return false;
			}
		}	
		return true;
	}

	
	public int getNumberOfOrdersRemainingForPublisher(Publisher publisher) {		
		return ORDER_LIMIT - publisher.getNumberOfCurrentOrders();
	}

	
	public Order issueOrder(Item item, Publisher publisher) {
		Date dueDate = Calendar.getInstance().getDueDate(ORDER_PERIOD);
		Order order = new Order(getNextOrderId(), item, publisher, dueDate);
		publisher.takeOutOrder(order);
		//item.takeOut();
		orders.put(order.getId(), order);
		currentOrders.put(item.getId(), order);
		return order;
	}
	
	
	public Order getOrderByItemId(long itemId) {
		if (currentOrders.containsKey(itemId)) {
			return currentOrders.get(itemId);
		}
		return null;
	}

	
	public double calculateOverDuePayment(Order order) {
		if (order.isOverDue()) {
		    Date dueDate = order.getDueDate();
			long daysOverDue = Calendar.getInstance().getDaysDifference(dueDate);
			double payment = daysOverDue * OVERDUE_PAYMENT_PER_DAY;
			return payment;
		}
		return 0.0;		
	}

	
	public double calculateDamageFee(boolean isDamaged) {
	    if (isDamaged) {
	        return DAMAGE_FEE;
	    }
	    return 0.0;
	}


	public void dischargeOrder(Order currentOrder, boolean isDamaged) {
		Publisher publisher = currentOrder.getPublisher();
		Item item  = currentOrder.getItem();
		long itemId = item.getId();

		if (currentOrder.isOverDue()) {
	        double overDuePayment = currentOrder.getPayments();      
	        publisher.incurPayment(overDuePayment); 		    
		}
		if (isDamaged) {
		    publisher.incurPayment(DAMAGE_FEE);
            damagedItems.put(itemId, item);
		}
		currentOrder.discharge(isDamaged);
		currentOrders.remove(itemId);
	}


	public void updateCurrentOrderStatus() {
		for (Order order : currentOrders.values()) {
			order.updateStatus(OVERDUE_PAYMENT_PER_DAY);
		}
	}


	public void repairItem(Item currentItem) {
	    long itemId = currentItem.getId();
		if (damagedItems.containsKey(itemId)) {
			currentItem.repair();
			damagedItems.remove(itemId);
		}
		else {
			throw new RuntimeException("Library: repairItem: item is not damaged");
		}
	}
	
	
}
