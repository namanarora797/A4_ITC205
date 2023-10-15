package library.entities;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class Order implements Serializable {
	
	private enum OrderState { CURRENT, OVER_DUE, DISCHARGED };
	
	private long orderId;
	private Item item;
	private Publisher publisher;
	private Date dueDate;
	private double payments;
	private OrderState state;

	
	public Order(long orderId, Item item, Publisher publisher, Date dueDate) {
		this.orderId = orderId;
		this.item = item;
		this.publisher = publisher;
		this.dueDate = dueDate;
		this.state = OrderState.CURRENT;
	}

	
    public void discharge(boolean isDamaged) {
        publisher.dischargeOrder(this);
        item.takeBack(isDamaged);
        state = OrderState.DISCHARGED;       
    }


	public void updateStatus(double overDueFeePerDay) {
		if (state == OrderState.CURRENT &&
			Calendar.getInstance().getDate().after(dueDate)) {
			this.state = OrderState.OVER_DUE;
		}
		if (isOverDue()) {
            long daysOverDue = Calendar.getInstance().getDaysDifference(dueDate);
            payments = overDueFeePerDay * daysOverDue;
            publisher.incurPayment(payments);
		}
	}

	
	public boolean isOverDue() {
		return state == OrderState.OVER_DUE;
	}

	
	public Long getId() {
		return orderId;
	}


	public Date getDueDate() {
		return dueDate;
	}
	
	
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		StringBuilder sb = new StringBuilder();
		sb.append("Order:  ").append(orderId).append("\n")
		  .append("  Publisher ").append(publisher.getId()).append(" : ")
		  .append(publisher.getFirstName()).append(" ").append(publisher.getLastName()).append("\n")
		  .append("  Item ").append(item.getId()).append(" : " )
		  .append(item.getType()).append("\n")
		  .append(item.getTitle()).append("\n")
		  .append("  DueDate: ").append(sdf.format(dueDate)).append("\n")
		  .append("  State: ").append(state).append("\n")
		  .append("  Overdue Payments: ").append(String.format("$%.2f", payments));	
		
		return sb.toString();
	}


	public Publisher getPublisher() {
		return publisher;
	}


	public Item getItem() {
		return item;
	}


    public double getPayments() {
         return payments;
    }

}
