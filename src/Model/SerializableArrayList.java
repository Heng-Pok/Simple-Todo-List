package Model;

/**
 * A version of Java's ArrayList that is serializable.
 * Is used to save todo lists.
 * Can only store strings.
 * 
 * @Author Hengsocheat Pok
 */
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;

public class SerializableArrayList implements Serializable {
	private ArrayList<String> arrayList;

	public SerializableArrayList(ObservableList<String> observableListOfTasks) {
		arrayList = new ArrayList<>(observableListOfTasks);
	}

	public SerializableArrayList() {
		arrayList = new ArrayList<>();
	}

	public void addItem(String item) {
		arrayList.add(item);
	}

	public ArrayList<String> getList() {
		return arrayList;
	}

}
