package library.orderitem;
import java.util.ArrayList;
import java.util.List;

import library.entities.Item;
import library.entities.Library;
import library.entities.Order;
import library.entities.Publisher;

public class OrderItemControl {
	
	private OrderItemUI ui;
	
	private Library library;
	private Publisher publisher;
	private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private ControlState state;
	
	private List<Item> pendingList;
	private List<Order> completedList;
	private Item item;
	private Order currentOrder;
	
	public OrderItemControl() {
		this.library = Library.getInstance();
		state = ControlState.INITIALISED;
	}
	

	public void setUI(OrderItemUI ui) {
		if (!state.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("OrderItemControl: cannot call setUI except in INITIALISED state");
		}
		this.ui = ui;
		ui.setReady();
		state = ControlState.READY;		
	}

		
	public void cardSwiped(long publisherId) {
		if (!state.equals(ControlState.READY)) {
			throw new RuntimeException("OrderItemControl: cannot call cardSwiped except in READY state");
		}
		publisher = library.getPublisher(publisherId);
		if (publisher == null) {
			ui.display("Invalid publisherId");
			return;
		}
		if (library.canPublisherOrder(publisher)) {
			pendingList = new ArrayList<>();
			ui.setScanning();
			state = ControlState.SCANNING; 
		}
		else {
			ui.display("Cannot order at this time");
			ui.setRestricted(); 
		}
	}
	
	
	public void itemScanned(int itemId, long numberOfItemsOrdered) {
		item = null;

		if (!state.equals(ControlState.SCANNING)) {
			throw new RuntimeException("OrderItemControl: cannot call itemScanned except in SCANNING state");
		}
		item = library.getItem(itemId);
		if (item == null) {
			ui.display("Invalid itemId");
			return;
		}
		if (!item.isAvailable()) {
			ui.display("Item cannot be ordered");
			return;
		}
		currentOrder = library.issueOrder(item, publisher);
		
		calculatePayment(item, numberOfItemsOrdered);
		pendingList.add(item);
		for (Item item : pendingList) {
			ui.display(item);
		}
		if (library.getNumberOfOrdersRemainingForPublisher(publisher) - pendingList.size() < 0) {
			ui.display("Order limit reached");
			orderingCompleted();
		}
	}
	
	
	public void orderingCompleted() {
		if (pendingList.size() == 0) 
			cancel();
		
		else {
			ui.display("\nFinal Ordering List");
			for (Item item : pendingList) {
				ui.display(item);
			}
			completedList = new ArrayList<Order>();
			ui.setFinalising();
			state = ControlState.FINALISING;
		}
	}


	public void commitOrders() {
		if (!state.equals(ControlState.FINALISING)) {
			throw new RuntimeException("OrderItemControl: cannot call commitOrders except in FINALISING state");
		}
		for (Item item : pendingList) {
			Order order = library.issueOrder(item, publisher);
			completedList.add(order);			
		}
		ui.display("Completed Order Slip");
		for (Order order : completedList) 
			ui.display(order);
		
		ui.setCompleted();
		state = ControlState.COMPLETED;
	}

	
	public void cancel() {
		ui.setCancelled();
		state = ControlState.CANCELLED;
	}

	public void calculatePayment(Item item, long numberOfItemsOrdered) {
		//if (!state.equals(ControlState.FINALISING)) {
			//throw new RuntimeException("OrderItemControl: cannot call calculatePayment except in FINALISING state");
		//}
		double price = item.getPrice();
		double totalPayments = 0.0;
        long numberOfItems = numberOfItemsOrdered;

        totalPayments = price * numberOfItems;
        
        currentOrder.getPublisher().incurPayment(totalPayments);

		
		currentOrder = null;
		ui.setReady();
		state = ControlState.READY;				
	}
	
	
}
