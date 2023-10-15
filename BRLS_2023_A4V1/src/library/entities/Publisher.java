package library.entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Publisher implements Serializable {

	private long publisherId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private long phoneNo;
	private double paymentsOwing;
	
	private Map<Long, Order> currentOrders;

	
	public Publisher(String firstName, String lastName, String emailAddress, long phoneNo, long publisherId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.phoneNo = phoneNo;
		this.publisherId = publisherId;
		
		this.currentOrders = new HashMap<>();
	}

	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Publisher:  ").append(publisherId).append("\n")
		  .append("  Name:  ").append(firstName).append(" ").append(lastName).append("\n")
		  .append("  Email: ").append(emailAddress).append("\n")
		  .append("  Phone: ").append(phoneNo)
		  .append("\n")
          .append(String.format("  Payments Owed :  $%.2f", paymentsOwing))
		  .append("\n");
		
		for (Order order : currentOrders.values()) {
			sb.append(order).append("\n");
		}		  
		return sb.toString();
	}

	
	public Long getId() {
		return publisherId;
	}

	
	public List<Order> getOrders() {
		return new ArrayList<Order>(currentOrders.values());
	}

	
	public int getNumberOfCurrentOrders() {
		return currentOrders.size();
	}

	
	public double paymentsOwed() {
		return paymentsOwing;
	}

	
	public void takeOutOrder(Order order) {
		if (!currentOrders.containsKey(order.getId())) {
			currentOrders.put(order.getId(), order);
		}
		else {
			throw new RuntimeException("Duplicate order added to publisher");
		}
	}

	
	public void dischargeOrder(Order order) {
	    long orderId = order.getId();
		if (currentOrders.containsKey(orderId)) {
		    paymentsOwing += order.getPayments();
			currentOrders.remove(orderId);
		}
		else {
			throw new RuntimeException("No such order held by publisher");
		}
	}

	
	public String getLastName() {
		return lastName;
	}

	
	public String getFirstName() {
		return firstName;
	}


	public void incurPayment(double payment) {
		paymentsOwing += payment;
	}
	
	
	public double makePayment(double amount) {
		if (amount < 0) {
			throw new RuntimeException("Publisher.makePayment: amount must be positive");
		}
		double change = 0;
		if (amount > paymentsOwing) {
			change = amount - paymentsOwing;
			paymentsOwing = 0;
		}
		else {
			paymentsOwing += amount;
		}
		return change;
	}

}
