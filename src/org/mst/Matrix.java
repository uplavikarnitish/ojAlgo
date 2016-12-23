package org.mst;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;

/**
 * Created by nuplavikar on 9/11/16.
 */
public class Matrix
{
    double mat[][];
    int m, n;


    public Matrix(int m, int n)
    {
        System.out.println("Creating matrix of size: ("+m+" x "+n+")");
        mat = new double[m][n];
        this.m = m;
        this.n = n;
    }

    public Matrix()
    {
        m = 5;
        n = 6;
        mat = new double[][] {
                {1, 0, 1, 0, 0, 0},
                {0, 1, 0, 0, 0, 0},
                {1, 1, 0, 0, 0, 0},
                {1, 0, 0, 1, 1, 0},
                {0, 0, 0, 1, 0, 1} };

        System.out.println("Matrix created "+this);

    }

    public Matrix(BasicMatrix.Factory<PrimitiveMatrix> basicMatFactPrimMat)
    {
        if ( basicMatFactPrimMat != null )
        {
            //basicMatFactPrimMat.s
        }
    }

    public static Matrix multiply( Matrix a, Matrix b)
    {
        if ( a.n != b.m )
        {
            return null;
        }

        Matrix c = new Matrix(a.m, b.n);

        for ( int i=0; i< a.m; i++ )
        {
            for ( int j = 0; j<b.n; j++ )
            {
                c.mat[i][j] = 0;
                for ( int k = 0; k<a.n; k++ )
                {
                    c.mat[i][j] += (a.mat[i][k] * b.mat[k][j]);
                }
            }
        }
        c.m = a.m;
        c.n = b.n;
        return c;
    }

    public int transpose()
    {
        double[][] matT = new double[this.n][this.m];
        for ( int i=0; i< this.m; i++ )
        {
            for ( int j = 0; j<this.n; j++ )
            {
                if ( i==j )
                {
                    matT[i][j] = this.mat[i][j];
                }
                else
                {
                    matT[j][i] = this.mat[i][j];
                }
            }
        }
        this.mat = matT;
        int temp = this.m;
        this.m = this.n;
        this.n = temp;
        return 0;
    }

    public Matrix getTransposeOf()
    {
        Matrix transposeOfMat = new Matrix(this.n, this.m);
        double[][] matT = transposeOfMat.mat;
        for ( int i=0; i< this.m; i++ )
        {
            for ( int j = 0; j<this.n; j++ )
            {
                if ( i==j )
                {
                    matT[i][j] = this.mat[i][j];
                }
                else
                {
                    matT[j][i] = this.mat[i][j];
                }
            }
        }
        return transposeOfMat;
    }

    public String toString()
    {
        String strToPrint = "\n------------- ("+this.m+" x "+this.n+") Matrix ------------- \n";
        for ( int i=0; i< this.m; i++ )
        {
            for ( int j = 0; j<this.n; j++ )
            {
                strToPrint += this.mat[i][j] + "\t\t";
            }
            strToPrint += "\n";
        }
        strToPrint += "============== = = =======================\n";
        return strToPrint;
    }
}
