package simulator;
import java.util.Arrays;

/**
 * A BitString class represents a series of 1s and 0s and can hold up to
 * a maximum of 16 bits and also keeps track of the number of bits stored.
 * It has operations to do various operations associated with 1s and 0s - 
 * substring, append, copy, setting and getting 2s complement value, etc. 
 * @author mmuppa
 *
 */
public class BitString {

	// Constants for range checking
	private final static int MAX_BITS = 32;
	public final static int MAX_VALUE = 2_147_483_647; // 2^31 - 1
	public final static int MIN_VALUE = -2_147_483_648; // -2^31
	private final static long MAX_UNSIGNED_VALUE = 4_294_967_295L; // 2^32 - 1
	
	
	private char[] mBits; 
	private int mLength;

	public BitString() { 
		mBits = new char[32];
	}
	
	/**
	 * Sets the corresponding bits by copying and also sets the length
	 * of the BitString. 
	 * @param bits Array of bits 
	 */
	public void setBits(char[] bits) {
		if (bits == null || bits.length > MAX_BITS) {
			throw new IllegalArgumentException("Invalid input: null or exceeds bit string length");
		}
		for (char a : bits) { 
			if (!(a == '1' || a == '0')) { 
				throw new IllegalArgumentException();
			}
		}
		mBits = Arrays.copyOf(bits, bits.length);
		mLength = bits.length;
	}

	/**
	 * Flips all the bits of the BitString. 
	 */
	public void invert() {
		if (mBits == null) {
			throw new IllegalArgumentException("Bit String must be set first.");
		}
		for (int i = 0; i < mLength; i++) {
			if (mBits[i] == '0') {
				mBits[i] = '1';
			} else {
				mBits[i] = '0';
			}
		}
	}

	/**
	 * Adds 1 to the BitString. 
	 */
	public void addOne() {
		if (mBits == null) {
			throw new IllegalArgumentException("Bit String must be set first.");
		}
		for (int i = mLength - 1; i >= 0; i--) {
			if (mBits[i] == '0') {
				mBits[i] = '1';
				return;
			} else {
				mBits[i] = '0';
			}
		}
	}

	/**
	 * Sets the unsigned binary representation of the input 
	 * decimal number 
	 * @param n the unsigned value
	 */
	public void setValue(int n) {
		// Check if it can be represented in the bits available
		if (n < 0 || n > MAX_UNSIGNED_VALUE) {
			throw new IllegalArgumentException("Cannot represent in "
					+ MAX_BITS + " bits.");
		}
		
		mBits = new char[MAX_BITS];
		mLength = MAX_BITS;
		for (int i = mLength - 1; i >= 0; i--) {
			mBits[i] = (n % 2 == 0) ? '0' : '1';
			n /= 2;
		}
	}

	/**
	 * Sets the 2s complement binary value of the input value
	 * @param n negative or positive decimal value
	 */
	public void setValue2sComp(int n) {
		if (n < MIN_VALUE || n > MAX_VALUE) {
			throw new IllegalArgumentException("Cannot represent in "
					+ MAX_BITS + " bits.");
		}
		boolean isNegative = n < 0;
		if (n < 0) {
			n *= -1;
		}
		setValue(n);
		if (isNegative) {
			invert();
			addOne();
		}
	}

	/**
	 * Displays the BitString in groups of four or
	 * in one group of 16. 
	 * @param groupsOfFour 
	 */
	public void display(boolean groupsOfFour) {
		for (int i = 0; i < mLength; i++) {
			if (groupsOfFour && (i % 4 == 0) && i != 0) {
				System.out.print(" ");
			}
			if (mBits[i] == '0') {
				System.out.print("0");
			} else {
				System.out.print("1");
			}
		}
	}

	/**
	 * Creates a copy of the BitString and returns
	 * @return copy of BitString object
	 */
	public BitString copy() {
		if (mBits == null) {
			throw new IllegalArgumentException("Nothing to copy.");
		}
		BitString copy = new BitString();
		copy.mLength = mLength;
		copy.mBits = Arrays.copyOf(mBits, mLength);
		return copy;
	}

