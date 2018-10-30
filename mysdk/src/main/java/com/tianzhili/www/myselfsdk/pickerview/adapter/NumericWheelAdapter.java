package com.tianzhili.www.myselfsdk.pickerview.adapter;


/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {
	
	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;
	
	// Values
	private int minValue;
	private int maxValue;

	/**
	 * Default constructor
	 */
	public NumericWheelAdapter() {
		this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	}

	/**
	 * Constructor
	 * @param minValue the wheel min value
	 * @param maxValue the wheel max value
	 */
	public NumericWheelAdapter(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public Object getItem(int index) {
		if(minValue<=maxValue){
			if (index >= 0 && index < getItemsCount()) {
				return minValue + index;
			}
		}else {
			if (index >= 0 && index < getItemsCount()) {
				if ((minValue + index) <24 ){
					return minValue + index;
				}else {
					return minValue + index-24;
				}
			}
		}

		return 0;
	}

	@Override
	public int getItemsCount() {
		if(minValue<=maxValue){
			return maxValue - minValue + 1;
		}else {
			return (24-minValue) + maxValue + 1;
		}
	}
	
	@Override
	public int indexOf(Object o){
		return (int)o - minValue;
	}
}
