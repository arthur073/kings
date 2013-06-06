package helloworld;

public class Sensor {
	
	private String name;
	private String value;
	
	public Sensor(String name, String value) {
		this.setName(name);
		this.setValue(value);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
