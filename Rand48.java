import java.io.*;
import java.util.*;

public final class Rand48 {
    public static Rand48 RNG = new Rand48();
    private static final long MODULO = (1L << 48);

    private long gen;

    public Rand48() {
        Rand48.RNG = this;
        setSeed(System.currentTimeMillis());
    }

    public void setSeed(long seed) {
        gen = ((seed << 16) | 0x330eL) & (MODULO - 1);
    }

    private long next() {
        gen = (0x5deece66dL * gen + 0xbL) & (MODULO - 1);
        return gen;
    }

    public double nextDouble() {
        return (double) next() / MODULO;
    }

//    public static void main(String[] args) {
//        Rand48 rand = new Rand48();
//        rand.setSeed(47L);
//        for(int i = 0; i < 20; i++) {
//            System.out.format("%.16f\n", rand.nextDouble());
//        }
//    }
}
