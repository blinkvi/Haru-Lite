package cc.unknown.util.structure.comparators;

public class ValuePair {
    private final String name;
    private final String value;

    public ValuePair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
    public String toString() {
        return name + "=" + value;
    }
}