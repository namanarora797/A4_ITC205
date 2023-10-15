package library.fixitem;
import java.util.Scanner;


public class FixItemUI {

	private enum UiState { INITIALISED, READY, INSPECTING, COMPLETED };

	private FixItemControl control;
	private Scanner scanner;
	private UiState state;

	
	public FixItemUI(FixItemControl control) {
		this.control = control;
		scanner = new Scanner(System.in);
		state = UiState.INITIALISED;
		control.setUi(this);
	}


	public void run() {
		displayOutput("Fix Item Use Case UI\n");
		
		while (true) {
			
			switch (state) {
			
    			case READY:
    				String itemStr = getInput("Scan Item (<enter> completes): ");
    				if (itemStr.length() == 0) {
    					control.processingCompleted();
    				}
    				else {
    					try {
    						long itemId = Long.valueOf(itemStr).longValue();
    						control.itemScanned(itemId);
    					}
    					catch (NumberFormatException e) {
    						displayOutput("Invalid itemId");
    					}
    				}
    				break;	
    				
    			case INSPECTING:
    				String answer = getInput("Fix Item? (Y/N) : ");
    				boolean isDamaged = false;
    				if (answer.toUpperCase().equals("Y")) {
    					isDamaged = true;
    				}
    				control.itemInspected(isDamaged);
    				break;
    								
    			case COMPLETED:
    				displayOutput("Fixing process complete");
    				return;
    			
    			default:
    				displayOutput("Unhandled state");
    				throw new RuntimeException("FixItemUI : unhandled state :" + state);					
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


	public void setInspecting() {
		this.state = UiState.INSPECTING;		
	}


	public void setReady() {
		this.state = UiState.READY;		
	}


	public void setCompleted() {
		this.state = UiState.COMPLETED;		
	}
	
	
}
