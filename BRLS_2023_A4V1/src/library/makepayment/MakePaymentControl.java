package library.makepayment;
import library.entities.Library;
import library.entities.Publisher;

public class MakePaymentControl {
	
	private MakePaymentUI ui;
	private enum ControlState { INITIALISED, READY, PAYING, COMPLETED, CANCELLED };
	private ControlState state;
	
	private Library library;
	private Publisher publisher;


	public MakePaymentControl() {
		this.library = Library.getInstance();
		state = ControlState.INITIALISED;
	}
	
	
	public void setUi(MakePaymentUI ui) {
		if (!state.equals(ControlState.INITIALISED)) {
			throw new RuntimeException("MakePaymentControl: cannot call setUI except in INITIALISED state");
		}	
		this.ui = ui;
		ui.setReady();
		state = ControlState.READY;		
	}


	public void cardSwiped(long publisherId) {
		if (!state.equals(ControlState.READY)) {
			throw new RuntimeException("MakePaymentControl: cannot call cardSwiped except in READY state");
		}
		publisher = library.getPublisher(publisherId);
		
		if (publisher == null) {
			ui.display("Invalid Publisher Id");
			return;
		}
		ui.display(publisher);
		ui.setPaying();
		state = ControlState.PAYING;
	}
	
	
	public double makePayment(double amount) {
		if (!state.equals(ControlState.PAYING)) 
			throw new RuntimeException("MakePaymentControl: cannot call makePayment except in PAYING state");
			
		double change = publisher.makePayment(amount);
		if (change > 0) 
			ui.display(String.format("All Paid! Change: $%.2f", change));
		
		ui.display(publisher);
		ui.setCompleted();
		state = ControlState.COMPLETED;
		return change;
	}
	
	public void cancel() {
		ui.setCancelled();
		state = ControlState.CANCELLED;
	}




}
