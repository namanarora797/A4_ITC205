package library.entities;
import java.io.Serializable;


@SuppressWarnings("serial")
public class Item implements Serializable {
	
	private ItemType type;
	private String author;
	private String title;
	private String callNo;
	private double price;
	private long id;
	
	private enum ItemState { AVAILABLE, ON_ORDER, DAMAGED, RESERVED };
	private ItemState state;
	
	
	public Item(String author, String title, String callNo, ItemType itemType, long id, double price) {
		this.type = itemType;
		this.author = author;
		this.title = title;
		this.callNo = callNo;
		this.id = id;
		this.price = price;
		this.state = ItemState.AVAILABLE;
	}

	
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Item: ").append(id).append("\n")
		  .append("  Type:   ").append(type).append("\n")
		  .append("  Title:  ").append(title).append("\n")
		  .append("  Author: ").append(author).append("\n")
		  .append("  CallNo: ").append(callNo).append("\n")
		  .append("  Price: ").append(price).append("\n")
		  .append("  State:  ").append(state);
		
		return stringBuilder.toString();
	}

	
	public Long getId() {
		return id;
	}

	public double getPrice() {
		return price;
	}

	public String getTitle() {
		return title;
	}

	
	public ItemType getType() {
		return type;
	}

	
	public boolean isAvailable() {
		return state == ItemState.AVAILABLE;
	}

	
	public boolean isOnOrder() {
		return state == ItemState.ON_ORDER;
	}

	
	public boolean isDamaged() {
		return state == ItemState.DAMAGED;
	}

	
	public void takeOut() {
		if (state.equals(ItemState.AVAILABLE)) {
			state = ItemState.ON_ORDER;
		}
		else {
			throw new RuntimeException(String.format("Item: cannot borrow item while item is in state: %s", state));
		}		
	}


	public void takeBack(boolean damaged) {
		if (state.equals(ItemState.ON_ORDER)) {
			if (damaged) {
				state = ItemState.DAMAGED;			
			}
			else {
				state = ItemState.AVAILABLE;		
			}
		}
		else {
			throw new RuntimeException(String.format("Item: cannot return item while item is in state: %s", state));
		}
	}

	
	public void repair() {
		if (state.equals(ItemState.DAMAGED)) {
			state = ItemState.AVAILABLE;
		}
		else {
			throw new RuntimeException(String.format("Item: cannot repair while Item is in state: %s", state));
		}
	}

}
