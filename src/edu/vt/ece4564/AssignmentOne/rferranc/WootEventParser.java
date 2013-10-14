package edu.vt.ece4564.AssignmentOne.rferranc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.ParseException;

import android.util.Log;

/**
 * Simple JSON {@link ContentHandler} to parse Woot API v2
 * 
 * @author Hamilton Turner
 */
public class WootEventParser implements ContentHandler {
	public enum KEY {
		Type("Type"), Id("Id"), Title("Title"), Site("Site"), StartDate(
				"StartDate"), EndDate("EndDate"), SalePrice("SalePrice"), 
				ARRAY("Array"), EVENT("Event"), UNKNOWN_KEY("Unknown_Key"), 
				UNKNOWN_OBJECT("Unknown_Object");

		private final String name;

		private KEY(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Stores all events internally, and once parsing is complete they can be
	 * retrieved
	 */
	public WootEventParser() {
		listener_ = new StoringWootEventListener();
	}

	private class StoringWootEventListener implements WootEventListener {

		private ArrayList<WootEvent> events_ = new ArrayList<WootEvent>();

		@Override
		public void onWootEvent(WootEvent event) {
			events_.add(event);
		}
	}

	public WootEventParser(WootEventListener listener) {
		if (listener_ == null)
			throw new IllegalArgumentException(
					"A WootEventListener is required");

		listener_ = listener;
	}

	private WootEventListener listener_;
	private Stack<KEY> currentKey = new Stack<WootEventParser.KEY>();
	private WootEvent currentEvent = new WootEvent();

	@Override
	public boolean startObjectEntry(String key) throws ParseException,
			IOException {
		try {
			Log.d("JSON Parser", "Adding " + KEY.valueOf(key).name);
			currentKey.push(KEY.valueOf(key));
		} catch (IllegalArgumentException iae) {
			Log.e("JSON Parser", "Unknown JSON Key encountered: " + key);
			currentKey.push(KEY.UNKNOWN_KEY);
			Log.d("JSON Parser", "Adding " + KEY.UNKNOWN_KEY.name);
		}
		return true;
	}

	@Override
	public boolean startObject() throws ParseException, IOException {
		if (currentKey.peek() == KEY.ARRAY && currentKey.size() == 1) {
			Log.d("JSON Parser", "Adding " + KEY.EVENT.name);
			currentKey.push(KEY.EVENT);
		} else {
			Log.d("JSON Parser", "Adding " + KEY.UNKNOWN_OBJECT.name);
			currentKey.push(KEY.UNKNOWN_OBJECT);
		}
		return true;
	}

	@Override
	public void startJSON() throws ParseException, IOException {
		Log.d("JSON Parser", "Start JSON");
	}

	@Override
	public boolean startArray() throws ParseException, IOException {
		Log.d("JSON Parser", "Adding " + KEY.ARRAY.name);
		currentKey.push(KEY.ARRAY);
		return true;
	}

	@Override
	public boolean primitive(Object value) throws ParseException, IOException {
		if (!currentKey.isEmpty()) {
			KEY key = currentKey.peek();
			switch (key) {
			case Type:
				currentEvent.setType((String) value);
				break;
			case Id:
				currentEvent.setID((String) value);
				break;
			case Site:
				currentEvent.setSite((String) value);
				break;
			case EndDate:
				currentEvent.setEndDate((String) value);
				break;
			case StartDate:
				currentEvent.setStartDate((String) value);
				break;
			case Title:
				currentEvent.setTitle((String) value);
				break;
			case SalePrice:
				String price = Double.toString((Double) value);
				if (currentEvent.getType() == "WootPlus")
					currentEvent
							.addItem(price + ": " + currentEvent.getTitle());
				else
					currentEvent.setPrice(price);
				break;
			default:
				Log.e("JSON Parser", "Ignoring value for key " + key.name());
				break;
			}
		} else {
			Log.e("JSON Parser", "Received value, but key was null");
		}

		return true;
	}

	@Override
	public boolean endObjectEntry() throws ParseException, IOException {
		Log.d("JSON Parser", "Removing " + currentKey.pop().name);
		return true;
	}

	@Override
	public boolean endObject() throws ParseException, IOException {
		KEY current = currentKey.pop();
		Log.d("JSON Parser", "Removing Object " + current.name);
		
		if (current == KEY.EVENT) {
			// Stack is empty, let's return the event
			listener_.onWootEvent(currentEvent);
			currentEvent = new WootEvent();
		}
		
		return true;
	}

	@Override
	public void endJSON() throws ParseException, IOException {
		Log.d("JSON Parser", "End JSON");
	}

	@Override
	public boolean endArray() throws ParseException, IOException {
		KEY current = currentKey.pop();
		Log.d("JSON Parser", "Removing " + current.name);
		return true;
	}
	
	public List<WootEvent> getEvents() {
		if (listener_ instanceof StoringWootEventListener)
			return ((StoringWootEventListener) listener_).events_;
		else 
			return new ArrayList<WootEvent>();
	}

}
