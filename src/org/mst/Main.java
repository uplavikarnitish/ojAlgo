package org.mst;

/**
 * Created by nuplavikar on 9/11/16.
 */
public class Main
{
    public static void main(String args[])
    {
        Matrix A = new Matrix();
        Matrix B = new Matrix();
        System.out.println("A.B = "+Matrix.multiply(A, B));
    }

}
