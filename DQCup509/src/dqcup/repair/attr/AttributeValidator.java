package dqcup.repair.attr;

public interface AttributeValidator {
	/**
	 * Used for verify validness of single attribute
	 * 
	 * @param value
	 * @return
	 */
	public boolean valid(String value);
}
