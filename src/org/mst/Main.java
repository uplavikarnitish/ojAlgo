package org.mst;

import org.ojalgo.access.Access1D;
import org.ojalgo.array.PrimitiveArray;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.decomposition.MatrixDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.RawStore;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.random.Weibull;

/**
 * Created by nuplavikar on 9/11/16.
 */
public class Main
{
    public static void main(String args[])
    {
        /*
        Matrix C = new Matrix();
        //Find C^T
        Matrix Ct = C.getTransposeOf();

        //Find Ct.C
        Matrix CtC = Matrix.multiply(Ct, C);

        //Find Ct.C
        Matrix CCt = Matrix.multiply(C, Ct);

        System.out.println("CtC = "+CtC);
        System.out.println("CCt = "+CCt);

        RawStore C_raw = new RawStore(C.mat, C.m, C.n);*/
        /*MatrixStore matrixStore = new MatrixStore();
        matrixStore.add(C_raw);
        System.out.println("Num. rows = "+matrixStore.countRows()+"\t\tNum. columns = "+matrixStore.countColumns());

        SingularValue singularValue = new Si*/

        final PhysicalStore.Factory<Double, PrimitiveDenseStore> doublePrimitiveDenseStoreFactory = PrimitiveDenseStore.FACTORY;
        final PrimitiveDenseStore primitiveDenseStore =
                 doublePrimitiveDenseStoreFactory.makeZero(5, 6);
        //row 0
        int rowNo=0;
        primitiveDenseStore.set(rowNo, 0, 1);
        primitiveDenseStore.set(rowNo, 1, 0);
        primitiveDenseStore.set(rowNo, 2, 1);
        primitiveDenseStore.set(rowNo, 3, 0);
        primitiveDenseStore.set(rowNo, 4, 0);
        primitiveDenseStore.set(rowNo, 5, 0);
        //row 1
        rowNo = 1;
        primitiveDenseStore.set(rowNo, 0, 0);
        primitiveDenseStore.set(rowNo, 1, 1);
        primitiveDenseStore.set(rowNo, 2, 0);
        primitiveDenseStore.set(rowNo, 3, 0);
        primitiveDenseStore.set(rowNo, 4, 0);
        primitiveDenseStore.set(rowNo, 5, 0);
        //row 2
        rowNo = 2;
        primitiveDenseStore.set(rowNo, 0, 1);
        primitiveDenseStore.set(rowNo, 1, 1);
        primitiveDenseStore.set(rowNo, 2, 0);
        primitiveDenseStore.set(rowNo, 3, 0);
        primitiveDenseStore.set(rowNo, 4, 0);
        primitiveDenseStore.set(rowNo, 5, 0);
        //row 3
        rowNo = 3;
        primitiveDenseStore.set(rowNo, 0, 1);
        primitiveDenseStore.set(rowNo, 1, 0);
        primitiveDenseStore.set(rowNo, 2, 0);
        primitiveDenseStore.set(rowNo, 3, 1);
        primitiveDenseStore.set(rowNo, 4, 1);
        primitiveDenseStore.set(rowNo, 5, 0);
        //row 4
        rowNo = 4;
        primitiveDenseStore.set(rowNo, 0, 0);
        primitiveDenseStore.set(rowNo, 1, 0);
        primitiveDenseStore.set(rowNo, 2, 0);
        primitiveDenseStore.set(rowNo, 3, 1);
        primitiveDenseStore.set(rowNo, 4, 0);
        primitiveDenseStore.set(rowNo, 5, 1);


        System.out.printf("Incidence matrix: "+primitiveDenseStore);

        PrimitiveDenseStore C = primitiveDenseStore;

        MatrixStore<Double> Ct = C.transpose();

        MatrixStore<Double> CCt = C.multiply(Ct);

        MatrixStore<Double> CtC = Ct.multiply(C);

        System.out.println("\nC^t(Transpose) = " + Ct);
        System.out.println("CCt = " + CCt);
        System.out.println("CtC = " + CtC);


        //SingularValue<Double> svd = MatrixDecomposition.Factory<SingularValue<Double>>;
        final SingularValue<Double> svd = SingularValue.PRIMITIVE.make();
        System.out.println("\n\niscomputed = "+svd.isComputed());
        svd.decompose(C);
        System.out.println("\n\niscomputed = "+svd.isComputed());
        MatrixStore<Double> U = svd.getQ1();
        MatrixStore<Double> V = svd.getQ2();
        MatrixStore<Double> Sigma = svd.getD();

        System.out.println("\n\nU = "+U);
        System.out.println("\n\nSigma = "+Sigma);
        System.out.println("\n\nV = "+V);


        //Testing orthogonality
        System.out.println("\n\nU*Ut = "+U.multiply(U.transpose()));
        System.out.println("\n\nV*Vt = "+V.transpose().multiply(V));

        //Computing U*Sigma*(Vt)

        System.out.println("\n\n\nC = "+U.multiply(Sigma.multiply(V.transpose())));

        //k-approximation calculation
        int k = 2;
        truncateSigmaMemFriendly(k, Sigma);

        //Access1D<Double> access1D = Sigma.sliceDiagonal(k, k);


        //System.out.println("Access1D: "+access1D);

        //Find CCt
//        Matrix B = new Matrix();
//        System.out.println("A.B = "+Matrix.multiply(A, B));
//        Matrix C = B.getTransposeOf();
//        System.out.println(" C = B' = "+C);
//        System.out.println("A*C = "+ Matrix.multiply(A, C));
//        System.out.println("C*A = "+ Matrix.multiply(C, A));
    }

    public static int truncateSigmaMemFriendly(long k, MatrixStore<Double> Sigma)
    {
        long rank;
        final PhysicalStore.Factory<Double, PrimitiveDenseStore> doublePrimitiveDenseStoreFactory = PrimitiveDenseStore.FACTORY;

//        PrimitiveArray primitiveArray = PrimitiveArray.make(k);
//        primitiveArray.fillMatching(Sigma.sliceDiagonal(0, 0));
//        System.out.println("primitiveArray: "+primitiveArray);
//        System.out.println(Sigma);
//
//        System.out.println("Sigma class "+Sigma.getClass());

        if ( Sigma.limitOfColumn(1000) != Sigma.limitOfRow(2000)  )
        {
            System.err.println("Sigma should be a square singular matrix: limitOfColumn:"+Sigma.limitOfColumn(1000)+" limitOfRow:"+Sigma.limitOfRow(2000));
        }
        System.out.println("Sigma--------->\n"+Sigma);
        System.out.println("Sigma: limitOfColumn:"+Sigma.limitOfColumn(1000)+" limitOfRow:"+Sigma.limitOfRow(2000));
        PrimitiveDenseStore a = doublePrimitiveDenseStoreFactory.;
        //rank = Sigma.limitOfColumn()

        return 0;
    }

}
