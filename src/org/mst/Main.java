package org.mst;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.RawStore;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.transformation.Householder;

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

        final BasicMatrix.Factory<PrimitiveMatrix> matFactoryPrimitive = PrimitiveMatrix.FACTORY;
        final PhysicalStore.Factory<Double, PrimitiveDenseStore> doublePrimitiveDenseStoreFactory = PrimitiveDenseStore.FACTORY;

        final PrimitiveMatrix matA = matFactoryPrimitive.makeZero(5, 6);

        final PrimitiveMatrix matB = matFactoryPrimitive.makeZero(6, 5);

        final PrimitiveMatrix matC = matA.multiply(matB);

        System.out.println("AxB: "+matC.toString());


        //Find CCt
//        Matrix B = new Matrix();
//        System.out.println("A.B = "+Matrix.multiply(A, B));
//        Matrix C = B.getTransposeOf();
//        System.out.println(" C = B' = "+C);
//        System.out.println("A*C = "+ Matrix.multiply(A, C));
//        System.out.println("C*A = "+ Matrix.multiply(C, A));
    }

}
