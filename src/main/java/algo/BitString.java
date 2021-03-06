package algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A bit string to save humans' lives.
 */
public class BitString implements Cloneable {
    private final int size;
    private final long[] data;

    private static final int DATA_CELL_EXP = 6;
    private static final long DATA_CELL_UNIT = 1L;
    private static final long ALL_BITS_SET = -1L;
    private static final long NONE_BITS_SET = 0L;

    private BitString(long[] data, int size) {
        this.size = size;
        this.data = data;
        if (((size - 1) >>> DATA_CELL_EXP) != data.length - 1) {
            throw new IllegalArgumentException("Data array is too small or too large");
        }
        if ((size & 63) != 0) {
            if ((data[data.length - 1] & bitMaskFrom(size)) != 0) {
                throw new IllegalArgumentException("Extra bits are non-zero (" + this.toString() + ")");
            }
        }
    }

    public BitString clone() {
        return new BitString(this.data.clone(), this.size);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + ", size " + size);
        }
    }

    private long bitMask(int from, int until) {
        if (from == until) {
            return 0;
        } else if ((until & 63) != (from & 63)) {
            return ((DATA_CELL_UNIT << (until - from)) - 1) << from;
        } else {
            return ALL_BITS_SET;
        }
    }

    private long bitMaskFrom(int from) {
        return ALL_BITS_SET << from;
    }

    private long bitMaskUntil(int until) {
        return ALL_BITS_SET >>> -until;
    }

    public int size() {
        return size;
    }

    public boolean get(int index) {
        checkIndex(index);
        return (data[index >>> DATA_CELL_EXP] & (DATA_CELL_UNIT << index)) != 0;
    }

    public void set(int index, boolean value) {
        checkIndex(index);
        if (value) {
            data[index >>> DATA_CELL_EXP] |= (DATA_CELL_UNIT << index);
        } else {
            data[index >>> DATA_CELL_EXP] &= ~(DATA_CELL_UNIT << index);
        }
    }

    public void flip(int index) {
        checkIndex(index);
        data[index >>> DATA_CELL_EXP] ^= (DATA_CELL_UNIT << index);
    }

    public void flip(int from, int until) {
        if (from > until) {
            throw new IllegalArgumentException("(from = " + from + ") > (until = " + until + ")");
        }
        int to = until - 1;
        if (from == to) {
            flip(from);
        } else if (from < to) {
            checkIndex(from);
            checkIndex(to);
            int fromCell = from >>> DATA_CELL_EXP;
            int toCell = to >>> DATA_CELL_EXP;
            if (fromCell == toCell) {
                data[fromCell] ^= bitMask(from, until);
            } else {
                data[fromCell] ^= bitMaskFrom(from);
                data[toCell] ^= bitMaskUntil(until);
                for (int i = fromCell + 1; i < toCell; ++i) {
                    data[i] = ~data[i];
                }
            }
        }
    }

    public boolean and(int from, int until) {
        if (from > until) {
            throw new IllegalArgumentException("(from = " + from + ") > (until = " + until + ")");
        }
        int to = until - 1;
        if (from == to) {
            return get(from);
        } else if (from < to) {
            checkIndex(from);
            checkIndex(to);
            int fromCell = from >>> DATA_CELL_EXP;
            int toCell = to >>> DATA_CELL_EXP;
            if (fromCell == toCell) {
                long mask = bitMask(from, until);
                return (data[fromCell] & mask) == mask;
            } else {
                long maskFrom = bitMaskFrom(from);
                if ((data[fromCell] & maskFrom) != maskFrom) {
                    return false;
                }
                long maskTo = bitMaskUntil(until);
                if ((data[toCell] & maskTo) != maskTo) {
                    return false;
                }
                for (int i = fromCell + 1; i < toCell; ++i) {
                    if (data[i] != ALL_BITS_SET) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            //empty query
            return true;
        }
    }

    public boolean or(int from, int until) {
        if (from > until) {
            throw new IllegalArgumentException("(from = " + from + ") > (until = " + until + ")");
        }
        int to = until - 1;
        if (from == to) {
            return get(from);
        } else if (from < to) {
            checkIndex(from);
            checkIndex(to);
            int fromCell = from >>> DATA_CELL_EXP;
            int toCell = to >>> DATA_CELL_EXP;
            if (fromCell == toCell) {
                long mask = bitMask(from, until);
                return (data[fromCell] & mask) != 0;
            } else {
                long maskFrom = bitMaskFrom(from);
                if ((data[fromCell] & maskFrom) != 0) {
                    return true;
                }
                long maskTo = bitMaskUntil(until);
                if ((data[toCell] & maskTo) != 0) {
                    return true;
                }
                for (int i = fromCell + 1; i < toCell; ++i) {
                    if (data[i] != NONE_BITS_SET) {
                        return true;
                    }
                }
                return false;
            }
        } else {
            //empty query
            return false;
        }
    }

    public int bitCount() {
        int rv = 0;
        for (long v : data) {
        	rv += Long.bitCount(v);
        }
        return rv;
    }
    
    public int hammingDist(Object o) {
        //if (this == o) {
        //    return 0;
        //}
        //if (o == null) {
        //    return this.toString().length();
        //}
        if (o.getClass() == this.getClass()) {
            BitString that = (BitString) (o);
            int diff = 0;
            for (int i = 0; i < data.length; i++) {
            	long v = data[i];
            	long w = that.data[i];            	
            	long x = v ^ w;
            	diff += Long.bitCount(x);
            }
            return diff;
        } else {
            return 0;
        }    	
    }   
    
    public BitString inverse() {        
    	for (int i = 0; i < data.length; i++) {        	                 	
        	long x = ~data[i];
        	data[i] = x;             	
        }
    	return this;    	
    }    
    
    public void permutation() {
    	ArrayList<Integer> indexes = new ArrayList<Integer>();// = new int[size];
    	BitString permut = this.clone();
    	for (int i = 0; i < size; i++) {
    		indexes.add(i);
    	    
    	}
    	
    	Collections.shuffle(indexes);
    	//System.out.println("list after shuffling : " + indexes);
    	
    	System.out.println();
    	for (int i = 0; i < size; ++i) {
    		permut.set(i, this.get(indexes.get(i)));
    	}
    	for (int i = 0; i < size; ++i) {
    		this.set(i, permut.get(i));
    	}
    	return;
    }
    
    
    public int LOmask(Object o) {   
    	
        if (o == null) {
            return 0;
        }
        if (o.getClass() == this.getClass()) {
            BitString that = (BitString) (o);
            int rv = 0;
            for (int i = 0; i < data.length; i++) {
            	long v = data[i];
            	long w = ~that.data[i];            	
            	long x = v ^ w;
            	if (x == -1) {
            		rv += 64;
            	} else {
            		return rv + Long.numberOfTrailingZeros(~x);
            	}            	
            }
            return rv;
        } else {
            return 0;
        }    	
    }   
    

    public int leadingOnes() {
        int rv = 0;
        for (long v : data) {
        	if (v == -1) {
        		rv += 64;
        	} else {
        		//System.out.println(Long.toBinaryString(v));
        		//System.out.println(Long.numberOfTrailingZeros(~v));
        		return rv + Long.numberOfTrailingZeros(~v);
        	}
        }
        return rv;
    }

    public String toString() {
        StringBuilder rv = new StringBuilder(size);
        for (int i = 0; i < size; ++i) {
            rv.append(get(i) ? '1' : '0');
        }
        return rv.toString();
    }

    public int hashCode() {
        long rv = size;
        for (long elem : data) {
            rv = 31 * rv + (elem ^ (elem >>> 32));
        }
        return (int) (rv);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() == this.getClass()) {
            BitString that = (BitString) (o);
            return size == that.size && Arrays.equals(data, that.data);
        } else {
            return false;
        }
    }

    public static BitString noneBitsSet(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Negative length requested");
        }
        long[] data = new long[(length + 63) >>> DATA_CELL_EXP];
        return new BitString(data, length);
    }

    public static BitString allBitsSet(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Negative length requested");
        }
        long[] data = new long[(length + 63) >>> DATA_CELL_EXP];
        Arrays.fill(data, -1L);
        data[data.length - 1] ^= ((1L << -length) - 1) << length;
        return new BitString(data, length);
    }

    public static BitString randomBitsSet(int length, Random random) {
        if (length < 0) {
            throw new IllegalArgumentException("Negative length requested");
        }
        long[] data = new long[(length + 63) >>> DATA_CELL_EXP];
        for (int i = 0; i < data.length; ++i) {
            data[i] = random.nextLong();
        }
        data[data.length - 1] &= ~(((1L << -length) - 1) << length);
        return new BitString(data, length);
    }

    private boolean runHiff(int from, int until, boolean expected, int[] toAdd) {
        if (from + 1 == until) {
            boolean rv = expected == get(from);
            toAdd[0] += rv ? 1 : 0;
            return rv;
        } else {
            int mid = (from + until) >>> 1;
            boolean l = runHiff(from, mid, expected, toAdd);
            boolean r = runHiff(mid, until, expected, toAdd);
            boolean rv = l & r;
            toAdd[0] += rv ? until - from : 0;
            return rv;
        }
    }

    public int hiffComponent(boolean countOnes) {
        if (Integer.bitCount(size) != 1) {
            throw new IllegalStateException("hiffComponent called on a string with length that is not 2^n");
        }
        int[] rv = new int[1];
        runHiff(0, size, countOnes, rv);
        return rv[0];
    }
}
