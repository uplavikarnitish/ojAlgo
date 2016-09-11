package org.mst;

import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.RawStore;

/**
 * Created by nuplavikar on 8/14/16.
 */
public class LSI {

    public double C[][], CT[][];
    public int m, n;
    LSI(int m, int n)
    {
        C = new double[m][n];
    }

    LSI()
    {
        m = 5;
        n = 6;
        C = new double[][] {
                {1, 0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0, 0},
                {1, 1, 0, 0, 0, 0},
                {1, 0, 0, 1, 1, 0},
                {0, 0, 0, 1, 0, 1} };

        printMatrix("C", C, m, n);
    }

    public int printMatrix(String str, double mat[][], int row, int col)
    {
        System.out.println(row+"x"+col+"\t"+str+" = ");
        for (int i=0; i<row; i++)
        {
            String rowStr = "";
            for (int j = 0; j<col; j++)
            {
                rowStr = rowStr + mat[i][j] + "\t"+ "\t";
            }
            System.out.println(rowStr+"\n");
        }
        return 0;
    }

    //public int createCT()
    {

    }

    //public double[][] createCCT(double C[][], int m, int n)

    //createU(double C[][], int m, int n)
    {

    }

    public int runLSI(double C_[][], int m, int n)
    {
        int err = 0;
        this.C = C_;
        this.m = m;
        this.n = n;

        //PhysicalStore C = new RawStore(C_, m, n);
        //SingularValue svd = new SingularValue();


        return err;
    }
}