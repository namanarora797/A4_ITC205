package library.orderitem;
import java.util.Scanner;


public class OrderItemUI {
	
	public static enum UiState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

	private UiState state;
	private OrderItemControl control;
	private Scanner scanner;

	
	public OrderItemUI(OrderItemControl control) {
		this.control = control;
		scanner = new Scanner(System.in);
		state = UiState.INITIALISED;
		control.setUI(this);
	}

	
	private String getInput(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}	
		
		
	private void displayOutput(Object object) {
		System.out.println(object);
	}
	
				
	public void run() {
		displayOutput("Order Item Use Case UI\n");
		
		while (true) {
			
			switch (state) {			
			
    			case CANCELLED:
    				displayOutput("Ordering Cancelled");
    				return;
    				
    			case READY:
    				String publisherIdStr = getInput("Swipe publisher card (press <enter> to cancel): ");
    				if (publisherIdStr.length() == 0) {
    					control.cancel();
    					break;
    				}
    				try {
    					long publisherId = Long.valueOf(publisherIdStr).longValue();
    					control.cardSwiped(publisherId);
    				}
    				catch (NumberFormatException e) {
    					displayOutput("Invalid Publisher Id");
    				}
    				break;
    				
    			case RESTRICTED:
    				getInput("Press <any key> to cancel");
    				control.cancel();
    				break;			
    				
    			case SCANNING:
    				String itemIdStr = getInput("Scan Item (<enter> completes): ");
    				String numberOfItemsStr = getInput("Enter number of items (press <enter> to cancel): ");
    				if (itemIdStr.length() == 0 || numberOfItemsStr.length() == 0) {
    					control.orderingCompleted();
    					break;
    				}
    				try {
    					int itemId = Integer.valueOf(itemIdStr).intValue();
        				long numberOfItems = Long.valueOf(numberOfItemsStr).longValue();
    					control.itemScanned(itemId, numberOfItems);
    					
    				} catch (NumberFormatException e) {
    					displayOutput("Invalid Item Id");
    				} 
    				break;					
    				
    			case FINALISING:
    				String answer = getInput("Commit orders? (Y/N): ");
    				if (answer.toUpperCase().equals("N")) {
    					control.cancel();					
    				} 
    				else {
    					control.commitOrders();
    					getInput("Press <any key> to complete ");
    				}
    				break;
    								
    			case COMPLETED:
    				displayOutput("Ordering Completed");
    				return;	
    				
    			default:
    				displayOutput("Unhandled state");
    				throw new RuntimeException("OrderItemUI : unhandled state :" + state);			
			}
		}		
	}


	public void display(Object object) {
		displayOutput(object);		
	}


	public void setReady() {
		state = UiState.READY;		
	}


	public void setScanning() {
		state = UiState.SCANNING;		
	}


	public void setRestricted() {
		state = UiState.RESTRICTED;		
	}

	public void setFinalising() {
		state = UiState.FINALISING;		
	}


	public void setCompleted() {
		state = UiState.COMPLETED;		
	}

	public void setCancelled() {
		state = UiState.CANCELLED;		
	}

}
