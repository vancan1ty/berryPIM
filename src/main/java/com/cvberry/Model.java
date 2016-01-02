package com.cvberry;

/**
 * Created by vancan1ty on 11/24/2015.
 *
 * Supposed to contain the "business logic" of your application.
 */
public class Model {

    public static int findNextBiggerPrime(int input) {
        int candidate = input+1;
        boolean primeFound = false;
        while(!primeFound) {
            int i;
            for (i = 2; i<=Math.sqrt(candidate);i++) {
                if (candidate % i == 0) {//then candidate is NOT a prime
                   candidate++;
                   break;
                }
            }
            if (i > Math.sqrt(candidate)) {//then we have a prime
               primeFound = true;
            }
        }
        return candidate;
    }
}
