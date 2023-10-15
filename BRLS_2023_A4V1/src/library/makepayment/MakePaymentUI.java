package library.makepayment;
import java.util.Scanner;


public class MakePaymentUI {


	private enum UiState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };

	private MakePaymentControl control;
	private Scanner scanner;
	private UiState state;

	
	public MakePaymentUI(MakePaymentControl control) {
		this.control = control;
		scanner = new Scanner(System.in);
		state = UiState.INITIALISED;
		control.setUi(this);
	}
	
	
	public void run() {
		displayOutput("Make Payment Use Case UI\n");
		
		while (true) {
			
			switch (state) {
			
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
    					displayOutput("Invalid publisherID");
    				}
    				break;
    				
    			case PAYING:
    				double amount = 0;
    				String amountStr = getInput("Enter amount (<Enter> cancels) : ");
    				if (amountStr.length() == 0) {
    					control.cancel();
    					break;
    				}
    				try {
    					amount = Double.valueOf(amountStr).doubleValue();
    				}
    				catch (NumberFormatException e) {}
    				if (amount <= 0) {
    					displayOutput("Amount must be positive");
    					break;
    				}
    				control.makePayment(amount);
    				break;
    								
    			case CANCELLED:
    				displayOutput("Make Payment process cancelled");
    				return;
    			
    			case COMPLETED:
    				displayOutput("Make Payment process complete");
    				return;
    			
    			default:
    				displayOutput("Unhandled state");
    				throw new RuntimeException("FixBookUI : unhandled state :" + state);			  			
			}		
		}		
	}

	
	private String getInput(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}	
		
		
	private void displayOutput(Object object) {
		System.out.println(object);
	}	
			

	public void display(Object object) {
		displayOutput(object);
	}


	public void setCompleted() {
		state = UiState.COMPLETED;		
	}


	public void setPaying() {
		state = UiState.PAYING;		
	}


	public void setCancelled() {
		state = UiState.CANCELLED;		
	}


	public void setReady() {
		state = UiState.READY;		
	}


}
