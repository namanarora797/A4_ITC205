package library;

import java.text.SimpleDateFormat;
import java.util.Scanner;

import library.entities.Item;
import library.entities.ItemType;
import library.orderitem.OrderItemUI;
import library.orderitem.OrderItemControl;
import library.entities.Calendar;
import library.entities.Library;
import library.entities.Order;
import library.entities.Publisher;
import library.fixitem.FixItemUI;
import library.fixitem.FixItemControl;
import library.makepayment.MakePaymentUI;
import library.makepayment.MakePaymentControl;
//import library.returnItem.ReturnItemUI;
//import library.returnItem.ReturnItemControl;


public class Main {
	
	private static Scanner scanner;
	private static Library library;
	private static Calendar calendar;
	private static SimpleDateFormat simpleDateFormat;

		private static String MENU = "\t\tLibrary Main Menu\n\n" +
			"\tAP  : add publisher\n" +
			"\tLP : list publishers\n\n" +	
			"\tAI  : add item\n" +
			"\tLI : list items\n" +
			"\tFI : fix item\n\n" +
			"\tO  : order an item\n" +
			"\tL  : list orders\n" +
			"\tP  : pay for order\n\n" +
			"\tT  : increment date\n" +
			"\tQ  : quit\n\n" +
			"\tChoice : ";	

	public static void main(String[] args) {		
		try {			
			scanner = new Scanner(System.in);
			library = Library.getInstance();
			calendar = Calendar.getInstance();
			simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
			for (Publisher publisher : library.listPublishers()) {
				output(publisher);
			}
			output(" ");
			for (Item item : library.listItems()) {
				output(item);
			}
						
			boolean finished = false;
			
			while (!finished) {
				
				output("\n" + simpleDateFormat.format(calendar.getDate()));
				String choice = input(MENU);
				
				switch (choice.toUpperCase()) {
				
				case "AP": 
					addPublisher();
					break;
					
				case "LP": 
					listPublishers();
					break;
					
				case "AI": 
					addItem();
					break;
					
				case "LI": 
					listItems();
					break;
					
				case "FI": 
					fixItems();
					break;
					
				case "O": 
					orderItems();
					break;
					
				case "L": 
					listCurrentOrders();
					break;
					
				case "P": 
					makePayments();
					break;
					
				case "T": 
					incrementDate();
					break;
					
				case "Q": 
					finished = true;
					break;
					
				default: 
					output("\nInvalid option\n");
					break;
				}
				
				Library.save();
			}			
		} catch (RuntimeException e) {
			output(e);
		}		
		output("\nEnded\n");
	}	

	
	private static void makePayments() {
		new MakePaymentUI(new MakePaymentControl()).run();		
	}


	private static void listCurrentOrders() {
		output("");
		for (Order order : library.listCurrentOrders()) {
			output(order + "\n");
		}		
	}



	private static void listItems() {
		output("");
		for (Item book : library.listItems()) {
			output(book + "\n");
		}		
	}



	private static void listPublishers() {
		output("");
		for (Publisher member : library.listPublishers()) {
			output(member + "\n");
		}		
	}



	private static void orderItems() {
		new OrderItemUI(new OrderItemControl()).run();		
	}


	private static void fixItems() {
		new FixItemUI(new FixItemControl()).run();		
	}


	private static void incrementDate() {
		try {
			int days = Integer.valueOf(input("Enter number of days: ")).intValue();
			calendar.incrementDate(days);
			library.updateCurrentOrderStatus();
			output(simpleDateFormat.format(calendar.getDate()));
			
		} catch (NumberFormatException e) {
			 output("\nInvalid number of days\n");
		}
	}


	private static void addItem() {
		
		ItemType itemType = null;
		String typeMenu = """
			Select item type:
			    B : Book
			    D : DVD video disk
			    V : VHS video cassette
			    C : CD audio disk
			    A : Audio cassette
			   Choice <Enter quits> : """;

		while (itemType == null) {
			String type = input(typeMenu);
			
			switch (type.toUpperCase()) {
			case "B": 
				itemType = ItemType.BOOK;
				break;
				
			case "D": 
				itemType = ItemType.DVD;
				break;
				
			case "V": 
				itemType = ItemType.VHS;
				break;
				
			case "C": 
				itemType = ItemType.CD;
				break;
				
			case "A": 
				itemType = ItemType.CASSETTE;
				break;
				
			case "": 
				return;
			
			default:
				output(type + " is not a recognised Item type");
	
			}
		}

		String author = input("Enter author: ");
		String title  = input("Enter title: ");
		String callNo = input("Enter call number: ");
		double price = Double.valueOf(input("Enter price: ")).doubleValue();
		Item item = library.addItem(author, title, callNo, itemType, price);
		output("\n" + item + "\n");
		
	}

	
	private static void addPublisher() {
		try {
			String firstName  = input("Enter publisher name: ");
			String lastName = input("Enter contact name: ");
			String emailAddress = input("Enter email address: ");
			long phoneNo = Long.valueOf(input("Enter phone number: ")).intValue();
			Publisher publisher = library.addPublisher(firstName, lastName, emailAddress, phoneNo);
			output("\n" + publisher + "\n");
			
		} catch (NumberFormatException e) {
			 output("\nInvalid phone number\n");
		}
		
	}


	private static String input(String prompt) {
		System.out.print(prompt);
		return scanner.nextLine();
	}
	
	
	
	private static void output(Object object) {
		System.out.println(object);
	}

	
}