	/**
	 * Returns the unsigned value of the BitString representation. 
	 * @return decimal unsigned value
	 */
	public int getValue() {
		if (mBits == null) {
			throw new IllegalArgumentException("Bit String must be set first.");
		}
		int value = 0;
		for (int i = 0; i < mLength; i++) {
			value *= 2;
			value += mBits[i] == '1' ? 1 : 0;
		}
		return value;
	}

	/**
	 * Returns the 2s complement value of the BitString representation. 
	 * @return decimal value
	 */
	public int getValue2sComp() {
		if (mBits == null) {
			throw new IllegalArgumentException("Bit String must be set first.");
		}
		boolean negative = mBits[0] == '1';
		if (negative) {
			BitString copyString = copy();
			copyString.invert();
			copyString.addOne();
			return -1 * copyString.getValue();
		} else {
			return getValue();
		}
	}

	public String getHex() {
		StringBuilder sb = new StringBuilder("0x");
		for (int i = 0; i < mLength; i += 4) {
			char[] bits = {mBits[i], mBits[i + 1], mBits[i + 2], mBits[i + 3]};
			BitString str = new BitString();
			str.setBits(bits);
			sb.append(Integer.toHexString(str.getValue2sComp()));
		}
		return sb.toString();
	}

	/**
	 * Returns a new BitString that is combination of this and the 
	 * other BitString appended to it. 
	 * @param other
	 * @return new BitString that combines both
	 */
	public BitString append(BitString other) {
		if (mBits == null || other == null) {
			throw new IllegalArgumentException("Bit String must be set first.");
		}
		BitString bitString = new BitString();
		if (mLength + other.mLength > MAX_BITS) {
			throw new IllegalArgumentException("Exceeds bit string length");
		}
		bitString.mBits = new char[mLength + other.mLength];
		int index = 0;
		for (int i = 0; i < this.mLength; i++) {
			bitString.mBits[index++] = this.mBits[i];
		}
		for (int i = 0; i < other.mLength; i++) {
			bitString.mBits[index++] = other.mBits[i];
		}
		bitString.mLength = mLength + other.mLength;

		return bitString;
	}

	/**
	 * Returns a substring of the given string. 
	 * @param source
	 * @param start
	 * @param length
	 * @return A new BitString is created from the source starting at the index
	 *         and with the length.
	 */
	public BitString substring(int start, int length) {
		BitString subStr = new BitString();
		subStr.mBits = new char[length];
		int i;
		for (i = 0; i < length; i++) {
			subStr.mBits[i] = mBits[start + i];
		}
		subStr.mLength = length;
		return subStr;
	}
	
	/**
	 * Yields the OpCode if the provided BitString is an instruction
	 * @param bs the opcode
	 * @return BitString
	 */
	public BitString getOpCode() { 
		return substring(0, 6);
	}
	
	public BitString getRs() { 
		return substring(6, 5);
	}
	
	public BitString getRt() { 
		return substring(11, 5);
	}
	
	public BitString getRd() { 
		return substring(16, 5);
	}
	
	public BitString getShamt() { 
		return substring(21, 5);
	}
	
	public BitString getFunct() { 
		return substring(26, 6);
	}
	
	public BitString getCnst() { 
		return substring(16, 16);
	}

	public BitString getPseudoAddr() { 
		return substring(6, 26);
	}
	
	/**
	 * Returns an array of the bits stored in the BitString
	 * @return character array of bits
	 */
	public char[] getBits() {
		return mBits;
	}

	/**
	 * Returns the length of the BitString
	 * @return length
	 */
	public int getLength() {
		return mLength;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (char c : mBits)
			sb.append(c);
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (other.getClass() != this.getClass())
			return false;
		BitString str = (BitString) other;
		return str.getValue() == this.getValue();
	}

}